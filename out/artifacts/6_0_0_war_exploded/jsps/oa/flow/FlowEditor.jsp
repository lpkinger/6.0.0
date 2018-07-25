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
<script type="text/javascript" src="<%=basePath%>resource/ux/layout/component/form/ItemSelector.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/layout/component/form/MultiSelect.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/form/MultiSelect.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/CheckColumn.js"></script>
<link rel="stylesheet" type="text/css" href="<%=basePath%>resource/ux/css/CheckHeader.css" />
<style type="text/css">
.x-flow-tbar .x-btn .x-btn-center .x-btn-inner{
	color:#367fae
}
.x-grid-checkheader {
    margin-top: 6px;
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
	background:#f2f2f2;
	border: none;
}
.x-toolbar-default{
	background-image:none;
	background-color:#f2f2f2;
}
.x-tab-bar{
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
.x-tab-default-top{
    border-color: #d0d0d0;
    border-radius: 4px 4px 0 0;
	margin: 0px 2px 4px 2px !important;
	box-shadow:none;
	background-image:none;
	background-color:#5d9ac1;
}
.x-border-box .x-tab-default-top {
    height: 22px;
}
.x-tab-top-active {
	border-radius: 4px 4px 0 0;
	box-shadow:none;
	background-image:none;
	background-color:#f2f2f2;
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
	background-color:#f2f2f2;
}
#flowfile .x-btn-default-toolbar-small-over{
	border:none;
	color:black;
	background-image:unset;
	padding:3px;
	opacity:1;
}
#flowDriverGrid-body{
	border-top:none !important;
}
#judgeArea .x-panel-body{
	background-color:ghostwhite;
}
.x-btn-gray{
	height:22px !important;
}
.x-panel-body-default {
    border: none;
    background-color: #f2f2f2;
    color: black;
}
</style>
<script type="text/javascript">
var caller = getUrlParam('caller');
var name = getUrlParam('name');//节点名称或者操作名称
//操作类型： 1.提交操作  Commit  2.普通操作  Turn  3.派生流程  Flow  4.派生任务  Task  5.派生意见  Update
//节点类型： 1.普通节点  task    2.决策节点  decision 
var type = getUrlParam('type');
var fd_id = getUrlParam('fd_id');//flow_define流程定义主键
var fromId = getUrlParam('fromId');//来源节点ID
var toId = getUrlParam('toId');//目标节点ID
var fromNodeName = getUrlParam('fromNodeName');//来源节点name
var toNodeName = getUrlParam('toNodeName');//目标节点name
var shortName = getUrlParam('shortName');//版本简称
Ext.Loader.setConfig({
	enabled: true,
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'oa.flow.FlowEditor'
    ],
    launch: function() {
    	if(type=='task'){//节点视图
         	Ext.create('erp.view.oa.flow.flowEditor.FlowNodeEditor');
    	}else if (type=='Turn'){//普通操作视图
    		Ext.create('erp.view.oa.flow.flowEditor.FlowTurnEditor');
    	}else if (type=='Commit'){//提交操作视图
    		Ext.create('erp.view.oa.flow.flowEditor.FlowCommitEditor');
    	}else if (type=='Flow'||type=='Task'){//派生流程操作视图
    		Ext.create('erp.view.oa.flow.flowEditor.FlowDeriveEditor');
    	}else if (type=='Update'){//派生意见操作视图
    		Ext.create('erp.view.oa.flow.flowEditor.FlowIdeaEditor');
    	}else if (type=='Judge'){//派生意见操作视图
    		Ext.create('erp.view.oa.flow.flowEditor.FlowJudgeEditor');
    	}else if (type=='judge'){//决策节点
    		Ext.create('erp.view.oa.flow.flowEditor.FlowJudgeNodeEditor');
    	}
    }
});
</script>
</head>
</html>