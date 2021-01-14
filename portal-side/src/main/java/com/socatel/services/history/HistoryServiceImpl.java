package com.socatel.services.history;

import com.socatel.models.Group;
import com.socatel.models.History;
import com.socatel.models.Organisation;
import com.socatel.models.User;
import com.socatel.publishers.ElasticsearchPublisher;
import com.socatel.repositories.HistoryRepository;
import com.socatel.utils.Constants;
import com.socatel.utils.enums.ESType;
import com.socatel.utils.enums.HistoryTypeEnum;
import com.socatel.utils.enums.VisibleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class HistoryServiceImpl implements HistoryService {

    private HistoryRepository historyRepository;
    private ElasticsearchPublisher elasticsearchPublisher;

    @Autowired
    public HistoryServiceImpl(HistoryRepository historyRepository,
                              ElasticsearchPublisher elasticsearchPublisher) {
        this.historyRepository = historyRepository;
        this.elasticsearchPublisher = elasticsearchPublisher;
    }

    @Override
    public Page<History> getUserHistory(User user, int page, int pageSize) {
        return historyRepository.findByUserAndLevel(user, VisibleEnum.VISIBLE, PageRequest.of(page, pageSize, Constants.SORT_BY_TIMESTAMP_DESC));
    }

    @Override
    public void createHistory(User user, Organisation organisation, Group group, String text, HistoryTypeEnum type, VisibleEnum visible) {
        elasticsearchPublisher.publishHistory(historyRepository.save(new History(user, organisation, group, text, type, visible)), ESType.CREATE);
    }

    @Override
    public void delete(History history) {
        historyRepository.delete(history);
        elasticsearchPublisher.publishHistory(history, ESType.DELETE);
    }

    @Override
    public History findById(Integer id) {
        return historyRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteAllByUser(User user) {
        historyRepository.deleteAllByUser(user);
        // TODO ES delete all
    }

}
