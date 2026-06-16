package com.inclusive.adaptiveeducationservice.adaptive.service;

import com.inclusive.adaptiveeducationservice.student.entity.StudentProfileEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdaptiveLearningEngine {

    public AdaptivePlan generate(StudentProfileEntity student) {
        var methodology = recommendedMethodology(student);
        var riskLevel = riskLevel(student);
        var resources = recommendedResources(student, methodology);
        var pathway = adaptivePathway(methodology);
        var teacherActions = teacherActions(student, methodology);
        var inclusionActions = inclusionActions(student);
        var familyActions = familyActions(student);

        return new AdaptivePlan(
                riskLevel,
                methodology,
                resources,
                pathway,
                teacherActions,
                inclusionActions,
                familyActions
        );
    }

    private String recommendedMethodology(StudentProfileEntity student) {
        var learningProfile = student.getLearningProfile();
        var preferences = student.getLearningPreferences();

        if ("DIVERGENT".equalsIgnoreCase(learningProfile)
                && preferences.contains("VISUAL")) {
            return "PROJECT_BASED_LEARNING";
        }

        if ("ASSIMILATING".equalsIgnoreCase(learningProfile)
                && preferences.contains("VISUAL")) {
            return "CONCEPTUAL_MODELING";
        }

        if ("CONVERGENT".equalsIgnoreCase(learningProfile)
                && preferences.contains("ACTIVE")) {
            return "PROBLEM_SOLVING";
        }

        if ("ACCOMMODATING".equalsIgnoreCase(learningProfile)
                || preferences.contains("ACTIVE")) {
            return "EXPERIENTIAL_LEARNING";
        }

        return "DIFFERENTIATED_INSTRUCTION";
    }

    private String riskLevel(StudentProfileEntity student) {
        if ("HIGH".equalsIgnoreCase(student.getSupportLevel())) {
            return "HIGH_EDUCATIONAL_SUPPORT_NEED";
        }

        if ("MEDIUM".equalsIgnoreCase(student.getSupportLevel())) {
            return "MODERATE_EDUCATIONAL_SUPPORT_NEED";
        }

        return "LOW_EDUCATIONAL_SUPPORT_NEED";
    }

    private List<String> recommendedResources(
            StudentProfileEntity student,
            String methodology
    ) {
        var resources = new ArrayList<String>();

        if (student.getLearningPreferences().contains("VISUAL")) {
            resources.add("Infographics and visual organizers");
            resources.add("Concept maps and diagrams");
        }

        if (student.getLearningPreferences().contains("ACTIVE")) {
            resources.add("Hands-on activities");
            resources.add("Collaborative challenges");
        }

        if ("SCIENTIFIC".equalsIgnoreCase(student.getVocationalInterest())) {
            resources.add("Simulations and inquiry-based experiments");
        }

        if ("PROJECT_BASED_LEARNING".equals(methodology)) {
            resources.add("Project templates and presentation guides");
        }

        if (resources.isEmpty()) {
            resources.add("Structured learning guide");
            resources.add("Teacher-guided formative activities");
        }

        return resources.stream().distinct().toList();
    }

    private List<String> adaptivePathway(String methodology) {
        if ("PROJECT_BASED_LEARNING".equals(methodology)) {
            return List.of(
                    "Explore the problem with visual examples",
                    "Plan a collaborative project",
                    "Create a concrete product",
                    "Reflect on learning progress",
                    "Present evidence of understanding"
            );
        }

        if ("CONCEPTUAL_MODELING".equals(methodology)) {
            return List.of(
                    "Introduce key concepts",
                    "Organize information in models",
                    "Compare examples and non-examples",
                    "Explain relationships",
                    "Apply the model to a new situation"
            );
        }

        if ("PROBLEM_SOLVING".equals(methodology)) {
            return List.of(
                    "Present a practical challenge",
                    "Identify known and unknown information",
                    "Test possible solutions",
                    "Receive formative feedback",
                    "Apply the solution independently"
            );
        }

        if ("EXPERIENTIAL_LEARNING".equals(methodology)) {
            return List.of(
                    "Start with direct experience",
                    "Discuss what happened",
                    "Connect experience with concepts",
                    "Apply learning in a new context",
                    "Evaluate progress"
            );
        }

        return List.of(
                "Identify learning goal",
                "Select support strategy",
                "Apply guided activity",
                "Review evidence",
                "Adjust next support action"
        );
    }

    private List<String> teacherActions(
            StudentProfileEntity student,
            String methodology
    ) {
        var actions = new ArrayList<String>();

        actions.add("Review the adaptive plan before the next lesson.");
        actions.add("Select two strategies that can be applied this week.");
        actions.add("Collect formative evidence during classroom activities.");
        actions.add("Use " + methodology + " as the main instructional approach.");

        if ("HIGH".equalsIgnoreCase(student.getSupportLevel())) {
            actions.add("Coordinate follow-up with the inclusion team.");
        }

        return actions;
    }

    private List<String> inclusionActions(StudentProfileEntity student) {
        var actions = new ArrayList<String>();

        if ("HIGH".equalsIgnoreCase(student.getSupportLevel())) {
            actions.add("Review reasonable adjustments with the teaching team.");
            actions.add("Document support evidence for institutional follow-up.");
            actions.add("Evaluate whether PIAR support planning is required.");
        } else if ("MEDIUM".equalsIgnoreCase(student.getSupportLevel())) {
            actions.add("Monitor progress and adjust classroom strategies.");
        } else {
            actions.add("Maintain regular classroom monitoring.");
        }

        return actions;
    }

    private List<String> familyActions(StudentProfileEntity student) {
        var actions = new ArrayList<String>();

        actions.add("Support learning routines with short and clear goals.");
        actions.add("Maintain communication with teachers and support staff.");

        if ("SCIENTIFIC".equalsIgnoreCase(student.getVocationalInterest())) {
            actions.add("Encourage curiosity through simple experiments and questions.");
        }

        if (student.getLearningPreferences().contains("VISUAL")) {
            actions.add("Use drawings, examples or visual notes at home.");
        }

        return actions;
    }

    public record AdaptivePlan(
            String riskLevel,
            String recommendedMethodology,
            List<String> recommendedResources,
            List<String> adaptivePathway,
            List<String> teacherActions,
            List<String> inclusionActions,
            List<String> familyActions
    ) {
    }
}