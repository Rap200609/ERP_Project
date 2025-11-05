package edu.univ.erp.service;

import edu.univ.erp.data.dao.CourseDao;
import edu.univ.erp.data.dao.SectionDao;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;

import java.util.List;

public class SectionService {
    private final SectionDao sectionDao;
    private final CourseDao courseDao;

    public SectionService() {
        this.sectionDao = new SectionDao();
        this.courseDao = new CourseDao();
    }

    public List<Course> listCourses() {
        return courseDao.findAll();
    }

    public List<Section> listSections() throws Exception {
        return sectionDao.findAll();
    }

    public Section getSection(int id) throws Exception {
        return sectionDao.findById(id);
    }

    public void createSection(Section section) throws Exception {
        sectionDao.insert(section);
    }

    public void updateSection(Section section) throws Exception {
        sectionDao.update(section);
    }

    public void deleteSection(int id) throws Exception {
        sectionDao.delete(id);
    }
}


