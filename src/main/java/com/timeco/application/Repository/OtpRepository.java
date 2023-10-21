package com.timeco.application.Repository;

import com.timeco.application.model.role.Role;
import com.timeco.application.model.user.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<Otp,Integer> {

    Otp  findByEmail(String email);

}
