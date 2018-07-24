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
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<style type= "text/css">
.x-grid3-cell-inner, .x-grid3-hd-inner{  
    overflow:hidden;  
    -o-text-overflow: ellipsis;  
    text-overflow: ellipsis;  
    padding:3px 3px 3px 5px;  
    /*white-space: nowrap;*/  
    white-space:normal !important;   
}  
.x-column-header-inner{
	text-align: center;
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
        'plm.record.WorkRecord'
    ],
    launch: function() {
    	Ext.create('erp.view.plm.record.WorkRecord');//创建视图
    }
});
var caller = 'WorkRecord';
var recorder = '<%=session.getAttribute("em_name")%>';
var emid = '<%=session.getAttribute("em_uu")%>';
var formCondition = "";
var wrId = getUrlParam('formCondition');
var gridCondition;
if(wrId.indexOf('wr_id')>-1){ //从审批流跳转的界面，主键是wr_id
	gridCondition = "(wr_raid in (select wr_raid from ResourceAssignment  left join workrecord on ra_id=wr_raid where "+(getUrlParam('formCondition')).replace('IS','=')+")) or wr_recorderemid= '"+emid+"' and " ;
}else{ //主键是ra_id
	gridCondition = "wr_recorderemid= '"+emid+"' AND " ;
}
function upload(){
	Ext.getCmp("form").upload();
}
function ftpupload(){
	Ext.getCmp("form").ftpupload();
}
</script>
<style type="text/css">

#filegrid .x-grid-cell{
	/* background-color:#F2F0F2; */
	background-color:white!important; 
	/* font-size:16px; */
}


</style>
</head>
<body >
</body>
</html>