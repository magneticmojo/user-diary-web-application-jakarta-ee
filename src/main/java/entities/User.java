package entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the User entity in the application. It is mapped to the 'Users' table in the database.
 * <p>
 * The User class contains various attributes related to the user's account,
 * such as password, email, activation status, deletion status, and a list of associated diary posts.
 *
 * @see DiaryPost
 * @author Björn Forsberg
 */
@Entity
@Table(name = "Users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryPost> diaryPosts = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public List<DiaryPost> getDiaryPosts() {
        return diaryPosts;
    }

    public void setDiaryPosts(List<DiaryPost> diaryPosts) {
        this.diaryPosts = diaryPosts;
    }

}
