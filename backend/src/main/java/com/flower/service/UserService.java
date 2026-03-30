package com.flower.service;

import com.flower.entity.User;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    Iterable<User> getAllUsers();
    Optional<User> getUserById(Integer id);
    Optional<User> getUserByUsername(String username);
    User registerUser(User user);
    User updateUserFull(Integer id, User user);
    User updatePassword(Integer id, String newPassword);
    User upgradeToMember(Integer id, int months);
    void deleteUser(Integer id);
    boolean isUsernameExists(String username);

    Map<String, Object> updateProfile(Integer id, String nickname, String gender, String bio, String avatar);
    Map<String, Object> checkNicknameAvailability(String nickname);
    Map<String, Object> checkNicknameAvailabilityForUpdate(Integer id, String nickname);
    User getUserProfile(Integer id);
    boolean isNicknameExists(String nickname);
    boolean isNicknameTakenByOther(String nickname, Integer excludeUserId);

    Map<String, Object> updateEmail(Integer id, String email);
    Map<String, Object> updatePasswordWithOldPassword(Integer id, String oldPassword, String newPassword);
    Map<String, Object> checkEmailAvailability(String email);
    Map<String, Object> checkEmailAvailabilityForUpdate(Integer id, String email);
    boolean isEmailExists(String email);
    boolean isEmailTakenByOther(String email, Integer excludeUserId);
    boolean verifyPassword(Integer id, String password);
}