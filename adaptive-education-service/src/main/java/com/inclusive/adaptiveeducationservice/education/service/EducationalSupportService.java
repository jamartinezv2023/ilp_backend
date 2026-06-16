package com.inclusive.adaptiveeducationservice.education.service;

import com.inclusive.adaptiveeducationservice.education.dto.EducationModuleSummaryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationalSupportService {

    public EducationModuleSummaryResponse institutionalOverview() {
        return new EducationModuleSummaryResponse(
                "Institutional Overview",
                "School leaders, inclusion coordinators and institutional teams",
                "Provide an executive view of inclusion, educational support and institutional follow-up.",
                "FUNCTIONAL_BASELINE_READY",
                List.of(
                        "Institutional inclusion monitoring",
                        "Active student support tracking",
                        "Pedagogical recommendation visibility",
                        "Family engagement overview",
                        "Educational support indicators"
                ),
                List.of(
                        "Connect real institutional data",
                        "Add school-year filters",
                        "Add grade and group segmentation",
                        "Integrate alert prioritization"
                )
        );
    }

    public EducationModuleSummaryResponse teacherWorkspace() {
        return new EducationModuleSummaryResponse(
                "Teacher Workspace",
                "Teachers and classroom support staff",
                "Support teachers with student profiles, pedagogical recommendations and classroom strategies.",
                "FUNCTIONAL_BASELINE_READY",
                List.of(
                        "Student list for classroom follow-up",
                        "Pedagogical recommendations",
                        "DUA strategy suggestions",
                        "Learning-style evidence",
                        "Classroom support actions"
                ),
                List.of(
                        "Connect teacher-course assignments",
                        "Add classroom observations",
                        "Add recommendation feedback",
                        "Add intervention follow-up"
                )
        );
    }

    public EducationModuleSummaryResponse studentSupport() {
        return new EducationModuleSummaryResponse(
                "Student Support",
                "Students, teachers, counsellors and support professionals",
                "Provide an educational support profile focused on learning, inclusion and longitudinal progress.",
                "FUNCTIONAL_BASELINE_READY",
                List.of(
                        "Educational characterization",
                        "Learning-style profile",
                        "Vocational orientation evidence",
                        "Support history",
                        "Longitudinal monitoring"
                ),
                List.of(
                        "Connect student records",
                        "Add Kolb profile integration",
                        "Add Felder-Silverman profile integration",
                        "Add Kuder vocational profile integration"
                )
        );
    }

    public EducationModuleSummaryResponse inclusionPiar() {
        return new EducationModuleSummaryResponse(
                "Inclusion & PIAR",
                "Inclusion teams, counsellors and institutional support professionals",
                "Manage reasonable adjustments, DUA strategies, PIAR evidence and educational support plans.",
                "FUNCTIONAL_BASELINE_READY",
                List.of(
                        "Reasonable adjustment tracking",
                        "PIAR support plan overview",
                        "DUA strategy alignment",
                        "Intervention evidence",
                        "Non-clinical educational support"
                ),
                List.of(
                        "Add PIAR templates",
                        "Add adjustment lifecycle",
                        "Add evidence upload",
                        "Add institutional review workflow"
                )
        );
    }

    public EducationModuleSummaryResponse familyEngagement() {
        return new EducationModuleSummaryResponse(
                "Family Engagement",
                "Families, guardians and educational caregivers",
                "Provide understandable educational progress and support recommendations for families.",
                "FUNCTIONAL_BASELINE_READY",
                List.of(
                        "Family-friendly progress summaries",
                        "Support recommendations at home",
                        "Communication with school teams",
                        "Student progress visibility",
                        "Inclusive accompaniment guidance"
                ),
                List.of(
                        "Add guardian access model",
                        "Add communication history",
                        "Add family action plans",
                        "Add notification preferences"
                )
        );
    }
}
