
package it.water.role;

import it.water.core.api.registry.ComponentRegistry;
import it.water.core.api.service.Service;

import com.intuit.karate.junit5.Karate;
import it.water.core.interceptors.annotations.Inject;
import it.water.core.testing.utils.bundle.TestRuntimeInitializer;
import it.water.core.testing.utils.junit.WaterTestExtension;
import it.water.core.testing.utils.runtime.TestRuntimeUtils;
import lombok.Setter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(WaterTestExtension.class)
public class WaterRoleRestApiTest implements Service {

    @Inject
    @Setter
    private ComponentRegistry componentRegistry;
    @BeforeEach
    void beforeEach(){
        TestRuntimeUtils.impersonateAdmin(componentRegistry);
    }

    @Karate.Test
    Karate restInterfaceTest() {
        return Karate.run("classpath:karate");
    }
}
