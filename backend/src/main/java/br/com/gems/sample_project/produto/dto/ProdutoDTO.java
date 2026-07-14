package br.com.gems.sample_project.produto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoDTO {

    private String id;

    private String nome;

    private String descricao;

    private BigDecimal preco;

    private LocalDateTime dataCriacao;

}
