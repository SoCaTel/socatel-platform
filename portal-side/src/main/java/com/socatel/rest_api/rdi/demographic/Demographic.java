package com.socatel.rest_api.rdi.demographic;

import java.util.List;

public class Demographic {
    private Integer doc_count_error_upper_bound;
    private Integer sum_other_doc_count;
    private List<Bucket> buckets;

    public Demographic() {}

    public Demographic(Integer doc_count_error_upper_bound, Integer sum_other_doc_count, List<Bucket> buckets) {
        this.doc_count_error_upper_bound = doc_count_error_upper_bound;
        this.sum_other_doc_count = sum_other_doc_count;
        this.buckets = buckets;
    }

    public Integer getDoc_count_error_upper_bound() {
        return doc_count_error_upper_bound;
    }

    public void setDoc_count_error_upper_bound(Integer doc_count_error_upper_bound) {
        this.doc_count_error_upper_bound = doc_count_error_upper_bound;
    }

    public Integer getSum_other_doc_count() {
        return sum_other_doc_count;
    }

    public void setSum_other_doc_count(Integer sum_other_doc_count) {
        this.sum_other_doc_count = sum_other_doc_count;
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<Bucket> buckets) {
        this.buckets = buckets;
    }

    @Override
    public String toString() {
        return "Demographic{" +
                "doc_count_error_upper_bound=" + doc_count_error_upper_bound +
                ", sum_other_doc_count=" + sum_other_doc_count +
                ", buckets=" + buckets +
                '}';
    }
}
