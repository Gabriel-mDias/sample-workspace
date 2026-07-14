package br.com.gems.sample_project;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Valida as fronteiras dos módulos (produto, security, notificacao) segundo as convenções
 * do Spring Modulith: ciclos entre módulos, acesso indevido a tipos internos, etc.
 */
class ModularityTest {

    @Test
    void verifiesModularStructure() {
        ApplicationModules.of( SampleProjectApplication.class ).verify();
    }

}
