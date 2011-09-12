package slim3bbsgae.model.bbs;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class BodyTest extends AppEngineTestCase {

    private Body model = new Body();

    @Test
    public void test() throws Exception {
        assertThat(model, is(notNullValue()));
    }
}
