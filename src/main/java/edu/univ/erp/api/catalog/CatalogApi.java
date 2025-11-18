package edu.univ.erp.api.catalog;

import edu.univ.erp.domain.CourseCatalogEntry;
import edu.univ.erp.service.catalog.CatalogService;

import java.util.Collections;
import java.util.List;

public class CatalogApi {
    private final CatalogService catalogService;

    public CatalogApi() {
        this(new CatalogService());
    }

    public CatalogApi(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public List<CourseCatalogEntry> loadCatalog() {
        try {
            return catalogService.getCatalogEntries();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}

