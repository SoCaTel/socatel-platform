package com.socatel.services.document;

import com.socatel.components.GCPStorage;
import com.socatel.exceptions.FileStorageException;
import com.socatel.exceptions.MyFileNotFoundException;
import com.socatel.models.*;
import com.socatel.repositories.DocumentRepository;
import com.socatel.services.chat.ChatService;
import com.socatel.services.feedback.FeedbackService;
import com.socatel.services.group.GroupService;
import com.socatel.services.post.PostService;
import com.socatel.services.proposition.PropositionService;
import com.socatel.services.service.ServiceService;
import com.socatel.utils.enums.PostPhaseEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * https://www.callicoder.com/spring-boot-file-upload-download-jpa-hibernate-mysql-database-example/
 */

@Service
public class DocumentServiceImpl implements DocumentService{

    private DocumentRepository documentRepository;
    @Autowired private MessageSource messageSource;
    @Autowired private GroupService groupService;
    @Autowired private PostService postService;
    @Autowired private ChatService chatService;
    @Autowired private GCPStorage gcpStorage;
    @Autowired private FeedbackService feedbackService;
    @Autowired private PropositionService propositionService;
    @Autowired private ServiceService serviceService;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public Document storeFile(MultipartFile file, Integer groupId, Integer postId, Integer messageId, Integer feedbackId, Integer propositionId, Integer serviceId, Locale locale) throws FileStorageException {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException(messageSource.getMessage("document.error.bad_path", null, locale) + " " + fileName);
            }

            Document dbFile = createDocument(fileName, file.getContentType(), file.getBytes(), groupId, postId, messageId, feedbackId, propositionId, serviceId);
            dbFile = documentRepository.save(dbFile);
            gcpStorage.storeFile(dbFile.getId(), fileName, file.getBytes(), file.getContentType());

            return dbFile;
        } catch (IOException ex) {
            throw new FileStorageException(messageSource.getMessage("document.error.unexpected", null, locale) + " " + fileName, ex);
        }
    }

    // TODO needed?
    private byte[] cropImage(byte[] image) throws IOException{
        // Get a BufferedImage object from a byte array
        InputStream in = new ByteArrayInputStream(image);
        BufferedImage originalImage = ImageIO.read(in);

        // Get image dimensions
        int height = originalImage.getHeight();
        int width = originalImage.getWidth();

        // Coordinates of the image's middle
        int xc = width / 2;
        int yc = height / 2;

        // Crop
        /*BufferedImage croppedImage = originalImage.getSubimage(
                xc - (squareSize / 2), // x coordinate of the upper-left corner
                yc - (squareSize / 2), // y coordinate of the upper-left corner
                squareSize,            // width
                squareSize             // height
        );*/
        return new byte[3];
    }

    @Override
    public void removeFile(Integer fileId) {
        documentRepository.deleteById(fileId);
    }

    private Document createDocument(String fileName, String contentType, byte[] data, Integer groupId, Integer postId, Integer messageId, Integer feedbackId, Integer propositionId, Integer serviceId) throws IOException {
        if (postId != null) {
            Post post = postService.findById(postId);
            if (post != null) {
                if (groupId != null && post.getPostPhase().equals(PostPhaseEnum.IDEATION)) {
                    Group group = groupService.findById(groupId);
                    return new Document(fileName, contentType, data, group, post);
                } else {
                    return new Document(fileName, contentType, data, post);
                }
            }
            else throw new IOException();
        } else if (groupId != null) {
            Group group = groupService.findById(groupId);
            if (group != null)
                return new Document(fileName, contentType, data, group);
            else throw new IOException();
        } else if (messageId != null) {
            Message message = chatService.findMessageById(messageId);
            if (message != null)
                return new Document(fileName, contentType, data, message);
            else throw new IOException();
        } else if (feedbackId != null) {
            Feedback feedback = feedbackService.findFeedbackById(feedbackId);
            if (feedback != null)
                return new Document(fileName, contentType, data, feedback.getGroup(), feedback);
            else throw new IOException();
        } else if (propositionId != null) {
            Proposition proposition = propositionService.findById(propositionId);
            if (proposition != null)
                return new Document(fileName, contentType, data, proposition);
        } else if (serviceId != null) {
            com.socatel.models.Service service = serviceService.findById(serviceId);
            if (service != null)
                return new Document(fileName, contentType, data, service);
        }
        return new Document(fileName, contentType, data);
    }

    @Override
    public Document getFile(Integer id, Locale locale) throws MyFileNotFoundException {
        return documentRepository.findById(id).orElseThrow(() -> new MyFileNotFoundException(messageSource.getMessage("document.error.not_found", null, locale) + " " + id));
    }

    @Override
    public List<Document> findAllByGroup(Group group) {
        return documentRepository.findAllByGroup(group);
    }

    @Override
    public List<Document> findAllByMessage(Message message) {
        return documentRepository.findAllByMessage(message);
    }

    @Override
    public List<Document> findAllByFeedback(Feedback feedback) {
        return documentRepository.findAllByFeedback(feedback);
    }

    @Override
    public List<Document> findAllByPost(Post post) {
        return documentRepository.findAllByPost(post);
    }

    @Override
    public List<Document> findAllByProposition(Proposition proposition) {
        return documentRepository.findAllByProposition(proposition);
    }

    @Override
    public List<Document> findAllByService(com.socatel.models.Service service) {
        return documentRepository.findAllByService(service);
    }
}
