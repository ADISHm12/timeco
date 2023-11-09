package com.timeco.application.Service.userservice;

import com.timeco.application.Dto.RegistrationDto;
import com.timeco.application.Repository.RoleRepository;
import com.timeco.application.Repository.UserRepository;
import com.timeco.application.model.role.Role;
import com.timeco.application.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
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
        System.out.println("Email received in loadUserByUsername: " + email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        if(user.isBlocked()){
            throw new DisabledException("User is blocked");
        }
        System.out.println("User found: " + user.getEmail());
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
    public void updateUserDetails(User updatedUser , Principal principal) {
        // Here, you can perform the logic to update the user's details in the database
        // First, you might want to fetch the existing user from the database
        String username= principal.getName();
        System.out.println(updatedUser.getFirstName()+"1111111111111111111111111111");
        System.out.println(updatedUser.getEmail()+"1111111111111111111111111111");

        User existingUser = userRepository.findByEmail(username);



        if (existingUser != null) {
            // Update the user's details based on the updatedUser object
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());

//             Save the updated user back to the database
            userRepository.save(existingUser);


        } else {
            // Handle the case where the user doesn't exist
            // You can throw an exception, return null, or handle it as needed

        }
    }

    @Override
    public int countCustomers() {
        return userRepository.countByIsBlocked(false);
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

