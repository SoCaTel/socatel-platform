package com.socatel.services.history;

import com.socatel.models.Group;
import com.socatel.models.History;
import com.socatel.models.Organisation;
import com.socatel.models.User;
import com.socatel.utils.enums.HistoryTypeEnum;
import com.socatel.utils.enums.VisibleEnum;
import org.springframework.data.domain.Page;

public interface HistoryService {
    Page<History> getUserHistory(User user, int page, int pageSize);
    void createHistory(User user, Organisation organisation, Group group, String text, HistoryTypeEnum type, VisibleEnum visible);
    void delete(History history);
    History findById(Integer id);
    void deleteAllByUser(User user);
}
