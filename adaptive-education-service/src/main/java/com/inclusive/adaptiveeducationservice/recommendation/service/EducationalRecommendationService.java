package com.inclusive.adaptiveeducationservice.recommendation.service;

import com.inclusive.adaptiveeducationservice.recommendation.dto.StudentRecommendationResponse;
import com.inclusive.adaptiveeducationservice.student.entity.StudentProfileEntity;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class EducationalRecommendationService {

    private final StudentProfileRepository studentProfileRepository;

    public EducationalRecommendationService(
            StudentProfileRepository studentProfileRepository
    ) {
        this.studentProfileRepository = studentProfileRepository;
    }

    public StudentRecommendationResponse generateForStudent(String studentId) {
        var student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student profile not found"
                ));

        return new StudentRecommendationResponse(
                student.getId(),
                student.getFullName(),
                student.getLearningProfile(),
                student.getVocationalInterest(),
                student.getSupportLevel(),
                teacherRecommendations(student),
                inclusionRecommendations(student),
                familyRecommendations(student),
                nextActions(student)
        );
    }

    private List<String> teacherRecommendations(StudentProfileEntity student) {
        var recommendations = new ArrayList<String>();

        recommendations.addAll(student.getPedagogicalRecommendations());

        if ("DIVERGENT".equalsIgnoreCase(student.getLearningProfile())) {
            recommendations.add("Use open-ended activities and visual exploration.");
            recommendations.add("Promote dialogue, reflection and collaborative work.");
        }

        if (student.getLearningPreferences().contains("VISUAL")) {
            recommendations.add("Use diagrams, visual organizers and color-coded examples.");
        }

        if (student.getLearningPreferences().contains("ACTIVE")) {
            recommendations.add("Use practical tasks, participation and short challenges.");
        }

        return recommendations.stream().distinct().toList();
    }

    private List<String> inclusionRecommendations(StudentProfileEntity student) {
        var recommendations = new ArrayList<String>();

        recommendations.addAll(student.getInclusiveStrategies());

        if ("HIGH".equalsIgnoreCase(student.getSupportLevel())) {
            recommendations.add("Prioritize institutional follow-up with the inclusion team.");
            recommendations.add("Document reasonable adjustments and classroom evidence.");
        }

        if ("MEDIUM".equalsIgnoreCase(student.getSupportLevel())) {
            recommendations.add("Monitor progress and review support strategies periodically.");
        }

        return recommendations.stream().distinct().toList();
    }

    private List<String> familyRecommendations(StudentProfileEntity student) {
        var recommendations = new ArrayList<String>();

        recommendations.add("Maintain regular communication with teachers and counsellors.");
        recommendations.add("Support study routines using clear and short goals.");

        if ("SCIENTIFIC".equalsIgnoreCase(student.getVocationalInterest())) {
            recommendations.add("Encourage curiosity through experiments, questions and problem-solving activities.");
        }

        if ("ARTISTIC".equalsIgnoreCase(student.getVocationalInterest())) {
            recommendations.add("Encourage creative expression through drawing, music, writing or design.");
        }

        if ("SOCIAL".equalsIgnoreCase(student.getVocationalInterest())) {
            recommendations.add("Encourage teamwork, empathy and community participation.");
        }

        return recommendations.stream().distinct().toList();
    }

    private List<String> nextActions(StudentProfileEntity student) {
        var actions = new ArrayList<String>();

        actions.add("Review student profile with the teaching team.");
        actions.add("Select classroom strategies for the next academic period.");

        if ("HIGH".equalsIgnoreCase(student.getSupportLevel())) {
            actions.add("Schedule inclusion team review.");
        }

        actions.add("Share family-friendly recommendations with guardians.");

        return actions.stream().distinct().toList();
    }
}