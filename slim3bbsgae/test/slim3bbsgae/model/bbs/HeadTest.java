package slim3bbsgae.model.bbs;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class HeadTest extends AppEngineTestCase {

    private Head model = new Head();

    @Test
    public void test() throws Exception {
        assertThat(model, is(notNullValue()));
    }
}
