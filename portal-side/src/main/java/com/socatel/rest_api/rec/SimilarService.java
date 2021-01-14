package com.socatel.rest_api.rec;

public class SimilarService {
    private Integer similar_service_id;
    private Integer rank;

    public SimilarService() {

    }

    public Integer getSimilar_service_id() {
        return similar_service_id;
    }

    public void setSimilar_service_id(Integer similar_service_id) {
        this.similar_service_id = similar_service_id;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "SimilarService{" +
                "similar_service_id='" + similar_service_id + '\'' +
                ", rank=" + rank +
                '}';
    }
}
