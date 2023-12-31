package com.timeco.application.Service.userservice;

import com.timeco.application.Dto.RegistrationDto;
import com.timeco.application.Dto.UserDto;
import com.timeco.application.Repository.RoleRepository;
import com.timeco.application.Repository.UserRepository;
import com.timeco.application.model.role.Role;
import com.timeco.application.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        if(user.isBlocked()){
            throw new DisabledException("User is blocked");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }


    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public User save(RegistrationDto registrationDto) {
        //check if the user with provided already exist
        User existingUser=userRepository.findByEmail(registrationDto.getEmail());
        if(existingUser!=null){
            registrationDto.setErrorMessage("The email address is already taken. Please choose another one.");
            return null;
        }
        String code = generateRandomCode(6);
        User user = new User(registrationDto.getFirstName(),
                registrationDto.getLastName(), registrationDto.getEmail(),
                registrationDto.getPhoneNumber(), passwordEncoder.encode(registrationDto.getPassword()),
                Arrays.asList(new Role("ROLE_USER")));
        return user;

    }

    @Override
    public void lockUser(Long id) {
        User lockUser = userRepository.findById(id).get();
        lockUser.setBlocked(true);

        userRepository.save(lockUser);
    }

    @Override
    public void unlockUser(Long id) {
        User lockUser = userRepository.findById(id).get();
        lockUser.setBlocked(false);
        userRepository.save(lockUser);
    }

    @Override
    public User getUserById(Long userId) {
        // Use the userRepository to retrieve the user by their ID
        return userRepository.findById(userId).orElse(null); // You may want to handle null or throw an exception if not found
    }

    @Override
    public User findUserByUsername(String username) {
       return userRepository.findByEmail(username);
    }


    @Override
    public List<User> getUsersByPartialEmailOrName(String searchTerm) {
        return userRepository.findByEmailContainingOrFirstNameContaining(searchTerm, searchTerm);


    }

    @Override
    @Transactional
    public boolean updateUserDetails(UserDto updatedUser , Principal principal) {
        String username= principal.getName();
        User existingUser = userRepository.findByEmail(username);
        if (existingUser != null) {
            // Update the user's details based on the updatedUser object
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            userRepository.save(existingUser);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public int countCustomers() {
        return userRepository.countByIsBlocked(false);
    }

    @Override
    public String generateRandomCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code = new StringBuilder();
        SecureRandom  random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }

        return code.toString();
    }

    @Override
    public Page<User> findAllusers(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page,pageSize);

        return userRepository.findAll(pageable);
    }


    public boolean isPasswordCorrect(User user, String currentPassword) {
        // Retrieve the stored password hash for the user
        String storedPassword = user.getPassword(); // Assuming you have a getPassword method in your User class

        // Use the PasswordEncoder to check if the currentPassword matches the storedPassword
        return passwordEncoder.matches(currentPassword, storedPassword);
    }

    @Override
    public void updatePassword(User currentUser, String newPassword) {
        // Check if the new password is different from the current one
        if (!passwordEncoder.matches(newPassword, currentUser.getPassword())) {
            // Encode the new password before saving it
            String newPasswordEncoded = passwordEncoder.encode(newPassword);

            // Update the user's password in your database
            currentUser.setPassword(newPasswordEncoded);
            userRepository.save(currentUser);
        }
    }




}

