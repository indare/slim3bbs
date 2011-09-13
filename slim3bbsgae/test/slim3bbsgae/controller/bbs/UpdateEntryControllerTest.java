package slim3bbsgae.controller.bbs;

import java.util.Date;
import java.util.List;

import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;
import org.junit.Test;

import slim3bbsgae.model.bbs.Body;
import slim3bbsgae.model.bbs.Head;
import slim3bbsgae.service.bbs.BbsService;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class UpdateEntryControllerTest extends ControllerTestCase {

    private BbsService service = new BbsService();
    private String keyString = null;
    private static final String PASSWORD = "password";
    
    //初期登録値
    private static final String BEFORE_SUBJECT = "タイトル";
    private static final String BEFORE_USERNAME = "投稿者名";
    private static final String BEFORE_TEXT = "本文";
    
    //更新後のバリデーションOK値
    private static final String AFTER_SUBJECT = "更新後タイトル";
    private static final String AFTER_USERNAME = "更新後投稿者名";
    private static final String AFTER_TEXT = "更新後本文";
    
    //文字制限用
    private static final String char50 = "０１２３４５６７８９" +
                                        "０１２３４５６７８９" +
                                        "０１２３４５６７８９" +
                                        "０１２３４５６７８９" +
                                        "０１２３４５６７８９";
    
    private static final String char51 = char50 + "０";
    
    private static final String char100 = char50 + char50;
            
    //テスト前準備
    @Override
    public void setUp() throws Exception{
        super.setUp();
        
        //テスト前にテストデータの登録
        insertEntry(BEFORE_SUBJECT,BEFORE_USERNAME,new Date(),BEFORE_TEXT,PASSWORD);
        
        //登録したデータを取得
        Head head = service.getAll().get(0);
        
        //Key取得
        keyString = Datastore.keyToString(head.getKey());
        
    }
    
    @Test
    public void 正常に動作させた時() throws Exception{
        updateEntry(AFTER_SUBJECT, AFTER_USERNAME, AFTER_TEXT);
        assertValidParameters(AFTER_SUBJECT, AFTER_USERNAME, AFTER_TEXT);
    }
    
    @Test
    public void タイトル空っぽエラー() throws Exception{
        updateEntry("", AFTER_USERNAME, AFTER_TEXT);
        assertInvalidParameters();
        assertThat(tester.getErrors().get("subject"), is(notNullValue()));
    }
    
    @Test
    public void 投稿者名空っぽエラー() throws Exception{
        updateEntry(AFTER_SUBJECT,"", AFTER_TEXT);
        assertInvalidParameters();
        assertThat(tester.getErrors().get("username"), is(notNullValue()));
    }
    
    @Test
    public void 本文空っぽエラー() throws Exception{
        updateEntry(AFTER_SUBJECT,AFTER_USERNAME,"");
        assertInvalidParameters();
        assertThat(tester.getErrors().get("text"), is(notNullValue()));
    }
    
    @Test
    public void タイトル50文字おｋ() throws Exception{
        updateEntry(char50,AFTER_USERNAME,AFTER_TEXT);
        assertValidParameters(char50,AFTER_USERNAME,AFTER_TEXT);
    }
    
    @Test
    public void タイトル51文字ダメｯｽ() throws Exception{
        updateEntry(char51,AFTER_USERNAME,AFTER_TEXT);
        assertInvalidParameters();
        assertThat(tester.getErrors().get("subject"), is(notNullValue()));
    }
    
    @Test
    public void 本文100文字おｋ() throws Exception{
        updateEntry(AFTER_SUBJECT, AFTER_USERNAME, char100);
        assertValidParameters(AFTER_SUBJECT, AFTER_USERNAME, char100);
    }
    
    @Test
    public void 後処理() throws Exception{
        //ﾃｽﾄデータの削除
        service.delete(Datastore.stringToKey(keyString));
        
        updateEntry(AFTER_SUBJECT,AFTER_USERNAME,AFTER_TEXT);
        
        List<Head> lstHead = service.getAll();
        assertThat( lstHead.size(), is(0));
        assertThat(tester.getErrors().get("message"), is(notNullValue()));
        assertThat(tester.getDestinationPath(), is("/bbs/"));
        assertThat(tester.isRedirect(), is(false));
        
    }
    
    private void insertEntry(
                    String subject,
                    String username,
                    Date postDate,
                    String text,
                    String password) throws Exception{
        
        //記事作成
        Head head = new Head();
        head.setSubject(subject);
        head.setUsername(username);
        head.setPostDate(postDate);
        head.setPassword(password);
        
        //本文
        Body body = new Body();
        body.setText(text);
        
        //データストアへ登録
        service.insert(head, body);
        
        
    }
    
    private UpdateEntryController updateEntry(String subject, String username, String text) throws Exception{
        tester.param("key", keyString);
        tester.param("password",PASSWORD);
        tester.param("subject",subject);
        tester.param("username",username);
        tester.param("text", text);
        tester.request.setMethod("POST");
        tester.start("/bbs/updateEntry");
        return tester.getController();
    }
    
    private void assertValidParameters(String subject,String username,String text) {
        
        //1件の記事が登録されてる
        List<Head> lstHead = service.getAll();
        assertThat(lstHead.size(),is(1));
        
        //1件の内容が更新したヤツ
        Head head = lstHead.get(0);
        
        assertThat(head.getSubject(), is(subject));
        assertThat(head.getUsername(), is(username));
        assertThat(head.getBodyRef().getModel().getText(), is(text));
        assertThat(tester.getErrors().isEmpty(), is(true));
        assertThat(tester.getDestinationPath(), is("/bbs/read?key=" + keyString));
        assertThat(tester.isRedirect(), is(true));
        
    }
    
    private void assertInvalidParameters() {
        List<Head> lstHead = service.getAll();
        assertThat(lstHead.size(), is(1));
        
        Head head = lstHead.get(0);
        assertThat(head.getSubject(), is(BEFORE_SUBJECT));
        assertThat(head.getUsername(), is(BEFORE_USERNAME));
        assertThat(head.getBodyRef().getModel().getText(), is(BEFORE_TEXT));
        
        assertThat(tester.getErrors().isEmpty(), is(false));
        
        assertThat(tester.getDestinationPath(), is("/bbs/edit.jsp"));
        assertThat(tester.isRedirect(), is(false));
        
    }
    
}

