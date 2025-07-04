package it.water.role.model;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModelProperty;
import it.water.core.api.model.Role;
import it.water.core.api.permission.ProtectedEntity;
import it.water.core.api.service.rest.WaterJsonView;
import it.water.core.permission.action.CrudActions;
import it.water.core.permission.annotations.AccessControl;
import it.water.core.permission.annotations.DefaultRoleAccess;
import it.water.core.validation.annotations.NoMalitiusCode;
import it.water.core.validation.annotations.NotNullOnPersist;
import it.water.repository.jpa.model.AbstractJpaEntity;
import it.water.role.actions.RoleActions;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;


/**
 * @Generated by Water Generator
 * Role Entity Class.
 */
//JPA
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
@Access(AccessType.FIELD)
//Lombok
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode(of = {"name"},callSuper = true)
//Actions and default roles access
@AccessControl(availableActions = {CrudActions.SAVE, CrudActions.UPDATE, CrudActions.FIND, CrudActions.FIND_ALL, CrudActions.REMOVE, RoleActions.ASSIGN, RoleActions.UNASSIGN},
        rolesPermissions = {
                //Admin role can do everything
                @DefaultRoleAccess(roleName = WaterRole.DEFAULT_MANAGER_ROLE, actions = {CrudActions.SAVE, CrudActions.UPDATE, CrudActions.FIND, CrudActions.FIND_ALL, CrudActions.REMOVE, RoleActions.ASSIGN, RoleActions.UNASSIGN}),
                //Viwer has read only access
                @DefaultRoleAccess(roleName = WaterRole.DEFAULT_VIEWER_ROLE, actions = {CrudActions.FIND, CrudActions.FIND_ALL}),
                //Editor can do anything but remove
                @DefaultRoleAccess(roleName = WaterRole.DEFAULT_EDITOR_ROLE, actions = {CrudActions.SAVE, CrudActions.UPDATE, CrudActions.FIND, CrudActions.FIND_ALL, RoleActions.ASSIGN, RoleActions.UNASSIGN})
        })
public class WaterRole extends AbstractJpaEntity implements Role, ProtectedEntity {

    public static final String DEFAULT_MANAGER_ROLE = "roleManager";
    public static final String DEFAULT_VIEWER_ROLE = "roleViewer";
    public static final String DEFAULT_EDITOR_ROLE = "roleEditor";

    /**
     * String name for Role
     */
    @Column
    @NotNullOnPersist
    @NotEmpty
    @NoMalitiusCode
    @Size(max = 255)
    @ApiModelProperty(required = false)
    @NonNull
    @JsonView(WaterJsonView.Public.class)
    private String name;
    /**
     * String description for Role
     */
    @Column(length = 3000)
    @NotNullOnPersist
    @Size(max = 3000)
    @NoMalitiusCode
    @ApiModelProperty(required = false)
    @NonNull
    @JsonView(WaterJsonView.Extended.class)
    private String description;

}