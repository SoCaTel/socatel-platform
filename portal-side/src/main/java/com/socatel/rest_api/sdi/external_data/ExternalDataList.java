package com.socatel.rest_api.sdi.external_data;

import java.util.LinkedList;
import java.util.List;

public class ExternalDataList {
    private List<ExternalData> searchByTopics;

    public ExternalDataList() {
        searchByTopics = new LinkedList<>();
    }

    public List<ExternalData> getSearchByTopics() {
        return searchByTopics;
    }

    public void setSearchByTopics(List<ExternalData> searchByTopics) {
        this.searchByTopics = searchByTopics;
    }
}
