package edu.univ.erp.service.student;

import edu.univ.erp.data.repository.EnrollmentRepository;
import edu.univ.erp.data.repository.SectionRepository;
import edu.univ.erp.domain.EnrolledSection;
import edu.univ.erp.domain.SectionAvailability;
import edu.univ.erp.domain.SectionSummary;
import edu.univ.erp.service.admin.DropDeadlineService;

import java.util.ArrayList;
import java.util.List;

public class EnrollmentService {

    public static class RegistrationResult {
        public final List<String> successfulSections = new ArrayList<>();
        public final List<String> errorMessages = new ArrayList<>();
    }

    public static class DropResult {
        public int droppedCount;
        public boolean deadlineExceeded = false;
    }

    private final SectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final DropDeadlineService deadlineService;

    public EnrollmentService() {
        this(new SectionRepository(), new EnrollmentRepository(), new DropDeadlineService());
    }

    public EnrollmentService(SectionRepository sectionRepository, EnrollmentRepository enrollmentRepository) {
        this(sectionRepository, enrollmentRepository, new DropDeadlineService());
    }

    public EnrollmentService(SectionRepository sectionRepository, EnrollmentRepository enrollmentRepository, DropDeadlineService deadlineService) {
        this.sectionRepository = sectionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.deadlineService = deadlineService;
    }

    public List<SectionAvailability> loadAvailability() throws Exception {
        return sectionRepository.fetchSectionAvailability();
    }

    public RegistrationResult registerForSections(int studentId, List<Integer> sectionIds) throws Exception {
        RegistrationResult result = new RegistrationResult();
        for (int sectionId : sectionIds) {
            SectionSummary summary = sectionRepository.findById(sectionId)
                    .orElse(null);
            if (summary == null) {
                result.errorMessages.add("• Section ID " + sectionId + ": Section not found");
                continue;
            }

            if (enrollmentRepository.isStudentAlreadyEnrolled(studentId, summary.getSectionId())) {
                result.errorMessages.add("• " + summary.getSectionCode() + " (" + summary.getCourseTitle() + "): Already enrolled");
                continue;
            }

            if (enrollmentRepository.isStudentEnrolledInCourse(studentId, summary.getCourseId())) {
                result.errorMessages.add("• " + summary.getSectionCode() + " (" + summary.getCourseTitle() + "): Already enrolled in another section of this course");
                continue;
            }

            int enrolledCount = enrollmentRepository.countEnrolledInSection(summary.getSectionId());
            if (enrolledCount >= summary.getCapacity()) {
                result.errorMessages.add("• " + summary.getSectionCode() + " (" + summary.getCourseTitle() + "): Section is full");
                continue;
            }

            enrollmentRepository.enrollStudent(studentId, summary.getSectionId());
            result.successfulSections.add(summary.getSectionCode() + " - " + summary.getCourseTitle());
        }
        return result;
    }

    public List<EnrolledSection> loadEnrolledSections(int studentId) throws Exception {
        return enrollmentRepository.findEnrolledSections(studentId);
    }

    public List<edu.univ.erp.domain.TimetableEntry> loadTimetable(int studentId) throws Exception {
        return enrollmentRepository.findTimetableEntries(studentId);
    }

    public DropResult dropSections(int studentId, List<Integer> sectionIds) throws Exception {
        DropResult result = new DropResult();
        
        // Check if drop deadline has passed
        if (deadlineService.isDropDeadlinePassed()) {
            result.deadlineExceeded = true;
            return result;
        }
        
        for (int sectionId : sectionIds) {
            result.droppedCount += enrollmentRepository.dropEnrollment(studentId, sectionId);
        }
        return result;
    }
}

