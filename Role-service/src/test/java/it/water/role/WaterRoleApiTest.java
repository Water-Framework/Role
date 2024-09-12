package it.water.role;

import it.water.core.api.bundle.Runtime;
import it.water.core.api.model.PaginableResult;
import it.water.core.api.permission.Role;
import it.water.core.api.permission.RoleManager;
import it.water.core.api.registry.ComponentRegistry;
import it.water.core.api.repository.query.Query;
import it.water.core.api.service.Service;
import it.water.core.interceptors.annotations.Inject;
import it.water.core.model.exceptions.ValidationException;
import it.water.core.model.exceptions.WaterRuntimeException;
import it.water.core.permission.exceptions.UnauthorizedException;
import it.water.core.testing.utils.api.TestPermissionManager;
import it.water.core.testing.utils.bundle.TestRuntimeInitializer;
import it.water.core.testing.utils.junit.WaterTestExtension;
import it.water.repository.entity.model.exceptions.DuplicateEntityException;
import it.water.role.api.RoleApi;
import it.water.role.api.RoleRepository;
import it.water.role.api.RoleSystemApi;
import it.water.role.model.WaterRole;
import lombok.Setter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Generated with Water Generator.
 * Test class for Role Services.
 * <p>
 * Please use RoleRestTestApi for ensuring format of the json response
 */
