package edu.univ.erp.api.admin;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.domain.CourseDetail;
import edu.univ.erp.domain.InstructorOption;
import edu.univ.erp.domain.SectionAssignment;
import edu.univ.erp.domain.SectionDetail;
import edu.univ.erp.service.admin.SectionService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AdminSectionApi {

    public static class SectionCommand {
        public int courseId;
        public String sectionCode;
        public String day;
        public String time;
        public String room;
        public String semester;
        public Integer year;
        public Integer capacity;
    }

    private final SectionService sectionService;

    public AdminSectionApi() {
        this(new SectionService());
    }

    public AdminSectionApi(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    public List<CourseDetail> listCourses() {
        try {
            return sectionService.listCourses();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public List<SectionDetail> listSections() {
        try {
            return sectionService.listSections();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public Optional<SectionDetail> loadSection(int sectionId) {
        try {
            return sectionService.loadSection(sectionId);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public List<SectionAssignment> listSectionAssignments() {
        try {
            return sectionService.listSectionAssignments();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public List<InstructorOption> listInstructorOptions() {
        try {
            return sectionService.listInstructorOptions();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public ApiResponse assignInstructor(int sectionId, Integer instructorId) {
        try {
            sectionService.assignInstructor(sectionId, instructorId);
            return ApiResponse.success("Instructor updated!");
        } catch (Exception ex) {
            return ApiResponse.failure("Error assigning instructor: " + ex.getMessage());
        }
    }

    public ApiResponse addSection(SectionCommand command) {
        Optional<String> error = validate(command);
        if (error.isPresent()) {
            return ApiResponse.failure(error.get());
        }
        try {
            SectionService.SectionCommand req = mapCommand(command);
            sectionService.createSection(req);
            return ApiResponse.success("Section added successfully!");
        } catch (Exception ex) {
            return ApiResponse.failure("Error adding section: " + ex.getMessage());
        }
    }

    public ApiResponse updateSection(int sectionId, SectionCommand command) {
        Optional<String> error = validate(command);
        if (error.isPresent()) {
            return ApiResponse.failure(error.get());
        }
        try {
            SectionService.SectionCommand req = mapCommand(command);
            sectionService.updateSection(sectionId, req);
            return ApiResponse.success("Section updated!");
        } catch (Exception ex) {
            return ApiResponse.failure("Error updating section: " + ex.getMessage());
        }
    }

    public ApiResponse deleteSection(int sectionId) {
        try {
            sectionService.deleteSection(sectionId);
            return ApiResponse.success("Section deleted");
        } catch (Exception ex) {
            return ApiResponse.failure("Error deleting section: " + ex.getMessage());
        }
    }

    private Optional<String> validate(SectionCommand command) {
        if (command.courseId <= 0) {
            return Optional.of("Course is required.");
        }
        if (command.sectionCode == null || command.sectionCode.isBlank()) {
            return Optional.of("Section code is required.");
        }
        if (command.day == null || command.day.isBlank()
                || command.time == null || command.time.isBlank()
                || command.room == null || command.room.isBlank()
                || command.semester == null || command.semester.isBlank()) {
            return Optional.of("Day, time, room, and semester are required.");
        }
        if (command.year == null || command.year <= 0) {
            return Optional.of("Year must be a positive number.");
        }
        if (command.capacity == null || command.capacity <= 0) {
            return Optional.of("Capacity must be greater than zero.");
        }
        return Optional.empty();
    }

    private SectionService.SectionCommand mapCommand(SectionCommand command) {
        SectionService.SectionCommand req = new SectionService.SectionCommand();
        req.courseId = command.courseId;
        req.sectionCode = command.sectionCode.trim();
        req.day = command.day.trim();
        req.time = command.time.trim();
        req.room = command.room.trim();
        req.semester = command.semester.trim();
        req.year = command.year;
        req.capacity = command.capacity;
        return req;
    }
}