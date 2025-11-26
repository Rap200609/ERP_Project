package edu.univ.erp.service.admin;

import edu.univ.erp.data.repository.CourseRepository;
import edu.univ.erp.domain.CourseDetail;

import java.util.List;
import java.util.Optional;

public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService() {
        this(new CourseRepository());
    }

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseDetail> listCourses() throws Exception {
        return courseRepository.findAllCourses();
    }

    public int createCourse(String code, String title, int credits, String description) throws Exception {
        return courseRepository.createCourse(code, title, credits, description);
    }

    public void updateCourse(int courseId, String code, String title, int credits, String description) throws Exception {
        courseRepository.updateCourse(courseId, code, title, credits, description);
    }

    public void deleteCourse(int courseId) throws Exception {
        courseRepository.deleteCourse(courseId);
    }

    public Optional<CourseDetail> findCourse(int courseId) throws Exception {
        return courseRepository.findCourseById(courseId);
    }
}

