package edu.univ.erp.service.student;

import edu.univ.erp.data.repository.EnrollmentRepository;
import edu.univ.erp.data.repository.GradeRepository;
import edu.univ.erp.domain.GradeComponent;
import edu.univ.erp.domain.StudentCourseOption;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GradeService {
    public static class GradeSummary {
        public final List<GradeComponent> components;
        public final double totalWeight;
        public final double weightedScore;

        public GradeSummary(List<GradeComponent> components, double totalWeight, double weightedScore) {
            this.components = components;
            this.totalWeight = totalWeight;
            this.weightedScore = weightedScore;
        }
    }

    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;

    public GradeService() {
        this(new EnrollmentRepository(), new GradeRepository());
    }

    public GradeService(EnrollmentRepository enrollmentRepository, GradeRepository gradeRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.gradeRepository = gradeRepository;
    }

    public List<StudentCourseOption> loadCourseOptions(int studentId) throws Exception {
        return enrollmentRepository.findCourseOptions(studentId);
    }

    public Optional<GradeSummary> loadGrades(int studentId, int sectionId) throws Exception {
        Optional<Integer> enrollmentIdOpt = gradeRepository.findEnrollmentId(studentId, sectionId);
        if (enrollmentIdOpt.isEmpty()) {
            return Optional.empty();
        }
        List<GradeComponent> components = gradeRepository.findGradesForEnrollment(enrollmentIdOpt.get());
        double totalWeight = components.stream().mapToDouble(GradeComponent::getWeight).sum();
        double weightedScore = components.stream().mapToDouble(GradeComponent::getWeightedContribution).sum();
        return Optional.of(new GradeSummary(components, totalWeight, weightedScore));
    }

    public String calculateLetterGrade(double percentage) {
        if (percentage >= 90) {
            return "A";
        } else if (percentage >= 80) {
            return "B";
        } else if (percentage >= 70) {
            return "C";
        } else if (percentage >= 60) {
            return "D";
        }
        return "F";
    }
}

