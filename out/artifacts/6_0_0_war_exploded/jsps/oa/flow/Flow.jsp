<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/BoxReorderer.js"></script>	
<script type="text/javascript" src="<%=basePath %>resource/ux/TabReorderer.js"></script>
<style type="text/css">
.form-field-allowBlank {
    background-color: #e9e9e9;
    background-image: none;
}
.x-flow-tbar .x-btn .x-btn-center .x-btn-inner{
	color:#367fae;
    line-height: 16px;
}
.x-btn-gray{
	border-color: #a1a1a1;
	background-image:none;
	background-color:#f2f2f2;
	background: -webkit-linear-gradient(#fff, #e3e3e3); /* Safari 5.1 - 6.0 */
  	background: -o-linear-gradient(#fff, #e3e3e3); /* Opera 11.1 - 12.0 */
  	background: -moz-linear-gradient(#fff, #e3e3e3); /* Firefox 3.6 - 15 */
  	background: linear-gradient(#fff, #e3e3e3);
}
.x-btn-gray:hover{
	border-color: #656565;
}
.flow_mainpanel > .x-panel-body-default{
	border-top:none !important;
}
.flow_mainpanel > .x-panel-header-default-top {
	height:40px;
	background-image:none;
	background-color:#f2f2f2;
    border-bottom: none;
}
.flow_mainpanel > .x-panel-header-default-top > .x-panel-header-body{
	height:40px;
}
.flow_mainpanel > .x-panel-header-default-top > .x-panel-header-body > .x-box-inner > .x-panel-header-text-container{
    padding-top: 6px;
    padding-left: 6px;
    height:40px !important;
}
.flow_mainpanel > .x-panel-header-default-top > .x-panel-header-body > .x-box-inner > .x-panel-header-text-container
> .x-panel-header-text{
	font-family: "microsoft yahei", sans-serif;
    font-size: 17px;
    color: black;
    font-weight: bold;
}
.x-border-box .x-tab-bar-top .x-tab-bar-strip {
    height: 1px;
}
#flow_tab-body{
	background:#e9e9e9;
    border: 1px solid #d0d0d0;
    border-top: none;
}
.x-toolbar-default{
    border: none !important;
	background-image:none;
	background-color:#e9e9e9;
}
.x-tab-bar{
	border-left: 1px solid #d0d0d0;
	border-right: 1px solid #d0d0d0;
	background-image:none;
	background-color:#f2f2f2;
}
.x-tab-bar-top .x-tab-bar-body{
	padding-left: 5px;
	padding-bottom:0px;
	border:none;
}
.x-border-box .x-tab-bar-top .x-tab-bar-body {
    height: 24px;
}
.x-tab {
    border-width: 1px;
}
.x-tab-default-top-active {
    border-bottom: 0px solid rgb(51, 51, 51) !important;
}
.x-tab-default-top{
    border-color: #d0d0d0 !important;
    border-radius: 4px 4px 0 0;
	margin: 0px 2px 4px 2px !important;
	box-shadow:none;
	background-image:none;
	background-color:#5d9ac1;
}
.x-border-box .x-tab-default-top {
    height: 23px;
}
.x-tab-top-active {
	border-radius: 4px 4px 0 0;
	box-shadow:none;
	background-image:none;
    background-color: #e9e9e9 !important;
}
.x-tab button .x-tab-inner{
	color:#fff;
    font-weight: normal;
}
.x-tab-active button .x-tab-inner {
    color:black;
    font-weight: bold;
}
.x-panel-other > .x-panel-body{
	padding-top: 5px;
}
.x-fieldset{
    margin-bottom: 0px;
    padding: 10px;
}
.x-grid-row .x-grid-cell {
	border-bottom: 1px solid #cacaca;
    border-right: 1px solid #cacaca;
}
.x-grid-cell-special .x-grid-cell-inner {
    padding: 0px;
}
#fieldgrid .x-grid-row-checker {
    margin-left: 5px;
    margin-top: 6px;
}
#flowfile{
	background-image:unset;
}
#flowfile .x-form-file-wrap .x-form-file-btn{
	border:none;
	background-image:unset;
	background-color:#e9e9e9;
}
#flowfile .x-btn-default-toolbar-small-over{
	border:none;
	color:black;
	background-image:unset;
	padding:3px;
	opacity:1;
}
.x-form-file-wrap .x-btn button {
    height: 20px;
    line-height: 1;
    background-color: #e9e9e9;
}
.flow_panel_style > .x-panel-body{
	overflow-x: hidden !important;
	margin-right: 5px;
    padding-right: 5px;
}
.x-form-display-field-body{
	background:#bdbdbd;
}
.x-form-display-field{
    margin-left: 15px;
	color:black;
	background:#bdbdbd !important;
    font-weight: 800;
}
.x-field-display .x-form-display-field-body,.x-field-display .x-form-display-field{
	background:#e9e9e9 !important;
}
</style>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true,
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'oa.flow.Flow'
    ],
    launch: function() {
         Ext.create('erp.view.oa.flow.Flow');
    }
});
var em_code = '<%=session.getAttribute("em_code")%>';
var caller = getUrlParam('whoami');
var usingButton = getUrlParam('_noc');//从首页跳转的已处理和已发起只能查看
var formCondition = getUrlParam('formCondition');
var nodeId = getUrlParam('nodeId'),intanceCodeValue,status;//intanceCodeValue 用来加载初始单据编号  status 用来判断草稿单据
if(!nodeId){
	Ext.Ajax.request({
		url : basePath + 'oa/flow/getNodeId.action',
		params : {
			caller:caller,
			id:formCondition?formCondition:''
		},
		async: false,
		method : 'post',
		callback : function(options,success,response){
			var res = new Ext.decode(response.responseText);
			if(res.exceptionInfo){
				showError(res.exceptionInfo);return;
			}
			if(res.success){
				nodeId = res.nodeId;
				intanceCodeValue = res.codeValue;
				status = res.status
			}
		}
	});
}
function SaveOneButton(msg, fn){
	Ext.MessageBox.show({
     	title: $I18N.common.msg.title_prompt,
     	msg: msg,
     	buttons: Ext.Msg.OK,
     	icon: Ext.Msg.INFO,
     	fn: fn
	});
}
function SaveTwoButton(msg, fn){
	Ext.MessageBox.show({
     	title: $I18N.common.msg.title_prompt,
     	msg: msg,
     	buttons: Ext.Msg.YESNO,
     	icon: Ext.Msg.INFO,
     	fn: fn
	});
}
function saveSuccess(fn){
	var box = Ext.create('Ext.window.MessageBox', {
		buttonAlign : 'center',
		buttons: [{
			text: '确定',
			handler: function(b) {
				var scope = b.ownerCt.ownerCt;
				scope.fireEvent('hide', scope, true);
			}
		}],
		listeners: {
			hide: function(w, ok) {
				w.close();
				if(typeof ok == 'boolean') {
					if(ok)
						fn && fn.call();
					else
						window.location.reload();
				} else
					fn && fn.call();
			}
		}
	});
	box.show({
		title : $I18N.common.msg.title_prompt,
		msg : $I18N.common.msg.success_save,
		icon : Ext.MessageBox.QUESTION
	});
}
</script>
</head>
</html>