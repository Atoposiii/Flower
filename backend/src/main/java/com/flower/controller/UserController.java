package com.flower.controller;

import com.flower.dto.LoginResponse;
import com.flower.entity.User;
import com.flower.service.UserService;
import com.flower.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<Iterable<User>> getAllUsers() {
        Iterable<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        if (userService.isUsernameExists(user.getUsername())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        User registeredUser = userService.registerUser(user);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginUser) {
        try {
            System.out.println("[DEBUG] login attempt: username=" + loginUser.getUsername()
                    + ", password=" + loginUser.getPassword());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Optional<User> user = userService.getUserByUsername(loginUser.getUsername());
            if (user.isPresent()) {
                User u = user.get();
                if ("banned".equals(u.getStatus())) {
                    return new ResponseEntity<>(Map.of("message", "账号已封禁，请联系管理员"), HttpStatus.FORBIDDEN);
                }
                String token = jwtUtil.generateToken(u.getId(), u.getUsername(), u.getRole());
                return new ResponseEntity<>(new LoginResponse(token, u), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println("[DEBUG] login failed: " + e.getClass().getName() + " - " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (userService.isUsernameExists(user.getUsername())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        User createdUser = userService.registerUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        User updatedUser = userService.updateUserFull(id, user);
        return updatedUser != null ? new ResponseEntity<>(updatedUser, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<User> updatePassword(@PathVariable Integer id, @RequestBody String newPassword) {
        User updatedUser = userService.updatePassword(id, newPassword);
        return updatedUser != null ? new ResponseEntity<>(updatedUser, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}/member")
    public ResponseEntity<User> upgradeToMember(@PathVariable Integer id, @RequestParam int months) {
        User updatedUser = userService.upgradeToMember(id, months);
        return updatedUser != null ? new ResponseEntity<>(updatedUser, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<User> getUserProfile(@PathVariable Integer id) {
        User user = userService.getUserProfile(id);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @PathVariable Integer id,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) String avatar) {
        Map<String, Object> result = userService.updateProfile(id, nickname, gender, bio, avatar);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        String message = (String) result.get("message");
        if (message != null && message.contains("该网名已被使用")) {
            return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/nickname/check")
    public ResponseEntity<Map<String, Object>> checkNicknameAvailability(@RequestParam String nickname) {
        Map<String, Object> result = userService.checkNicknameAvailability(nickname);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/nickname/check/{id}")
    public ResponseEntity<Map<String, Object>> checkNicknameAvailabilityForUpdate(
            @PathVariable Integer id,
            @RequestParam String nickname) {
        Map<String, Object> result = userService.checkNicknameAvailabilityForUpdate(id, nickname);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/account/{id}/email")
    public ResponseEntity<Map<String, Object>> updateEmail(
            @PathVariable Integer id,
            @RequestParam String email) {
        Map<String, Object> result = userService.updateEmail(id, email);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/account/{id}/password")
    public ResponseEntity<Map<String, Object>> updatePasswordWithOldPassword(
            @PathVariable Integer id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        Map<String, Object> result = userService.updatePasswordWithOldPassword(id, oldPassword, newPassword);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/account/{id}/verify-password")
    public ResponseEntity<Map<String, Object>> verifyPassword(
            @PathVariable Integer id,
            @RequestParam String password) {
        Map<String, Object> result = new java.util.HashMap<>();
        boolean isValid = userService.verifyPassword(id, password);
        result.put("valid", isValid);
        result.put("message", isValid ? "密码验证成功" : "密码错误");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/email/check")
    public ResponseEntity<Map<String, Object>> checkEmailAvailability(@RequestParam String email) {
        Map<String, Object> result = userService.checkEmailAvailability(email);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/email/check/{id}")
    public ResponseEntity<Map<String, Object>> checkEmailAvailabilityForUpdate(
            @PathVariable Integer id,
            @RequestParam String email) {
        Map<String, Object> result = userService.checkEmailAvailabilityForUpdate(id, email);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        SecurityContextHolder.clearContext();
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("message", "退出登录成功");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
