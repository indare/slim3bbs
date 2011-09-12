package slim3bbsgae.service.bbs;

import java.util.List;

import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;

import slim3bbsgae.meta.bbs.BodyMeta;
import slim3bbsgae.meta.bbs.CommentMeta;
import slim3bbsgae.meta.bbs.HeadMeta;
import slim3bbsgae.model.bbs.Body;
import slim3bbsgae.model.bbs.Comment;
import slim3bbsgae.model.bbs.Head;


public class BbsService {

    public List<Head> getAll(){
        HeadMeta m = HeadMeta.get();
        return Datastore.query(m).sort(m.postDate.desc).asList();
    }
    
    public void insert(Head head, Body body) throws Exception {
        head.setKey(Datastore.allocateId(HeadMeta.get()));
        body.setKey(Datastore.allocateId(head.getKey(),BodyMeta.get()));
        head.getBodyRef().setModel(body);
        
        Transaction tx = Datastore.beginTransaction();
        
        try{
                Datastore.put(tx,head,body);
                tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }
    
    public void insert(Head head,Comment comment) throws Exception {
        
        long newCommentId = head.getLastCommentId() + 1L;
        
        Key commentKey = Datastore.createKey(head.getKey(), CommentMeta.get(), newCommentId );
        comment.setKey(commentKey);
        comment.getHeadRef().setModel(head);
        
        head.setLastCommentId(newCommentId);
        head.setLastCommentDate(comment.getPostDate());
        
        Transaction tx = Datastore.beginTransaction();
        
        try {
            Datastore.get(tx,HeadMeta.get(), head.getKey(), head.getVersion());
            Datastore.put(tx,head,comment);
            tx.commit();
        } catch (Exception e) {
            if(tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }
    
    public Head get(Key headKey) throws Exception {
        return Datastore.getOrNull(HeadMeta.get(), headKey);
    }
    
    public void update(Head head,Body body) throws Exception {
        Transaction tx = Datastore.beginTransaction();
        
        try{
            Datastore.get(tx, HeadMeta.get(), head.getKey(), head.getVersion());
            Datastore.put(tx, head, body);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }
    
    public void delete(Key headKey) throws Exception {
        Transaction tx = Datastore.beginTransaction();
        try {
            Head head = Datastore.get(tx, HeadMeta.get(), headKey);
            Key bodyKey = head.getBodyRef().getKey();
            Datastore.delete(tx, headKey, bodyKey);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
        
    }
}
