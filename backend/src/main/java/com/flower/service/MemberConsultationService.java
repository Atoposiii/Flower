package com.flower.service;

import com.flower.entity.MemberConsultation;

import java.util.List;
import java.util.Optional;

public interface MemberConsultationService {
    Iterable<MemberConsultation> getAllConsultations();
    Optional<MemberConsultation> getConsultationById(Integer id);
    List<MemberConsultation> getConsultationsByUserId(Integer userId);
    List<MemberConsultation> getConsultationsByStatus(String status);
    List<MemberConsultation> getPendingConsultations();
    MemberConsultation createConsultation(Integer userId, MemberConsultation consultation);
    MemberConsultation replyConsultation(Integer id, String replyContent, String replierName);
    MemberConsultation closeConsultation(Integer id);
    void deleteConsultation(Integer id);
}
