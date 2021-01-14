package com.socatel.repositories;

import com.socatel.models.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
    List<Document> findAllByGroup(Group group);
    List<Document> findAllByMessage(Message message);
    List<Document> findAllByFeedback(Feedback feedback);
    List<Document> findAllByPost(Post post);
    List<Document> findAllByProposition(Proposition proposition);
    List<Document> findAllByService(Service service);
}
