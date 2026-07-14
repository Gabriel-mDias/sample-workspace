package br.com.gems.sample_project.produto.service;

import br.com.gems.exception.exception.BusinessException;
import br.com.gems.sample_project.produto.dto.ProdutoDTO;
import br.com.gems.sample_project.produto.dto.ProdutoFilterParams;
import br.com.gems.sample_project.produto.dto.ProdutoResponseDTO;
import br.com.gems.sample_project.produto.entity.Produto;
import br.com.gems.sample_project.produto.event.ProdutoExcluidoEvent;
import br.com.gems.sample_project.produto.repository.ProdutoRepository;
import br.com.gems.utils.ObjectUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ModelMapper mapper;
    private final ProdutoRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public ProdutoDTO findById( String id ) {
        return this.findById( id == null ? null : UUID.fromString( id ) );
    }

    public ProdutoDTO findById( UUID id ) {
        if ( ObjectUtil.isNullOrEmpty( id ) ) {
            throw new BusinessException( "O id não foi informado!" );
        }
        return repository.findById( id )
                .map( entity -> mapper.map( entity, ProdutoDTO.class ) )
                .orElseThrow( () -> new BusinessException( "Produto não encontrado!" ) );
    }

    public Page<ProdutoResponseDTO> search( ProdutoFilterParams filterParams, Pageable pageable ) {
        return repository.search( filterParams, pageable );
    }

    public void delete( String id ) {
        this.delete( id == null ? null : UUID.fromString( id ) );
    }

    @Transactional( rollbackOn = Exception.class )
    public void delete( UUID id ) {
        if ( ObjectUtil.isNullOrEmpty( id ) ) {
            throw new BusinessException( "O id não foi informado!" );
        }

        if ( !repository.existsById( id ) ) {
            throw new BusinessException( "Produto não encontrado!" );
        }

        // Soft-delete via @SQLDelete na entidade Produto.
        repository.deleteById( id );

        // Evento síncrono (mesma transação): outros módulos (ex.: notificacao) reagem sem chamada direta.
        eventPublisher.publishEvent( new ProdutoExcluidoEvent( id ) );
    }

    @Transactional( rollbackOn = Exception.class )
    public ProdutoDTO insert( ProdutoDTO dto ) {
        if ( ObjectUtil.isNullOrEmpty( dto ) ) {
            throw new BusinessException( "Os dados não foram informados!" );
        }

        validate( dto, true );

        dto.setDataCriacao( LocalDateTime.now() );

        var entity = repository.save( mapper.map( dto, Produto.class ) );

        return mapper.map( entity, ProdutoDTO.class );
    }

    @Transactional( rollbackOn = Exception.class )
    public ProdutoDTO save( UUID id, ProdutoDTO dto ) {
        var entity = repository.findById( id )
                .orElseThrow( () -> new BusinessException( "Produto não encontrado!" ) );

        validate( dto, false );

        entity.setNome( dto.getNome() );
        entity.setDescricao( dto.getDescricao() );
        entity.setPreco( dto.getPreco() );

        return mapper.map( repository.save( entity ), ProdutoDTO.class );
    }

    private void validate( ProdutoDTO dto, boolean isInsert ) {
        var errors = new ArrayList<String>();

        if ( ObjectUtil.isNullOrEmpty( dto.getNome() ) ) {
            errors.add( "O nome é obrigatório." );
        } else if ( isInsert && repository.existsByNomeIgnoreCase( dto.getNome() ) ) {
            errors.add( "Já existe um produto cadastrado com este nome." );
        }

        if ( ObjectUtil.isNullOrEmpty( dto.getPreco() ) ) {
            errors.add( "O preço é obrigatório." );
        } else if ( dto.getPreco().compareTo( BigDecimal.ZERO ) <= 0 ) {
            errors.add( "O preço deve ser maior que zero." );
        }

        if ( !errors.isEmpty() ) {
            throw new BusinessException( errors );
        }
    }

}
