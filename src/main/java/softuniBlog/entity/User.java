package softuniBlog.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    private Integer id;

    private String email;

    private String fullName;

    private String password;

    private Set<Role> roles;

    private Set<Article> articles;

    private Set<Comment> comments;

    private Set<Reply> replies;

    private Set<Message> sendMessages;

    private Set<Message> receiveMessages;


    private String description;

    private String picture;


    public User(String email, String fullName, String password, String picture) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.picture = picture;
        //this.description = description;

        this.roles = new HashSet<>();
        this.articles = new HashSet<>();
        this.comments = new HashSet<>();
        this.replies = new HashSet<>();
        this.sendMessages = new HashSet<>();
        this.receiveMessages = new HashSet<>();

    }

    public User() {    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "email", unique = true, nullable = false)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "fullName", nullable = false)
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(name = "password", length = 60, nullable = false)
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles")
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    @OneToMany(mappedBy = "author")
    public Set<Article> getArticles() {
        return articles;
    }

    public void setArticles(Set<Article> articles) {
        this.articles = articles;
    }

    @Column(name = "picture", nullable = false)
    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @OneToMany(mappedBy = "author")
    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @OneToMany(mappedBy = "author")
    public Set<Reply> getReplies() {
        return replies;
    }

    public void setReplies(Set<Reply> replies) {
        this.replies = replies;
    }

    @OneToMany(mappedBy = "author")
    public Set<Message> getSendMessages() {
        return sendMessages;
    }

    public void setSendMessages(Set<Message> sendMessages) {
        this.sendMessages = sendMessages;
    }

    @OneToMany(mappedBy = "receive")
    public Set<Message> getReceiveMessages() {
        return receiveMessages;
    }

    public void setReceiveMessages(Set<Message> receiveMessages) {
        this.receiveMessages = receiveMessages;
    }

    @Column(columnDefinition = "text", nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Transient
    public boolean isAdmin() {
        return this.getRoles()
                   .stream()
                   .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
    }

    @Transient
    public  boolean isAuthor(Article article) {
        return Objects.equals(this.getId(), article.getAuthor().getId());
    }

    @Transient
    public  boolean isLiked(Set<String> likedUsers){
        return likedUsers.contains(this.getId().toString());
    }

    @Transient
    public  boolean isCommentAuthor(Comment comment){
        return Objects.equals(this.getId(), comment.getAuthor().getId());
    }

    @Transient
    public Integer notReadMessages(User user){
        Integer notReadMessages = 0;
        for (Message message : user.getReceiveMessages()) {
            if (message.getStatus().equals("not read")) {
                notReadMessages += 1;
            }
        }

        return notReadMessages;

    }

}