@ExtendWith(WaterTestExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WaterRoleApiTest implements Service {

    @Inject
    @Setter
    private ComponentRegistry componentRegistry;

    @Inject
    @Setter
    private RoleApi roleApi;

    @Inject
    @Setter
    private Runtime runtime;

    @Inject
    @Setter
    private RoleRepository roleRepository;

    @Inject
    @Setter
    //default permission manager in test environment;
    private TestPermissionManager permissionManager;

    @Inject
    @Setter
    //test role manager
    private RoleManager roleManager;

    //admin user
    private it.water.core.api.model.User adminUser;
    private it.water.core.api.model.User roleManagerUser;
    private it.water.core.api.model.User roleViewerUser;
    private it.water.core.api.model.User roleEditorUser;

    private Role roleManagerRole;
    private Role roleViewerRole;
    private Role roleEditorRole;

    @BeforeAll
    public void beforeAll() {
        //getting user
        roleManagerRole = roleManager.getRole(WaterRole.DEFAULT_MANAGER_ROLE);
        roleViewerRole = roleManager.getRole(WaterRole.DEFAULT_VIEWER_ROLE);
        roleEditorRole = roleManager.getRole(WaterRole.DEFAULT_EDITOR_ROLE);
        Assertions.assertNotNull(roleManagerRole);
        Assertions.assertNotNull(roleViewerRole);
        Assertions.assertNotNull(roleEditorRole);
        //impersonate admin so we can test the happy path
        adminUser = permissionManager.findUser("admin");
        roleManagerUser = permissionManager.addUser("manager", "name", "lastname", "manager@a.com", false);
        roleViewerUser = permissionManager.addUser("viewer", "name", "lastname", "viewer@a.com", false);
        roleEditorUser = permissionManager.addUser("editor", "name", "lastname", "editor@a.com", false);
        //starting with admin permissions
        roleManager.addRole(roleManagerUser.getId(), roleManagerRole);
        roleManager.addRole(roleViewerUser.getId(), roleViewerRole);
        roleManager.addRole(roleEditorUser.getId(), roleEditorRole);
        //default security context in test environment is admin
    }

    /**
     * Testing basic injection of basic component for role entity.
     */
    @Test
    @Order(1)
    public void componentsInsantiatedCorrectly() {
        this.roleApi = this.componentRegistry.findComponent(RoleApi.class, null);
        Assertions.assertNotNull(this.roleApi);
        Assertions.assertNotNull(this.componentRegistry.findComponent(RoleSystemApi.class, null));
        this.roleRepository = this.componentRegistry.findComponent(RoleRepository.class, null);
        Assertions.assertNotNull(this.roleRepository);
    }

    /**
     * Testing simple save and version increment
     */
    @Test
    @Order(2)
    public void saveOk() {
        WaterRole entity = createRole(0);
        entity = this.roleApi.save(entity);
        Assertions.assertEquals(1, entity.getEntityVersion());
        Assertions.assertTrue(entity.getId() > 0);
        Assertions.assertEquals("exampleField0", entity.getName());
    }

    /**
     * Testing update logic, basic test
     */
    @Test
    @Order(3)
    public void updateShouldWork() {
        Query q = this.roleRepository.getQueryBuilderInstance().createQueryFilter("exampleField=exampleField0");
        WaterRole entity = this.roleApi.find(q);
        Assertions.assertNotNull(entity);
        entity = updateRole(entity, "exampleFieldUpdated", entity.getDescription());
        entity = this.roleApi.update(entity);
        Assertions.assertEquals("exampleFieldUpdated", entity.getName());
        Assertions.assertEquals(2, entity.getEntityVersion());
    }

    /**
     * Testing update logic, basic test
     */
    @Test
    @Order(4)
    public void updateShouldFailWithWrongVersion() {
        Query q = this.roleRepository.getQueryBuilderInstance().createQueryFilter("exampleField=exampleFieldUpdated");
        WaterRole errorEntity = this.roleApi.find(q);
        Assertions.assertEquals("exampleFieldUpdated", errorEntity.getName());
        Assertions.assertEquals(2, errorEntity.getEntityVersion());
        errorEntity.setEntityVersion(1);
        Assertions.assertThrows(WaterRuntimeException.class, () -> this.roleApi.update(errorEntity));
    }

    /**
     * Testing finding all entries with no pagination
     */
    @Test
    @Order(5)
    public void findAllShouldWork() {
        PaginableResult<WaterRole> all = this.roleApi.findAll(null, -1, -1, null);
        Assertions.assertTrue(all.getResults().size() == 1);
    }

    /**
     * Testing finding all entries with settings related to pagination.
     * Searching with 5 items per page starting from page 1.
     */
    @Test
    @Order(6)
    public void findAllPaginatedShouldWork() {
        for (int i = 2; i < 11; i++) {
            WaterRole u = createRole(i);
            this.roleApi.save(u);
        }
        PaginableResult<WaterRole> paginated = this.roleApi.findAll(null, 7, 1, null);
        Assertions.assertEquals(7, paginated.getResults().size());
        Assertions.assertEquals(1, paginated.getCurrentPage());
        Assertions.assertEquals(2, paginated.getNextPage());
        paginated = this.roleApi.findAll(null, 7, 2, null);
        Assertions.assertEquals(3, paginated.getResults().size());
        Assertions.assertEquals(2, paginated.getCurrentPage());
        Assertions.assertEquals(1, paginated.getNextPage());
    }

    /**
     * Testing removing all entities using findAll method.
     */
    @Test
    @Order(7)
    public void removeAllShouldWork() {
        PaginableResult<WaterRole> paginated = this.roleApi.findAll(null, -1, -1, null);
        paginated.getResults().forEach(entity -> {
            this.roleApi.remove(entity.getId());
        });
        Assertions.assertTrue(this.roleApi.countAll(null) == 0);
    }

    /**
     * Testing failure on duplicated entity
     */
    @Test
    @Order(8)
    public void saveShouldFailOnDuplicatedEntity() {
        WaterRole entity = createRole(1);
        this.roleApi.save(entity);
        WaterRole duplicated = this.createRole(1);
        //cannot insert new entity wich breaks unique constraint
        Assertions.assertThrows(DuplicateEntityException.class, () -> this.roleApi.save(duplicated));
        WaterRole secondEntity = createRole(2);
        this.roleApi.save(secondEntity);
        WaterRole updatedSecondEntity = updateRole(secondEntity, "updateName", secondEntity.getDescription());
        //cannot update an entity colliding with other entity on unique constraint
        Assertions.assertThrows(DuplicateEntityException.class, () -> this.roleApi.update(updatedSecondEntity));
    }

    /**
     * Testing failure on validation failure for example code injection
     */
    @Test
    @Order(9)
    public void updateShouldFailOnValidationFailure() {
        WaterRole newEntity = new WaterRole("<script>function(){alert('ciao')!}</script>", "description");
        Assertions.assertThrows(ValidationException.class, () -> this.roleApi.save(newEntity));
    }

    /**
     * Testing Crud operations on manager role
     */
    @Order(10)
    @Test
    public void managerCanDoEverything() {
        TestRuntimeInitializer.getInstance().impersonate(roleManagerUser, runtime);
        final WaterRole entity = createRole(101);
        WaterRole savedEntity = Assertions.assertDoesNotThrow(() -> this.roleApi.save(entity));
        WaterRole updatedEntity = updateRole(savedEntity, "newSavedEntity", savedEntity.getDescription());
        Assertions.assertDoesNotThrow(() -> this.roleApi.update(savedEntity));
        Assertions.assertDoesNotThrow(() -> this.roleApi.find(updatedEntity.getId()));
        Assertions.assertDoesNotThrow(() -> this.roleApi.remove(updatedEntity.getId()));

    }

    @Order(11)
    @Test
    public void viewerCannotSaveOrUpdateOrRemove() {
        TestRuntimeInitializer.getInstance().impersonate(roleViewerUser, runtime);
        final WaterRole entity = createRole(201);
        Assertions.assertThrows(UnauthorizedException.class, () -> this.roleApi.save(entity));
        //viewer can search
        WaterRole found = Assertions.assertDoesNotThrow(() -> this.roleApi.findAll(null, -1, -1, null).getResults().stream().findFirst()).get();
        Assertions.assertDoesNotThrow(() -> this.roleApi.find(found.getId()));
        //viewer cannot update or remove
        WaterRole updateFound = updateRole(found, "newName", found.getDescription());
        Assertions.assertThrows(UnauthorizedException.class, () -> this.roleApi.update(updateFound));
        Assertions.assertThrows(UnauthorizedException.class, () -> this.roleApi.remove(updateFound.getId()));
    }

    @Order(12)
    @Test
    public void editorCannotRemove() {
        TestRuntimeInitializer.getInstance().impersonate(roleEditorUser, runtime);
        final WaterRole entity = createRole(301);
        WaterRole savedEntity = Assertions.assertDoesNotThrow(() -> this.roleApi.save(entity));
        WaterRole updatedEntity = updateRole(savedEntity, "editorNewSavedEntity", savedEntity.getDescription());
        Assertions.assertDoesNotThrow(() -> this.roleApi.update(entity));
        Assertions.assertDoesNotThrow(() -> this.roleApi.find(updatedEntity.getId()));
        Assertions.assertThrows(UnauthorizedException.class, () -> this.roleApi.remove(updatedEntity.getId()));
    }

    private WaterRole createRole(int seed) {
        Role entity = new WaterRole("exampleField" + seed, "description" + seed);
        return (WaterRole) entity;
    }

    private WaterRole updateRole(WaterRole role, String newName, String newDescription) {
        WaterRole updateRole = new WaterRole(newName, newDescription);
        updateRole.setId(role.getId());
        updateRole.setEntityVersion(role.getEntityVersion());
        return updateRole;
    }
}
