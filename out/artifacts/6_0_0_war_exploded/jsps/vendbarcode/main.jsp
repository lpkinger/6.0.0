<%@page import="com.uas.erp.model.Master"%>
<%@page import="com.uas.erp.model.Employee"%>
<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
  	<link rel="icon" href="<%=basePath %>resource/images/icon_title.png" type="image/x-icon"/>
  	<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"/>
  	<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/css/TabScrollerMenu.css"/>
  	<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"/>
  	<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" />
  	<link rel="stylesheet" href="<%=basePath %>resource/css/tree.css" type="text/css"/>
  	<link rel="stylesheet" href="<%=basePath %>jsps/oa/doc/resources/css/button.css" type="text/csss"></link>
  	<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
  	<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js?_em=<%=session.getAttribute("em_uu") %>"></script>
	<script type="text/javascript" src="<%=basePath %>resource/ux/TabScrollerMenu.js"></script>
	<script type="text/javascript" src="<%=basePath %>resource/ux/TabCloseMenu.js"></script>	
	<script type="text/javascript" src="<%=basePath %>resource/jquery/jquery-1.4.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>resource/ux/layout/component/form/ItemSelector.js"></script>
	<script type="text/javascript" src="<%=basePath%>resource/ux/layout/component/form/MultiSelect.js"></script>
	<script type="text/javascript" src="<%=basePath%>resource/ux/form/MultiSelect.js"></script>
	<script type="text/javascript" src="<%=basePath%>resource/other/vendLogin.js"></script>
	<style type="text/css">
	i{
	  display:block;
	  background:red;
	  border-radius:50%;
	  width:5px;
	  height:5px;
	  top:0px;
	  right:0px;
	  position:absolute;
	}
		.x-window-header-default-top {
-moz-box-shadow: #ebe7e7 0 1px 0px 0 inset, #ebe7e7 -1px 0 0px 0 inset, #ebe7e7 1px 0 0px 0 inset;
-webkit-box-shadow: #ebe7e7 0 1px 0px 0 inset, #ebe7e7 -1px 0 0px 0 inset, #ebe7e7 1px 0 0px 0 inset;
-o-box-shadow: #ebe7e7 0 1px 0px 0 inset, #ebe7e7 -1px 0 0px 0 inset, #ebe7e7 1px 0 0px 0 inset;
box-shadow: #ebe7e7 0 1px 0px 0 inset, #ebe7e7 -1px 0 0px 0 inset, #ebe7e7 1px 0 0px 0 inset;
}	
		.settings9{background-color:#D5D5D5 !important;}
		.main-btn-underline .x-btn-inner{font-size:13px;}
		.main-btn-underline-over .x-btn-inner{text-decoration: underline;color: blue;}
    	.bottom-left{float: left;cursor: pointer;padding-bottom: 6px;margin-left: 3px;text-decoration: none;font-weight:lighter;font-family: "宋体"}
    	.bottom_right{text-decoration: none;float:right;cursor: pointer;padding-bottom: 4px; margin-left: 1px;font-weight:lighter;color: blue;font-family: "宋体"}
		.bottom_right:hover{float:right;color: green;cursor: pointer;text-decoration: underline;}
    	.float{ width:200px; padding:5px 10px; border:1px solid #ffecb0; font-size:12px; background-color:#fffee0; -moz-box-shadow:1px 1px 2px rgba(0,0,0,.2); -webkit-box-shadow:1px 1px 2px rgba(0,0,0,.2); box-shadow:1px 1px 2px rgba(0,0,0,.2); position:absolute; -moz-border-radius:5px; -webkit-border-radius:5px; border-radius:5px; }
		.float .close-ico{ position:absolute; top:5px; right:5px; display:block; width:16px; height:16px; background-image:url(../resource/images/cup.gif); text-indent:-900px; overflow:hidden; }
		.float .close-ico:hover{ background-position:0 -16px;}
		.float p{ line-height:22px}
		.x-panel-body-default {
			background: #f1f2f5;
			border: none !important;
		}
		#ucloud-body{
			z-index:10!important;
			border-radius: 10px!important;
		    margin: -1px!important;
		    width: 561px!important;
		    height: 510px!important;
		    left: -18px!important;
		    top: -5px!important;
		    background-image: url("http://localhost:8080/ERP/resource/images/ucloud.png");
		    background-size: 580px 500px;
		    background-position: 0px 0px;
		
		}
		.searchMenu{
			position:absolute;top:110;left:180;width:auto;height:auto;z-index:4;visibility:hidden;border: 1;background-color: BECEBE;
			border:1px solid #ffecb0; -moz-box-shadow:1px 1px 2px rgba(0,0,0,.2); -webkit-box-shadow:1px 1px 2px rgba(0,0,0,.2); box-shadow:1px 1px 2px rgba(0,0,0,.2); -moz-border-radius:5px; -webkit-border-radius:5px; border-radius:5px;
		}
		.new_btn{
			background:#72d1ff;
			font-family: 微软雅黑;
		}
		.focus_textfield{
			 outline: 0;
    border-color: rgba(82, 168, 236, 0.8);
    -webkit-box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1), 0 0 8px rgba(82, 168, 236, 0.6);
    -moz-box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1), 0 0 8px rgba(82, 168, 236, 0.6);
    box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1), 0 0 8px rgba(82, 168, 236, 0.6);
		}
		.over_btn{
			background:#EC6D51;
			font-family: 微软雅黑;
		}
		.menuDiv{
			position:absolute;width:auto;height:auto;z-index:4;visibility:hidden;border: 1;background-color: #dedede;
			border:1px; -moz-box-shadow:1px 1px 2px rgba(0,0,0,.2); -webkit-box-shadow:1px 1px 2px rgba(0,0,0,.2); box-shadow:1px 1px 2px rgba(0,0,0,.2); -moz-border-radius:5px; -webkit-border-radius:5px; border-radius:5px;
		}
		.menuDiv a{
			text-decoration: none;
		}
		.menuDiv a:hover{
			color: black;
			font-weight: bold;
		}
		
		.main-btn-left {
			border: none;
			float: left;
			font-size: 13px;
		}
		.main-btn-right {
			border: none;
			float: right;
			font-size: 13px;
		}
		.main-btn-left a:hover,.main-btn-right font:hover{
			text-decoration: underline;
		}
		.menuitem-lock{
			background: url('<%=basePath %>resource/images/icon/lock.png') no-repeat center center;
		}
		.main-btn-user {
			background-image: url('<%=basePath %>resource/images/wishmaster.gif');
		}
		.main-btn-uuHelper {
			display: inline-block;
			margin-right:2px;
			margin-top:-4px;
			width:16px;
			height:16px;
			vertical-align:middle;
			background:url('<%=basePath %>resource/images/icon/uuHepler.png') no-repeat;
		}
		.main-btn-link {
			background-image: url('<%=basePath %>resource/images/ie.png');
		}
		.main-btn-msg {
			background-image: url('<%=basePath %>resource/images/mainpage/message.gif');
		}
		#myinfo{
			font-size: 13px;
			display: none;
		}
		#myinfo font,input{
			font-size: 13px;
		}
		.label{
			width: 45px;
			font-family: 隶书;
			color: blue;
		}
		#myinfo input{
			border: none;
			width: 100px;
		}
		#phones {
		    background-color: #fff;
		    text-shadow: #fff 0 1px 0;
		    position: relative;
		    display: block;
		    height: auto;
		}
		
		#phones div.phone img {
		    margin-bottom: 1px;
		}
		
		#phones div.phone {
		    float: left;
		    padding: 1px 1px;
		    margin: 0px;
		    text-align: center;
		    line-height: 14px;
		    color: #333;
		    font-size: 10px;
		    font-family: "Helvetica Neue",sans-serif;
		    height: 130px;
		    width: 140px;
		    overflow: hidden;
		    border: 1px solid gray;
		    cursor: pointer;
		}
		
		.x-ie6 #phones div.phone,
		.x-ie7 #phones div.phone,
		.x-ie8 #phones div.phone {
		    border-top: none;
		    padding: 3px 2px;
		    margin: 2px;
		}
		#phones div.phone-hover {
		    background-color: #eee;
		}
		#phones div{
			background-color: #D3E1F1 !important;
		}
		#phones div:hover{
			background-color: #D6B1F1 !important;
		}
		#phones .x-item-selected {
		    background-color: #D6B1F1 !important;
		}
		
		#phones div.phone strong {
		    color: #000;
		    display: block;
		}
		
		#phones div.phone span {
		    color: #999;
		}
		.process-lazy {
			color: red;
		}
		.UClose{
			border: 0px!important; 
			background-image: -webkit-linear-gradient(top, #FEFFFF, #FEFFFF) !important;
		}
		.allNavigationWindow .x-window-header-default-top {
			background-color:  #F6F6F6; 
		} 
		.allNavigationWindow{ background-color: #F5F3F3; }
		
		#searchMaster,#searchMaster div,#searchMaster input {
			width: 99.5% !important;
		}
		#menuMaster{
		    top: 29px !important;
		}
		
	</style>
	<script type="text/javascript">
	Ext.Loader.setConfig({
		enabled: true
	});//开启动态加载
	Ext.application({
	    name: 'erp',//为应用程序起一个名字,相当于命名空间
	    appFolder: basePath+'app',//app文件夹所在路径
	    controllers: [//声明所用到的控制层
	        'vendbarcode.main'
	    ],
	    launch: function() {
	        Ext.create('erp.view.vendbarcode.main.main');
	    }
	});
	var emp = '<%=session.getAttribute("employee")%>';
	if(emp=='null'){
		document.location.href = basePath + "vendbarcode/relogin.action";
	}
	var username = '<%=session.getAttribute("username")%>';
	var em_name = '<%=session.getAttribute("em_name")%>';
	var em_uu = '<%=session.getAttribute("em_uu")%>';
	var en_uu = '<%=session.getAttribute("en_uu")%>';
	var em_code = '<%=session.getAttribute("em_code")%>';
	var en_email ='<%=session.getAttribute("en_email")%>';
	var em_type = '<%=session.getAttribute("em_type")%>';
	var em_id ='<%=session.getAttribute("em_id")%>';
	var en_admin = '<%=session.getAttribute("en_admin")%>';
	var changepsw= '<%=session.getAttribute("changepsw")%>';
	var em_defaulthsid = '<%=session.getAttribute("em_defaulthsid")%>';
	var hascheckInitpwd ='<%=session.getAttribute("hascheckInitpwd")%>';
	var UCloud ='<%=session.getAttribute("UCloud")%>';
		function selectAll(){
			if($("#selectAll").attr("checked") == true){
  				$(":checkbox").attr("checked","checked");
  			}else{
  				$(":checkbox").attr("checked","");
  			}
		}
		function getSearchKeys(){
			var sf = Ext.getCmp('searchField');
			sf.setValue('');
			var condition = '';
			$.each($("input:checked"),function(){
  				if(this.id != "selectAll"){
  					if(this.name != "condition"){
  						if(sf.value == '' || sf.value == null || sf.value == '快速查找'){
  	  						sf.setValue($(this).next().text());  						
  	  					} else if(! contains(sf.value, $(this).next().text(), true)){
  	  						sf.setValue(sf.value + ';' + $(this).next().text());
  	  					}
  					} else {
  						condition = this.value;
  					}
  				}
  			});
			if(condition == 'and'){
				sf.setValue(sf.value.replace(/;/g, '&&'));  							
			} else {
				sf.setValue(sf.value.replace(/;/g, '##'));
			}
			$('#searchMenu').hide();
			sf.onTriggerClick(sf.value);
		}
		function deleteFromCookie(id, value){
			$("#"+id).remove();
			var searchKeys = Ext.util.Cookies.get('searchKeys');
			if(contains(searchKeys, '||' + value + '||')){
				searchKeys = searchKeys.replace('||' + value, '');
			} else if(contains(searchKeys, value + '||')){
				searchKeys = searchKeys.replace(value + '||', '');
			} else if(contains(searchKeys, '||' + value)){
				searchKeys = searchKeys.replace('||' + value, '');
			}
			Ext.util.Cookies.set('searchKeys', searchKeys);
		}
		function logout(){
			Ext.Msg.confirm('温馨提示',"确定退出吗?",
					ok);
			function ok(btn){
				if(btn == 'yes'){
					Ext.Ajax.request({
						url: basePath + "vendbarcode/logout.action",
						method: 'GET',
						callback: function(opt, s, r) {
							document.location.href = basePath + "vendbarcode/relogin.action";
						}
					});
				} else {
					return;
				}								
			};
		}
		function main_relogin(){
			showLoginDiv();
		}
		window.onload = function() {
			//禁用右键菜单
			Ext.getDoc().on("contextmenu", function(e){
			    //e.stopEvent();
			});
			SetCookie('em_name', em_name);
			SetCookie('em_uu', em_uu);
			SetCookie('en_name', en_name);
			SetCookie('en_uu', en_uu);
			SetCookie('em_code', em_code);
			/* SetCookie('printType', en_admin); */
		};
	</script>
  </head>
  <body>
   <textarea id="textarea_text" style="position:absolute;left:-1000px"></textarea>
   <script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
  </body>
  <script>
<%
	Object obj = session.getAttribute("employee");
	String sob = "";
	String sobText = "";
	String password="";
	if(obj != null) {
		Employee employee = (Employee)obj;
		sob = employee.getEm_master();
		Master master = employee.getCurrentMaster();
		password = employee.getEm_password();
		if (master != null) {
			sob = master.getMa_name();
			sobText = master.getMa_function();
		}
	}
	Object is_saas = session.getAttribute("isSaas");
	boolean isSaas = is_saas != null && Boolean.valueOf(is_saas.toString());
%>
var sob = '<%=sob%>';
var sobText = '<%=sobText%>';
var isSaas = <%=isSaas%>;
function hiddenPic(){ 
	document.getElementById("Layer1").innerHTML = ""; 
	document.getElementById("Layer1").style.display = "none"; 
} 
  </script>
</html>