package com.socatel.services.document;

import com.socatel.exceptions.FileStorageException;
import com.socatel.exceptions.MyFileNotFoundException;
import com.socatel.models.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;

public interface DocumentService {
    Document storeFile(MultipartFile file, Integer groupId, Integer postId, Integer messageId, Integer feedbackId, Integer propositionId, Integer serviceId, Locale locale) throws FileStorageException;

    void removeFile(Integer fileId);

    Document getFile(Integer id, Locale locale) throws MyFileNotFoundException;
    List<Document> findAllByGroup(Group group);

    List<Document> findAllByMessage(Message message);

    List<Document> findAllByFeedback(Feedback feedback);

    List<Document> findAllByPost(Post post);

    List<Document> findAllByProposition(Proposition proposition);

    List<Document> findAllByService(Service service);
}
