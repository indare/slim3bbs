package slim3bbsgae.controller.bbs;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.controller.validator.Validators;
import org.slim3.util.ApplicationMessage;
import org.slim3.util.BeanUtil;

import slim3bbsgae.model.bbs.Body;
import slim3bbsgae.model.bbs.Head;
import slim3bbsgae.service.bbs.BbsService;

public class EditController extends Controller {

    @Override
    public Navigation run() throws Exception {
        
        if(!isPost() || !validate()) {
            //Post以外のリクエスト　か
            //バリデーションエラーの時
            //は　記事詳細に戻す
            return forward("read");
        }
        
        BbsService service = new BbsService();
        Head head = service.get(asKey("key"));
        
        if(head == null) {
            //指定キーに該当する記事がないのでその旨のエラーメッセージ設定
            errors.put("message",ApplicationMessage.get("error.entry.notfound"));
            //トップへ戻る
            return forward(basePath);
        }
        
        if(!asString("password").equals(head.getPassword())) {
            //パスワード不一致
            errors.put("password", ApplicationMessage.get("error.password.invalid"));
            //トップへ戻る
            return forward("read");
        }
        
        //パスワードが一致していて、記事が存在する
        Body body = head.getBodyRef().getModel();
        BeanUtil.copy(head,request);
        requestScope("text", body.getText());
        
        return forward("edit.jsp");
        
    }
    
    private boolean validate() {
        Validators v = new Validators(request);
        
        v.add("key", v.required());
        v.add("password", v.required());
        return v.validate();
    }
}
