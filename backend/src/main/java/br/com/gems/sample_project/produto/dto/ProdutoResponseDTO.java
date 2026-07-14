package br.com.gems.sample_project.produto.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoResponseDTO(
        UUID id,
        String nome,
        BigDecimal preco
) {
}
