package edu.univ.erp.service.catalog;

import edu.univ.erp.data.repository.CourseRepository;
import edu.univ.erp.domain.CourseCatalogEntry;

import java.util.List;

public class CatalogService {
    private final CourseRepository courseRepository;

    public CatalogService() {
        this(new CourseRepository());
    }

    public CatalogService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseCatalogEntry> getCatalogEntries() throws Exception {
        return courseRepository.fetchCatalogEntries();
    }
}

