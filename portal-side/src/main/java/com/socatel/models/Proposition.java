package com.socatel.models;

import com.socatel.models.relationships.PropositionUserVote;
import com.socatel.utils.enums.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "so_proposition")
public class Proposition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proposition_id")
    private int id;

    @Column(name = "proposition_text")
    private String text;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "proposition_type")
    private PropositionTypeEnum type;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "proposition_hat")
    private PropositionHatEnum hat;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name="feedback_id")
    private Feedback feedback;

    @Column(name = "proposition_upvotes")
    private Integer upvotes;

    @Column(name = "proposition_downvotes")
    private Integer downvotes;

    @Column(name = "proposition_timestamp")
    private Timestamp timestamp;

    @Column(name = "proposition_visible")
    @Enumerated(EnumType.ORDINAL)
    private VisibleEnum visible;

    @Column(name = "proposition_pin")
    @Enumerated(EnumType.ORDINAL)
    private PinEnum pin;

    @OneToMany(mappedBy = "proposition", fetch = FetchType.LAZY)
    private Set<PropositionUserVote> usersLikes;

    @Transient
    private List<Document> documents;

    public Proposition() {
        upvotes = 0;
        downvotes = 0;
        visible = VisibleEnum.VISIBLE;
        pin = PinEnum.NOT_PINNED;
    }

    public Proposition(String text, PropositionTypeEnum type, Post solution, User user) {
        this.text = text;
        this.type = type;
        this.post = solution;
        this.user = user;
        upvotes = 0;
        downvotes = 0;
        visible = VisibleEnum.VISIBLE;
        pin = PinEnum.NOT_PINNED;
    }

    public Proposition(String text, Feedback feedback, User user) {
        this.text = text;
        this.feedback = feedback;
        this.user = user;
        upvotes = 0;
        downvotes = 0;
        visible = VisibleEnum.VISIBLE;
        pin = PinEnum.NOT_PINNED;
    }

    public void downVote(boolean add) {
        if (add) downvotes++;
        else downvotes--;
    }

    public void upVote(boolean add) {
        if (add) upvotes++;
        else upvotes--;
    }

    public boolean hasUpVoted(String username) {
        for (PropositionUserVote vote : usersLikes)
            if (vote.getVoteType().equals(VoteTypeEnum.UP_VOTED) && vote.getUser().getUsername().equals(username))
                return true;
        return false;
    }

    public boolean hasDownVoted(String username) {
        for (PropositionUserVote vote : usersLikes)
            if (vote.getVoteType().equals(VoteTypeEnum.DOWN_VOTED) && vote.getUser().getUsername().equals(username))
                return true;
        return false;
    }

    public List<User> getUsersUpvote() {
        return usersLikes.stream().filter(v->v.getVoteType().equals(VoteTypeEnum.UP_VOTED)).map(PropositionUserVote::getUser).collect(Collectors.toList());
    }

    public List<User> getUsersDownvote() {
        return usersLikes.stream().filter(v->v.getVoteType().equals(VoteTypeEnum.DOWN_VOTED)).map(PropositionUserVote::getUser).collect(Collectors.toList());
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

    public PropositionTypeEnum getType() {
        return type;
    }

    public void setType(PropositionTypeEnum type) {
        this.type = type;
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

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public Integer getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(Integer upvotes) {
        this.upvotes = upvotes;
    }

    public Integer getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(Integer downvotes) {
        this.downvotes = downvotes;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Set<PropositionUserVote> getUsersLikes() {
        return usersLikes;
    }

    public void setUsersLikes(Set<PropositionUserVote> usersLikes) {
        this.usersLikes = usersLikes;
    }

    public PropositionHatEnum getHat() {
        return hat;
    }

    public void setHat(PropositionHatEnum hat) {
        this.hat = hat;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
