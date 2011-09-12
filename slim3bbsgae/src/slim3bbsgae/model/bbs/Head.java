package slim3bbsgae.model.bbs;

import java.io.Serializable;
import java.util.Date;

import com.google.appengine.api.datastore.Key;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.InverseModelListRef;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

import slim3bbsgae.meta.bbs.CommentMeta;

@Model(schemaVersion = 1)
public class Head implements Serializable {

    private static final long serialVersionUID = 1L;

    @Attribute(primaryKey = true)
    private Key key;

    @Attribute(version = true)
    private Long version;

    //記事見出し
    private String subject;
    
    //投稿日時
    private Date postDate;
    
    //投稿者名
    private String username;
    
    //編集用パスワード
    private String password;
    
    //最新コメントID
    private Long lastCommentId = 0L;
    
    //最新コメント日付
    private Date lastCommentDate;
    
    //Bodyへの1:1リレーション
    private ModelRef<Body> bodyRef = new ModelRef<Body>(Body.class);
    
    //Commentへの1:nリレーション
    @Attribute(persistent=false)
    private InverseModelListRef<Comment, Head> commentRef = 
        new InverseModelListRef<Comment, Head>(Comment.class, CommentMeta.get().headRef, this);
    
    /**
     * Returns the key.
     *
     * @return the key
     */
    public Key getKey() {
        return key;
    }

    /**
     * Sets the key.
     *
     * @param key
     *            the key
     */
    public void setKey(Key key) {
        this.key = key;
    }

    /**
     * Returns the version.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version
     *            the version
     */
    public void setVersion(Long version) {
        this.version = version;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public Date getPostDate() {
        return postDate;
    }
    public void setPostDate(Date postDate){
        this.postDate = postDate;        
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
    
    public Long getLastCommentId() {
        return lastCommentId;
    }
    
    public void setLastCommentId(Long lastCommentId) {
        this.lastCommentId = lastCommentId;
    }
    
    public Date getLastCommentDate() {
        return lastCommentDate;
    }
    
    public void setLastCommentDate(Date lastCommentDate) {
        this.lastCommentDate = lastCommentDate;
    }
    
    public ModelRef<Body> getBodyRef() {
        return bodyRef;
    }
    
    /**
     * @return the commentRef
     */
    public InverseModelListRef<Comment, Head> getCommentRef() {
        return commentRef;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Head other = (Head) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }
}
