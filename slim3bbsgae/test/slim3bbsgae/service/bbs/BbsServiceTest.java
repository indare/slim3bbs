package slim3bbsgae.service.bbs;

import java.util.Date;
import java.util.List;

import org.slim3.tester.AppEngineTestCase;
import org.slim3.util.DateUtil;
import org.junit.Test;

import com.google.appengine.api.datastore.Key;

import slim3bbsgae.model.bbs.Body;
import slim3bbsgae.model.bbs.Comment;
import slim3bbsgae.model.bbs.Head;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class BbsServiceTest extends AppEngineTestCase {

    private BbsService service = new BbsService();

    @Test
    public void testCRUD() throws Exception {
        assertThat(service, is(notNullValue()));
        
        //記事一覧の取得
        List<Head> headList = service.getAll();
        
        /* テストスタート */
        
        //0件かどうか
        assertNotNull(headList);
        assertTrue(headList.isEmpty());
        
        /* テストエンド */
        
        //記事の作成
        Head head = new Head();
        head.setSubject("記事その１");
        head.setUsername("ユーザー１");
        head.setPostDate(new Date());
        
        //本文の作成
        Body body = new Body();
        
        //データストアへ追加
        service.insert(head,body);
        
        //追加後の記事一覧の取得
        headList = service.getAll();
        
        /* テストスタート */
        
        //1件かどうか
        assertNotNull(headList);
        assertTrue(headList.size() == 1);
        
        //内容確認の為、1件目取得
        Head storedHead = headList.get(0);
        
        //登録したheadと取得したheadが等しいか
        assertEqualsHead(head,storedHead);
        
        //登録後のバージョンが１な事
        assertEquals(storedHead.getVersion(),Long.valueOf(1L));
        
        /* テストエンド */
        
        //記事1件の取得テスト
        storedHead = service.get(storedHead.getKey());
        
        /* テストスタート */
        
        //Getした記事のkeyが取得できているか
        assertNotNull(storedHead);
        
        //さっき登録したHeadかどうか
        assertEqualsHead(head,storedHead);
        
        //バージョンが１かどうか
        assertEquals(storedHead.getVersion(),Long.valueOf(1L));
        
        /* テストエンド */
        
        //記事の上書き
        storedHead.setSubject("上書き記事");
        Body storedBody = storedHead.getBodyRef().getModel();
        storedBody.setText("上書き本文");
        
        //データストアへ上書き
        service.update(storedHead,storedBody);
        
        //上書きした後の記事取得
        headList = service.getAll();

        /* テストスタート */
        assertNotNull(headList);
        assertTrue(headList.size() == 1);
        
        //中身のチェック
        Head updatedHead = headList.get(0);
        
        //修正後と取得した物が等しいこと
        assertEquals(storedHead,updatedHead);
        
        //上書き更新したのでバージョンが2になっていること
        assertEquals(updatedHead.getVersion(),Long.valueOf(2L));
        
        /* テストエンド */
        
        //記事の削除
        service.delete(updatedHead.getKey());
        
        //削除した後の記事取得
        storedHead = service.get(updatedHead.getKey());
        
        //削除した後の一覧
        headList = service.getAll();
        
        /* テストスタート */
        
        //削除したからNullになる
        assertNull(storedHead);
        
        //記事一覧は0件
        assertNotNull(headList);
        assertTrue(headList.isEmpty());
        
        /* テストエンド */
    }
 
    @Test
    public void postCommentTest() throws Exception {
        //記事作成
        Head head = new Head();
        head.setSubject("記事１");
        head.setUsername("ユーザー１");
        head.setPostDate(new Date());
        
        //本文作成
        Body body = new Body();
        body.setText("テスト本文");
        
        //データストアへ更新
        service.insert(head, body);
        
        //記事一覧取得
        List<Head> headList = service.getAll();
        
        /* テスト　スタート */
        //記事が1件なのを確認
        assertNotNull(headList);
        assertTrue(headList.size() == 1);
        /*　テスト　エンド */
        
        //記事を取得
        Head storedHead = headList.get(0);
        
        //記事のコメント一覧を取得
        List<Comment> commentList = storedHead.getCommentRef().getModelList();
        
        /* テストスタート */
        //コメント一覧が取得できている事
        assertNotNull(commentList);
        
        //コメントがまだないこと
        assertTrue(commentList.isEmpty());
        /* テストエンド */
        
        //コメントの作成
        Comment comment = createComment("ユーザー２", new Date(), "コメントテスト");
        
        //データストアへ更新
        service.insert(storedHead, comment);
        
        //記事の再取得
        storedHead = service.get(storedHead.getKey());
        
        //コメント一覧の取得
        commentList = storedHead.getCommentRef().getModelList();
        
        /* テストスタート */
        assertNotNull(commentList);
        assertTrue(commentList.size() == 1);
        
        //中身チェック
        Comment postedComment = commentList.get(0);
        assertEquals(comment.getUsername(),postedComment.getUsername());
        assertEquals(comment.getComment(),postedComment.getComment());
        assertEquals(comment.getPostDate(), postedComment.getPostDate());
        
        //PKのコメントIDが1であること
        assertEquals(postedComment.getKey().getId(),1L);
        
        //コメント件数が１であること
        assertTrue(storedHead.getLastCommentId() == 1L);
        
        //最終コメント登録日が登録時と同一であること。
        assertEquals(storedHead.getLastCommentDate(),comment.getPostDate());
        /* テスト　エンド */
        
    }
    
    @Test
    public void testHeadSort() throws Exception{
        
        Key keys[] = new Key[9];
        
        //投稿日付を適当に並べて登録
        keys[0] = insertHead("題名", "ユーザ", toDate("2010/10/18 15:45:00"), "本文");
        keys[1] = insertHead("題名", "ユーザ", toDate("2011/01/01 00:00:00"), "本文");
        keys[2] = insertHead("題名", "ユーザ", toDate("2011/01/01 12:34:56"), "本文");
        keys[3] = insertHead("題名", "ユーザ", toDate("2011/05/05 10:30:30"), "本文");
        keys[4] = insertHead("題名", "ユーザ", toDate("2010/10/22 15:45:45"), "本文");
        keys[5] = insertHead("題名", "ユーザ", toDate("2012/01/01 23:59:10"), "本文");
        keys[6] = insertHead("題名", "ユーザ", toDate("2011/05/05 11:30:30"), "本文");
        keys[7] = insertHead("題名", "ユーザ", toDate("2010/10/20 15:45:00"), "本文");
        keys[8] = insertHead("題名", "ユーザ", toDate("2010/10/22 15:45:00"), "本文");
        
        //一覧の取得
        List<Head> headList = service.getAll();
        assertNotNull(headList);
        assertTrue(headList.size() == 9);
        
        //投稿日付の降順で並んでいるかチェック
        Head preHead = null;
        for (Head head : headList){
            if (preHead == null) {
                preHead = head;
                continue;
            }
            
            //1件前の日付より古い日付であること
            assertTrue(head.getPostDate().before(preHead.getPostDate()));
            preHead = head;
        }
        
        //後片付け
        for (Key key : keys){
            service.delete(key);
        }
        
    }
    
    @Test
    public void testCommentSort() throws Exception {

        Key key;
        
        //記事の登録
        key = insertHead("タイトル", "ユーザー", new Date(), "本文");
        
        //一覧の取得
        List<Head> headList = service.getAll();
        assertNotNull(headList);
        assertTrue(headList.size() == 1);
        
        //記事の取得
        Head head = headList.get(0);
        
        //取得した記事にコメントを大量投稿してみる
        int maxCount = 1000;
        for (int i = 0 ; i< maxCount ; i++){
            Comment newComment = createComment("ユーザー", new Date(), "コメント" + i);
            service.insert(head, newComment);
        }
        
        //投稿した後の記事を再取得
        head = service.get(head.getKey());
        
        //コメント一覧をリレーションシップで取得
        List<Comment> commentList = head.getCommentRef().getModelList();
        assertNotNull(commentList);
        assertTrue(commentList.size() == maxCount);
        
        //コメントID順かどうかチェック
        Comment preComment = null;
        for(Comment comment : commentList) {
            if (preComment == null) {
                preComment = comment;
                continue;
            }
            
            //1個前のコメントIDより１大きい事
            assertEquals(comment.getKey().getId(), preComment.getKey().getId() + 1);
            preComment = comment;
        }
        
        //コメントの数が指定数あること
        assertEquals(head.getLastCommentId(), Long.valueOf(maxCount));
        
        //最終コメント日付が最後のコメント日付と同じ事
        assertEquals(head.getLastCommentDate(), preComment.getPostDate());
        
        //後片付け
        service.delete(key);
    }
    
    private void assertEqualsHead(Head head1, Head head2) {
        // 共にNullではないこと 
        assertNotNull(head1);
        assertNotNull(head2);
        // 記事タイトル、ユーザ名、投稿日時が等しいこと 
        assertEquals(head1.getSubject(), head2.getSubject());
        assertEquals(head1.getUsername(), head2.getUsername());
        assertEquals(head1.getPostDate(), head2.getPostDate());
        // 共にリレーションシップからBodyが取得できること 
        Body body1 = head1.getBodyRef().getModel();
        Body body2 = head2.getBodyRef().getModel();
        assertNotNull(body1);
        assertNotNull(body2);
        // 本文が等しいこと 
        assertEquals(body1.getText(), body2.getText());
    }
    
    private Comment createComment(String username, Date postDate, String text) throws Exception {
        //コメントの作成
        Comment comment = new Comment();
        comment.setUsername(username);
        comment.setComment(text);
        comment.setPostDate(postDate);
        return comment;
    }
    
    private Date toDate(String yyyyMMddHHmmss){
        return DateUtil.toDate(yyyyMMddHHmmss, "yyyy/MM/dd HH:mm:ss");
    }
    
    
    private Key insertHead(String subject, String username, Date postDate, String text) throws Exception{
        
        //記事の作成
        Head head = new Head();
        head.setSubject(subject);
        head.setUsername(username);
        head.setPostDate(postDate);
        
        //本文の作成
        Body body = new Body();
        body.setText(text);
        
        //データストアへ更新
        service.insert(head, body);
        
        return head.getKey();
        
    }
}