package com.flower.service.impl;

import com.flower.entity.MemberConsultation;
import com.flower.entity.User;
import com.flower.repository.MemberConsultationRepository;
import com.flower.repository.UserRepository;
import com.flower.service.MemberConsultationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("all")
public class MemberConsultationServiceImpl implements MemberConsultationService {

    @Resource
    private MemberConsultationRepository consultationRepository;

    @Resource
    private UserRepository userRepository;

    @Override
    public Iterable<MemberConsultation> getAllConsultations() {
        return consultationRepository.findAll();
    }

    @Override
    public Optional<MemberConsultation> getConsultationById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return consultationRepository.findById(id);
    }

    @Override
    public List<MemberConsultation> getConsultationsByUserId(Integer userId) {
        if (userId == null) {
            return List.of();
        }
        return consultationRepository.findByUserId(userId);
    }

    @Override
    public List<MemberConsultation> getConsultationsByStatus(String status) {
        if (status == null) {
            return consultationRepository.findAllByOrderByCreateTimeDesc();
        }
        return consultationRepository.findByStatusOrderByCreateTimeDesc(status);
    }

    @Override
    public List<MemberConsultation> getPendingConsultations() {
        return consultationRepository.findByStatusOrderByCreateTimeDesc("pending");
    }

    @Override
    public MemberConsultation createConsultation(Integer userId, MemberConsultation consultation) {
        if (userId == null || consultation == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        consultation.setUser(userOptional.get());
        consultation.setStatus("pending");
        return consultationRepository.save(consultation);
    }

    @Override
    public MemberConsultation replyConsultation(Integer id, String replyContent, String replierName) {
        if (id == null || replyContent == null) {
            return null;
        }

        Optional<MemberConsultation> consultationOptional = consultationRepository.findById(id);
        if (!consultationOptional.isPresent()) {
            return null;
        }

        MemberConsultation consultation = consultationOptional.get();
        consultation.setReplyContent(replyContent);
        consultation.setReplyTime(new Date());
        consultation.setReplierName(replierName);
        consultation.setStatus("answered");

        return consultationRepository.save(consultation);
    }

    @Override
    public MemberConsultation closeConsultation(Integer id) {
        if (id == null) {
            return null;
        }

        Optional<MemberConsultation> consultationOptional = consultationRepository.findById(id);
        if (!consultationOptional.isPresent()) {
            return null;
        }

        MemberConsultation consultation = consultationOptional.get();
        consultation.setStatus("closed");

        return consultationRepository.save(consultation);
    }

    @Override
    public void deleteConsultation(Integer id) {
        if (id != null) {
            consultationRepository.deleteById(id);
        }
    }
}
