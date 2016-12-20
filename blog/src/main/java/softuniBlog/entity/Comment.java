package softuniBlog.entity;

import javax.persistence.*;
import javax.xml.crypto.Data;
import java.util.Date;

@Entity
@Table(name="comments")
public class Comment {

    private Integer id;

    private String content;

    private User author;

    private Article article;

    private Date date;

    private Integer commentsLike;

    private String likedUsers;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(columnDefinition = "text", nullable = false)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getCommentsLike() {
        return commentsLike;
    }

    public void setCommentsLike(Integer commentsLike) {
        this.commentsLike = commentsLike;
    }

    public String getLikedUsers() {
        return likedUsers;
    }

    public void setLikedUsers(String likedUsers) {
        this.likedUsers = likedUsers;
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
    @JoinColumn(nullable = false, name = "articleId")
    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Comment(String content, User author, Article article, Date date, Integer commentsLike, String likedUsers) {
        this.content = content;
        this.author = author;
        this.article = article;
        this.date = date;
        this.commentsLike = commentsLike;
        this.likedUsers = likedUsers;
    }

    public Comment() {
    }
}
