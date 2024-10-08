package it.water.role.api;

import it.water.core.api.model.Role;
import it.water.core.api.service.BaseEntitySystemApi;
import it.water.role.model.WaterRole;

import java.util.Collection;

/**
 * @Generated by Water Generator
 * This interface defines the internally exposed methods for the entity and allows interaction with it bypassing permission system.
 * The main goals of RoleSystemApi is to validate the entity and pass it to the persistence layer.
 */
public interface RoleSystemApi extends BaseEntitySystemApi<WaterRole> {
    Collection<Role> findUserRoles(long userId);

    void addUserRole(long userId, Role role);

    void removeUserRole(long userId, Role role);
}