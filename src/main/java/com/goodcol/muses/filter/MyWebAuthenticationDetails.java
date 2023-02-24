//package com.goodcol.muses.filter;
//
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.security.web.authentication.WebAuthenticationDetails;
//
///**
// * 自定义web认证的details属性
// *
// * @author Zhangzp
// * @date 2023年01月12日 09:14
// */
//public class MyWebAuthenticationDetails extends WebAuthenticationDetails {
//
//    private final String tenantName;
//
//    public MyWebAuthenticationDetails(HttpServletRequest request) {
//        super(request);
//        tenantName = request.getParameter("tenantName");
//    }
//
//    public String getTenantName(){
//        return this.tenantName;
//    }
//
//    @Override
//    public String toString() {
//        return super.toString() + "; tenantName: " + this.getTenantName();
//    }
//}
