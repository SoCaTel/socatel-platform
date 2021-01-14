package com.socatel.rest_api.rdi;

public class ServiceByKeywords {

    private Integer service_id;
    private Integer locality_id;
    private Double score;

    public ServiceByKeywords() {}

    public ServiceByKeywords(Integer service_id, Integer locality_id, Double score) {
        this.service_id = service_id;
        this.locality_id = locality_id;
        this.score = score;
    }

    public Integer getService_id() {
        return service_id;
    }

    public void setService_id(Integer service_id) {
        this.service_id = service_id;
    }

    public Integer getLocality_id() {
        return locality_id;
    }

    public void setLocality_id(Integer locality_id) {
        this.locality_id = locality_id;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "ServiceByKeywords{" +
                "service_id=" + service_id +
                ", locality_id=" + locality_id +
                ", score=" + score +
                '}';
    }
}
