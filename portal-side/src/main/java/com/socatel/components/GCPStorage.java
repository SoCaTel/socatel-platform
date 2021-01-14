package com.socatel.components;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.StorageOptions;
import com.socatel.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Store files in Google Cloud Platform Storage
 */
@Component
public class GCPStorage {
    private Bucket bucket;

    /**
     * Get bucket to perform operations
     */
    @Autowired
    public GCPStorage() {
        // Local
        //localBucketConfiguration();
        // Production
        productionBucketConfiguration();
    }

    /**
     * Local bucket configuration
     */
    private void localBucketConfiguration() {
        try {
            StorageOptions storageOptions = StorageOptions.newBuilder()
                    .setProjectId(Constants.CLOUD_GCP_PROJECT_ID)
                    .setCredentials(GoogleCredentials.fromStream(new
                            FileInputStream(Constants.CLOUD_GCP_CREDENTIALS_PATH))).build();
            this.bucket = storageOptions.getService().get(Constants.CLOUD_GCP_STORAGE_BUCKET_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Production bucket configuration
     */
    private void productionBucketConfiguration() {
        this.bucket = StorageOptions.getDefaultInstance().getService().get(Constants.CLOUD_GCP_STORAGE_BUCKET_NAME);
    }

    /**
     * Store file in bucket
     * @param id document id
     * @param fileName document name
     * @param data file content
     * @param contentType content type
     */
    public void storeFile(Integer id, String fileName, byte[] data, String contentType) {
        bucket.create(generateBlobName(id, fileName), data, contentType);
    }

    /**
     * Get file content from bucket
     * @param id document id
     * @param fileName document name
     * @return file content
     */
    public byte[] getFile(Integer id, String fileName) {
        return bucket.get(generateBlobName(id, fileName)).getContent();
    }

    /**
     * Create blob name from id and file name
     * @param id document id
     * @param fileName document name
     * @return blobName
     */
    private String generateBlobName(Integer id, String fileName) {
        return id + "-" + fileName;
    }
}
