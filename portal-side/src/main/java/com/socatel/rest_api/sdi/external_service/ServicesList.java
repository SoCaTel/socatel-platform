package com.socatel.rest_api.sdi.external_service;

import java.util.List;

public class ServicesList {
    private List<ServiceByTopic> servicesByTopics;

    public ServicesList() {}

    public List<ServiceByTopic> getServicesByTopics() {
        return servicesByTopics;
    }

    public void setServicesByTopics(List<ServiceByTopic> servicesByTopics) {
        this.servicesByTopics = servicesByTopics;
    }

    @Override
    public String toString() {
        return "ServicesList{" +
                "servicesByTopics=" + servicesByTopics +
                '}';
    }
}
