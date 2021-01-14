package com.socatel.models.relationships;

import com.socatel.models.Post;
import com.socatel.models.User;
import com.socatel.models.keys.UserPostKey;
import com.socatel.utils.enums.VoteTypeEnum;

import javax.persistence.*;

@Entity
@Table(name = "so_user_post_vote")
@IdClass(UserPostKey.class)
public class UserPostVote {
    @Id
    @Column(name = "post_id")
    private int postId;

    @Id
    @Column(name = "user_id")
    private int userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user_id")
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("post_id")
    @JoinColumn(name="post_id")
    private Post post;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "user_post_vote_type")
    private VoteTypeEnum voteType;

    public UserPostVote() {}

    public UserPostVote(User user, Post post) {
        this.user = user;
        this.post = post;
        userId = user.getId();
        postId = post.getId();
        voteType = VoteTypeEnum.NO_VOTED;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public VoteTypeEnum getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteTypeEnum voteType) {
        this.voteType = voteType;
    }
}
