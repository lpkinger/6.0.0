<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
response.setHeader("Content-Transfer-Encoding","binary");  
response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");       
response.setHeader("Pragma", "public");
response.setHeader("Content-Type","application/force-download");
response.setHeader("Content-Type","application/vnd.ms-excel");
response.setHeader("Content-Disposition","attachment;filename=" + request.getParameter("file") + ".xls");
out.print(new String(request.getParameter("content").toString().getBytes("iso8859-1"), "utf-8"));
%>