package com.socatel.services.post;

import com.socatel.models.Feedback;
import com.socatel.models.Group;
import com.socatel.models.Organisation;
import com.socatel.models.Post;
import com.socatel.utils.enums.PostPhaseEnum;
import com.socatel.utils.enums.VisibleEnum;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.util.List;

public interface PostService {
    List<Post> findPostsByGroupIdAndPostPhase(Integer groupId, PostPhaseEnum postPhase);

    List<Post> findOrgProposals();

    Page<Post> findPostsByAuthorId(Integer userId, int page, int pageSize);
    List<Post> findAllGroupIdeas(Integer groupId);

    List<Post> findGroupBestIdea(Integer groupId);

    List<Post> findPopularPosts(Integer groupId);
    Long countGroupComments(Integer groupId);
    Long countGroupIdeas(Integer groupId);
    Long countByGroupIdAndTimestampAfter(Integer groupId, Timestamp timestamp);
    Post save(Post post);

    List<Post> findByFeedbackAndVisible(Feedback feedback, VisibleEnum visible);

    Post findById(Integer postId);
    void delete(Post post);
    Long countAllIdeas();

    Boolean hasOrganisationNotAppliedToIdea(Group group, Organisation organisation);

    void mask(Integer id);
    void unmask(Integer id);
}
