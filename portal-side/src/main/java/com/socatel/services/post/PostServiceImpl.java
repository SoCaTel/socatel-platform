package com.socatel.services.post;

import com.socatel.models.Feedback;
import com.socatel.models.Group;
import com.socatel.models.Organisation;
import com.socatel.models.Post;
import com.socatel.publishers.ElasticsearchPublisher;
import com.socatel.repositories.PostRepository;
import com.socatel.services.history.HistoryService;
import com.socatel.utils.Constants;
import com.socatel.utils.enums.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private ElasticsearchPublisher elasticsearchPublisher;
    private HistoryService historyService;

    @Autowired
    public PostServiceImpl(PostRepository postRepository,
                           ElasticsearchPublisher elasticsearchPublisher,
                           HistoryService historyService){
        this.postRepository = postRepository;
        this.elasticsearchPublisher = elasticsearchPublisher;
        this.historyService = historyService;
    }

    @Override
    public List<Post> findPostsByGroupIdAndPostPhase(Integer groupId, PostPhaseEnum postPhase) { return postRepository.findByGroup_IdAndPostPhaseAndPostParentIsNullAndVisibleAndFeedbackIsNull(groupId, postPhase, VisibleEnum.VISIBLE, Constants.SORT_BY_PIN.and(Constants.SORT_BY_TIMESTAMP_ASC)); }

    @Override
    public List<Post> findOrgProposals() { return postRepository.findByPostPhaseAndPostParentIsNullAndVisibleAndFeedbackIsNull(PostPhaseEnum.ORG_APPLY, VisibleEnum.PENDING, Constants.SORT_BY_PIN.and(Constants.SORT_BY_TIMESTAMP_ASC)); }

    @Override
    public Page<Post> findPostsByAuthorId(Integer userId, int page, int pageSize) { return postRepository.findByAuthor_Id(userId, PageRequest.of(page, pageSize, Constants.SORT_BY_TIMESTAMP_DESC)); }

    @Override
    public List<Post> findAllGroupIdeas(Integer groupId) {
        return postRepository.findByGroup_IdAndPostTypeAndVisible(groupId, PostTypeEnum.IDEA, VisibleEnum.VISIBLE, Constants.SORT_BY_UPVOTES_DESC.and(Constants.SORT_BY_DOWNVOTES_ASC));
    }

    @Override
    public List<Post> findGroupBestIdea(Integer groupId) {
        return postRepository.findTop1ByGroup_IdAndPostTypeAndVisible(groupId, PostTypeEnum.IDEA, VisibleEnum.VISIBLE, Constants.SORT_BY_UPVOTES_DESC.and(Constants.SORT_BY_DOWNVOTES_ASC));
    }

    @Override
    public List<Post> findPopularPosts(Integer groupId) {
        return postRepository.findByGroup_IdAndPostPhaseAndPostParentIsNullAndVisibleAndFeedbackIsNull(groupId, PostPhaseEnum.IDEATION, VisibleEnum.VISIBLE, Constants.SORT_BY_UPVOTES_DESC.and(Constants.SORT_BY_DOWNVOTES_ASC));
    }

    @Override
    public Long countGroupComments(Integer groupId) {
        return postRepository.countByGroup_IdAndPostType(groupId, PostTypeEnum.COMMENT);
    }

    @Override
    public Long countGroupIdeas(Integer groupId) {
        return postRepository.countByGroup_IdAndPostType(groupId, PostTypeEnum.IDEA);
    }

    @Override
    public Boolean hasOrganisationNotAppliedToIdea(Group group, Organisation organisation) {
        return postRepository.countOrganisationApplied(group.getId(), organisation.getId(), PostPhaseEnum.ORG_APPLY) == 0;
    }

    @Override
    public void mask(Integer id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post != null) {
            post.setVisible(VisibleEnum.HIDDEN);
            postRepository.save(post);
            elasticsearchPublisher.publishPost(post, ESType.UPDATE);
        }
    }

    @Override
    public void unmask(Integer id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post != null) {
            post.setVisible(VisibleEnum.VISIBLE);
            postRepository.save(post);
            elasticsearchPublisher.publishPost(post, ESType.UPDATE);
        }
    }

    @Override
    public Long countByGroupIdAndTimestampAfter(Integer groupId, Timestamp timestamp) {
        return postRepository.countByGroupIdAndTimestampAfter(groupId, timestamp);
    }

    @Override
    public Post save(Post post) {
        boolean newPost = post.getId() == 0;
        Post saved = postRepository.save(post);
        if (newPost) {
            historyService.createHistory(post.getAuthor(), null, post.getGroup(), "history.posted_group", HistoryTypeEnum.POSTED_GROUP, VisibleEnum.VISIBLE);
            elasticsearchPublisher.publishPost(saved, ESType.CREATE);
        }
        else elasticsearchPublisher.publishPost(saved, ESType.UPDATE);
        return saved;
    }

    @Override
    public List<Post> findByFeedbackAndVisible(Feedback feedback, VisibleEnum visible) {
        return postRepository.findByFeedbackAndVisible(feedback, visible, Constants.SORT_BY_PIN.and(Constants.SORT_BY_TIMESTAMP_ASC));
    }

    @Override
    public Post findById(Integer postId) {
        return postRepository.findById(postId).orElse(null);
    }

    @Override
    public void delete(Post post) {
        postRepository.delete(post);
        elasticsearchPublisher.publishPost(post, ESType.DELETE);
    }

    @Override
    public Long countAllIdeas() {
        return postRepository.countByPostType(PostTypeEnum.IDEA);
    }

}
