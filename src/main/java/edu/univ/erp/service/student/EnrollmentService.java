package edu.univ.erp.service.student;

import edu.univ.erp.data.repository.EnrollmentRepository;
import edu.univ.erp.data.repository.SectionRepository;
import edu.univ.erp.domain.EnrolledSection;
import edu.univ.erp.domain.SectionAvailability;
import edu.univ.erp.domain.SectionSummary;

import java.util.ArrayList;
import java.util.List;

public class EnrollmentService {

    public static class RegistrationResult {
        public final List<String> successfulSections = new ArrayList<>();
        public final List<String> errorMessages = new ArrayList<>();
    }

    public static class DropResult {
        public int droppedCount;
    }

    private final SectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService() {
        this(new SectionRepository(), new EnrollmentRepository());
    }

    public EnrollmentService(SectionRepository sectionRepository, EnrollmentRepository enrollmentRepository) {
        this.sectionRepository = sectionRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<SectionAvailability> loadAvailability() throws Exception {
        return sectionRepository.fetchSectionAvailability();
    }

    public RegistrationResult registerForSections(int studentId, List<String> sectionCodes) throws Exception {
        RegistrationResult result = new RegistrationResult();
        for (String sectionCode : sectionCodes) {
            SectionSummary summary = sectionRepository.findByCode(sectionCode)
                    .orElse(null);
            if (summary == null) {
                result.errorMessages.add("• " + sectionCode + ": Section not found");
                continue;
            }

            if (enrollmentRepository.isStudentAlreadyEnrolled(studentId, summary.getSectionId())) {
                result.errorMessages.add("• " + sectionCode + " (" + summary.getCourseTitle() + "): Already enrolled");
                continue;
            }

            int enrolledCount = enrollmentRepository.countEnrolledInSection(summary.getSectionId());
            if (enrolledCount >= summary.getCapacity()) {
                result.errorMessages.add("• " + sectionCode + " (" + summary.getCourseTitle() + "): Section is full");
                continue;
            }

            enrollmentRepository.enrollStudent(studentId, summary.getSectionId());
            result.successfulSections.add(sectionCode + " - " + summary.getCourseTitle());
        }
        return result;
    }

    public List<EnrolledSection> loadEnrolledSections(int studentId) throws Exception {
        return enrollmentRepository.findEnrolledSections(studentId);
    }

    public List<edu.univ.erp.domain.TimetableEntry> loadTimetable(int studentId) throws Exception {
        return enrollmentRepository.findTimetableEntries(studentId);
    }

    public DropResult dropSections(int studentId, List<String> sectionCodes) throws Exception {
        DropResult result = new DropResult();
        for (String code : sectionCodes) {
            result.droppedCount += enrollmentRepository.dropEnrollment(studentId, code);
        }
        return result;
    }
}

