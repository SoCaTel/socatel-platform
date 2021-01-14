package com.socatel.models;

import com.socatel.components.Methods;
import com.socatel.models.relationships.UserPostVote;
import com.socatel.utils.Constants;
import com.socatel.utils.enums.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "so_post")
public class Post implements Comparable<Post>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int id;

    @Column(name = "post_text")
    private String text;

    @Column(name = "post_timestamp")
    private Timestamp timestamp;

    @Column(name = "post_upvotes")
    private int upvotes;

    @Column(name = "post_downvotes")
    private int downvotes;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "post_type")
    private PostTypeEnum postType;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "post_phase")
    private PostPhaseEnum postPhase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_parent_id")
    private Post postParent;

    @ManyToOne
    @JoinColumn(name="feedback_id")
    private Feedback feedback;

    @Column(name = "post_visible")
    @Enumerated(EnumType.ORDINAL)
    private VisibleEnum visible;

    @Column(name = "post_pin")
    @Enumerated(EnumType.ORDINAL)
    private PinEnum pin;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "postParent")
    private Set<Post> replies;

    @ManyToOne
    @JoinColumn(name="author_user_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="organisation_id")
    private Organisation organisation;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private Set<UserPostVote> usersLikes;

    @Transient
    private List<Document> documents;

    public String fancyTimestamp() {
        return Methods.formatDate(new Date(timestamp.getTime()));
    }

    public boolean canBeModifiedBy(User user) {
        return user.getUsername().equals(author.getUsername()) || user.isModerator() || user.isFacilitator();
    }

    public void downVote(boolean add) {
        if (add) downvotes++;
        else downvotes--;
    }

    public void upVote(boolean add) {
        if (add) upvotes++;
        else upvotes--;
    }

    public List<User> getUsersUpvote() {
        return usersLikes.stream().filter(v->v.getVoteType().equals(VoteTypeEnum.UP_VOTED)).map(UserPostVote::getUser).collect(Collectors.toList());
    }

    public List<User> getUsersDownvote() {
        return usersLikes.stream().filter(v->v.getVoteType().equals(VoteTypeEnum.DOWN_VOTED)).map(UserPostVote::getUser).collect(Collectors.toList());
    }

    public boolean hasUpVoted(String username) {
        for (UserPostVote vote : usersLikes)
            if (vote.getVoteType().equals(VoteTypeEnum.UP_VOTED) && vote.getUser().getUsername().equals(username))
                return true;
        return false;
    }

    public boolean hasDownVoted(String username) {
        for (UserPostVote vote : usersLikes)
            if (vote.getVoteType().equals(VoteTypeEnum.DOWN_VOTED) && vote.getUser().getUsername().equals(username))
                return true;
        return false;
    }

    public boolean isReply() {
        return postParent != null;
    }

    public Page<Post> repliesPage() {
        List<Post> replies = new ArrayList<>(this.replies);
        replies = replies.stream().filter(post -> post.getVisible().equals(VisibleEnum.VISIBLE)).collect(Collectors.toList());
        replies.sort((p1, p2) -> {
            int pinned = p2.getPin().compareTo(p1.getPin());
            if (pinned != 0) return pinned;
            return p1.getTimestamp().compareTo(p2.getTimestamp());
        });
        return new PageImpl<>(replies, PageRequest.of(0, Constants.TOPIC_REPLIES_PAGE_SIZE, Constants.SORT_BY_PIN.and(Constants.SORT_BY_TIMESTAMP_ASC)), replies.size());
    }

    public Post() {
        upvotes = 0;
        downvotes = 0;
        visible = VisibleEnum.VISIBLE;
        pin = PinEnum.NOT_PINNED;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public PostTypeEnum getPostType() {
        return postType;
    }

    public void setPostType(PostTypeEnum postType) {
        this.postType = postType;
    }

    public PostPhaseEnum getPostPhase() {
        return postPhase;
    }

    public void setPostPhase(PostPhaseEnum postPhase) {
        this.postPhase = postPhase;
    }

    public Post getPostParent() {
        return postParent;
    }

    public void setPostParent(Post postParent) {
        this.postParent = postParent;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Set<Post> getReplies() {
        return replies;
    }

    public void setReplies(Set<Post> replies) {
        this.replies = replies;
    }

    public VisibleEnum getVisible() {
        return visible;
    }

    public void setVisible(VisibleEnum visible) {
        this.visible = visible;
    }

    public PinEnum getPin() {
        return pin;
    }

    public void setPin(PinEnum pin) {
        this.pin = pin;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    @Override
    public int compareTo(Post o) {
        return timestamp.compareTo(o.getTimestamp());
    }

    public Set<UserPostVote> getUsersLikes() {
        return usersLikes;
    }

    public void setUsersLikes(Set<UserPostVote> usersLikes) {
        this.usersLikes = usersLikes;
    }
}
