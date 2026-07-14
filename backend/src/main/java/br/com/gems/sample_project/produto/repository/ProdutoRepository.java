package br.com.gems.sample_project.produto.repository;

import br.com.gems.jpa.repository.BaseCustomJpaRepository;
import br.com.gems.sample_project.produto.dto.ProdutoFilterParams;
import br.com.gems.sample_project.produto.dto.ProdutoResponseDTO;
import br.com.gems.sample_project.produto.entity.Produto;
import br.com.gems.utils.ObjectUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, UUID>, BaseCustomJpaRepository<Produto> {

    boolean existsByNomeIgnoreCase( String nome );

    default Page<ProdutoResponseDTO> search( ProdutoFilterParams filterParams, Pageable pageable ) {
        return new PageImpl<>( this.searchQuery( filterParams, pageable ), pageable, this.countQuery( filterParams ) );
    }

    private Long countQuery( ProdutoFilterParams filterParams ) {
        var hql = new StringBuilder();
        var params = new HashMap<String, Object>();

        hql.append( " SELECT count(p.id) " );
        hql.append( " FROM Produto p " );
        appendFilters( filterParams, hql, params );

        return this.executeCountHql( hql, params );
    }

    private List<ProdutoResponseDTO> searchQuery( ProdutoFilterParams filterParams, Pageable pageable ) {
        var hql = new StringBuilder();
        var params = new HashMap<String, Object>();

        hql.append( " SELECT new br.com.gems.sample_project.produto.dto.ProdutoResponseDTO( " );
        hql.append( "       p.id, " );
        hql.append( "       p.nome, " );
        hql.append( "       p.preco " );
        hql.append( " ) " );
        hql.append( " FROM Produto p " );
        appendFilters( filterParams, hql, params );

        return this.executeHql( hql, params, pageable, ProdutoResponseDTO.class );
    }

    private void appendFilters( ProdutoFilterParams filterParams, StringBuilder hql, HashMap<String, Object> params ) {
        hql.append( " WHERE 1=1 " );

        if ( ObjectUtil.isNotNullAndNotEmpty( filterParams.getNome() ) ) {
            hql.append( " AND LOWER(p.nome) LIKE :nome " );
            params.put( "nome", "%" + filterParams.getNome().toLowerCase() + "%" );
        }
    }

}
