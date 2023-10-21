package com.timeco.application.Repository;

import com.timeco.application.model.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface RoleRepository extends JpaRepository<Role,Long> {

    Role findByName(String name);

}
