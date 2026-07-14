package br.com.gems.sample_project.notificacao.listener;

import br.com.gems.sample_project.produto.event.ProdutoExcluidoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Demonstra reação cross-module a um evento de domínio: o módulo {@code notificacao} reage
 * à exclusão de um {@code Produto} (módulo {@code produto}) sem nenhuma chamada direta entre
 * os domínios — apenas via {@link ApplicationModuleListener} consumindo o Event POJO publicado
 * por {@code ProdutoService.delete}.
 */
@Slf4j
@Component
public class ProdutoExcluidoListener {

    @ApplicationModuleListener
    public void aoExcluirProduto( ProdutoExcluidoEvent event ) {
        log.info( "Notificação: produto {} foi excluído — envio de notificação seria disparado aqui.", event.id() );
    }

}
