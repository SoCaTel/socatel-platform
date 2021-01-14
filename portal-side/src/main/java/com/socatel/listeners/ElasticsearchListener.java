package com.socatel.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socatel.events.ElasticsearchEvent;
import com.socatel.utils.Constants;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component //TODO Uncomment for production
public class ElasticsearchListener implements ApplicationListener<ElasticsearchEvent> {

    @Autowired private RestHighLevelClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onApplicationEvent(ElasticsearchEvent event) {
        switch (event.getEsType()) {
            case CREATE:
                IndexRequest indexRequest = new IndexRequest(event.getIndex(), Constants.INDEX_TYPE, event.getId());
                try {
                    indexRequest.source(mapper.writeValueAsString(event.getSource()), XContentType.JSON);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                try {
                    client.index(indexRequest, RequestOptions.DEFAULT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case UPDATE:
                UpdateRequest updateRequest = new UpdateRequest(event.getIndex(), Constants.INDEX_TYPE, event.getId());
                try {
                    updateRequest.doc(mapper.writeValueAsString(event.getSource()), XContentType.JSON);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                try {
                    client.update(updateRequest, RequestOptions.DEFAULT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case DELETE:
                DeleteRequest deleteRequest = new DeleteRequest(event.getIndex(), Constants.INDEX_TYPE, event.getId());
                try {
                    client.delete(deleteRequest, RequestOptions.DEFAULT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
