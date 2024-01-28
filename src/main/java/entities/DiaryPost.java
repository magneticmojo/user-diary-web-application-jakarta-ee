package entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a diary post entity in the application, which is mapped to the "Diary_Posts" table in the database.
 * It includes properties such as title, post content, timestamp, and associated image.
 * <p>
 * The entity makes use of Jakarta Persistence annotations to define the mapping between the class and the database.
 *
 * @author Björn Forsberg
 */
@Entity
@Table(name = "Diary_Posts")
public class DiaryPost implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String post;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    private String imageCaption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image image;

    /**
     * Default no-args constructor as stipulated by the Javabean convention, and necessary for JPA and Hibernate.
     */
    public DiaryPost() {
    }

    public DiaryPost(String title, String post, Date timestamp) {
        this.title = title;
        this.post = post;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPost() {
        return post;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getImageCaption() {
        return imageCaption;
    }

    public void setImageCaption(String imageCaption) {
        this.imageCaption = imageCaption;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

}
