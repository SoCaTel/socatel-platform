package com.socatel.rest_api.rdi.demographic;

public class Bucket {
    private Integer key;
    private Integer doc_count;

    public Bucket() {}

    public Bucket(Integer key, Integer doc_count) {
        this.key = key;
        this.doc_count = doc_count;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getDoc_count() {
        return doc_count;
    }

    public void setDoc_count(Integer doc_count) {
        this.doc_count = doc_count;
    }

    @Override
    public String toString() {
        return "Bucket{" +
                "key=" + key +
                ", doc_count=" + doc_count +
                '}';
    }
}
