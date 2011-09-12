package slim3bbsgae.controller.bbs;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.util.ApplicationMessage;

import com.google.appengine.api.datastore.Key;

import slim3bbsgae.model.bbs.Head;
import slim3bbsgae.service.bbs.BbsService;

public class ReadController extends Controller {

    @Override
    public Navigation run() throws Exception {
        BbsService service = new BbsService();
        Head head = null;
        
        try{
            Key key = asKey("key");
            head = service.get(key);
        }catch (Exception e){
            //Key不正 
        }
        
        //記事取得確認
        if(head == null){
            //エラー設定
            errors.put("message", ApplicationMessage.get("error.entry.notfound"));
            return forward(basePath);
        }
        
        //記事があるので、リクエストスコープへ設定
        requestScope("head", head);
        requestScope("body", head.getBodyRef().getModel());
        requestScope("commentList", head.getCommentRef().getModelList());
        return forward("read.jsp");
    }
}
