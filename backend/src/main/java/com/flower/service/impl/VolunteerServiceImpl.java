package com.flower.service.impl;

import com.flower.entity.*;
import com.flower.repository.*;
import com.flower.entity.VolunteerActivityRecord;
import com.flower.service.VolunteerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings("all")
public class VolunteerServiceImpl implements VolunteerService {

    @Resource
    private VolunteerRepository volunteerRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private MemberRecordRepository memberRecordRepository;

    @Resource
    private VolunteerActivityRepository activityRepository;

    @Resource
    private ActivityRegistrationRepository registrationRepository;

    @Resource
    private SignInRecordRepository signInRecordRepository;

    @Resource
    private VolunteerActivityRecordRepository volunteerActivityRecordRepository;

    @Override
    public Iterable<Volunteer> getAllVolunteers() {
        return volunteerRepository.findAll();
    }

    @Override
    public Optional<Volunteer> getVolunteerById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return volunteerRepository.findById(id);
    }

    @Override
    public Optional<Volunteer> getVolunteerByUserId(Integer userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return volunteerRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Volunteer applyVolunteer(Integer userId, Volunteer volunteer) {
        if (userId == null || volunteer == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        Optional<Volunteer> existingVolunteer = volunteerRepository.findByUserId(userId);
        if (existingVolunteer.isPresent()) {
            throw new IllegalArgumentException("用户已经是志愿者");
        }

        Volunteer newVolunteer = new Volunteer();
        newVolunteer.setUser(userOptional.get());
        newVolunteer.setRealName(volunteer.getRealName());
        newVolunteer.setIdCard(volunteer.getIdCard());
        newVolunteer.setAddress(volunteer.getAddress());
        newVolunteer.setSkills(volunteer.getSkills());
        newVolunteer.setStatus("pending");
        newVolunteer.setServiceHours(0);
        newVolunteer.setTotalServiceHours(0);
        newVolunteer.setApplicationTime(new Date());

        return volunteerRepository.save(newVolunteer);
    }

    @Override
    @Transactional
    public Volunteer approveVolunteer(Integer id, boolean approve) {
        if (id == null) {
            return null;
        }

        Optional<Volunteer> volunteerOptional = volunteerRepository.findById(id);
        if (!volunteerOptional.isPresent()) {
            return null;
        }

        Volunteer volunteer = volunteerOptional.get();
        volunteer.setStatus(approve ? "approved" : "rejected");
        if (approve) {
            volunteer.setApprovalTime(new Date());
        }

        return volunteerRepository.save(volunteer);
    }

    @Override
    @Transactional
    public Volunteer updateServiceHours(Integer id, int hours) {
        if (id == null) {
            return null;
        }

        Optional<Volunteer> volunteerOptional = volunteerRepository.findById(id);
        if (!volunteerOptional.isPresent()) {
            return null;
        }

        Volunteer volunteer = volunteerOptional.get();
        volunteer.setServiceHours(volunteer.getServiceHours() + hours);
        volunteer.setTotalServiceHours(volunteer.getTotalServiceHours() + hours);
        volunteer.setLastServiceTime(new Date());

        return volunteerRepository.save(volunteer);
    }

    @Override
    public List<Volunteer> getPendingVolunteers() {
        return volunteerRepository.findByStatus("pending");
    }

    @Override
    public List<Volunteer> getApprovedVolunteers() {
        return volunteerRepository.findByStatus("approved");
    }

    @Override
    public List<Volunteer> getVolunteersByStatus(String status) {
        return volunteerRepository.findByStatus(status);
    }

    @Override
    public List<Volunteer> getVolunteersByServiceHours() {
        return volunteerRepository.findByOrderByServiceHoursDesc();
    }

    @Override
    @Transactional
    public Volunteer quitVolunteer(Integer id) {
        if (id == null) {
            return null;
        }

        Optional<Volunteer> volunteerOptional = volunteerRepository.findById(id);
        if (!volunteerOptional.isPresent()) {
            return null;
        }

        Volunteer volunteer = volunteerOptional.get();
        volunteer.setStatus("quit");
        volunteer.setQuitTime(new Date());

        return volunteerRepository.save(volunteer);
    }

    @Override
    public void deleteVolunteer(Integer id) {
        if (id != null) {
            volunteerRepository.deleteById(id);
        }
    }

    @Override
    public boolean isUserVolunteer(Integer userId) {
        if (userId == null) return false;
        Optional<Volunteer> volunteer = volunteerRepository.findByUserId(userId);
        return volunteer.isPresent() && "approved".equals(volunteer.get().getStatus());
    }

    @Override
    public boolean isUserMember(Integer userId) {
        if (userId == null) return false;
        Optional<MemberRecord> member = memberRecordRepository.findByUserId(userId);
        return member.isPresent() && "active".equals(member.get().getStatus());
    }

    @Override
    @Transactional
    public Volunteer becomeVolunteer(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (isUserVolunteer(userId)) {
            throw new IllegalArgumentException("用户已经是志愿者");
        }

        Optional<Volunteer> existingQuit = volunteerRepository.findByUserId(userId);
        if (existingQuit.isPresent() && "quit".equals(existingQuit.get().getStatus())) {
            Volunteer volunteer = existingQuit.get();
            volunteer.setStatus("approved");
            volunteer.setQuitTime(null);
            volunteer.setServiceHours(0);
            volunteer.setTotalServiceHours(0);
            volunteer.setApplicationTime(new Date());
            volunteer.setApprovalTime(new Date());
            return volunteerRepository.save(volunteer);
        }

        Volunteer newVolunteer = new Volunteer();
        newVolunteer.setUser(userOptional.get());
        newVolunteer.setStatus("approved");
        newVolunteer.setServiceHours(0);
        newVolunteer.setTotalServiceHours(0);
        newVolunteer.setApplicationTime(new Date());
        newVolunteer.setApprovalTime(new Date());

        return volunteerRepository.save(newVolunteer);
    }

    @Override
    @Transactional
    public Map<String, Object> quitVolunteerWithWarning(Integer userId) {
        Map<String, Object> result = new HashMap<>();

        if (userId == null) {
            result.put("success", false);
            result.put("message", "用户ID不能为空");
            return result;
        }

        Optional<Volunteer> volunteerOpt = volunteerRepository.findByUserId(userId);
        if (!volunteerOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "用户不是志愿者");
            return result;
        }

        Volunteer volunteer = volunteerOpt.get();
        int previousHours = volunteer.getServiceHours();

        volunteer.setStatus("quit");
        volunteer.setQuitTime(new Date());
        volunteer.setServiceHours(0);
        volunteer.setTotalServiceHours(0);
        volunteer.setRank(null);
        volunteerRepository.save(volunteer);

        result.put("success", true);
        result.put("message", "退出成功！退出后志愿时长将归零，从排行榜中移除。");
        result.put("previousHours", previousHours);
        result.put("warning", "退出志愿者的后果：\n1. 您的所有志愿时长将归零\n2. 您将从志愿排行榜中被移除\n3. 之前参加的未完成活动的记录将被清除\n4. 如需重新加入，需再次申请");

        return result;
    }

    @Override
    public List<Volunteer> getVolunteerRanking() {
        List<Volunteer> volunteers = volunteerRepository.findApprovedVolunteersOrderByServiceHoursDesc();
        int rank = 1;
        for (Volunteer volunteer : volunteers) {
            volunteer.setRank(rank++);
            if (rank > 10) break;
        }
        return volunteers.subList(0, Math.min(volunteers.size(), 10));
    }

    @Override
    public List<Volunteer> getActiveVolunteersWithHours() {
        return volunteerRepository.findActiveVolunteersWithHours();
    }

    @Override
    @Transactional
    public Map<String, Object> approveVolunteerWithCheck(Integer id) {
        Map<String, Object> result = new HashMap<>();
        Optional<Volunteer> opt = volunteerRepository.findById(id);
        if (!opt.isPresent()) {
            result.put("success", false);
            result.put("message", "志愿者记录不存在");
            return result;
        }
        Volunteer volunteer = opt.get();
        if (!"pending".equals(volunteer.getStatus())) {
            result.put("success", false);
            result.put("message", "只有待审核状态的申请才能审核通过");
            return result;
        }
        volunteer.setStatus("approved");
        volunteer.setApprovalTime(new Date());
        volunteerRepository.save(volunteer);
        result.put("success", true);
        result.put("message", "志愿者审核通过");
        result.put("data", volunteer);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> rejectVolunteerWithCheck(Integer id) {
        Map<String, Object> result = new HashMap<>();
        Optional<Volunteer> opt = volunteerRepository.findById(id);
        if (!opt.isPresent()) {
            result.put("success", false);
            result.put("message", "志愿者记录不存在");
            return result;
        }
        Volunteer volunteer = opt.get();
        if (!"pending".equals(volunteer.getStatus())) {
            result.put("success", false);
            result.put("message", "只有待审核状态的申请才能拒绝");
            return result;
        }
        volunteer.setStatus("rejected");
        volunteerRepository.save(volunteer);
        result.put("success", true);
        result.put("message", "志愿者申请已拒绝");
        result.put("data", volunteer);
        return result;
    }

    @Override
    @Transactional
    public VolunteerActivityRecord addActivityRecord(Integer volunteerId, VolunteerActivityRecord record) {
        record.setVolunteerId(volunteerId);
        VolunteerActivityRecord saved = volunteerActivityRecordRepository.save(record);

        // 累加服务时长到志愿者
        if (saved.getServiceHours() != null && saved.getServiceHours() > 0) {
            volunteerRepository.findById(volunteerId).ifPresent(volunteer -> {
                int current = volunteer.getServiceHours() == null ? 0 : volunteer.getServiceHours();
                int total = volunteer.getTotalServiceHours() == null ? 0 : volunteer.getTotalServiceHours();
                volunteer.setServiceHours(current + saved.getServiceHours());
                volunteer.setTotalServiceHours(total + saved.getServiceHours());
                volunteer.setLastServiceTime(new Date());
                volunteerRepository.save(volunteer);
            });
        }

        return saved;
    }

    @Override
    public List<VolunteerActivityRecord> getActivityRecordsByVolunteerId(Integer volunteerId) {
        return volunteerActivityRecordRepository.findByVolunteerIdOrderByParticipationTimeDesc(volunteerId);
    }

    @Override
    public List<VolunteerActivity> getAllActivities() {
        return activityRepository.findAll();
    }

    @Override
    public Optional<VolunteerActivity> getActivityById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return activityRepository.findById(id);
    }

    @Override
    @Transactional
    public VolunteerActivity createActivity(VolunteerActivity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("活动信息不能为空");
        }
        activity.setCurrentParticipants(0);
        if (activity.getIsMemberOnly() == null) {
            activity.setIsMemberOnly(false);
        }
        if (activity.getStatus() == null || activity.getStatus().isEmpty()) {
            activity.setStatus("active");
        }
        return activityRepository.save(activity);
    }

    @Override
    @Transactional
    public VolunteerActivity updateActivity(Integer id, VolunteerActivity activity) {
        if (id == null || activity == null) {
            return null;
        }

        Optional<VolunteerActivity> activityOpt = activityRepository.findById(id);
        if (!activityOpt.isPresent()) {
            return null;
        }

        VolunteerActivity existingActivity = activityOpt.get();
        existingActivity.setTitle(activity.getTitle());
        existingActivity.setDescription(activity.getDescription());
        existingActivity.setActivityTime(activity.getActivityTime());
        existingActivity.setEndTime(activity.getEndTime());
        existingActivity.setLocation(activity.getLocation());
        existingActivity.setMaxParticipants(activity.getMaxParticipants());
        existingActivity.setStatus(activity.getStatus());
        existingActivity.setActivityType(activity.getActivityType());
        existingActivity.setDifficultyLevel(activity.getDifficultyLevel());
        existingActivity.setRequiredHours(activity.getRequiredHours());
        existingActivity.setRewardPoints(activity.getRewardPoints());
        existingActivity.setOrganizerName(activity.getOrganizerName());
        existingActivity.setContactPhone(activity.getContactPhone());
        existingActivity.setContactEmail(activity.getContactEmail());
        existingActivity.setIsMemberOnly(activity.getIsMemberOnly());
        existingActivity.setSignInStartTime(activity.getSignInStartTime());
        existingActivity.setSignInEndTime(activity.getSignInEndTime());
        existingActivity.setIsAllowLateSignIn(activity.getIsAllowLateSignIn());

        return activityRepository.save(existingActivity);
    }

    @Override
    @Transactional
    public void deleteActivity(Integer id) {
        if (id != null) {
            activityRepository.deleteById(id);
        }
    }

    @Override
    public List<VolunteerActivity> getAvailableActivities() {
        return activityRepository.findByStatusIn(Arrays.asList("active", "published"));
    }

    @Override
    public List<VolunteerActivity> getMemberOnlyActivities() {
        List<VolunteerActivity> allActivities = activityRepository.findAll();
        List<VolunteerActivity> memberOnly = new ArrayList<>();
        for (VolunteerActivity activity : allActivities) {
            if (Boolean.TRUE.equals(activity.getIsMemberOnly())) {
                memberOnly.add(activity);
            }
        }
        return memberOnly;
    }

    @Override
    public List<VolunteerActivity> getActivitiesByStatus(String status) {
        return activityRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public Map<String, Object> registerForActivity(Integer userId, Integer activityId) {
        Map<String, Object> result = new HashMap<>();

        if (userId == null || activityId == null) {
            result.put("success", false);
            result.put("message", "参数不能为空");
            return result;
        }

        if (!isUserVolunteer(userId)) {
            result.put("success", false);
            result.put("message", "您需要先成为志愿者才能报名参加活动");
            return result;
        }

        Optional<VolunteerActivity> activityOpt = activityRepository.findById(activityId);
        if (!activityOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "活动不存在");
            return result;
        }

        VolunteerActivity activity = activityOpt.get();

        String status = activity.getStatus();
        if (!"published".equals(status) && !"active".equals(status)) {
            result.put("success", false);
            result.put("message", "活动未开放报名");
            return result;
        }

        if (activity.getCurrentParticipants() != null &&
            activity.getMaxParticipants() != null &&
            activity.getCurrentParticipants() >= activity.getMaxParticipants()) {

            if (Boolean.TRUE.equals(activity.getIsMemberOnly())) {
                result.put("success", false);
                result.put("message", "活动已满员，此活动仅限会员参加");
                return result;
            }

            if (!isUserMember(userId)) {
                result.put("success", false);
                result.put("message", "活动已满员，会员可优先参加");
                return result;
            }
        }

        if (registrationRepository.existsByActivityIdAndVolunteerId(activityId,
                volunteerRepository.findByUserId(userId).get().getId())) {
            result.put("success", false);
            result.put("message", "您已报名此活动");
            return result;
        }

        Optional<Volunteer> volunteerOpt = volunteerRepository.findByUserId(userId);
        if (!volunteerOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "志愿者信息不存在");
            return result;
        }

        ActivityRegistration registration = new ActivityRegistration();
        registration.setActivity(activity);
        registration.setVolunteer(volunteerOpt.get());
        registration.setStatus("registered");
        registration.setSignInStatus("unsigned");
        registration.setIsMemberJoined(isUserMember(userId));
        registrationRepository.save(registration);

        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        activityRepository.save(activity);

        result.put("success", true);
        result.put("message", "报名成功");
        result.put("activity", activity);
        result.put("isMemberJoined", isUserMember(userId));

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> cancelRegistration(Integer userId, Integer activityId) {
        Map<String, Object> result = new HashMap<>();

        if (userId == null || activityId == null) {
            result.put("success", false);
            result.put("message", "参数不能为空");
            return result;
        }

        Optional<Volunteer> volunteerOpt = volunteerRepository.findByUserId(userId);
        if (!volunteerOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "志愿者信息不存在");
            return result;
        }

        Optional<ActivityRegistration> regOpt = registrationRepository.findByActivityIdAndVolunteerId(
                activityId, volunteerOpt.get().getId());
        if (!regOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "您未报名此活动");
            return result;
        }

        ActivityRegistration registration = regOpt.get();

        if ("signed".equals(registration.getSignInStatus()) || "completed".equals(registration.getStatus())) {
            result.put("success", false);
            result.put("message", "已签到的活动无法取消报名");
            return result;
        }

        registrationRepository.delete(registration);

        Optional<VolunteerActivity> activityOpt = activityRepository.findById(activityId);
        if (activityOpt.isPresent()) {
            VolunteerActivity activity = activityOpt.get();
            activity.setCurrentParticipants(Math.max(0, activity.getCurrentParticipants() - 1));
            activityRepository.save(activity);
        }

        result.put("success", true);
        result.put("message", "取消报名成功");

        return result;
    }

    @Override
    public List<ActivityRegistration> getUserRegistrations(Integer userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        Optional<Volunteer> volunteerOpt = volunteerRepository.findByUserId(userId);
        if (!volunteerOpt.isPresent()) {
            return new ArrayList<>();
        }
        return registrationRepository.findByVolunteerIdOrderByRegistrationTimeDesc(volunteerOpt.get().getId());
    }

    @Override
    public List<ActivityRegistration> getActivityRegistrations(Integer activityId) {
        if (activityId == null) {
            return new ArrayList<>();
        }
        return registrationRepository.findByActivityId(activityId);
    }

    @Override
    public boolean hasUserRegistered(Integer userId, Integer activityId) {
        if (userId == null || activityId == null) {
            return false;
        }
        Optional<Volunteer> volunteerOpt = volunteerRepository.findByUserId(userId);
        if (!volunteerOpt.isPresent()) {
            return false;
        }
        return registrationRepository.existsByActivityIdAndVolunteerId(activityId, volunteerOpt.get().getId());
    }

    @Override
    @Transactional
    public Map<String, Object> signIn(Integer userId, Integer activityId) {
        Map<String, Object> result = new HashMap<>();

        if (userId == null || activityId == null) {
            result.put("success", false);
            result.put("message", "参数不能为空");
            return result;
        }

        Optional<Volunteer> volunteerOpt = volunteerRepository.findByUserId(userId);
        if (!volunteerOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "志愿者信息不存在");
            return result;
        }

        Optional<ActivityRegistration> regOpt = registrationRepository.findByActivityIdAndVolunteerId(
                activityId, volunteerOpt.get().getId());
        if (!regOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "您未报名此活动");
            return result;
        }

        ActivityRegistration registration = regOpt.get();

        if ("signed".equals(registration.getSignInStatus())) {
            result.put("success", false);
            result.put("message", "您已签到");
            return result;
        }

        Optional<VolunteerActivity> activityOpt = activityRepository.findById(activityId);
        if (!activityOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "活动不存在");
            return result;
        }

        VolunteerActivity activity = activityOpt.get();
        Date now = new Date();

        if (activity.getSignInStartTime() != null && now.before(activity.getSignInStartTime())) {
            result.put("success", false);
            result.put("message", "签到时间未开始");
            return result;
        }

        if (activity.getSignInEndTime() != null && now.after(activity.getSignInEndTime())) {
            if (!Boolean.TRUE.equals(activity.getIsAllowLateSignIn())) {
                result.put("success", false);
                result.put("message", "签到时间已结束");
                return result;
            }
        }

        registration.setSignInStatus("signed");
        registration.setSignInTime(now);
        registrationRepository.save(registration);

        SignInRecord signInRecord = new SignInRecord();
        signInRecord.setRegistration(registration);
        signInRecord.setSignInTime(now);
        signInRecord.setStatus("signed_in");
        signInRecordRepository.save(signInRecord);

        long hours = 0;
        if (activity.getActivityTime() != null && activity.getEndTime() != null) {
            long diffMillis = activity.getEndTime().getTime() - activity.getActivityTime().getTime();
            hours = TimeUnit.HOURS.convert(diffMillis, TimeUnit.MILLISECONDS);
        }
        if (hours <= 0 && activity.getRequiredHours() != null) {
            hours = activity.getRequiredHours();
        }

        registration.setActualHours((int) hours);
        registrationRepository.save(registration);

        result.put("success", true);
        result.put("message", "签到成功");
        result.put("signInTime", now);
        result.put("expectedHours", hours);

        return result;
    }

    @Override
    public Map<String, Object> getSignInStatus(Integer userId, Integer activityId) {
        Map<String, Object> result = new HashMap<>();

        if (userId == null || activityId == null) {
            result.put("signed", false);
            result.put("message", "参数不完整");
            return result;
        }

        Optional<Volunteer> volunteerOpt = volunteerRepository.findByUserId(userId);
        if (!volunteerOpt.isPresent()) {
            result.put("signed", false);
            result.put("message", "志愿者信息不存在");
            return result;
        }

        Optional<ActivityRegistration> regOpt = registrationRepository.findByActivityIdAndVolunteerId(
                activityId, volunteerOpt.get().getId());
        if (!regOpt.isPresent()) {
            result.put("signed", false);
            result.put("registered", false);
            result.put("message", "未报名");
            return result;
        }

        ActivityRegistration registration = regOpt.get();
        result.put("registered", true);
        result.put("signed", "signed".equals(registration.getSignInStatus()));
        result.put("signInStatus", registration.getSignInStatus());
        result.put("signInTime", registration.getSignInTime());
        result.put("actualHours", registration.getActualHours());
        result.put("status", registration.getStatus());

        return result;
    }

    @Override
    public List<ActivityRegistration> getUnsignInUsers(Integer activityId) {
        if (activityId == null) {
            return new ArrayList<>();
        }
        return registrationRepository.findByActivityIdAndSignInStatus(activityId, "unsigned");
    }

    @Override
    @Transactional
    public Map<String, Object> processSignInResult(Integer registrationId) {
        Map<String, Object> result = new HashMap<>();

        if (registrationId == null) {
            result.put("success", false);
            result.put("message", "参数不能为空");
            return result;
        }

        Optional<ActivityRegistration> regOpt = registrationRepository.findById(registrationId);
        if (!regOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "报名记录不存在");
            return result;
        }

        ActivityRegistration registration = regOpt.get();

        if (!"signed".equals(registration.getSignInStatus())) {
            result.put("success", false);
            result.put("message", "用户未签到，无法发放志愿时长");
            return result;
        }

        if (registration.getActualHours() == null || registration.getActualHours() <= 0) {
            result.put("success", false);
            result.put("message", "志愿时长未设定");
            return result;
        }

        Volunteer volunteer = registration.getVolunteer();
        volunteer.setServiceHours(volunteer.getServiceHours() + registration.getActualHours());
        volunteer.setTotalServiceHours(volunteer.getTotalServiceHours() + registration.getActualHours());
        volunteer.setLastServiceTime(new Date());
        volunteerRepository.save(volunteer);

        registration.setStatus("completed");
        registration.setConfirmTime(new Date());
        registrationRepository.save(registration);

        SignInRecord signInRecord = signInRecordRepository.findByRegistrationIdAndStatus(registrationId, "signed_in")
                .orElse(null);
        if (signInRecord != null) {
            signInRecord.setStatus("completed");
            signInRecord.setHoursEarned(registration.getActualHours());
            signInRecordRepository.save(signInRecord);
        }

        result.put("success", true);
        result.put("message", "志愿时长已发放");
        result.put("hours", registration.getActualHours());
        result.put("totalHours", volunteer.getServiceHours());

        return result;
    }

    @Override
    public Map<String, Object> calculateActivityHours(Integer activityId) {
        Map<String, Object> result = new HashMap<>();

        if (activityId == null) {
            result.put("success", false);
            result.put("message", "活动ID不能为空");
            return result;
        }

        Optional<VolunteerActivity> activityOpt = activityRepository.findById(activityId);
        if (!activityOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "活动不存在");
            return result;
        }

        VolunteerActivity activity = activityOpt.get();
        long hours = 0;

        if (activity.getActivityTime() != null && activity.getEndTime() != null) {
            long diffMillis = activity.getEndTime().getTime() - activity.getActivityTime().getTime();
            hours = TimeUnit.HOURS.convert(diffMillis, TimeUnit.MILLISECONDS);
        }

        if (hours <= 0 && activity.getRequiredHours() != null) {
            hours = activity.getRequiredHours();
        }

        result.put("success", true);
        result.put("activityId", activityId);
        result.put("activityTitle", activity.getTitle());
        result.put("calculatedHours", hours);
        result.put("activityTime", activity.getActivityTime());
        result.put("endTime", activity.getEndTime());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> grantActivityHours(Integer activityId) {
        Map<String, Object> result = new HashMap<>();

        if (activityId == null) {
            result.put("success", false);
            result.put("message", "活动ID不能为空");
            return result;
        }

        List<ActivityRegistration> registrations = registrationRepository.findByActivityId(activityId);
        int grantedCount = 0;
        int skippedCount = 0;

        for (ActivityRegistration registration : registrations) {
            if ("completed".equals(registration.getStatus())) {
                skippedCount++;
                continue;
            }
            if (!"signed".equals(registration.getSignInStatus())) {
                continue;
            }
            if (registration.getActualHours() == null || registration.getActualHours() <= 0) {
                continue;
            }

            Volunteer volunteer = registration.getVolunteer();
            volunteer.setServiceHours(volunteer.getServiceHours() + registration.getActualHours());
            volunteer.setTotalServiceHours(volunteer.getTotalServiceHours() + registration.getActualHours());
            volunteer.setLastServiceTime(new Date());
            volunteerRepository.save(volunteer);

            registration.setStatus("completed");
            registration.setConfirmTime(new Date());
            registrationRepository.save(registration);

            SignInRecord signInRecord = signInRecordRepository
                    .findByRegistrationIdAndStatus(registration.getId(), "signed_in").orElse(null);
            if (signInRecord != null) {
                signInRecord.setStatus("completed");
                signInRecord.setHoursEarned(registration.getActualHours());
                signInRecordRepository.save(signInRecord);
            }

            grantedCount++;
        }

        result.put("success", true);
        result.put("message", grantedCount > 0 ? "已为 " + grantedCount + " 人发放志愿时长" : "0人需要发放，已全部处理");
        result.put("grantedCount", grantedCount);
        result.put("skippedCount", skippedCount);

        return result;
    }

    @Override
    public List<Map<String, Object>> getMyRegistrations(Integer userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        Optional<Volunteer> volunteerOpt = volunteerRepository.findByUserId(userId);
        if (!volunteerOpt.isPresent()) {
            return new ArrayList<>();
        }

        List<ActivityRegistration> registrations =
                registrationRepository.findByVolunteerIdOrderByRegistrationTimeDesc(volunteerOpt.get().getId());

        List<Map<String, Object>> result = new ArrayList<>();
        for (ActivityRegistration reg : registrations) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", reg.getId());
            item.put("status", reg.getStatus());
            item.put("signInStatus", reg.getSignInStatus());
            item.put("signInTime", reg.getSignInTime());
            item.put("actualHours", reg.getActualHours());
            item.put("registrationTime", reg.getRegistrationTime());
            if (reg.getActivity() != null) {
                item.put("activityId", reg.getActivity().getId());
                item.put("activityTitle", reg.getActivity().getTitle());
                item.put("activityLocation", reg.getActivity().getLocation());
                item.put("activityTime", reg.getActivity().getActivityTime());
                item.put("activityEndTime", reg.getActivity().getEndTime());
                item.put("activityStatus", reg.getActivity().getStatus());
            }
            result.add(item);
        }
        return result;
    }
}