package edu.univ.erp.service.admin;

import edu.univ.erp.data.repository.CourseRepository;
import edu.univ.erp.data.repository.InstructorRepository;
import edu.univ.erp.data.repository.SectionRepository;
import edu.univ.erp.domain.CourseDetail;
import edu.univ.erp.domain.InstructorOption;
import edu.univ.erp.domain.SectionAssignment;
import edu.univ.erp.domain.SectionDetail;

import java.util.List;
import java.util.Optional;

public class SectionService {

    public static class SectionCommand {
        public int courseId;
        public String sectionCode;
        public String day;
        public String time;
        public String room;
        public String semester;
        public int year;
        public int capacity;
    }

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;

    public SectionService() {
        this(new SectionRepository(), new CourseRepository(), new InstructorRepository());
    }

    public SectionService(SectionRepository sectionRepository,
                          CourseRepository courseRepository,
                          InstructorRepository instructorRepository) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
    }

    public List<CourseDetail> listCourses() throws Exception {
        return courseRepository.findAllCourses();
    }

    public List<SectionDetail> listSections() throws Exception {
        return sectionRepository.findAllSectionDetails();
    }

    public Optional<SectionDetail> loadSection(int sectionId) throws Exception {
        return sectionRepository.findSectionDetailById(sectionId);
    }

    public void createSection(SectionCommand command) throws Exception {
        sectionRepository.createSection(
                command.courseId,
                command.sectionCode,
                command.day,
                command.time,
                command.room,
                command.semester,
                command.year,
                command.capacity
        );
    }

    public void updateSection(int sectionId, SectionCommand command) throws Exception {
        sectionRepository.updateSection(
                sectionId,
                command.courseId,
                command.sectionCode,
                command.day,
                command.time,
                command.room,
                command.semester,
                command.year,
                command.capacity
        );
    }

    public void deleteSection(int sectionId) throws Exception {
        sectionRepository.deleteSection(sectionId);
    }

    public List<SectionAssignment> listSectionAssignments() throws Exception {
        return sectionRepository.fetchSectionAssignments();
    }

    public List<InstructorOption> listInstructorOptions() throws Exception {
        return instructorRepository.fetchInstructorOptions();
    }

    public void assignInstructor(int sectionId, Integer instructorId) throws Exception {
        sectionRepository.assignInstructor(sectionId, instructorId);
    }
}