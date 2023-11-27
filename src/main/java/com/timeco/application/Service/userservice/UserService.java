package com.timeco.application.Service.userservice;


import com.timeco.application.Dto.RegistrationDto;
import com.timeco.application.Dto.UserDto;
import com.timeco.application.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public interface UserService extends UserDetailsService {

    User save(RegistrationDto registrationDto);
    public void lockUser(Long id);
    public void unlockUser(Long id);
    public User getUserById(Long userId) ;
    public User findUserByUsername(String username);

    public boolean isPasswordCorrect(User user, String currentPassword);

    public void updatePassword(User currentUser, String newPassword);
    List<User> getUsersByPartialEmailOrName(String searchTerm);


    boolean updateUserDetails(UserDto updatedUser, Principal principal);

    int countCustomers();

    String generateRandomCode(int length) ;



    Page<User> findAllusers(int page, int pageSize);
}