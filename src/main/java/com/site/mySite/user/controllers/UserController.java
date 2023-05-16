package com.site.mySite.user.controllers;

import com.site.mySite.user.models.Role;
import com.site.mySite.user.models.User;
import com.site.mySite.user.models.UserStatus;
import com.site.mySite.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Optional;


@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('permission:write')")
    public String usersPage(Model model) {
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("title", "Пользователи");
        model.addAttribute("users", users);
        return "user/users";
    }

    @GetMapping("/addNewUser")
    @PreAuthorize("hasAuthority('permission:write')")
    public String addNewUser(Model model) {
        return "user/addNewUser";
    }

    @PostMapping("/addNewUser")
    @PreAuthorize("hasAuthority('permission:write')")
    public String addNewUser(@RequestParam String login, @RequestParam String firstName,
                             @RequestParam String lastName, @RequestParam String password,
                             @RequestParam Role role, @RequestParam UserStatus status,
                             Model model)
    {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        String encodePassword = passwordEncoder.encode(password);
        User user = new User(login, firstName, lastName, encodePassword, role, status);
        userRepository.save(user);
        return "redirect:/users";
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAuthority('permission:write')")
    public String userDetails(@PathVariable(value = "id") long id, Model model) {
        if (!userRepository.existsById(id)) {
            return "redirect:/users";
        }
        Optional<User> user = userRepository.findById(id);
        ArrayList<User> res = new ArrayList<>();
        user.ifPresent(res::add);
        model.addAttribute("user", res);
        return "user/userDetails";
    }

    @GetMapping("/user/{id}/edit")
    @PreAuthorize("hasAuthority('permission:write')")
    public String userEdit(@PathVariable (value = "id") long id, Model model) {
        if (!userRepository.existsById(id)) {
            return "redirect:/users";
        }
        Optional<User> user = userRepository.findById(id);
        ArrayList<User> res = new ArrayList<>();
        user.ifPresent(res::add);
        model.addAttribute("user", res);
        return "user/userEdit";
    }

    @PostMapping("/user/{id}/edit")
    @PreAuthorize("hasAuthority('permission:write')")
    public String updateUser(@PathVariable (value = "id") long id, @RequestParam String login,
                             @RequestParam String firstName, @RequestParam String lastName,
                             @RequestParam Role role, @RequestParam UserStatus status, Model model)
    {
        User user = userRepository.findById(id).orElseThrow();
        user.setLogin(login);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setStatus(status);
        userRepository.save(user);
        return "redirect:/user/{id}";
    }

    @PostMapping("/user/{id}/remove")
    @PreAuthorize("hasAuthority('permission:write')")
    public String deleteUser(@PathVariable (value = "id") long id, Model model) {
        User user = userRepository.findById(id).orElseThrow();
        userRepository.delete(user);
        return "redirect:/users";
    }
}
