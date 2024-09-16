
/*
 * Copyright 2024 Aristide Cittadino
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.water.role;

import com.intuit.karate.junit5.Karate;
import it.water.core.api.bundle.Runtime;
import it.water.core.api.model.User;
import it.water.core.api.registry.ComponentRegistry;
import it.water.core.api.user.UserManager;
import it.water.core.security.model.principal.UserPrincipal;
import it.water.implementation.spring.security.SpringSecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;

@SpringBootTest(classes = RoleApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "water.rest.security.jwt.validate=false",
        "water.testMode=true"
})
public class RoleRestSpringApiTest {

    @Autowired
    private ComponentRegistry componentRegistry;

    @BeforeEach
    void impersonateAdmin() {
        //jwt token service is disabled, we just inject admin user for bypassing permission system
        //just remove this line if you want test with permission system working
        fillSecurityContextWithAdmin();
    }

    @Karate.Test
    Karate restInterfaceTest() {
        return Karate.run("../Role-service/src/test/resources/karate");
    }

    private void fillSecurityContextWithAdmin() {
        Runtime runtime = this.componentRegistry.findComponent(Runtime.class, null);
        UserManager userManager = this.componentRegistry.findComponent(UserManager.class, null);
        User u = userManager.findUser("admin");
        UserPrincipal userPrincipal = new UserPrincipal("admin", true, 1, User.class.getName());
        runtime.fillSecurityContext(new SpringSecurityContext(Collections.singleton(userPrincipal)));
    }
}
