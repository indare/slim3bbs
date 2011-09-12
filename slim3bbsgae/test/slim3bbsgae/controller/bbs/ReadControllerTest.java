package slim3bbsgae.controller.bbs;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;

import slim3bbsgae.model.bbs.Body;
import slim3bbsgae.model.bbs.Head;
import slim3bbsgae.service.bbs.BbsService;

public class ReadControllerTest extends ControllerTestCase {

    private BbsService service = new BbsService();
    private String keyString = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        insertEntry("テスト記事","テストユーザー", new Date(), "テスト本文" );
        Head head = service.getAll().get(0);
        keyString = Datastore.keyToString(head.getKey());
        
    }
    
    private void insertEntry(String subject, String username, Date postDate, String text) throws Exception{
        
        Head head = new Head();
        head.setSubject(subject);
        head.setUsername(username);
        head.setPostDate(postDate);
        
        Body body = new Body();
        body.setText(text);
        
        service.insert(head, body);
    }
    
    @Test
    public void testValidParameter() throws Exception {
        tester.param("key",keyString);
        tester.start("/bbs/read");
        ReadController controller = tester.getController();
        
        assertThat(controller, is(notNullValue()));
        assertThat(tester.requestScope("head"), is(notNullValue()));
        assertThat(tester.requestScope("body"), is(notNullValue()));
        assertThat(tester.requestScope("commentList"), is(notNullValue()));
        assertThat(tester.getErrors().isEmpty(), is(true));
        assertThat(tester.getDestinationPath(), is("/bbs/read.jsp"));
        assertThat(tester.isRedirect(), is(false));
    }
    
    @Test
    public void testAfterDeleted() throws Exception {
        service.delete(Datastore.stringToKey(keyString));
        
        tester.param("key",keyString);
        tester.start("/bbs/read");
        ReadController controller = tester.getController();
        
        assertThat(controller, is(notNullValue()));
        assertThat(tester.getErrors().get("message"), is(notNullValue()));
        assertThat(tester.getDestinationPath(), is("/bbs/"));
        assertThat(tester.isRedirect(), is(false));
    }
    
    @Test
    public void testInvalidParameter() throws Exception{
        tester.param("key","notUsageKey");
        tester.start("/bbs/read");
        ReadController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.getDestinationPath(), is("/bbs/"));
        assertThat(tester.isRedirect(), is(false));
    }
    
}
