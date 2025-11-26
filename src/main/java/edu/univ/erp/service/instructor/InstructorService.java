package edu.univ.erp.service.instructor;

import edu.univ.erp.data.repository.EnrollmentRepository;
import edu.univ.erp.data.repository.GradeRepository;
import edu.univ.erp.data.repository.SectionRepository;
import edu.univ.erp.domain.ComponentStats;
import edu.univ.erp.domain.GradeComponent;
import edu.univ.erp.domain.SectionDetail;
import edu.univ.erp.domain.StudentProfile;
import edu.univ.erp.domain.GradeExportRow;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class InstructorService {
    private final SectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;

    public InstructorService() {
        this(new SectionRepository(), new EnrollmentRepository(), new GradeRepository());
    }

    public InstructorService(SectionRepository sectionRepository, EnrollmentRepository enrollmentRepository, GradeRepository gradeRepository) {
        this.sectionRepository = sectionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.gradeRepository = gradeRepository;
    }

    public List<SectionDetail> listSectionsForInstructor(int instructorId) throws Exception {
        return sectionRepository.findSectionsByInstructor(instructorId);
    }

    public List<StudentProfile> listStudentsForSection(int sectionId) throws Exception {
        return enrollmentRepository.findStudentsInSection(sectionId);
    }

    public List<GradeComponent> loadGradesForStudent(int studentId, int sectionId) throws Exception {
        return enrollmentRepository.findEnrollmentId(studentId, sectionId)
                .map(enrollmentId -> {
                    try {
                        return gradeRepository.findGradesForEnrollment(enrollmentId);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .orElse(Collections.emptyList());
    }

    public void updateGrades(int studentId, int sectionId, Map<String, Double> scores) throws Exception {
        var enrollmentIdOpt = enrollmentRepository.findEnrollmentId(studentId, sectionId);
        if (enrollmentIdOpt.isEmpty()) {
            throw new IllegalArgumentException("Enrollment not found for student " + studentId + " in section " + sectionId);
        }
        int enrollmentId = enrollmentIdOpt.get();
        for (Map.Entry<String, Double> entry : scores.entrySet()) {
            gradeRepository.updateGradeScore(enrollmentId, entry.getKey(), entry.getValue());
        }
    }

    public int addComponentForSection(int sectionId, String component, double maxScore, double weight) throws Exception {
        return gradeRepository.insertComponentForSection(sectionId, component, maxScore, weight);
    }

    public int deleteComponentForSection(int sectionId, String component) throws Exception {
        return gradeRepository.deleteComponentForSection(sectionId, component);
    }

    public List<String> listComponentNames(int sectionId) throws Exception {
        return gradeRepository.findComponentNamesForSection(sectionId);
    }

    public List<ComponentStats> getComponentStats(int sectionId) throws Exception {
        return gradeRepository.findComponentStatsForSection(sectionId);
    }

    public List<GradeExportRow> getGradesForExport(int instructorId) throws Exception {
        return gradeRepository.findGradesForExport(instructorId);
    }
}
