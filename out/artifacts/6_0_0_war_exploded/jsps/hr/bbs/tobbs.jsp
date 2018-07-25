<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script type="text/javascript">
    var url = getUrlParam('url');
</script>
</head>
<body>
<%
 String username=session.getAttribute("em_code").toString();
 String url=request.getParameter("url"); 
 if(url.length()==0){
	 url="/JF";
 }
 Cookie cookie = new Cookie("jforumSSOCookieNameUser",username);// 保存用户名到Cookie 
 cookie.setPath("/"); 
    //String host = request.getServerName();//本地测试时注释掉的代码 
    //cookie.setDomain(host);//本地测试时需注释掉的代码 
    cookie.setMaxAge(-1); 
    response.addCookie(cookie); 
    System.out.println(url);
    response.sendRedirect(url);//跳转到jforum论坛,看看是否登录了 
    %>
</body>
</html>