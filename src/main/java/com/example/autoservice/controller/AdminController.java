package com.example.autoservice.controller;

import com.example.autoservice.entity.Role;
import com.example.autoservice.entity.User;
import com.example.autoservice.repository.RoleRepository;
import com.example.autoservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Контроллер для управления пользователями и ролями администратором.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Отображает список всех пользователей.
     *
     * @param model Модель для передачи данных в представление.
     * @return Имя представления для отображения списка пользователей.
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users";
    }

    /**
     * Переключает роль пользователя между ADMIN и USER.
     *
     * @param id       Идентификатор пользователя.
     * @param roleName Название роли.
     * @return Перенаправление на страницу со списком пользователей.
     */
    @PostMapping("/users/{id}/toggleRole")
    @PreAuthorize("hasRole('ADMIN')")
    public String toggleRole(@PathVariable("id") Long id, @RequestParam("roleName") String roleName) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new IllegalArgumentException("Invalid role name: " + roleName));

        Set<Role> roles = new HashSet<>();
        if ("ADMIN".equals(roleName)) {
            roles.add(role);
        } else {
            roles.add(roleRepository.findByName("USER").orElseThrow(() -> new IllegalArgumentException("Role USER not found")));
        }
        user.setRoles(roles);
        userRepository.save(user);

        return "redirect:/admin/users";
    }
}
