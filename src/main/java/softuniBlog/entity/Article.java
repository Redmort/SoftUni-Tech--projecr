package softuniBlog.entity;

import javax.persistence.*;
import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "articles")
public class Article {

    private Integer id;

    private String title;

    private String content;

    private User author;

    private Category category;

    private Date date;

    private  String status;

    private  Integer articleLike;

    private String likedUsers;

    private Set<Comment> comments;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(columnDefinition = "text", nullable = false)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @ManyToOne()
    @JoinColumn(nullable = false, name = "authorId")
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @ManyToOne()
    @JoinColumn(nullable = false, name = "categoryId")
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Column(nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getArticleLike() {
        return articleLike;
    }

    public void setArticleLike(Integer articleLike) {
        this.articleLike = articleLike;
    }

    public String getLikedUsers() {
        return likedUsers;
    }

    public void setLikedUsers(String likedUsers) {
        this.likedUsers = likedUsers;
    }

    @OneToMany(mappedBy = "article")
    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }


    public Article(String title, String content, User author, Category category, Date date, String status, Integer articleLike, String likedUsers) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.category = category;
        this.date = date;
        this.status = status;
        this.articleLike = articleLike;
        this.likedUsers = likedUsers;

        this.comments = new HashSet<>();
    }

    public Article() {
    }

    @Transient
    public String getSummary(){

        Integer a = (500>= this.getContent().length())? this.getContent().length():500;

        if (this.getContent().length()<=500) {
            return this.getContent().substring(0, a);
        }

        return this.getContent().substring(0, a) + "...";
    }

    @Transient
    public Integer likeCount(Set<String> likes){
        return likes.size();
    }

}
