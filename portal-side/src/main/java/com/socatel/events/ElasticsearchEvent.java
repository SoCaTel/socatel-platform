package com.socatel.events;

import com.socatel.utils.enums.ESType;
import org.springframework.context.ApplicationEvent;

public class ElasticsearchEvent<T> extends ApplicationEvent {

    private T source;
    private ESType esType;
    private String index;
    private String id;

    public ElasticsearchEvent(T source, ESType esType, String index, String id) {
        super(source);
        this.source = source;
        this.esType = esType;
        this.index = index;
        this.id = id;
    }

    public ElasticsearchEvent(T source, ESType esType, String index, Integer id) {
        super(source);
        this.source = source;
        this.esType = esType;
        this.index = index;
        this.id = String.valueOf(id);
    }

    @Override
    public T getSource() {
        return source;
    }

    public void setSource(T source) {
        this.source = source;
    }

    public ESType getEsType() {
        return esType;
    }

    public void setEsType(ESType esType) {
        this.esType = esType;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
