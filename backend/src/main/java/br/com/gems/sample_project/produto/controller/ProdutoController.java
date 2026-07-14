package br.com.gems.sample_project.produto.controller;

import br.com.gems.exception.base.BaseController;
import br.com.gems.sample_project.produto.dto.ProdutoDTO;
import br.com.gems.sample_project.produto.dto.ProdutoFilterParams;
import br.com.gems.sample_project.produto.dto.ProdutoResponseDTO;
import br.com.gems.sample_project.produto.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag( name = "Produtos", description = "Gerenciamento de produtos" )
@RestController
@RequestMapping( "/api/produtos" )
@RequiredArgsConstructor
public class ProdutoController extends BaseController {

    private final ProdutoService service;

    @Operation( summary = "Busca produto por ID", description = "Retorna os dados completos de um produto pelo seu UUID." )
    @GetMapping( "/{id}" )
    @PreAuthorize( "hasAnyRole('ADMIN','USER')" )
    public ResponseEntity<ProdutoDTO> findById( @PathVariable UUID id ) {
        return ResponseEntity.ok( service.findById( id ) );
    }

    @Operation( summary = "Pesquisa paginada de produtos", description = "Filtra por nome. Retorna página de resultados." )
    @PostMapping( "/search" )
    @PreAuthorize( "hasAnyRole('ADMIN','USER')" )
    public ResponseEntity<Page<ProdutoResponseDTO>> search(
            @RequestBody ProdutoFilterParams filterParams,
            @PageableDefault( page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.DESC ) Pageable pageable
    ) {
        return ResponseEntity.ok( service.search( filterParams, pageable ) );
    }

    @Operation( summary = "Cadastra novo produto", responses = {
        @ApiResponse( responseCode = "201", description = "Produto criado com sucesso" ),
        @ApiResponse( responseCode = "400", description = "Dados inválidos ou nome duplicado (BusinessException)" )
    } )
    @PostMapping
    @PreAuthorize( "hasAnyRole('ADMIN','USER')" )
    public ResponseEntity<ProdutoDTO> insert( @RequestBody ProdutoDTO dto, HttpServletRequest request ) {
        super.setBodyToExceptionLog( dto, request );
        return ResponseEntity.status( HttpStatus.CREATED ).body( service.insert( dto ) );
    }

    @Operation( summary = "Atualiza dados de um produto", responses = {
        @ApiResponse( responseCode = "200", description = "Produto atualizado com sucesso" ),
        @ApiResponse( responseCode = "400", description = "Dados inválidos (BusinessException)" ),
        @ApiResponse( responseCode = "404", description = "Produto não encontrado" )
    } )
    @PutMapping( "/{id}" )
    @PreAuthorize( "hasAnyRole('ADMIN','USER')" )
    public ResponseEntity<ProdutoDTO> save( @PathVariable UUID id, @RequestBody ProdutoDTO dto, HttpServletRequest request ) {
        super.setBodyToExceptionLog( dto, request );
        dto.setId( id.toString() );
        return ResponseEntity.ok( service.save( id, dto ) );
    }

    @Operation( summary = "Exclui (soft-delete) um produto", description = "Exclusão lógica. Publica ProdutoExcluidoEvent para outros módulos (ex.: notificacao).", responses = {
        @ApiResponse( responseCode = "204", description = "Excluído com sucesso" ),
        @ApiResponse( responseCode = "403", description = "Apenas ADMIN pode excluir" ),
        @ApiResponse( responseCode = "404", description = "Produto não encontrado" )
    } )
    @DeleteMapping( "/{id}" )
    @PreAuthorize( "hasRole('ADMIN')" )
    public ResponseEntity<Void> delete( @PathVariable UUID id ) {
        service.delete( id );
        return ResponseEntity.noContent().build();
    }

}
