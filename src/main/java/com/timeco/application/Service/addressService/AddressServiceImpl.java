package com.timeco.application.Service.addressService;

import com.timeco.application.Dto.AddressDto;
import com.timeco.application.Repository.AddressRepository;
import com.timeco.application.Repository.UserRepository;
import com.timeco.application.model.user.UserAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.mail.Address;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {


    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;


    @ModelAttribute("userDetails")
    public UserDetails userDetails() {
        // Get the currently logged-in user's information
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }


    @Override
    public UserAddress save( AddressDto addressDto,Long userId) {

        com.timeco.application.model.user.User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            // Handle the case where the user with the given userId is not found
            // You can throw an exception or handle it as appropriate for your application
            return null;
        }

        UserAddress userAddress = new UserAddress();
        userAddress.setUser(user); // Associate the User entity
        userAddress.setUserName(addressDto.getUserName());
        userAddress.setAddress(addressDto.getAddress());
        userAddress.setCity(addressDto.getCity());
        userAddress.setState(addressDto.getState());
        userAddress.setCountry(addressDto.getCountry());
        userAddress.setPinCode(addressDto.getPinCode());
        userAddress.setMobile(addressDto.getMobile());

        // Save the UserAddress object to the repository
       return addressRepository.save(userAddress);
    }

    @Override
    public List<UserAddress> getAllAddresses() {
        return addressRepository.findAll();
    }
}
