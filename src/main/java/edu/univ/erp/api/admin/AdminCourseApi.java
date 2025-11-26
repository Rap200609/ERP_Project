package edu.univ.erp.api.admin;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.domain.CourseDetail;
import edu.univ.erp.service.admin.CourseService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
 
public class AdminCourseApi {
    private final CourseService courseService;

    public AdminCourseApi() {
        this(new CourseService());
    }

    public AdminCourseApi(CourseService courseService) {
        this.courseService = courseService;
    }

    public List<CourseDetail> listCourses() {
        try {
            return courseService.listCourses();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public ApiResponse addCourse(String code, String title, int credits, String description) {
        if (code == null || code.isBlank() || title == null || title.isBlank()) {
            return ApiResponse.failure("Code and title are required.");
        }
        try {
            courseService.createCourse(code, title, credits, description);
            return ApiResponse.success("Course added successfully!");
        } catch (Exception ex) {
            return ApiResponse.failure("Error adding course: " + ex.getMessage());
        }
    }

    public ApiResponse updateCourse(int courseId, String code, String title, int credits, String description) {
        try {
            courseService.updateCourse(courseId, code, title, credits, description);
            return ApiResponse.success("Course updated!");
        } catch (Exception ex) {
            return ApiResponse.failure("Error updating course: " + ex.getMessage());
        }
    }

    public ApiResponse deleteCourse(int courseId) {
        try {
            courseService.deleteCourse(courseId);
            return ApiResponse.success("Course deleted");
        } catch (Exception ex) {
            return ApiResponse.failure("Error deleting course: " + ex.getMessage());
        }
    }

    public Optional<CourseDetail> loadCourse(int courseId) {
        try {
            return courseService.findCourse(courseId);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}

