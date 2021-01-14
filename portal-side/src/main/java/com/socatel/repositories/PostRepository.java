package com.socatel.repositories;

import com.socatel.models.Feedback;
import com.socatel.models.Post;
import com.socatel.utils.enums.PostPhaseEnum;
import com.socatel.utils.enums.PostTypeEnum;
import com.socatel.utils.enums.VisibleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface PostRepository extends JpaRepository<Post,Integer> {
    List<Post> findByGroup_IdAndPostPhaseAndPostParentIsNullAndVisibleAndFeedbackIsNull(int group_id, PostPhaseEnum postPhase, VisibleEnum visible, Sort sort);
    List<Post> findByPostPhaseAndPostParentIsNullAndVisibleAndFeedbackIsNull(PostPhaseEnum postPhase, VisibleEnum visible, Sort sort);
    Page<Post> findByAuthor_Id(Integer userId, Pageable pageable);
    List<Post> findByGroup_IdAndPostTypeAndVisible(int group_id, PostTypeEnum postType, VisibleEnum visible, Sort sort);
    List<Post> findTop1ByGroup_IdAndPostTypeAndVisible(int group_id, PostTypeEnum postType, VisibleEnum visible, Sort sort);
    Long countByGroup_IdAndPostType(int group_id, PostTypeEnum postType);
    Long countByPostType(PostTypeEnum postType);
    Long countByGroupIdAndTimestampAfter(int group_id, Timestamp timestamp);
    @Query("select count(p) from Post p where p.group.id = :group_id and p.author.organisation.id = :organisation_id and p.postPhase = :phase")
    Long countOrganisationApplied(int group_id, int organisation_id, PostPhaseEnum phase);
    List<Post> findByFeedbackAndVisible(Feedback feedback, VisibleEnum visible, Sort sort);
}
