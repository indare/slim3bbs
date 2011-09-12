package slim3bbsgae.controller.bbs;

import java.util.List;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import slim3bbsgae.model.bbs.Head;
import slim3bbsgae.service.bbs.BbsService;

public class IndexController extends Controller {

    @Override
    public Navigation run() throws Exception {
        BbsService service = new BbsService();
        List<Head> headList = service.getAll();
        requestScope("headList", headList);
        return forward("index.jsp");
    }
}
