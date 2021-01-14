package com.socatel.controllers_rest;

import com.socatel.components.GCPStorage;
import com.socatel.models.Document;
import com.socatel.services.document.DocumentService;
import com.socatel.utils.UploadFileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

@RestController
public class DocumentRestController {

    @Autowired private DocumentService documentService;
    @Autowired private GCPStorage gcpStorage;

    private String getDownloadUri(Integer id) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(String.valueOf(id))
                .toUriString();
    }

    @PostMapping("/uploadFile/group/{group_id}")
    public UploadFileResponse uploadFileGroup(@RequestParam("file") MultipartFile file,
                                              @PathVariable("group_id") Integer groupId, Locale locale) {
        return uploadFile(file, groupId, null, null, null, null, locale);
    }

    @PostMapping("/uploadFile/group/{group_id}/{post_id}")
    public UploadFileResponse uploadFilePost(@RequestParam("file") MultipartFile file,
                                             @PathVariable("group_id") Integer groupId,
                                             @PathVariable("post_id") Integer postId, Locale locale) {
        return uploadFile(file, groupId, postId, null, null, null, locale);
    }

    @PostMapping("/uploadFile/message/{message_id}")
    public UploadFileResponse uploadFileMessage(@RequestParam("file") MultipartFile file,
                                                @PathVariable("message_id") Integer messageId, Locale locale) {
        return uploadFile(file, null, null, messageId, null, null, locale);
    }

    /*@PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, Locale locale) {
        return Arrays.stream(files)
                .map(file -> uploadFile(file, locale))
                .collect(Collectors.toList());
    }*/

    private UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file,
                                          Integer groupId, Integer postId, Integer messageId, Integer feedbackId, Integer propositionId, Locale locale) {
        Document document = documentService.storeFile(file, groupId, postId, messageId, feedbackId, propositionId, null, locale);
        String fileDownloadUri = getDownloadUri(document.getId());
        return new UploadFileResponse(document.getName(), fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @GetMapping("/downloadFile/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer fileId, Locale locale) {
        Document document = documentService.getFile(fileId, locale);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + "\"")
                .body(new ByteArrayResource(gcpStorage.getFile(document.getId(), document.getName())));
    }

    @GetMapping("/getFile/{fileId}")
    public ResponseEntity<Resource> getFile(@PathVariable Integer fileId, Locale locale) {
        Document document = documentService.getFile(fileId, locale);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + document.getName() + "\"")
                .body(new ByteArrayResource(gcpStorage.getFile(document.getId(), document.getName())));
    }

    @PostMapping("/removeFile/{fileId}")
    public ResponseEntity removeFile(@PathVariable Integer fileId) {
        documentService.removeFile(fileId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/show-image/{id}")
    public void showImage(@PathVariable Integer id, HttpServletResponse response, Locale locale) throws IOException {
        InputStream is;
        Document document = documentService.getFile(id, locale);
        response.setContentType(document.getType());
        if (document.getId() <= 16)
            is = new ClassPathResource("static/images/demo/" + document.getName()).getInputStream();
        else
            is = new ByteArrayInputStream(gcpStorage.getFile(document.getId(), document.getName()));
        StreamUtils.copy(is, response.getOutputStream());
        // ADD <img th:src=@{/show-image/{user.document.id}}>
    }

}