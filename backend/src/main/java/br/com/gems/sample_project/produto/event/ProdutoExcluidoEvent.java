package br.com.gems.sample_project.produto.event;

import java.util.UUID;

/**
 * Event POJO publicado após a exclusão (soft-delete) de um {@code Produto}.
 * Apenas dados — sem lógica. Consumido por outros módulos (ex.: {@code notificacao})
 * via {@code @ApplicationModuleListener}, sem chamada direta entre domínios.
 */
public record ProdutoExcluidoEvent(UUID id) {
}
