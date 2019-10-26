package com.ityongman.delegate.httpReq;

import com.ityongman.delegate.httpReq.ctl.ItemController;
import com.ityongman.delegate.httpReq.ctl.TradeController;
import com.ityongman.delegate.httpReq.ctl.UserController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class DispatcherServlet {

    private static Map<String , Object> controllers = new HashMap<>();
    static {
        controllers.put("queryUserById", new UserController());
        controllers.put("queryOrderById", new TradeController());
        controllers.put("queryItemById", new ItemController());
    }


//    public String doDispatcher(HttpServletRequest req , HttpServletResponse resp){
//        String reqUri = req.getRequestURI();
//        String id = req.getParameter("id");
    public String doDispatcher(String reqUri , String id){
        Object objCtl = controllers.get(reqUri);
        String ret = "SUCCESS" ;
        if (objCtl instanceof UserController) {
            UserController userCtl = (UserController) objCtl;
            ret = userCtl.queryUserById(id);
        } else if(objCtl instanceof TradeController) {
            TradeController tradeCtl = (TradeController) objCtl;
            ret = tradeCtl.queryOrderById(id);
        } else if(objCtl instanceof  ItemController) {
            ItemController itemCtl = (ItemController) objCtl;
            ret = itemCtl.queryItemById(id);
        } else {
            ret = "Request Error , 404 not found !!!" ;
        }
        return ret;
    }
}
