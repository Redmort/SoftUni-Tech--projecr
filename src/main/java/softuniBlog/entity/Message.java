package softuniBlog.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "messages")
public class Message {

    private Integer id;

    private String title;

    private String content;

    private User author;

    private User receive;

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
    @JoinColumn(nullable = false, name = "receiveId")
    public User getReceive() {
        return receive;
    }

    public void setReceive(User receive) {
        this.receive = receive;
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

    public Message(String title, String content, User author, User receive, Date date, String status) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.receive = receive;
        this.date = date;
        this.status = status;
    }

    public Message() {
    }

    @Transient
    public Boolean isUnread(Message message){
        return message.status.equals("not read");
    }

}

