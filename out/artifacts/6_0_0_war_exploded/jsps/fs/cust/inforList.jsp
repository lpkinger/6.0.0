<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css"/>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
</head>
<body >
<div style="width:90%;line-height:27px;font-size:16px;margin:0 auto;text-align:left;border-color:#0000FF;border: 0px none">
<div style="line-height:37px;font-size:20px;color:black;"><b>需提供以下资料清单（加盖公章）</b></div>
<div style="margin-left:50px">1、营业执照复印件</div>
<div style="margin-left:50px">2、组织机构代码证复印件</div>
<div style="margin-left:50px">3、国税、地税登记证复印件</div>
<div style="margin-left:50px">4、开户许可证复印件</div>
<div style="margin-left:50px">5、法人代表及实际控制人身份证复印件</div>
<div style="margin-left:50px">6、法人代表证明书</div>
<div style="margin-left:50px">7、公司近三年审计报告及最近一期的财务报表及科目明细（前五大）</div>
<div style="margin-left:50px">8、近1年主要结算银行流水对账单和用于公司账务结算的个人账户结算单复印件</div>
<div style="margin-left:50px">9、前五大上下游客户近一年贸易合同，货运证明或其他表明货物确已发运的单据；交易发票；提货单、质检证明（凭证）、预付款（定金）凭证复印件</div>
<div style="margin-left:50px">10、公司基本情况表</div>
<div style="margin-left:50px">11、拟融资应收账款发票清单、采购合同明细</div>
<div style="margin-left:50px">12、拟融资应收账款客户近一年的付款凭证（银行回单）</div>
<div style="margin-left:50px">13、拟融资应收账款相关贸易合同，货运证明或其他表明货物确已发运的单据；交易发票；提货单、质检证明（凭证）、预付款（定金）凭证</div>
<div style="margin-left:50px">14、近两年缴税完税凭证、水电费单、员工工资单等证明文件</div>
<div style="margin-left:50px">15、我公司要求的其他资料</div></div>
</body>
</html>