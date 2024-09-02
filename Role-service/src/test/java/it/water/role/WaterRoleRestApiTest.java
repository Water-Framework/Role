
package it.water.role;

import it.water.core.api.service.Service;

import com.intuit.karate.junit5.Karate;
import it.water.core.testing.utils.junit.WaterTestExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(WaterTestExtension.class)
public class WaterRoleRestApiTest implements Service {
    
    @Karate.Test
    Karate restInterfaceTest() {
        return Karate.run("classpath:karate");
    }
}