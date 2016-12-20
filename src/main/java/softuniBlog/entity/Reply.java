package softuniBlog.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "replies")
public class Reply {

    private Integer id;

    private String content;

    private User author;

    private Comment comment;

    private Date date;

    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
    @JoinColumn(nullable = false, name = "commentId")
    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    @Column(nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Reply(String content, User author, Comment comment, Date date, String status) {
        this.content = content;
        this.author = author;
        this.comment = comment;
        this.date = date;
        this.status = status;
    }

    public Reply() {
    }
}
