# Role Module — Role-Based Access Control

## Purpose
Manages the **Role** layer of the Water Framework's RBAC (Role-Based Access Control) system. Roles are named groups that aggregate permissions. Users are assigned roles, and roles are granted permissions on entity actions. This module provides CRUD for roles, role-to-user assignment, and the `WaterRoleManager` integration component used by other modules.

## Sub-modules

| Sub-module | Runtime | Key Classes |
|---|---|---|
| `Role-api` | All | `RoleApi`, `RoleSystemApi`, `RoleRestApi`, `RoleRepository`, `UserRoleRepository`, `RoleManager` |
| `Role-model` | All | `WaterRole`, `WaterUserRole`, `RoleActions`, `RoleConstants` |
| `Role-service` | Water/OSGi | Service impl, REST controller, `WaterRoleManager`, `RoleIntegrationLocalClient` |
| `Role-service-spring` | Spring Boot | Spring MVC REST controllers, Spring Boot app config |

## WaterRole Entity

```java
@Entity
@Table(name = "water_role")
@AccessControl(
    availableActions = {CrudActions.class, RoleActions.class},
    rolesPermissions = {
        @DefaultRoleAccess(roleName = "roleManager", actions = {CrudActions.class, RoleActions.class}),
        @DefaultRoleAccess(roleName = "roleViewer",  actions = {CrudActions.FIND, CrudActions.FIND_ALL}),
        @DefaultRoleAccess(roleName = "roleEditor",  actions = {CrudActions.UPDATE, CrudActions.FIND, CrudActions.FIND_ALL})
    }
)
public class WaterRole extends AbstractJpaEntity implements ProtectedEntity {
    @NotNull @Column(unique = true) @NoMalitiusCode
    private String name;

    @NoMalitiusCode
    private String description;
}
```

## WaterUserRole Entity (Join Table)

```java
@Entity
@Table(name = "water_user_role",
       uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "roleId"}))
public class WaterUserRole extends AbstractJpaEntity {
    @NotNull
    private long userId;

    @NotNull
    private long roleId;
}
```

## RoleActions (Custom Actions)
Extends `CrudActions` with role-specific operations:

```java
public class RoleActions {
    public static final String ASSIGN   = "ASSIGN";    // assign role to user
    public static final String UNASSIGN = "UNASSIGN";  // remove role from user

    // Bitmask values (after CRUD's 16):
    // ASSIGN   = 32
    // UNASSIGN = 64
}
```

## Key Interfaces

### RoleApi / RoleSystemApi
```java
WaterRole save(WaterRole role);
WaterRole update(WaterRole role);
WaterRole find(long id);
WaterRole findByName(String name);
PaginatedResult<WaterRole> findAll(int delta, int page, Query filter);
void remove(long id);

// Role assignment
void addRole(long userId, WaterRole role);     // @AllowPermissions(actions=ASSIGN)
void removeRole(long userId, WaterRole role);  // @AllowPermissions(actions=UNASSIGN)
boolean existsRole(String roleName);
WaterRole getRole(String roleName);
```

### RoleManager (cross-module integration)
Used by other modules (e.g., `User`, `Permission`) to resolve roles without depending on `RoleApi` directly:

```java
public interface RoleManager {
    WaterRole getRole(String roleName);
    boolean existsRole(String roleName);
    List<WaterRole> getUserRoles(long userId);
    void addRole(long userId, WaterRole role);
    void removeRole(long userId, WaterRole role);
    boolean userHasRole(long userId, String roleName);
}
```

`WaterRoleManager` is the concrete implementation, registered as `@FrameworkComponent`.

### RoleIntegrationLocalClient
In-process implementation of `RoleManager` for same-JVM deployments. Used when the Role module is deployed alongside the consumer module.

## Default Roles Created at Startup
The framework creates these default roles on first boot:
- `admin` — super-admin, bypasses all permission checks
- `roleManager` — full CRUD + assign/unassign on roles
- `roleViewer` — read-only access to roles
- `roleEditor` — update access to roles

Each business module (User, Company, etc.) creates its own domain-specific roles (e.g., `userManager`, `companyViewer`).

## REST Endpoints

| Method | Path | Permission |
|---|---|---|
| `POST` | `/water/roles` | roleManager |
| `PUT` | `/water/roles` | roleManager |
| `GET` | `/water/roles/{id}` | roleViewer |
| `GET` | `/water/roles` | roleViewer |
| `DELETE` | `/water/roles/{id}` | roleManager |
| `POST` | `/water/roles/{roleId}/users/{userId}` | ASSIGN |
| `DELETE` | `/water/roles/{roleId}/users/{userId}` | UNASSIGN |

## Dependencies
- `it.water.repository.jpa:JpaRepository-api` — `AbstractJpaEntity`
- `it.water.core:Core-permission` — `@AccessControl`, `CrudActions`
- `it.water.rest:Rest-persistence` — `BaseEntityRestApi`
- No dependency on `User` module — role-user mapping uses only `userId` (long)

## Integration Pattern
Other modules reference `RoleManager` (not `RoleApi`) to avoid tight coupling:

```java
@FrameworkComponent
public class MyService {
    @Inject @Setter private RoleManager roleManager;

    @OnActivate
    void init() {
        // Create module-specific roles if they don't exist
        if (!roleManager.existsRole("myEntityManager")) {
            // role creation handled by @DefaultRoleAccess on entity's @AccessControl
        }
    }
}
```

## Testing
- Unit tests: `WaterTestExtension` — test CRUD + assign/unassign + `WaterRoleManager` resolution
- REST tests: **Karate only** — never JUnit direct calls to `RoleRestController`
- Inject `RoleManager` and `UserManager` together to test full role-user assignment lifecycle

## Code Generation Rules
- Custom module roles are defined via `@DefaultRoleAccess` on the entity's `@AccessControl` annotation — never create roles programmatically in service code
- `WaterRoleManager` resolves roles by name — always use `roleManager.getRole(roleName)` over direct repository queries
- `RoleRestController` tested **exclusively via Karate**
- New custom role actions: extend bitmask from 32 upwards (powers of 2), document in `RoleActions` or the module's `<Entity>Actions` class
