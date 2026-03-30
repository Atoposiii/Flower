package com.flower.service.impl;

import com.flower.entity.MemberRecord;
import com.flower.entity.User;
import com.flower.repository.MemberRecordRepository;
import com.flower.repository.UserRepository;
import com.flower.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@SuppressWarnings("all")
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;

    @Resource
    private MemberRecordRepository memberRecordRepository;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username);
    }

    @Override
    public User registerUser(User user) {
        if (user == null) {
            return null;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        return userRepository.save(user);
    }

    @Override
    public User updateUserFull(Integer id, User user) {
        if (id == null || user == null) {
            return null;
        }

        Optional<User> existingUserOptional = userRepository.findById(id);
        if (!existingUserOptional.isPresent()) {
            return null;
        }

        User existingUser = existingUserOptional.get();
        existingUser.setUsername(user.getUsername());
        existingUser.setNickname(user.getNickname());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    @Override
    public User updatePassword(Integer id, String newPassword) {
        if (id == null || newPassword == null || newPassword.isEmpty()) {
            return null;
        }

        Optional<User> existingUserOptional = userRepository.findById(id);
        if (!existingUserOptional.isPresent()) {
            return null;
        }

        User existingUser = existingUserOptional.get();
        existingUser.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(existingUser);
    }

    @Override
    public User upgradeToMember(Integer id, int months) {
        if (id == null) {
            return null;
        }

        Optional<User> existingUserOptional = userRepository.findById(id);
        if (!existingUserOptional.isPresent()) {
            return null;
        }

        User existingUser = existingUserOptional.get();
        existingUser.setRole("MEMBER");

        MemberRecord memberRecord = new MemberRecord();
        memberRecord.setUser(existingUser);
        memberRecord.setJoinDate(new Date());
        memberRecord.setMemberLevel("VIP");
        memberRecord.setStatus("active");

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.MONTH, months);
        Date expiryDate = calendar.getTime();
        memberRecord.setExpiryDate(expiryDate);

        memberRecordRepository.save(memberRecord);
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Integer id) {
        if (id != null) {
            userRepository.deleteById(id);
        }
    }

    @Override
    public boolean isUsernameExists(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public Map<String, Object> updateProfile(Integer id, String nickname, String gender, String bio, String avatar) {
        Map<String, Object> result = new HashMap<>();

        if (id == null) {
            result.put("success", false);
            result.put("message", "用户ID不能为空");
            return result;
        }

        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        if (nickname != null && !nickname.isEmpty()) {
            if (userRepository.existsByNicknameAndIdNot(nickname, id)) {
                result.put("success", false);
                result.put("message", "该网名已被使用，请更换");
                return result;
            }
        }

        User user = userOpt.get();
        if (nickname != null) user.setNickname(nickname);
        if (gender != null) user.setGender(gender);
        if (bio != null) user.setBio(bio);
        if (avatar != null) user.setAvatar(avatar);

        userRepository.save(user);

        result.put("success", true);
        result.put("message", "个人信息更新成功");
        result.put("user", user);

        return result;
    }

    @Override
    public Map<String, Object> checkNicknameAvailability(String nickname) {
        Map<String, Object> result = new HashMap<>();

        if (nickname == null || nickname.isEmpty()) {
            result.put("available", false);
            result.put("message", "网名不能为空");
            return result;
        }

        boolean exists = userRepository.existsByNickname(nickname);
        result.put("available", !exists);
        result.put("message", exists ? "网名已被占用" : "网名可用");
        result.put("nickname", nickname);

        return result;
    }

    @Override
    public Map<String, Object> checkNicknameAvailabilityForUpdate(Integer id, String nickname) {
        Map<String, Object> result = new HashMap<>();

        if (nickname == null || nickname.isEmpty()) {
            result.put("available", false);
            result.put("message", "网名不能为空");
            return result;
        }

        if (id == null) {
            result.put("available", false);
            result.put("message", "用户ID不能为空");
            return result;
        }

        boolean exists = userRepository.existsByNicknameAndIdNot(nickname, id);
        result.put("available", !exists);
        result.put("message", exists ? "网名已被占用" : "网名可用");
        result.put("nickname", nickname);

        return result;
    }

    @Override
    public User getUserProfile(Integer id) {
        if (id == null) {
            return null;
        }
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public boolean isNicknameExists(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return false;
        }
        return userRepository.existsByNickname(nickname);
    }

    @Override
    public boolean isNicknameTakenByOther(String nickname, Integer excludeUserId) {
        if (nickname == null || nickname.isEmpty()) {
            return false;
        }
        return userRepository.existsByNicknameAndIdNot(nickname, excludeUserId);
    }

    @Override
    public Map<String, Object> updateEmail(Integer id, String email) {
        Map<String, Object> result = new HashMap<>();

        if (id == null) {
            result.put("success", false);
            result.put("message", "用户ID不能为空");
            return result;
        }

        if (email == null || email.isEmpty()) {
            result.put("success", false);
            result.put("message", "邮箱不能为空");
            return result;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            result.put("success", false);
            result.put("message", "邮箱格式不正确");
            return result;
        }

        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        if (userRepository.existsByEmail(email)) {
            User currentUser = userOpt.get();
            if (!email.equals(currentUser.getEmail())) {
                result.put("success", false);
                result.put("message", "该邮箱已被使用");
                return result;
            }
        }

        User user = userOpt.get();
        user.setEmail(email);
        userRepository.save(user);

        result.put("success", true);
        result.put("message", "邮箱修改成功");
        result.put("email", email);

        return result;
    }

    @Override
    public Map<String, Object> updatePasswordWithOldPassword(Integer id, String oldPassword, String newPassword) {
        Map<String, Object> result = new HashMap<>();

        if (id == null) {
            result.put("success", false);
            result.put("message", "用户ID不能为空");
            return result;
        }

        if (oldPassword == null || oldPassword.isEmpty()) {
            result.put("success", false);
            result.put("message", "原密码不能为空");
            return result;
        }

        if (newPassword == null || newPassword.isEmpty()) {
            result.put("success", false);
            result.put("message", "新密码不能为空");
            return result;
        }

        if (newPassword.length() < 6) {
            result.put("success", false);
            result.put("message", "新密码长度不能少于6位");
            return result;
        }

        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            result.put("success", false);
            result.put("message", "原密码错误");
            return result;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        result.put("success", true);
        result.put("message", "密码修改成功");

        return result;
    }

    @Override
    public Map<String, Object> checkEmailAvailability(String email) {
        Map<String, Object> result = new HashMap<>();

        if (email == null || email.isEmpty()) {
            result.put("available", false);
            result.put("message", "邮箱不能为空");
            return result;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            result.put("available", false);
            result.put("message", "邮箱格式不正确");
            return result;
        }

        boolean exists = userRepository.existsByEmail(email);
        result.put("available", !exists);
        result.put("message", exists ? "该邮箱已被使用" : "邮箱可用");
        result.put("email", email);

        return result;
    }

    @Override
    public Map<String, Object> checkEmailAvailabilityForUpdate(Integer id, String email) {
        Map<String, Object> result = new HashMap<>();

        if (email == null || email.isEmpty()) {
            result.put("available", false);
            result.put("message", "邮箱不能为空");
            return result;
        }

        if (id == null) {
            result.put("available", false);
            result.put("message", "用户ID不能为空");
            return result;
        }

        boolean exists = userRepository.existsByEmail(email);
        Optional<User> currentUser = userRepository.findById(id);

        if (exists && currentUser.isPresent() && !email.equals(currentUser.get().getEmail())) {
            result.put("available", false);
            result.put("message", "该邮箱已被使用");
        } else {
            result.put("available", true);
            result.put("message", "邮箱可用");
        }

        result.put("email", email);
        return result;
    }

    @Override
    public boolean isEmailExists(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean isEmailTakenByOther(String email, Integer excludeUserId) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        Optional<User> currentUser = userRepository.findById(excludeUserId);
        if (currentUser.isPresent() && email.equals(currentUser.get().getEmail())) {
            return false;
        }
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean verifyPassword(Integer id, String password) {
        if (id == null || password == null || password.isEmpty()) {
            return false;
        }
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            return false;
        }
        return passwordEncoder.matches(password, userOpt.get().getPassword());
    }
}