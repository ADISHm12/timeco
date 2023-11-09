package com.timeco.application.Service.addressService;

import com.timeco.application.Dto.AddressDto;
import com.timeco.application.model.user.User;
import com.timeco.application.model.user.UserAddress;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import java.util.List;

@Service
public interface AddressService {
   UserAddress save(AddressDto addressDto,Long userId);
//   public List<UserAddress> getAllAddresses();

   public void updateUserAddress(Long addressId,UserAddress updatedAddress);

    List<UserAddress> findByUserId(Long id);

//    List<UserAddress> getAddressByUser(User user);
}
