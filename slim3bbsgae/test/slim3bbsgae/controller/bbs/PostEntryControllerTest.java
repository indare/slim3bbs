package slim3bbsgae.controller.bbs;

import org.slim3.tester.ControllerTestCase;
import org.junit.Test;

import slim3bbsgae.service.bbs.BbsService;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class PostEntryControllerTest extends ControllerTestCase {

    //入力受け入れができるデータ
    private static final String SUBJECT = "タイトル";
    private static final String USERNAME = "投稿者名";
    private static final String TEXT = "本文";
    private static final String PASSWORD = "password";
    private static final String char50 =
                                    "０１２３４５６７８９" + 
                                    "０１２３４５６７８９" +
                                    "０１２３４５６７８９" +
                                    "０１２３４５６７８９" +
                                    "０１２３４５６７８９";
    
    private static final String char51 =
                                    "０１２３４５６７８９" + 
                                    "０１２３４５６７８９" +
                                    "０１２３４５６７８９" +
                                    "０１２３４５６７８９" +
                                    "０１２３４５６７８９" +
                                    "０";
    
    //Service
    private BbsService service = new BbsService();
    
    @Test
    public void 正しいデータで投稿() throws Exception {
        //全入力値のテスト
        PostEntryController controller = postEntry(SUBJECT, USERNAME, TEXT, PASSWORD);
        
        /* テスト　スタート */
        assertThat(controller, is(notNullValue()));
        assertValidParameters();
        /* テスト　エンド */
        
    }
    
    //記事作成でPOSTした時
    private PostEntryController postEntry(String subject, String username, String text, String password) throws Exception{
        tester.param("subject", subject);
        tester.param("username", username);
        tester.param("text", text);
        tester.param("password", password);
        tester.request.setMethod("POST");
        tester.start("/bbs/postEntry");
        return tester.getController();
    }
    
    @Test
    public void パスワード無しで投稿() throws Exception{
        //入力値確認（ノーパスワード）
        postEntry(SUBJECT, USERNAME, TEXT, "");
        
        /* テストスタート */
        assertValidParameters();
        /* テストエンド */
        
    }
    
    @Test
    public void 件名無しで投稿() throws Exception {
        //タイトル未記入でNGになる事を確認
        postEntry("", USERNAME, TEXT, PASSWORD);
        
        /* テストスタート */
        assertInvalidParameters();
        assertThat(tester.getErrors().get("subject"), is(notNullValue()));
        /* テストエンド */
        
    }
    
    @Test
    public void 件名が51文字() throws Exception {
        
        postEntry(char51, USERNAME, TEXT, PASSWORD);
        
        /* テストスタート */
        assertInvalidParameters();
        assertThat(tester.getErrors().get("subject"), is(notNullValue()));
        /* テストエンド */
    }
    
    @Test
    public void 件名が50文字() throws Exception {
        
        postEntry(char50, USERNAME, TEXT, PASSWORD);
        /* テストスタート */
        assertValidParameters();
        /* テストエンド */
    }
    
    @Test
    public void 投稿者名無し() throws Exception {
        postEntry(SUBJECT, "", TEXT, PASSWORD);
        
        /* テストスタート */
        assertInvalidParameters();
        assertThat(tester.getErrors().get("username"), is(notNullValue()));
        /* テストエンド */
    }
    
    @Test
    public void 投稿者名が51文字() throws Exception {
        postEntry(SUBJECT, char51, TEXT, PASSWORD);
        /* テストスタート */
        assertInvalidParameters();
        assertThat(tester.getErrors().get("username"), is(notNullValue()));
        /* テストエンド */
        
    }
    
    @Test
    public void 投稿者名が50文字() throws Exception {
        postEntry(SUBJECT, char50, TEXT, PASSWORD);
        /* テストスタート */
        assertValidParameters();
        /* テストエンド */
    }
    
    @Test
    public void 本文未入力() throws Exception {
        postEntry(SUBJECT, USERNAME, "", PASSWORD);
        
        /* テストスタート */
        assertInvalidParameters();
        assertThat(tester.getErrors().get("text"), is(notNullValue()));
        /* テストエンド */
    }
    
    @Test
    public void 本文100文字大丈夫() throws Exception {
        String char100 = char50 + char50;
        postEntry(SUBJECT, USERNAME, char100, PASSWORD);
        /* テストスタート */
        assertValidParameters();
        /* テストエンド */
    }
    
    @Test
    public void パスワード未記入okpk() throws Exception {
        postEntry(SUBJECT, USERNAME, TEXT, "");
        
        /* テストスタート */
        assertValidParameters();
        /* テストエンド */
    }
    
    @Test
    public void パスワード5文字ダメ() throws Exception {
        String char5 = "01234";
        postEntry(SUBJECT, USERNAME, TEXT, char5);
        
        /* テストスタート */
        assertInvalidParameters();
        assertThat(tester.getErrors().get("password"), is(notNullValue()));
        /* テストエンド */
    }
    
    @Test
    public void パスワード6文字おｋｐｋ() throws Exception {
        
        String char6 = "012345";
        
        postEntry(SUBJECT, USERNAME, TEXT, char6);
        
        /* テストスタート */
        assertValidParameters();
        /* テストエンド */
    }
    
    @Test
    public void パスワード20文字おｋｐｋ() throws Exception {
        String char20 = "0123456789" +
                        "0123456789";
        
        postEntry(SUBJECT, USERNAME, TEXT, char20);
        /* テストスタート */
        assertValidParameters();
        /* テストエンド */
    }
    
    @Test
    public void パスワード21文字ダメｯｽ() throws Exception {
        String char21 = "0123456789" +
                        "0123456789" + 
                        "0";
        postEntry(SUBJECT, USERNAME, TEXT, char21);
        
        /* テストスタート */
        assertInvalidParameters();
        assertThat(tester.getErrors().get("password"), is(notNullValue()));
        /* テストエンド */
    }
    
    //バリデーションOKチェック
    private void assertValidParameters() {
        
        //登録確認
        assertThat(service.getAll().size(), is(1));
        
        //エラーが無いこと
        assertThat(tester.getErrors().isEmpty(), is(true));
        
        //TOPにリダイレクトしてる事
        assertThat(tester.getDestinationPath(), is("/bbs/"));
        assertThat(tester.isRedirect(), is(true));
        
    }
    
    //バリデーションNGチェック
    private void assertInvalidParameters() {
        //記事がまだ無いこと
        assertThat(service.getAll().size(), is(0));
        
        //エラーがあること
        assertThat(tester.getErrors().isEmpty(), is(false));
        
        //記事作成ページにForwardしてる事
        assertThat(tester.getDestinationPath(), is("/bbs/create"));
        assertThat(tester.isRedirect(), is(false));
    }
    
}
