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

package it.water.role.service;

import it.water.core.api.model.Role;
import it.water.core.api.repository.query.Query;
import it.water.core.api.role.RoleManager;
import it.water.core.interceptors.annotations.FrameworkComponent;
import it.water.core.interceptors.annotations.Inject;
import it.water.repository.entity.model.exceptions.NoResultException;
import it.water.role.api.RoleSystemApi;
import it.water.role.model.WaterRole;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@FrameworkComponent
public class WaterRoleManager implements RoleManager {
    private static Logger log = LoggerFactory.getLogger(WaterRoleManager.class);
    @Inject
    @Setter
    private RoleSystemApi roleSystemApi;

    @Override
    public Role createIfNotExists(String roleName) {
        Role found = findRole(roleName);
        if (found == null) {
            WaterRole waterRole = new WaterRole(roleName, "");
            waterRole = roleSystemApi.save(waterRole);
            found = waterRole;
        }
        return found;
    }

    @Override
    public boolean exists(String roleName) {
        return findRole(roleName) != null;
    }

    @Override
    public boolean hasRole(long userId, String roleName) {
        return roleSystemApi.findUserRoles(userId).stream().anyMatch(role -> role.getName().equals(roleName));
    }

    @Override
    public Set<Role> getUserRoles(long userId) {
        Set<Role> userRoles = Collections.unmodifiableSet(new HashSet<>(roleSystemApi.findUserRoles(userId)));
        return userRoles;
    }

    @Override
    public boolean addRole(long userId, Role role) {
        roleSystemApi.addUserRole(userId, role);
        return true;
    }

    @Override
    public Role getRole(String roleName) {
        Query q = this.roleSystemApi.getQueryBuilderInstance().field("name").equalTo(roleName);
        return this.roleSystemApi.find(q);
    }

    @Override
    public boolean removeRole(long userId, Role role) {
        this.roleSystemApi.removeUserRole(userId, role);
        return true;
    }

    private Role findRole(String roleName) {
        Query roleByName = roleSystemApi.getQueryBuilderInstance().field("name").equalTo(roleName);
        Role found = null;
        try {
            found = roleSystemApi.find(roleByName);
        } catch (NoResultException e) {
            //doNothing
            log.debug("Role {} not exists, creating it", roleName);
        }
        return found;
    }
}
