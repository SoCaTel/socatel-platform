package com.socatel.services.service;

import com.socatel.models.Group;
import com.socatel.models.Organisation;
import com.socatel.models.Service;
import com.socatel.models.Theme;
import com.socatel.publishers.ElasticsearchPublisher;
import com.socatel.repositories.ServiceRepository;
import com.socatel.utils.enums.ESType;
import com.socatel.utils.enums.ServiceStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

    private ServiceRepository serviceRepository;
    private ElasticsearchPublisher elasticsearchPublisher;

    @Autowired
    public ServiceServiceImpl(ServiceRepository serviceRepository,
                              ElasticsearchPublisher elasticsearchPublisher) {
        this.serviceRepository = serviceRepository;
        this.elasticsearchPublisher = elasticsearchPublisher;
    }

    @Override
    public void registerService(Service service, Organisation organisation) {
        service.setOrganisation(organisation);
        elasticsearchPublisher.publishService(serviceRepository.save(service), ESType.CREATE);
    }

    @Override
    public List<Service> findAllByIds(List<Integer> ids) {
        return serviceRepository.findAllById(ids);
    }

    @Override
    public Service registerService(Service service, Organisation organisation, Group group, Set<Theme> themes) {
        service.setThemes(themes);
        service.setGroup(group);
        service.setOrganisation(organisation);
        service.setLanguage(group.getLanguage());
        service.setLocality(group.getLocality());
        service.setStatus(ServiceStatus.SUGGESTED);
        Service s = serviceRepository.save(service);
        elasticsearchPublisher.publishService(s, ESType.CREATE);
        return s;
    }

    @Override
    public List<Service> findByLocalityAndLanguage(Integer localityId, Integer parentLocalityId, String languageCode, String secondLanguageCode) {
        return serviceRepository.findServicesByLocalityAndLanguage(localityId, parentLocalityId, languageCode, secondLanguageCode);
    }

    @Override
    public List<Service> findByLanguage(String languageCode) {
        return serviceRepository.findServicesByLanguage(languageCode);
    }

    @Override
    public Service findById(Integer id) {
        return serviceRepository.findById(id).orElse(null);
    }

    @Override
    public Service findByGroup(Group group) {
        return serviceRepository.findByGroup(group);
    }

    @Override
    public void save(Service service) {
        boolean newService = service.getId() == 0;
        Service saved = serviceRepository.save(service);
        if (newService) elasticsearchPublisher.publishService(saved, ESType.CREATE);
        else elasticsearchPublisher.publishService(saved, ESType.UPDATE);
    }

    @Override
    public List<Service> findByOrganisationApproved(Organisation organisation) {
        return serviceRepository.findByOrganisationAndStatus(organisation, ServiceStatus.APPROVED);
    }

    @Override
    public List<Service> findByOrganisationSuggested(Organisation organisation) {
        return serviceRepository.findByOrganisationAndStatus(organisation, ServiceStatus.SUGGESTED);
    }

    @Override
    public List<Service> findByOrganisationRejected(Organisation organisation) {
        return serviceRepository.findByOrganisationAndStatus(organisation, ServiceStatus.REJECTED);
    }

    @Override
    public List<Service> findAllSuggested(Integer localityId, Integer parentLocalityId, String lang, String secondLang) {
        return serviceRepository.findAllByStatus(ServiceStatus.SUGGESTED, localityId, parentLocalityId, lang, secondLang); // TODO add sort?
    }
}
