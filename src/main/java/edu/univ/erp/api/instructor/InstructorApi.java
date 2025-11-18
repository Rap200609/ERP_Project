package edu.univ.erp.api.instructor;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.domain.ComponentStats;
import edu.univ.erp.domain.GradeComponent;
import edu.univ.erp.domain.SectionDetail;
import edu.univ.erp.domain.StudentProfile;
import edu.univ.erp.service.instructor.InstructorService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class InstructorApi {
    private final InstructorService instructorService;

    public InstructorApi() {
        this(new InstructorService());
    }

    public InstructorApi(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    public List<SectionDetail> listSections(int instructorId) {
        try {
            return instructorService.listSectionsForInstructor(instructorId);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public List<StudentProfile> listStudents(int sectionId) {
        try {
            return instructorService.listStudentsForSection(sectionId);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public List<GradeComponent> loadGrades(int studentId, int sectionId) {
        try {
            return instructorService.loadGradesForStudent(studentId, sectionId);
        } catch (RuntimeException ex) {
            // unwrap the runtime exception used in loadGradesForStudent
            return Collections.emptyList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public ApiResponse saveGrades(int studentId, int sectionId, Map<String, Double> scores) {
        try {
            instructorService.updateGrades(studentId, sectionId, scores);
            return ApiResponse.success("Grades saved successfully.");
        } catch (Exception ex) {
            return ApiResponse.failure("Failed to save grades: " + ex.getMessage());
        }
    }

    public ApiResponse addComponent(int sectionId, String component, double maxScore, double weight) {
        try {
            int affected = instructorService.addComponentForSection(sectionId, component, maxScore, weight);
            return ApiResponse.success("Component added for " + affected + " enrollments.");
        } catch (Exception ex) {
            return ApiResponse.failure("Failed to add component: " + ex.getMessage());
        }
    }

    public ApiResponse deleteComponent(int sectionId, String component) {
        try {
            int affected = instructorService.deleteComponentForSection(sectionId, component);
            return ApiResponse.success("Component removed from " + affected + " enrollments.");
        } catch (Exception ex) {
            return ApiResponse.failure("Failed to delete component: " + ex.getMessage());
        }
    }

    public List<String> listComponentNames(int sectionId) {
        try {
            return instructorService.listComponentNames(sectionId);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public List<ComponentStats> loadComponentStats(int sectionId) {
        try {
            return instructorService.getComponentStats(sectionId);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public List<edu.univ.erp.domain.GradeExportRow> loadGradesForExport(int instructorId) {
        try {
            return instructorService.getGradesForExport(instructorId);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
