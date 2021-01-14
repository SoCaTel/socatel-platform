package com.socatel.services.proposition;

import com.socatel.models.Feedback;
import com.socatel.models.Proposition;
import com.socatel.publishers.ElasticsearchPublisher;
import com.socatel.repositories.PropositionRepository;
import com.socatel.utils.Constants;
import com.socatel.utils.enums.ESType;
import com.socatel.utils.enums.PropositionTypeEnum;
import com.socatel.utils.enums.VisibleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropositionServiceImpl implements PropositionService {

    private PropositionRepository propositionRepository;
    private ElasticsearchPublisher elasticsearchPublisher;

    @Autowired
    public PropositionServiceImpl(PropositionRepository propositionRepository,
                                  ElasticsearchPublisher elasticsearchPublisher) {
        this.propositionRepository = propositionRepository;
        this.elasticsearchPublisher = elasticsearchPublisher;
    }

    @Override
    public List<Proposition> findBenefits(Integer postId) {
        return propositionRepository.findByPostIdAndTypeAndVisible(postId, PropositionTypeEnum.BENEFIT, VisibleEnum.VISIBLE, Constants.SORT_BY_PIN.and(Constants.SORT_BY_TIMESTAMP_ASC));
    }

    @Override
    public List<Proposition> findPros(Integer postId) {
        return propositionRepository.findByPostIdAndTypeAndVisible(postId, PropositionTypeEnum.PRO, VisibleEnum.VISIBLE, Constants.SORT_BY_PIN.and(Constants.SORT_BY_TIMESTAMP_ASC));
    }

    @Override
    public List<Proposition> findContras(Integer postId) {
        return propositionRepository.findByPostIdAndTypeAndVisible(postId, PropositionTypeEnum.CONTRA, VisibleEnum.VISIBLE, Constants.SORT_BY_PIN.and(Constants.SORT_BY_TIMESTAMP_ASC));
    }

    @Override
    public List<Proposition> findOthers(Integer postId) {
        return propositionRepository.findByPostIdAndTypeAndVisible(postId, PropositionTypeEnum.OTHER, VisibleEnum.VISIBLE, Constants.SORT_BY_PIN.and(Constants.SORT_BY_TIMESTAMP_ASC));
    }

    @Override
    public List<Proposition> findByFeedbackAndVisible(Feedback feedback, VisibleEnum visible) {
        return propositionRepository.findByFeedbackAndVisible(feedback, visible, Constants.SORT_BY_PIN.and(Constants.SORT_BY_TIMESTAMP_ASC));
    }

    @Override
    public void mask(Integer id) {
        Proposition proposition = propositionRepository.findById(id).orElse(null);
        if (proposition != null) {
            proposition.setVisible(VisibleEnum.HIDDEN);
            propositionRepository.save(proposition);
            elasticsearchPublisher.publishProposition(proposition, ESType.UPDATE);
        }
    }

    @Override
    public void unmask(Integer id) {
        Proposition proposition = propositionRepository.findById(id).orElse(null);
        if (proposition != null) {
            proposition.setVisible(VisibleEnum.VISIBLE);
            propositionRepository.save(proposition);
            elasticsearchPublisher.publishProposition(proposition, ESType.UPDATE);
        }
    }

    @Override
    public Proposition save(Proposition proposition) {
        boolean newProposition = proposition.getId() == 0;
        Proposition saved = propositionRepository.save(proposition);
        if (newProposition) elasticsearchPublisher.publishProposition(saved, ESType.CREATE);
        else elasticsearchPublisher.publishProposition(saved, ESType.UPDATE);
        return saved;
    }

    @Override
    public Proposition findById(Integer id) {
        return propositionRepository.findById(id).orElse(null);
    }
}
