<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath%>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath%>resource/css/main.css" type="text/css"></link>
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" />
<script type="text/javascript" src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/CheckColumn.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/layout/component/form/ItemSelector.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/layout/component/form/MultiSelect.js"></script> 
<script type="text/javascript" src="<%=basePath%>resource/ux/form/MultiSelect.js"></script>
<style type="text/css">
.formbase .x-grid-row .x-grid-cell {
	background-color: white
}

.formbase.x-panel-header-text {
	color: black;
}

.formbase .x-panel-header-default {
	color: black;
	font-weight: bold ;
	background-image:none;
	background:white;
  	border-style:none!important;
	border:none!important;  
}

.x-grid-accordion-hd{
	color: black!important;
	font-weight: bold!important
}

.formbase .x-grid-cell-inner {
	background-image:none;
	background:white;
}

.savebtn {
	height: 28px !important
}

.x-grid-cell{
	background-color:white!important
}

.x-panel .x-grid-body{
	background:white!important
}

.msg-form-disable{
	background:white!important
}

.x-component-default{
	padding:0!important
}

.x-mask{
	background:none!important; /*disabled样式*/
}

.mui-switch {
	width: 35px;
	height: 18px;
	position: relative;
	border: 1px solid #dfdfdf;
	background-color: #fdfdfd;
	box-shadow: #dfdfdf 0 0 0 0 inset;
	border-radius: 8px;
	background-clip: content-box;
	display: inline-block;
	-webkit-appearance: none;
	user-select: none;
	outline: none;
}

.mui-switch:before {
	content: '';
	width: 15px;
	height: 15px;
	position: absolute;
	top: 0px;
	left: 0;
	border-radius: 20px;
	border-top-left-radius: 20px;
	border-top-right-radius: 20px;
	border-bottom-left-radius: 20px;
	border-bottom-right-radius: 20px;
	background-color: #fff;
	box-shadow: 0 1px 3px rgba(0, 0, 0, 0.4);
}

.mui-switch:checked {
	border-color: #64bd63;
	box-shadow: #64bd63 0 0 0 16px inset;
	background-color: #64bd63;
}

.mui-switch:checked:before {
	left: 19px;
}

.mui-switch.mui-switch-animbg {
	transition: background-color ease 0.4s;
}

.mui-switch.mui-switch-animbg:before {
	transition: left 0.3s;
}

.mui-switch.mui-switch-animbg:checked {
	box-shadow: #dfdfdf 0 0 0 0 inset;
	background-color: #64bd63;
	transition: border-color 0.4s, background-color ease 0.4s;
}

.mui-switch.mui-switch-animbg:checked:before {
	transition: left 0.3s;
}

.mui-switch.mui-switch-anim {
	transition: border cubic-bezier(0, 0, 0, 1) 0.4s, box-shadow
		cubic-bezier(0, 0, 0, 1) 0.4s;
}

.mui-switch.mui-switch-anim:before {
	transition: left 0.3s;
}

.mui-switch.mui-switch-anim:checked {
	box-shadow: #64bd63 0 0 0 16px inset;
	background-color: #64bd63;
	transition: border ease 0.4s, box-shadow ease 0.4s, background-color
		ease 1.2s;
}

.mui-switch.mui-switch-anim:checked:before {
	transition: left 0.3s;
}

.title{
	position:fixed;
	left:1;
	top:0;
	z-index:100;
}

</style>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled : true
});//开启动态加载
Ext.application({
	name : 'erp',//为应用程序起一个名字,相当于命名空间
	appFolder : basePath + 'app',//app文件夹所在路径
	controllers : [//声明所用到的控制层
	'sysmng.MsgSetting' ],
	launch : function() {
		Ext.create('erp.view.sysmng.MsgSetting');//创建视图
	}
});

var emtype = '<%=session.getAttribute("em_type")%>'; 
var caller = getUrlParam('whoami');

function changeEnable(x,dataIndex,value,prefix) {
	var grid = Ext.getCmp(prefix+'roleGrid');
	var record = grid.store.getAt(x);
	if(value==-1&&!grid.readOnly){
		record.dirty = true;
		value = 0;
	}else if(!grid.readOnly){
		record.dirty = true;
		value = -1;
	}
	record.set(dataIndex,value);
}

function changeActEnable(id,prefix){
	var checkbox = Ext.getDom(id);
	var grid = Ext.getCmp(prefix+'roleGrid');
	var container = Ext.getCmp(prefix + 'content');
	grid.readOnly = !checkbox.checked;
	if(checkbox.checked){
		container.expand();
	}
	Ext.Array.each(grid.store.data.items,function(item,index){
		if(checkbox.checked){		
			item.set('mr_isused',-1);
		}else{
			item.set('mr_isused',0);
		}	
		item.dirty = true;
	});
	if(!checkbox.checked){
		container.collapse();
	}
	//设置固定接收人的启用
	Ext.getCmp(prefix + '_mr_isused').setValue(checkbox.checked?-1:0);
}

</script>
</head>
<body>
</body>
</html>