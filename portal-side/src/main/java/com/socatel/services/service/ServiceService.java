package com.socatel.services.service;

import com.socatel.models.Group;
import com.socatel.models.Organisation;
import com.socatel.models.Service;
import com.socatel.models.Theme;

import java.util.List;
import java.util.Set;

public interface ServiceService {
    void registerService(Service service, Organisation organisation);
    List<Service> findByOrganisationApproved(Organisation organisation);
    List<Service> findByOrganisationSuggested(Organisation organisation);
    List<Service> findByOrganisationRejected(Organisation organisation);
    List<Service> findAllSuggested(Integer localityId, Integer parentLocalityId, String lang, String secondLang);

    List<Service> findAllByIds(List<Integer> ids);

    Service registerService(Service service, Organisation organisation, Group group, Set<Theme> themes);

    List<Service> findByLocalityAndLanguage(Integer localityId, Integer parentLocalityId, String languageCode, String secondLanguageCode);

    List<Service> findByLanguage(String languageCode);

    Service findById(Integer id);

    Service findByGroup(Group group);

    void save(Service service);
}
