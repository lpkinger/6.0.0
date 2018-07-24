{success:true}
<%
    String username = request.getParameter("j_username");
    if (username == null) {
        if (session.getAttribute("username") == null) {
            session.setAttribute("username", "guest");
        }
    } else {
        session.setAttribute("username", username);
    }
%>