package edu.univ.erp.api.student;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.domain.EnrolledSection;
import edu.univ.erp.domain.SectionAvailability;
import edu.univ.erp.domain.TimetableEntry;
import edu.univ.erp.domain.StudentCourseOption;
import edu.univ.erp.service.student.EnrollmentService;
import edu.univ.erp.service.student.GradeService;
import edu.univ.erp.service.student.TranscriptService;
import edu.univ.erp.service.admin.DropDeadlineService;
import edu.univ.erp.util.MaintenanceManager;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StudentApi {
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;
    private final TranscriptService transcriptService;
    private final DropDeadlineService deadlineService;

    public StudentApi() {
        this(new EnrollmentService(), new GradeService(), new TranscriptService(), new DropDeadlineService());
    }

    public StudentApi(EnrollmentService enrollmentService) {
        this(enrollmentService, new GradeService(), new TranscriptService(), new DropDeadlineService());
    }

    public StudentApi(EnrollmentService enrollmentService, GradeService gradeService) {
        this(enrollmentService, gradeService, new TranscriptService(), new DropDeadlineService());
    }

    public StudentApi(EnrollmentService enrollmentService, GradeService gradeService, TranscriptService transcriptService) {
        this(enrollmentService, gradeService, transcriptService, new DropDeadlineService());
    }

    public StudentApi(EnrollmentService enrollmentService, GradeService gradeService, TranscriptService transcriptService, DropDeadlineService deadlineService) {
        this.enrollmentService = enrollmentService;
        this.gradeService = gradeService;
        this.transcriptService = transcriptService;
        this.deadlineService = deadlineService;
    }

    public List<SectionAvailability> loadRegistrationCatalog() {
        try {
            return enrollmentService.loadAvailability();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public ApiResponse registerSections(int studentId, List<Integer> sectionIds) {
        if (MaintenanceManager.isMaintenanceModeOn()) {
            return ApiResponse.failure("Maintenance mode is active. Registration is disabled.");
        }
        try {
            EnrollmentService.RegistrationResult result = enrollmentService.registerForSections(studentId, sectionIds);
            if (result.successfulSections.isEmpty() && result.errorMessages.isEmpty()) {
                return ApiResponse.failure("No sections selected for registration.");
            }
            StringBuilder message = new StringBuilder();
            if (!result.successfulSections.isEmpty()) {
                message.append("Successfully registered for:\n");
                for (String section : result.successfulSections) {
                    message.append("✓ ").append(section).append("\n");
                }
            }
            if (!result.errorMessages.isEmpty()) {
                if (message.length() > 0) {
                    message.append("\n");
                }
                message.append("Registration failed:\n");
                for (String error : result.errorMessages) {
                    message.append("• ").append(error).append("\n");
                }
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

    public ApiResponse dropSections(int studentId, List<Integer> sectionIds) {
        if (MaintenanceManager.isMaintenanceModeOn()) {
            return ApiResponse.failure("Maintenance mode is active. Drop section is disabled.");
        }
        try {
            EnrollmentService.DropResult result = enrollmentService.dropSections(studentId, sectionIds);
            
            if (result.deadlineExceeded) {
                return ApiResponse.failure("Drop deadline has passed. You can no longer drop sections.");
            }
            
            if (result.droppedCount == 0) {
                return ApiResponse.failure("No sections were selected to drop.");
            }
            return ApiResponse.success("Successfully dropped " + result.droppedCount + " section(s).");
        } catch (Exception ex) {
            return ApiResponse.failure("Error dropping sections: " + ex.getMessage());
        }
    }

    public ApiResponse validateSelection(List<Boolean> selections) {
        boolean any = false;
        for (Boolean isChecked : selections) {
            if (isChecked == true) {
                any = true;
                break;
            }
        }
        if (!any) {
            return ApiResponse.failure("No rows selected.");
        }
        return ApiResponse.success("Selection OK");
    }

    public LocalDate getDropDeadline() {
        try {
            return deadlineService.getDropDeadline();
        } catch (Exception ex) {
            return null;
        }
    }

    public boolean isDropDeadlinePassed() {
        try {
            return deadlineService.isDropDeadlinePassed();
        } catch (Exception ex) {
            return false;
        }
    }
}

