package com.timeco.application.web.admincontrollers;


import com.timeco.application.Repository.UserRepository;
import com.timeco.application.Service.userservice.UserService;
import com.timeco.application.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;






    @GetMapping("/block/{id}")
    public String blockUser(@PathVariable Long id) {

        userService.lockUser(id);

        return "redirect:/admin/listUsers";
    }

    @GetMapping("/unBlock/{id}")
    public String unblockUser(@PathVariable Long id) {

        userService.unlockUser(id);
        return "redirect:/admin/listUsers";
    }
    @GetMapping ("/listUsers")
    public String UserManagement(Model model){

        List<User> users=userRepository.findAll();
        model.addAttribute("customers",users);
        return "userList";
    }
    @GetMapping("/searchUser")
    public String searchUser(@RequestParam("searchUser")String search,Model model)
    {
        List<User> list=userService.getUsersByPartialEmailOrName(search);
        model.addAttribute("customers",list);
        return  "userList";
    }


}
