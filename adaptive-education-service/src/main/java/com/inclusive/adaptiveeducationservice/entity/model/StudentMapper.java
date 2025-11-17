package com.inclusive.adaptiveeducationservice.entity.model;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;

public final class StudentMapper {

    private StudentMapper() {
    }

    public static StudentDTO toDto(Student student) {
        if (student == null) {
            return null;
        }
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setUserId(student.getUserId());
        dto.setFullName(student.getFullName());
        dto.setEmail(student.getEmail());
        dto.setAge(student.getAge());
        dto.setGender(student.getGender());
        dto.setDisabilityStatus(student.getDisabilityStatus());
        dto.setNeedsAssistiveTechnology(student.getNeedsAssistiveTechnology());
        dto.setSchoolLevel(student.getSchoolLevel());
        dto.setSocioEconomicStatus(student.getSocioEconomicStatus());
        dto.setLocation(student.getLocation());
        dto.setFamilyStructure(student.getFamilyStructure());
        dto.setGuardianName(student.getGuardianName());
        dto.setEthnicity(student.getEthnicity());
        dto.setAttendanceRate(student.getAttendanceRate());
        dto.setAverageGrade(student.getAverageGrade());
        dto.setMathScore(student.getMathScore());
        dto.setReadingScore(student.getReadingScore());
        dto.setScienceScore(student.getScienceScore());
        dto.setHomeworkCompletionRate(student.getHomeworkCompletionRate());
        dto.setRepeatingGrade(student.getRepeatingGrade());
        dto.setBehavioralNotes(student.getBehavioralNotes());
        dto.setDisciplinaryActions(student.getDisciplinaryActions());
        dto.setLearningStyleFelder(student.getLearningStyleFelder());
        dto.setLearningStyleKolb(student.getLearningStyleKolb());
        dto.setVocationalProfileKuder(student.getVocationalProfileKuder());
        dto.setAdaptiveContentProfile(student.getAdaptiveContentProfile());
        dto.setEmotionalStateTrend(student.getEmotionalStateTrend());
        dto.setEngagementCluster(student.getEngagementCluster());
        dto.setPredictedDropoutRisk(student.getPredictedDropoutRisk());
        dto.setRecommendedLearningPath(student.getRecommendedLearningPath());
        dto.setDeviceAccess(student.getDeviceAccess());
        dto.setInternetAccess(student.getInternetAccess());
        dto.setPreferredStudyTime(student.getPreferredStudyTime());
        dto.setSiblingsInSchool(student.getSiblingsInSchool());
        dto.setLanguageSpokenAtHome(student.getLanguageSpokenAtHome());
        dto.setPreferredContentFormat(student.getPreferredContentFormat());
        dto.setReadingLevel(student.getReadingLevel());
        dto.setNumeracyLevel(student.getNumeracyLevel());
        dto.setAccountStatus(student.getAccountStatus());
        dto.setAvatarUrl(student.getAvatarUrl());
        dto.setBirthDate(student.getBirthDate());
        dto.setReceivesPsychologicalSupport(student.getReceivesPsychologicalSupport());
        dto.setReceivesSpecialEducationSupport(student.getReceivesSpecialEducationSupport());
        dto.setCreatedAt(student.getCreatedAt());
        dto.setUpdatedAt(student.getUpdatedAt());
        return dto;
    }

    public static Student toEntity(StudentDTO dto) {
        if (dto == null) {
            return null;
        }
        Student student = new Student();
        student.setId(dto.getId());
        student.setUserId(dto.getUserId());
        student.setFullName(dto.getFullName());
        student.setEmail(dto.getEmail());
        student.setAge(dto.getAge());
        student.setGender(dto.getGender());
        student.setDisabilityStatus(dto.getDisabilityStatus());
        student.setNeedsAssistiveTechnology(dto.getNeedsAssistiveTechnology());
        student.setSchoolLevel(dto.getSchoolLevel());
        student.setSocioEconomicStatus(dto.getSocioEconomicStatus());
        student.setLocation(dto.getLocation());
        student.setFamilyStructure(dto.getFamilyStructure());
        student.setGuardianName(dto.getGuardianName());
        student.setEthnicity(dto.getEthnicity());
        student.setAttendanceRate(dto.getAttendanceRate());
        student.setAverageGrade(dto.getAverageGrade());
        student.setMathScore(dto.getMathScore());
        student.setReadingScore(dto.getReadingScore());
        student.setScienceScore(dto.getScienceScore());
        student.setHomeworkCompletionRate(dto.getHomeworkCompletionRate());
        student.setRepeatingGrade(dto.getRepeatingGrade());
        student.setBehavioralNotes(dto.getBehavioralNotes());
        student.setDisciplinaryActions(dto.getDisciplinaryActions());
        student.setLearningStyleFelder(dto.getLearningStyleFelder());
        student.setLearningStyleKolb(dto.getLearningStyleKolb());
        student.setVocationalProfileKuder(dto.getVocationalProfileKuder());
        student.setAdaptiveContentProfile(dto.getAdaptiveContentProfile());
        student.setEmotionalStateTrend(dto.getEmotionalStateTrend());
        student.setEngagementCluster(dto.getEngagementCluster());
        student.setPredictedDropoutRisk(dto.getPredictedDropoutRisk());
        student.setRecommendedLearningPath(dto.getRecommendedLearningPath());
        student.setDeviceAccess(dto.getDeviceAccess());
        student.setInternetAccess(dto.getInternetAccess());
        student.setPreferredStudyTime(dto.getPreferredStudyTime());
        student.setSiblingsInSchool(dto.getSiblingsInSchool());
        student.setLanguageSpokenAtHome(dto.getLanguageSpokenAtHome());
        student.setPreferredContentFormat(dto.getPreferredContentFormat());
        student.setReadingLevel(dto.getReadingLevel());
        student.setNumeracyLevel(dto.getNumeracyLevel());
        student.setAccountStatus(dto.getAccountStatus());
        student.setAvatarUrl(dto.getAvatarUrl());
        student.setBirthDate(dto.getBirthDate());
        student.setReceivesPsychologicalSupport(dto.getReceivesPsychologicalSupport());
        student.setReceivesSpecialEducationSupport(dto.getReceivesSpecialEducationSupport());
        student.setCreatedAt(dto.getCreatedAt());
        student.setUpdatedAt(dto.getUpdatedAt());
        return student;
    }
}
