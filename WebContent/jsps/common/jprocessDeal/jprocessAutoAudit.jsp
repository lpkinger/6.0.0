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
<style type="text/css"> 

#toolbar{
	margin-top: 0px !important;
	border-width: 0px !important;
	background-color: transparent !important;

}
#newrrequireapply-body{
	border: none;
	background: transparent !important;

 }
#disablebtn{
	background-image: url("images/disable.png");
	background-repeat:no-repeat;
	height: 20px;
	width: 20px;
	background-color: transparent !important;
	border-width: 0px;
	margin-top: 5px;
}
#applybtn{
	background-image: url("images/apply.png");
	background-repeat:no-repeat;
	height: 20px;
	width: 20px;
	background-color: transparent !important;
	border-width: 0px;
	margin-top: 5px;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true,
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'common.AutoAudit'
    ],
    launch: function() {
         Ext.create('erp.view.common.JProcess.JProcessAutoAudit.Viewport');
    }
});
var caller = "JProcess";
var dbfinds = [];
var en_uu = '<%=session.getAttribute("en_uu")%>';
var em_uu='<%=session.getAttribute("em_uu")%>';
var formCondition = '';
var gridCondition = '';
var basestarttime=new Date();
var nodeId='<%=request.getAttribute("jp_nodeId")%>';
var ISexecuted=getUrlParam("_do")==1;
var master=getUrlParam("newMaster");
var _center=getUrlParam("_center");

function otherrulesclick(processid,name,caller){

	var windows= Ext.create('Ext.window.Window', {   
 		x: Ext.getBody().getWidth()/5, 
		y: Ext.getBody().getHeight()/8,
 		width:"60%",
 		height:"80%",
 		modal:true,
 		id:'autoauditwindow',
 		closable:true,     	
 		border: false,     		
 		resizable :false,
 		header: false,
 		draggable: false,
 		title :'流程信息:'+processid+'/'+name,
 		buttonAlign:'center',
 		items: {xtype: 'component',
		id:'iframe_detail',   					
		autoEl: {
			tag: 'iframe',
			style: 'height: 100%; width: 100%; border: none;',
			src: basePath +"jsps/common/jprocessDeal/otherProcessRules.jsp?processid="+processid+"&name="+name+"&caller="+caller
		}
	},
		
	
	 });
	 windows.show();
	
}


</script>
</head>
<body style="size: 1">
</body>
</html>