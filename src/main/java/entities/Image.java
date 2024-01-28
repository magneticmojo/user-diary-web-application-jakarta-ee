package entities;

import jakarta.persistence.*;

/**
 * Represents an image entity in the application, which is mapped to the "Images" table in the database.
 * The class includes properties for storing the image's binary data, MIME type, and associated diary post.
 * <p>
 * The entity makes use of Jakarta Persistence annotations to define the mapping between the class and the database.
 *
 * @author AuthorNameHere
 * @version 1.0
 */
@Entity
@Table(name = "Images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Lob
    @Column(length = 1048576)
    private byte[] imageData;

    private String mimeType;

    @OneToOne(mappedBy = "image")
    private DiaryPost diaryPost;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public DiaryPost getDiaryPost() {
        return diaryPost;
    }

    public void setDiaryPost(DiaryPost diaryPost) {
        this.diaryPost = diaryPost;
    }

}
