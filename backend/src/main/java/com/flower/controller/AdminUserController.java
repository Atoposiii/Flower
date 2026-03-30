package com.flower.controller;

import com.flower.entity.User;
import com.flower.entity.Volunteer;
import com.flower.entity.MemberRecord;
import com.flower.repository.UserRepository;
import com.flower.service.VolunteerService;
import com.flower.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VolunteerService volunteerService;

    @Autowired
    private MemberService memberService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        List<User> allUsers = userRepository.findAll();
        if (keyword != null && !keyword.isEmpty()) {
            allUsers = allUsers.stream()
                    .filter(u -> u.getUsername().contains(keyword) ||
                            (u.getNickname() != null && u.getNickname().contains(keyword)) ||
                            (u.getEmail() != null && u.getEmail().contains(keyword)))
                    .collect(Collectors.toList());
        }
        if (role != null && !role.isEmpty()) {
            allUsers = allUsers.stream()
                    .filter(u -> role.equals(u.getRole()))
                    .collect(Collectors.toList());
        }
        if (status != null && !status.isEmpty()) {
            allUsers = allUsers.stream()
                    .filter(u -> status.equals(u.getStatus()))
                    .collect(Collectors.toList());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + size, allUsers.size());
        List<User> pageContent = start < allUsers.size() ?
                allUsers.subList(start, end) : List.of();

        List<Map<String, Object>> enrichedContent = pageContent.stream()
                .map(u -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("id", u.getId());
                    info.put("username", u.getUsername());
                    info.put("nickname", u.getNickname());
                    info.put("email", u.getEmail());
                    info.put("phone", u.getPhone());
                    info.put("gender", u.getGender());
                    info.put("avatar", u.getAvatar());
                    info.put("role", u.getRole());
                    info.put("status", u.getStatus());
                    info.put("isMember", u.getIsMember());
                    info.put("createTime", u.getCreateTime());
                    boolean isVolunteer = volunteerService.getVolunteerByUserId(u.getId()).isPresent();
                    info.put("isVolunteer", isVolunteer);
                    return info;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("content", enrichedContent);
        result.put("totalElements", allUsers.size());
        result.put("totalPages", (int) Math.ceil((double) allUsers.size() / size));
        result.put("currentPage", page);
        result.put("pageSize", size);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Integer id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            Map<String, Object> userInfo = buildUserDetailInfo(userOpt.get());
            return new ResponseEntity<>(userInfo, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<Map<String, Object>> getUserDetail(@PathVariable Integer id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            Map<String, Object> userInfo = buildUserDetailInfo(userOpt.get());
            return new ResponseEntity<>(userInfo, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            @RequestParam(required = false) String reason) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "用户不存在");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        User user = userOpt.get();
        String beforeStatus = user.getStatus();
        user.setStatus(status);
        userRepository.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "用户状态更新成功");
        result.put("beforeStatus", beforeStatus);
        result.put("afterStatus", status);
        if (reason != null) {
            result.put("reason", reason);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/{id}/ban")
    public ResponseEntity<Map<String, Object>> banUser(
            @PathVariable Integer id,
            @RequestParam(required = false) String reason) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "用户不存在");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        User user = userOpt.get();
        if ("admin".equals(user.getRole())) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "不能封禁管理员账号");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        user.setStatus("banned");
        userRepository.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "账号封禁成功");
        result.put("reason", reason != null ? reason : "违规操作");

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/{id}/unban")
    public ResponseEntity<Map<String, Object>> unbanUser(@PathVariable Integer id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "用户不存在");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        User user = userOpt.get();
        user.setStatus("active");
        userRepository.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "账号解封成功");

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/{id}/force-quit-volunteer")
    public ResponseEntity<Map<String, Object>> forceQuitVolunteer(
            @PathVariable Integer id,
            @RequestParam(required = false) String reason) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "用户不存在");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        Optional<Volunteer> volunteerOpt = volunteerService.getVolunteerByUserId(id);
        if (volunteerOpt.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "该用户不是志愿者");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        Volunteer volunteer = volunteerOpt.get();
        volunteerService.quitVolunteer(volunteer.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "强制退出志愿者成功");
        result.put("reason", reason != null ? reason : "管理员操作");

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping({"/{id}/force-cancel-member", "/{id}/force-cancel-membership"})
    public ResponseEntity<Map<String, Object>> forceCancelMember(
            @PathVariable Integer id,
            @RequestParam(required = false) String reason) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "用户不存在");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        Optional<MemberRecord> memberOpt = memberService.getMemberRecordByUserId(id);
        if (memberOpt.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "该用户不是会员");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        memberService.cancelMembership(id, reason != null ? reason : "管理员强制取消");

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "强制退会成功");
        result.put("reason", reason != null ? reason : "管理员操作");

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/volunteers")
    public ResponseEntity<Map<String, Object>> getVolunteerUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        Iterable<Volunteer> volunteerIterable = volunteerService.getAllVolunteers();
        List<Volunteer> allVolunteers = StreamSupport.stream(volunteerIterable.spliterator(), false)
                .collect(Collectors.toList());
        if (status != null && !status.isEmpty()) {
            allVolunteers = allVolunteers.stream()
                    .filter(v -> status.equals(v.getStatus()))
                    .collect(Collectors.toList());
        }

        List<Map<String, Object>> volunteerUsers = allVolunteers.stream()
                .map(v -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("volunteerId", v.getId());
                    info.put("userId", v.getUser().getId());
                    info.put("username", v.getUser().getUsername());
                    info.put("nickname", v.getUser().getNickname());
                    info.put("status", v.getStatus());
                    info.put("serviceHours", v.getServiceHours());
                    info.put("applicationTime", v.getApplicationTime());
                    return info;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("content", volunteerUsers);
        result.put("totalElements", volunteerUsers.size());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/members")
    public ResponseEntity<Map<String, Object>> getMemberUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        List<MemberRecord> allMembers = memberService.getAllActiveMembers();
        if (status != null && !status.isEmpty()) {
            allMembers = allMembers.stream()
                    .filter(m -> status.equals(m.getStatus()))
                    .collect(Collectors.toList());
        }

        List<Map<String, Object>> memberUsers = allMembers.stream()
                .map(m -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("memberId", m.getId());
                    info.put("userId", m.getUser().getId());
                    info.put("username", m.getUser().getUsername());
                    info.put("nickname", m.getUser().getNickname());
                    info.put("memberLevel", m.getMemberLevel());
                    info.put("status", m.getStatus());
                    info.put("joinDate", m.getJoinDate());
                    info.put("expiryDate", m.getExpiryDate());
                    return info;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("content", memberUsers);
        result.put("totalElements", memberUsers.size());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        List<User> allUsers = userRepository.findAll();
        List<User> activeUsers = allUsers.stream()
                .filter(u -> "active".equals(u.getStatus()))
                .collect(Collectors.toList());
        List<User> bannedUsers = allUsers.stream()
                .filter(u -> "banned".equals(u.getStatus()))
                .collect(Collectors.toList());

        Iterable<Volunteer> volunteerIterable = volunteerService.getAllVolunteers();
        List<Volunteer> allVolunteers = StreamSupport.stream(volunteerIterable.spliterator(), false)
                .collect(Collectors.toList());
        List<MemberRecord> allMembers = memberService.getAllActiveMembers();

        List<Map<String, Object>> bothRoles = allUsers.stream()
                .filter(u -> {
                    boolean isVolunteer = allVolunteers.stream()
                            .anyMatch(v -> v.getUser().getId().equals(u.getId()) && "approved".equals(v.getStatus()));
                    boolean isMember = allMembers.stream()
                            .anyMatch(m -> m.getUser().getId().equals(u.getId()) && "active".equals(m.getStatus()));
                    return isVolunteer && isMember;
                })
                .map(u -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("userId", u.getId());
                    info.put("username", u.getUsername());
                    info.put("nickname", u.getNickname());
                    return info;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("totalUsers", allUsers.size());
        result.put("activeUsers", activeUsers.size());
        result.put("bannedUsers", bannedUsers.size());
        result.put("totalVolunteers", allVolunteers.size());
        result.put("activeVolunteers", allVolunteers.stream().filter(v -> "approved".equals(v.getStatus())).count());
        result.put("totalMembers", allMembers.size());
        result.put("activeMembers", allMembers.stream().filter(m -> "active".equals(m.getStatus())).count());
        result.put("bothRolesCount", bothRoles.size());
        result.put("bothRolesUsers", bothRoles);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Map<String, Object> buildUserDetailInfo(User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("email", user.getEmail());
        userInfo.put("phone", user.getPhone());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("gender", user.getGender());
        userInfo.put("bio", user.getBio());
        userInfo.put("role", user.getRole());
        userInfo.put("status", user.getStatus());
        userInfo.put("isMember", user.getIsMember());
        userInfo.put("memberExpireTime", user.getMemberExpireTime());
        userInfo.put("createTime", user.getCreateTime());

        Optional<Volunteer> volunteerOpt = volunteerService.getVolunteerByUserId(user.getId());
        userInfo.put("isVolunteer", volunteerOpt.isPresent());
        if (volunteerOpt.isPresent()) {
            Volunteer volunteer = volunteerOpt.get();
            Map<String, Object> volunteerInfo = new HashMap<>();
            volunteerInfo.put("id", volunteer.getId());
            volunteerInfo.put("status", volunteer.getStatus());
            volunteerInfo.put("serviceHours", volunteer.getServiceHours());
            volunteerInfo.put("applicationTime", volunteer.getApplicationTime());
            volunteerInfo.put("approvalTime", volunteer.getApprovalTime());
            userInfo.put("volunteerInfo", volunteerInfo);
        }

        Optional<MemberRecord> memberOpt = memberService.getMemberRecordByUserId(user.getId());
        userInfo.put("isMemberRecord", memberOpt.isPresent());
        if (memberOpt.isPresent()) {
            MemberRecord member = memberOpt.get();
            Map<String, Object> memberInfo = new HashMap<>();
            memberInfo.put("id", member.getId());
            memberInfo.put("memberLevel", member.getMemberLevel());
            memberInfo.put("status", member.getStatus());
            memberInfo.put("joinDate", member.getJoinDate());
            memberInfo.put("expiryDate", member.getExpiryDate());
            memberInfo.put("totalPaid", member.getTotalPaid());
            userInfo.put("memberInfo", memberInfo);
        }

        return userInfo;
    }
}