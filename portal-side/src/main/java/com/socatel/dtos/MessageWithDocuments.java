package com.socatel.dtos;

import com.socatel.models.Document;
import com.socatel.models.Message;

import java.util.LinkedList;
import java.util.List;

public class MessageWithDocuments {
    private Message message;
    private List<Document> documents;

    public MessageWithDocuments(Message message) {
        this.message = message;
        documents = new LinkedList<>();
    }

    public MessageWithDocuments(Message message, List<Document> documents) {
        this.message = message;
        this.documents = documents;
    }

    public void addDocument(Document d) {
        documents.add(d);
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}