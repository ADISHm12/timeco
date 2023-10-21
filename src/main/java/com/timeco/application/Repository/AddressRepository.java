package com.timeco.application.Repository;

import com.timeco.application.model.user.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<UserAddress, Long> {
    // Repository methods
}
