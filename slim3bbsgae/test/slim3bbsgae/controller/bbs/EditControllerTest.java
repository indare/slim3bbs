package slim3bbsgae.controller.bbs;

import java.util.Date;

import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;
import org.junit.Test;

import slim3bbsgae.model.bbs.Body;
import slim3bbsgae.model.bbs.Head;
import slim3bbsgae.service.bbs.BbsService;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class EditControllerTest extends ControllerTestCase {

    private BbsService service = new BbsService();
    private String keyString = null; 
    
    //ケース用正解パスワード
    private static final String PASSWORD = "password";
    
    //テスト前の準備
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        //ﾃｽﾄ用記事登録
        insertEntry("テスト記事","テストユーザー",new Date(),"テスト用本文",PASSWORD);
        
        //登録記事を取得
        Head head = service.getAll().get(0);
        
        //記事のKeyを保持
        keyString = Datastore.keyToString(head.getKey());
        
    }
    
    @Test
    public void 編集に入るときにアタリパスワード入力() throws Exception {
        //詳細ページから正しいパスワードが送られてきたかエミュる
        tester.param("key",keyString);
        tester.param("password",PASSWORD);
        tester.request.setMethod("POST");
        tester.start("/bbs/edit");
        
        EditController controller = tester.getController();
        
        //編集ページの表示
        
        /* テスト スタート */
        assertThat(controller, is(notNullValue()));
        
        //requestScopeに記事がセットされているか
        assertThat(tester.requestScope("key"), is(notNullValue()));
        assertThat(tester.requestScope("password"), is(notNullValue()));
        assertThat(tester.requestScope("subject"), is(notNullValue()));
        assertThat(tester.requestScope("username"), is(notNullValue()));
        assertThat(tester.requestScope("text"), is(notNullValue()));
        
        //エラーは発生してない事
        assertThat(tester.getErrors().isEmpty(), is(true));
        
        //記事編集のjspへフォワードを書けている事
        assertThat(tester.getDestinationPath(), is("/bbs/edit.jsp"));
        assertThat(tester.isRedirect(), is(false));
        
    }
    
    @Test
    public void 編集に入るときにウソパスワード入力() throws Exception {
        //パスワードを間違えてみる
        tester.param("key", keyString);
        tester.param("password","usopyo-n");
        tester.request.setMethod("POST");
        tester.start("/bbs/edit");
        
        EditController controller = tester.getController();
        
        //詳細ページへ戻るはず
        
        //コントローラーを取得できているか
        assertThat(controller, is(notNullValue()));
        
        assertThat(tester.requestScope("key"), is(notNullValue()));
        assertThat(tester.getErrors().get("password"), is(notNullValue()));
        assertThat(tester.getDestinationPath(), is("/bbs/read"));
        assertThat(tester.isRedirect(), is(false));
    }
            
    @Test
    public void 編集に入るときに記事が消されてた時() throws Exception {
        
        //該当記事を削除してしまう。
        service.delete(Datastore.stringToKey(keyString));
        
        //正しく動作させてみる
        tester.param("key",keyString);
        tester.param("password", PASSWORD);
        tester.request.setMethod("POST");
        tester.start("/bbs/edit");
        
        EditController controller = tester.getController();
        
        assertThat(controller, is(notNullValue()));
        assertThat(tester.getErrors().get("message"), is(notNullValue()));
        assertThat(tester.getDestinationPath(), is("/bbs/"));
        assertThat(tester.isRedirect(), is(false));
    }
    
    //記事の登録
    private void insertEntry(String subject,String username,Date postDate,String text,String password) throws Exception{
        
        //記事ヘッダ
        Head head = new Head();
        head.setSubject(subject);
        head.setUsername(username);
        head.setPostDate(postDate);
        head.setPassword(password);
        
        //記事本文
        Body body = new Body();
        body.setText(text);
        
        service.insert(head, body);
        
    }
    
}
