package com.socatel.rest_api.rec;

public class SimilarGroup {
    private Integer similar_group_id;
    private Integer rank;

    public SimilarGroup() {}

    public Integer getSimilar_group_id() {
        return similar_group_id;
    }

    public void setSimilar_group_id(Integer similar_group_id) {
        this.similar_group_id = similar_group_id;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "SimilarGroup{" +
                "similar_group_id=" + similar_group_id +
                ", rank=" + rank +
                '}';
    }
}
