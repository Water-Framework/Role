package it.water.role.repository;

import it.water.core.interceptors.annotations.FrameworkComponent;
import it.water.repository.jpa.WaterJpaRepositoryImpl;
import it.water.role.api.RoleRepository;
import it.water.role.model.WaterRole;

/**
 * @Generated by Water Generator
 * Repository Class for Role entity.
 */
@FrameworkComponent
public class WaterRoleRepositoryImpl extends WaterJpaRepositoryImpl<WaterRole> implements RoleRepository {
    private static final String ROLE_PERSISTENCE_UNIT = "role-persistence-unit";

    public WaterRoleRepositoryImpl() {
        super(WaterRole.class, ROLE_PERSISTENCE_UNIT);
    }
}
