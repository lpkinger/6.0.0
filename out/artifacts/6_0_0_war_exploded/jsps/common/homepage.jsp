<%@ page language="java" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<style type="text/css">
BODY
{
scrollbar-face-color:rgb(87,137,188); 
scrollbar-shadow-color:#ffffff; 
scrollbar-highlight-color:#FFFFFF; 
scrollbar-3dlight-color:#3366cc; 
scrollbar-darkshadow-color:#666666; 
scrollbar-track-color:rgb(116,171,219); 
scrollbar-arrow-color:#ffffff;
    FONT-FAMILY: "宋体";
    FONT-SIZE: 9pt;
    background-color:#f1f1f1;
    COLOR: #2e4f6e;
    LINE-HEIGHT: 150%;
    MARGIN-TOP: 1px;
    MARGIN-LEFT: 0px;
    MARGIN-RIGHT: 0px;
    PADDING:6px;
}
.STYLE1 {color: #000000}
.STYLE2 {font-size: 9pt}
.STYLE3 {color: #0033CC}
.STYLE5 {font-size: 9pt;PADDING-BOTTOM: 6px;}
</style>
<script language="Javascript" type="text/javascript" >
<!--
    function alter_del() {
        var k = window.confirm("    您的操作将导致数据被删除，请确认操作无误！");
        if (k) {
            return true;
        }
        else {
            return false;
        }
    }

    function msover() {
        {
            event.srcElement.style.color = "#111111";
            event.srcElement.style.backgroundColor = "#EAF8FF";
            event.srcElement.style.cursor = "hand";
        }
    }
    function msout() {
        {
            event.srcElement.style.color = "black"; event.srcElement.style.backgroundColor = "rgb(214,229,244)";
            event.srcElement.style.cursor = "auto";
        }
    }
    function newsDetail(which) {
        var topw;
        var leftw;
        topw = (screen.availHeight - 500) * 0.5;
        leftw = (screen.availWidth - 630) * 0.5;
        var modalProperty = "Help=no;status:no;dialogWidth=630px;dialogHeight=500px;scroll=auto;dialogLeft=" + leftw + ";dialogTop=" + topw + ";";
        showModalDialog("news.aspx?code=" + which, "", modalProperty);
    }
    function workplanDetail(which) {
        window.open("WorkReport/plan.aspx?code=" + which, "", "Status=yes,scrollbars=yes,resizable=yes,width=" + (screen.availWidth - 10) + ",height=" + (screen.availHeight - 50) + ",top=0,left=0");
    }
    function workreportDetail(which) {
        window.open("WorkReport/report.aspx?code=" + which, "", "Status=yes,scrollbars=yes,resizable=yes,width=" + (screen.availWidth - 10) + ",height=" + (screen.availHeight - 50) + ",top=0,left=0");
    }
    function visitplanDetail(which) {
        window.open("CRM/visit.aspx?code=" + which, "", "Status=yes,scrollbars=yes,resizable=yes,width=" + (screen.availWidth - 10) + ",height=" + (screen.availHeight - 50) + ",top=0,left=0");
    }
    function newMessage() {
        window.open("system/message.aspx", "", "Status=yes,scrollbars=yes,resizable=yes,width=" + (screen.availWidth - 10) + ",height=" + (screen.availHeight - 50) + ",top=0,left=0");
    }
    function docs() {
        window.open("docs/docs.aspx", "", "Status=yes,scrollbars=yes,resizable=yes,width=" + (screen.availWidth - 10) + ",height=" + (screen.availHeight - 50) + ",top=0,left=0");
    }
    function affiche() {
        window.open("system/affiche_record.aspx?code=", "", "Status=yes,scrollbars=yes,resizable=yes,width=" + (screen.availWidth - 10) + ",height=" + (screen.availHeight - 50) + ",top=0,left=0");
    }
    function Calendar() {
        window.open("WorkReport/Calendar.aspx", "", "Status=yes,scrollbars=yes,resizable=yes,width=" + (screen.availWidth - 10) + ",height=" + (screen.availHeight - 50) + ",top=0,left=0");
    }
    function changepassword() {
        var topw;
        var leftw;
        topw = (screen.availHeight - 390) * 0.5;
        leftw = (screen.availWidth - 560) * 0.5;
        window.open("system/changepassword.aspx", "", "Status=yes,scrollbars=yes,resizable=yes,width=560,height=390,top=" + topw + ",left=" + leftw);
    }
    function saleflow() {
        window.open("Flow/SaleFlow.aspx", "", "Status=yes,scrollbars=yes,resizable=yes,width=" + (screen.availWidth - 10) + ",height=" + (screen.availHeight - 50) + ",top=0,left=0");
    }
    function IMG1_onclick() {

    }

    function example1_onclick() {

    }

//-->
</script>
</HEAD>
<BODY  link="000000" vlink="333333" alink="336699" leftmargin="0" >
  <table width="98%" border="0" align="left" cellpadding=0 cellspacing=0 bordercolor=red bgcolor="#f1f1f1">
  	<tr>
  		<td width="82%" valign="top">
          <table border="0" bordercolor=#000000 cellpadding="0" cellspacing="0" width="100%"><tr><td>
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
			<tr>
			  <td align="left" valign="top" ><img src='<%=basePath %>resource/images/mainpage/maintitle1.jpg'></td>
			  <td align="left" ><a href="sale/sale2.aspx"></a> <a href="webpart2.aspx"></a></td>
			</tr>
			</table>
	<table border=0 bordercolor=yellow cellpadding=0 cellspacing=0 width=100%>
					<tr><td width="36"></td>
					<td width="15" valign="bottom"><img src="<%=basePath %>resource/images/mainpage/mainleft.jpg"></td>
					<td>
				<table width="100%">
				  <tr><td class="tdlist_02_1 STYLE2">
				  <tr>
				    <td align="right" class="tdlist_02_1 STYLE2"><a href="common/datalist.aspx?whoami=affiche" target="_blank" class="STYLE3">.........更多新闻</a></td>
				    </tr>
				</table>					  </td>
						<td width="15" valign="bottom"><img src="<%=basePath %>resource/images/mainpage/mainright.jpg"></td>
			  </tr></table>						
			<table border=0 bordercolor=black cellpadding=0  cellspacing=0 width=100%>
					<tr><td width="36" height="14"></td>
					<td width="33" valign="top"><img src='<%=basePath %>resource/images/mainpage/mainbottomleft.jpg'></td>
					<td height="14" background="<%=basePath %>resource/images/mainpage/mainbg.jpg">&nbsp;</td>
					<td width=33 valign="top"><img src='<%=basePath %>resource/images/mainpage/mainbottomright.jpg'></td>
		    </tr></table>

			
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
              <tr>
                <td align="left" ><img src="<%=basePath %>resource/images/mainpage/maintitle3.jpg" id="IMG1"/></td>
              </tr>
            </table>
			<table border=0 bordercolor=yellow cellpadding=0 cellspacing=0 width=100%>
              <tr>
                <td width="36"></td>
                <td width="15" valign="bottom"><img src="<%=basePath %>resource/images/mainpage/mainleft.jpg"></td>
                <td><table width="100%" border="1"  align="center" cellpadding="2" cellspacing="0" bordercolorlight="#bdd8f0" bordercolordark="#efeeee">
                    <tr>
                      <td align="left" valign="top" bgcolor="#F7F7FF" class="tdlist_02_1 STYLE2"><a href="#">已处理流程</a></td>
                      <td align="left" bgcolor="#F7F7FF" class="tdlist_02_1 STYLE2"><a href="#">待处理流程</a></td>
                    </tr>
                    <tr>
                      <td width="364"align="left" valign="top" bgcolor="#ebeced" class="tdlist_02_1 STYLE2"><a 
                                href="common/datalist.aspx?whoami=MessageBoxDidNode">已处理节点</a> </td>
                      <td width="364" align="left" bgcolor="#ebeced" class="tdlist_02_1 STYLE2"><a href="system/FlowInstallView1.aspx?code=" target=_blank >待处理流程明细</a></td>
                    </tr>
                    <tr>
                      <td width="364"align="left" valign="top" bgcolor="#ebeced" class="tdlist_02_1 STYLE2"><a href="#">超时处理节点</a></td>
                      <td width="364" align="left" bgcolor="#ebeced" class="tdlist_02_1 STYLE2"><a href="system/FlowInstallView1.aspx?code=" target=_blank > </a> <a href="#">超时未处理流程</a> </td>
                    </tr>
                    <%--  <tr>
                        <td align="left" valign="top" bgcolor="#F7F7FF" class="tdlist_02_1 STYLE2">
                        <a href="common/datalist.aspx?whoami=MessageBox&tempwhoami=MyOvertimeFlow1"></a>                        <a href="system/batchflow.aspx?code=" target=_blank >批处理流程</a></td>
                        <td align="left" bgcolor="#F7F7FF" class="tdlist_02_1 STYLE2"></td>
                      </tr>--%>
                </table></td>
                <td width="15" valign="bottom"><img src="<%=basePath %>resource/images/mainpage/mainright.jpg"> </td>
              </tr>
            </table>
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
			<tr><td width="71%" align="left"><img src="<%=basePath %>resource/images/mainpage/maintitle4.jpg" align="left" /></td>
			  <td width="29%" align="right"><img src="<%=basePath %>resource/images/mainpage/m1.gif" width="87" height="28" border="0" usemap="#Map" /></td>
			</tr>
			</table>
								
					<table border="0" bordercolor="yellow" cellpadding="0" cellspacing="0" width="100%">
                      <tr>
                        <td width="36"></td>
                        <td width="15" valign="bottom"><img src="<%=basePath %>resource/images/mainpage/mainleft.jpg" /></td>
                        <td><table width="100%" border="1"  align="center" cellpadding="0" cellspacing="0" bordercolorlight="#bdd8f0" bordercolordark="#efeeee">
                            <tr>
                              <td width="2%" align="center" bgcolor="#ebeced"><img src="<%=basePath %>resource/images/mainpage/m2.gif" width="14" height="17" /></td>
                              <td width="3%" align="center" bgcolor="#ebeced"><img src="<%=basePath %>resource/images/mainpage/icon_headerFooter.gif" width="20" height="18" /></td>
                              <td width="13%" bgcolor="#ebeced"><span class="STYLE2">发送人</span></td>
                              <td width="51%" bgcolor="#ebeced"><span class="STYLE2">主题</span></td>
                              <td width="31%" bgcolor="#ebeced"><span class="STYLE2">发送时间</span></td>
                            </tr></table></td>
                        <td width="15" valign="bottom"><img src="<%=basePath %>resource/images/mainpage/mainright.jpg" /></td>
                      </tr>
		    </table>
					<table border=0 bordercolor=black cellpadding=0  cellspacing=0 width=100%>
					<tr><td width="36" height="14"></td>
					<td width="33" valign="top"><img src='<%=basePath %>resource/images/mainpage/mainbottomleft.jpg'></td>
					<td height="14" background="<%=basePath %>resource/images/mainpage/mainbg.jpg">&nbsp;</td>
					<td width=33 valign="top"><img src='<%=basePath %>resource/images/mainpage/mainbottomright.jpg'></td>
			</tr></table>
			        <table border=0 bordercolor=black cellpadding=0  cellspacing=0 width=100%>
					<tr><td width="36" height="14"></td>
					<td width="33" valign="top"><img src='<%=basePath %>resource/images/mainpage/mainbottomleft.jpg'></td>
					<td height="14" background="<%=basePath %>resource/images/mainpage/mainbg.jpg">&nbsp;</td>
					<td width=33 valign="top"><img src='<%=basePath %>resource/images/mainpage/mainbottomright.jpg'></td>
			</tr></table>
				<table border="0" cellpadding="0" cellspacing="0" width="100%" >
			<tr>
				<td align="left" ><img src="<%=basePath %>resource/images/mainpage/maintitle2.jpg" /></td>
			    </tr>
			</table>
			    <table border="0" bordercolor="yellow" cellpadding="0" cellspacing="0" width="100%">
                  <tr>
                    <td width="36"></td>
                    <td width="15" valign="bottom"><img src="<%=basePath %>resource/images/mainpage/mainleft.jpg" /></td>
                    <td><table width="100%" border="1"  align="center" cellpadding="1" cellspacing="0" bordercolorlight="#bdd8f0" bordercolordark="#efeeee">
                      <tr>
                          <td width="100%" colspan="4" align="left" bgcolor="#ebeced" ></td>
                      </tr>
                        <tr>
                          <td colspan="4" align="left" bgcolor="#ebeced" ></td>
                        </tr>
                    </table></td>
                    <td width="15" valign="bottom"><img src="<%=basePath %>resource/images/mainpage/mainright.jpg" /></td>
                  </tr>
                </table>
			    <table border=0 bordercolor=black cellpadding=0  cellspacing=0 width=100%>
					<tr><td width="36" height="14"></td>
					<td width="33" valign="top"><img src='<%=basePath %>resource/images/mainpage/mainbottomleft.jpg'></td>
					<td height="14" background="<%=basePath %>resource/images/mainpage/mainbg.jpg">&nbsp;</td>
					<td width=33 valign="top"><img src='<%=basePath %>resource/images/mainpage/mainbottomright.jpg'></td>
		    </tr></table>
</td></tr></table></td>	
        <td width="16%" valign="top"><table width="100%" border="0" cellpadding="0" cellspacing="0">
    <%--       <tr>
            <td align="center"><a href="#" target =_blank  ><img src="<%=basePath %>resource/images/mainpage/create.jpg" name="example1" width="66" height="76" border="0" id="example1"/></a></td>
            <td align="center"><a href="#" target="_blank" ><img src="<%=basePath %>resource/images/mainpage/flow.jpg" name="example1" width="66" height="76" border="0" id="example1" /></a></td>
          </tr> --%>
          <tr>
            <td align="center"><a href="javascript:affiche();"><img src="<%=basePath %>resource/images/mainpage/news.png" name="example5" width="66" height="76" border="0" id="example5" /></a></td>
            <td align="center"><a href="common/datalist.aspx?whoami=addressList" target="_blank"><img src="<%=basePath %>resource/images/mainpage/addressbook.png" name="example5" width="66" height="76" border="0" id="example5" /></a></td>
          </tr>
          <tr>
            <td align="center"><a href="javascript:Calendar();"><img src="<%=basePath %>resource/images/mainpage/calendar.png" name="example7" width="66" height="76" border="0" id="example7" /></a></td>
            <td align="center"><a href="JmailTest/login.aspx" target="_blank"><img src="<%=basePath %>resource/images/mainpage/email.png" name="example8" width="66" height="76" border="0" id="example8" /></a></td>
          </tr>
          <tr>
            <td align="center"><img src="<%=basePath %>resource/images/mainpage/help.png" name="example4" width="66" height="76" border="0" id="example4" /></td>
            <td align="center"><a href="http://dbserver/bbs" target="_blank"><img src="<%=basePath %>resource/images/mainpage/bbs.png" name="example3" width="66" height="76" border="0" id="example3" /></a></td>
          </tr>
          <tr>
            <td align="center"><a href="javascript:changepassword();" ><img src="<%=basePath %>resource/images/mainpage/setting.png" name="example6" width="66" height="76" border="0" id="example6" /></a></td>
            <td align="center"><a href="default.aspx" target="_parent"><img src="<%=basePath %>resource/images/mainpage/close.png" name="example6" width="66" height="76" border="0" id="example6" /></a></td>
          </tr>
          <tr>
            <td colspan="2"><table border="0" bordercolor="#ffff00" cellpadding="0" cellspacing="0" width="100%" >
                <tr>
                  <td colspan="2" valign="bottom">&nbsp;</td>
                </tr>
                <tr>
                  <td width="12" valign="bottom"><img src="<%=basePath %>resource/images/mainpage/maintitle5.jpg" /></td>
                  <td width="13" align="right" valign="bottom"><a href="system/oftenModule.aspx" target="_blank"><img src="<%=basePath %>resource/images/mainpage/c1.gif" alt="配置我的常用模块" width="29" height="28" border="0" align="right" /></a></td>
                </tr>
            </table></td>
          </tr>
          <tr>
            <td colspan="2"><table border="0" bordercolor="yellow" cellpadding="0" cellspacing="0" width="100%">
                <tr>
                  <td width="15" valign="bottom"><img src="<%=basePath %>resource/images/mainpage/mainleft.jpg" /></td>
                  <td>
                    &nbsp;</td>
                  <td width="15" valign="bottom"><img src="<%=basePath %>resource/images/mainpage/mainright.jpg" /></td>
                </tr>
            </table></td>
          </tr>
          <tr>
            <td colspan="2"><table border="0" bordercolor="black" cellpadding="0"  cellspacing="0" width="100%">
                <tr>
                  <td width="33" valign="top"><img src="<%=basePath %>resource/images/mainpage/mainbottomleft.jpg" /></td>
                  <td height="14" background="<%=basePath %>resource/images/mainpage/mainbg.jpg">&nbsp;</td>
                  <td width="33" valign="top"><img src="<%=basePath %>resource/images/mainpage/mainbottomright.jpg" /></td>
                </tr>
            </table></td>
          </tr>
        </table></td>
  	</tr>
</table>
<map name="Map" id="Map">
<area shape="rect" coords="2,2,26,26" href="<%=basePath %>common/homepage.jsp>" alt="刷新" />
<area shape="rect" coords="31,2,56,25" href="javascript:newMessage();" alt="发消息" />
<area shape="rect" coords="60,2,85,25" href="common/datalist.aspx?whoami=message"  target="_blank" alt="详细列表" />
</map>
</body>
</html>