package br.com.gems.sample_project.produto.service;

import br.com.gems.exception.exception.BusinessException;
import br.com.gems.sample_project.produto.dto.ProdutoDTO;
import br.com.gems.sample_project.produto.entity.Produto;
import br.com.gems.sample_project.produto.event.ProdutoExcluidoEvent;
import br.com.gems.sample_project.produto.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith( MockitoExtension.class )
class ProdutoServiceTest {

    @Mock
    private ModelMapper mapper;

    @Mock
    private ProdutoRepository repository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ProdutoService service;

    private ProdutoDTO dtoValido() {
        var dto = new ProdutoDTO();
        dto.setNome( "Produto Teste" );
        dto.setDescricao( "Descrição de teste" );
        dto.setPreco( BigDecimal.TEN );
        return dto;
    }

    @Test
    void deveLancarExcecao_quandoNomeEmBranco() {
        // Arrange
        var dto = dtoValido();
        dto.setNome( "  " );

        // Act
        var exception = assertThrows( BusinessException.class, () -> service.insert( dto ) );

        // Assert
        assertTrue( exception.getMessage().contains( "nome" ) );
        verify( repository, never() ).save( any() );
    }

    @Test
    void deveLancarExcecao_quandoNomeDuplicado() {
        // Arrange
        var dto = dtoValido();
        when( repository.existsByNomeIgnoreCase( "Produto Teste" ) ).thenReturn( true );

        // Act
        var exception = assertThrows( BusinessException.class, () -> service.insert( dto ) );

        // Assert
        assertTrue( exception.getMessage().toLowerCase().contains( "nome" ) );
        verify( repository, never() ).save( any() );
    }

    @Test
    void deveLancarExcecao_quandoProdutoNaoEncontradoPorId() {
        // Arrange
        var id = UUID.randomUUID();
        when( repository.findById( id ) ).thenReturn( Optional.empty() );

        // Act
        var exception = assertThrows( BusinessException.class, () -> service.findById( id ) );

        // Assert
        assertEquals( "Produto não encontrado!", exception.getMessage() );
    }

    @Test
    void devePublicarEvento_quandoExcluirProduto() {
        // Arrange
        var id = UUID.randomUUID();
        when( repository.existsById( id ) ).thenReturn( true );

        // Act
        service.delete( id );

        // Assert
        verify( repository ).deleteById( id );

        var captor = ArgumentCaptor.forClass( ProdutoExcluidoEvent.class );
        verify( eventPublisher ).publishEvent( captor.capture() );
        assertEquals( id, captor.getValue().id() );
    }

    @Test
    void deveInserirEPersistir_quandoDadosValidos() {
        // Arrange
        var dto = dtoValido();
        var entity = new Produto();
        var savedEntity = new Produto();
        var responseDto = new ProdutoDTO();

        when( repository.existsByNomeIgnoreCase( "Produto Teste" ) ).thenReturn( false );
        when( mapper.map( dto, Produto.class ) ).thenReturn( entity );
        when( repository.save( entity ) ).thenReturn( savedEntity );
        when( mapper.map( savedEntity, ProdutoDTO.class ) ).thenReturn( responseDto );

        // Act
        var result = service.insert( dto );

        // Assert
        assertEquals( responseDto, result );
        verify( repository ).save( entity );
    }

}
