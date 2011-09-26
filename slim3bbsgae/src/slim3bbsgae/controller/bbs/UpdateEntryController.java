package slim3bbsgae.controller.bbs;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.controller.validator.Validators;
import org.slim3.util.ApplicationMessage;
import org.slim3.util.BeanUtil;

import slim3bbsgae.model.bbs.Body;
import slim3bbsgae.model.bbs.Head;
import slim3bbsgae.service.bbs.BbsService;

public class UpdateEntryController extends Controller {

    @Override
    public Navigation run() throws Exception {
        //POST以外での呼び出しは不可
        if(!isPost()) {
            return redirect(basePath);
        }
        
        //バリデーションエラーの場合は詳細へ
        if(!validate()) {
            return forward("edit.jsp");
        }
        
        BbsService service = new BbsService();
        Head head = null;
        
        try {
            //指定Keyの記事を取得
            head = service.get(asKey("key"));
        } catch (Exception e) {
            //TODO Key不正            
        }
        
        //記事が取得出来なかった場合
        if(head == null) {
            errors.put("message",ApplicationMessage.get("error.entry.notfound"));
            return forward(basePath);
        }
        
        //パスワード不一致
        if(!asString("password").equals(head.getPassword())) {
            errors.put("password",ApplicationMessage.get("error.password.invalid"));
            return forward("read");
        }
        
        //リクエスト値をセット
        Body body = head.getBodyRef().getModel();
        
        BeanUtil.copy(request, head);
        body.setText(asString("text"));
        
        //上書き
        service.update(head, body);
        
        //詳細へ戻る
        return redirect(basePath + "read?key=" + asString("key"));
        
    }
    
    private boolean validate() {
        Validators v = new Validators(request);
        v.add("subject", v.required(),v.maxlength(50));
        v.add("username", v.required(), v.maxlength(50));
        v.add("text", v.required());
        return v.validate();
    }
}
