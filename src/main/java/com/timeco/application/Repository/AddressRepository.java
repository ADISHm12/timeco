package com.timeco.application.Repository;

import com.timeco.application.model.user.User;
import com.timeco.application.model.user.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<UserAddress, Long> {
    List <UserAddress> findByUserId(Long userId);

    List<UserAddress> getAddressByUser(User user);
    // Repository methods
}
