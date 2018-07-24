
<%@page import="java.text.Format"%>
<%@page import="com.businessobjects12.prompting.objectmodel.common.Values"%>
<%@page import="com.crystaldecisions.reports.queryengine.collections.Fields"%>
<%@page import="com.businessobjects.reports.dpom.processingplan.Field"%>
<%@page import="com.crystaldecisions.sdk.occa.report.data.ParameterFieldRangeValue"%>
<%@page import="com.crystaldecisions.sdk.occa.report.data.ParameterFieldDiscreteValue"%>
<%@page import="com.crystaldecisions.reports.queryengine.collections.ParameterValues"%>
<%@page import="com.crystaldecisions.sdk.occa.report.data.ParameterField"%>
<%@page import="com.crystaldecisions.reports.reportengineinterface.JPEReportSourceFactory"%>
<%@page import="com.crystaldecisions.sdk.occa.report.reportsource.IReportSourceFactory2"%>
<%@page import="com.crystaldecisions.sdk.occa.report.data.ConnectionInfos"%>
<%@page import="com.crystaldecisions.sdk.occa.report.data.ConnectionInfo"%>
<%@page import="com.crystaldecisions.sdk.occa.report.data.IConnectionInfo"%>
<%@page import="com.crystaldecisions.sdk.occa.report.reportsource.IReportSource"%>
<%@page import="com.crystaldecisions.sdk.occa.report.lib.PropertyBag"%>

<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>

<%--database connection --%>    
<%--webreporting.jar  --%>
<%@page import="com.crystaldecisions.report.web.viewer.*" %>
<%--jrcerom.jar --%>
<%@ page import="com.crystaldecisions.reports.sdk.*" %>
<%    
	/*JRCResultDatasource jrcd = new JRCResultDatasource("view_report.rpt");
    if(!jrcd.isReportSourceInSession("reportSource",session)){
         response.sendRedirect("error.html");
     }*/
   //  String path="salelist.rpt";
     //获取参数
     String reportName=request.getParameter("reportfile");
     //String reportName=(String)request.getAttribute("reportfile");
     String rcondition=request.getParameter("rcondition");
     System.out.println(reportName);
     System.out.println(rcondition);
     IReportSourceFactory2 rsf=new JPEReportSourceFactory();
     java.util.Locale localetest=java.util.Locale.CHINA;
  //   IReportSource rptSource=(IReportSource)rsf.createReportSource(path, localetest);
   
    IReportSource rptSource=(IReportSource)rsf.createReportSource(reportName, localetest);
   
     ConnectionInfos conninfos=new ConnectionInfos();
     IConnectionInfo connInfo1=new ConnectionInfo();
     PropertyBag propertyBag=new PropertyBag();
     
     propertyBag.put("Database Class Name","oracle.jdbc.driver.OracleDriver");
     propertyBag.put("Connection URL","jdbc:oracle:thin:@192.168.253.111:1521:orcl");
             
     connInfo1.setUserName("uaserp600");
     connInfo1.setPassword("tx2x9saq");
     connInfo1.setAttributes(propertyBag);
     conninfos.add(connInfo1);
     CrystalReportViewer crViewer = new CrystalReportViewer();
     crViewer.setDatabaseLogonInfos(conninfos);
     crViewer.setOwnPage(true);
     crViewer.setOwnForm(true);
     crViewer.setEnableParameterPrompt(false);
     crViewer.setDisplayGroupTree(false);
     crViewer.setHasSearchButton(false);
     crViewer.setHasPrintButton(true);
     crViewer.setHasExportButton(true);
     crViewer.setHasLogo(false);
     crViewer.setGroupTreeWidth(0);
     crViewer.setDisplayGroupTree(false);
     //crViewer.getParameterFields()
     crViewer.setReportSource(rptSource); //reportSource=session.getAttribute("reportSource");
    // crViewer.setSelectionFormula("{sale.sa_id}='3267'");
     crViewer.setSelectionFormula(rcondition);
     //rptSource.refresh();
     crViewer.setPrintMode(CrPrintMode.ACTIVEX);     
     crViewer.processHttpRequest(request,response,this.getServletConfig().getServletContext(),out);
     crViewer.refresh();
     //crViewer.dispose();
     //rptSource.dispose(); 
 %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

</body>
</html>