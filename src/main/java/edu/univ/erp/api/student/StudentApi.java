package edu.univ.erp.api.student;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.domain.EnrolledSection;
import edu.univ.erp.domain.SectionAvailability;
import edu.univ.erp.domain.TimetableEntry;
import edu.univ.erp.domain.StudentCourseOption;
import edu.univ.erp.service.student.EnrollmentService;
import edu.univ.erp.service.student.GradeService;
import edu.univ.erp.service.student.TranscriptService;
import edu.univ.erp.util.MaintenanceManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StudentApi {
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;
    private final TranscriptService transcriptService;

    public StudentApi() {
        this(new EnrollmentService(), new GradeService(), new TranscriptService());
    }

    public StudentApi(EnrollmentService enrollmentService) {
        this(enrollmentService, new GradeService(), new TranscriptService());
    }

    public StudentApi(EnrollmentService enrollmentService, GradeService gradeService) {
        this(enrollmentService, gradeService, new TranscriptService());
    }

    public StudentApi(EnrollmentService enrollmentService, GradeService gradeService, TranscriptService transcriptService) {
        this.enrollmentService = enrollmentService;
        this.gradeService = gradeService;
        this.transcriptService = transcriptService;
    }

    public List<SectionAvailability> loadRegistrationCatalog() {
        try {
            return enrollmentService.loadAvailability();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public ApiResponse registerSections(int studentId, List<String> sectionCodes) {
        if (MaintenanceManager.isMaintenanceModeOn()) {
            return ApiResponse.failure("Maintenance mode is active. Registration is disabled.");
        }
        try {
            EnrollmentService.RegistrationResult result = enrollmentService.registerForSections(studentId, sectionCodes);
            if (result.successfulSections.isEmpty() && result.errorMessages.isEmpty()) {
                return ApiResponse.failure("No sections selected for registration.");
            }
            StringBuilder message = new StringBuilder();
            if (!result.successfulSections.isEmpty()) {
                message.append("Successfully registered for:\n");
                result.successfulSections.forEach(s -> message.append("✓ ").append(s).append("\n"));
            }
            if (!result.errorMessages.isEmpty()) {
                if (message.length() > 0) {
                    message.append("\n");
                }
                message.append("Registration failed:\n");
                result.errorMessages.forEach(msg -> message.append(msg).append("\n"));
            }
            boolean success = !result.successfulSections.isEmpty() && result.errorMessages.isEmpty();
            return ApiResponse.of(success, message.toString().trim());
        } catch (Exception ex) {
            return ApiResponse.failure("Error during registration: " + ex.getMessage());
        }
    }

    public List<EnrolledSection> loadEnrolledSections(int studentId) {
        try {
            return enrollmentService.loadEnrolledSections(studentId);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public List<TimetableEntry> loadTimetable(int studentId) {
        try {
            return enrollmentService.loadTimetable(studentId);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public List<StudentCourseOption> loadCourseOptions(int studentId) {
        try {
            return gradeService.loadCourseOptions(studentId);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public Optional<GradeService.GradeSummary> loadGrades(int studentId, int sectionId) {
        try {
            return gradeService.loadGrades(studentId, sectionId);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public String calculateLetterGrade(double percentage) {
        return gradeService.calculateLetterGrade(percentage);
    }

    public Optional<TranscriptService.TranscriptData> buildTranscript(int studentId) {
        try {
            return Optional.of(transcriptService.buildTranscript(studentId));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public ApiResponse dropSections(int studentId, List<String> sectionCodes) {
        if (MaintenanceManager.isMaintenanceModeOn()) {
            return ApiResponse.failure("Maintenance mode is active. Drop section is disabled.");
        }
        try {
            EnrollmentService.DropResult result = enrollmentService.dropSections(studentId, sectionCodes);
            if (result.droppedCount == 0) {
                return ApiResponse.failure("No sections were selected to drop.");
            }
            return ApiResponse.success("Successfully dropped " + result.droppedCount + " section(s).");
        } catch (Exception ex) {
            return ApiResponse.failure("Error dropping sections: " + ex.getMessage());
        }
    }

    public ApiResponse validateSelection(List<Boolean> selections) {
        boolean any = selections.stream().anyMatch(Boolean.TRUE::equals);
        if (!any) {
            return ApiResponse.failure("No rows selected.");
        }
        return ApiResponse.success("Selection OK");
    }
}

