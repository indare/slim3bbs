package slim3bbsgae.controller.bbs;

import java.util.Date;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.controller.validator.Validators;
import org.slim3.util.BeanUtil;
import org.slim3.util.StringUtil;

import slim3bbsgae.model.bbs.Body;
import slim3bbsgae.model.bbs.Head;
import slim3bbsgae.service.bbs.BbsService;

public class PostEntryController extends Controller {

    @Override
    public Navigation run() throws Exception {
        if (!isPost() || !validate()) {
            return forward("create");
        }
        
        Head head = new Head();
        Body body = new Body();
        
        BeanUtil.copy(request, head);
        BeanUtil.copy(request, body);
        head.setPostDate(new Date());
        
        BbsService service = new BbsService();
        service.insert(head, body);
        
        return redirect(basePath);
        
    }
    
    private boolean validate() {
        Validators v = new Validators(request);
        v.add("subject", v.required(), v.maxlength(50));
        v.add("username", v.required(), v.maxlength(50));
        v.add("text", v.required());
        
        if(!StringUtil.isEmpty(param("password"))){
            v.add("password", v.minlength(6), v.maxlength(20));
        }
        return v.validate();
    }
}
