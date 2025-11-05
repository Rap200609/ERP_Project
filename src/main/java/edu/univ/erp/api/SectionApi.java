package edu.univ.erp.api;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.SectionService;

import java.util.List;

public class SectionApi {
    private final SectionService sectionService;

    public SectionApi() {
        this.sectionService = new SectionService();
    }

    public List<Course> getCourses() {
        return sectionService.listCourses();
    }

    public List<Section> getSections() throws Exception {
        return sectionService.listSections();
    }

    public Section getSection(int id) throws Exception {
        return sectionService.getSection(id);
    }

    public void addSection(Section s) throws Exception {
        sectionService.createSection(s);
    }

    public void updateSection(Section s) throws Exception {
        sectionService.updateSection(s);
    }

    public void deleteSection(int id) throws Exception {
        sectionService.deleteSection(id);
    }
}


