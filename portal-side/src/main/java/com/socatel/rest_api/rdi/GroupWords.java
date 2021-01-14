package com.socatel.rest_api.rdi;

public class GroupWords {
    private String word;
    private Integer total_occurrences;

    public GroupWords() {}

    public GroupWords(String word, Integer total_occurrences) {
        this.word = word;
        this.total_occurrences = total_occurrences;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getTotal_occurrences() {
        return total_occurrences;
    }

    public void setTotal_occurrences(Integer total_occurrences) {
        this.total_occurrences = total_occurrences;
    }

    @Override
    public String toString() {
        return "GroupWords{" +
                "word='" + word + '\'' +
                ", total_occurrences=" + total_occurrences +
                '}';
    }
}
