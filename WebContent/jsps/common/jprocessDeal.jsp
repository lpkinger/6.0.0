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
.myradio{
	border: 0 !important;
  /*   background-color: #FFFAFA;  */
	font-size: 15px !important;
	font-weight: bold !important;	
	/* color:#515151; */
}
.x-html-editor-wrap textarea {
		background-color: #FAFAFA;
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
        'common.JProcessDeal'
    ],
    launch: function() {
         Ext.create('erp.view.common.JProcess.Viewport');
    }
});
var caller = "JProcess";
var dbfinds = [];
var en_uu = '<%=session.getAttribute("en_uu")%>';
var formCondition = '';
var gridCondition = '';
var basestarttime=new Date();
var nodeId='<%=request.getAttribute("jp_nodeId")%>';
var nextnodeId=null;
var requiredFields=null; 
var conditionValidation=null;
var canexecute=false;
var ISexecuted=getUrlParam("_do")==1;
var master=getUrlParam("newMaster");
var _center=getUrlParam("_center");
var TaskId=null;
var ProcessData=null;
var forknode=0;//判断当前审批节点是否并行节点
function scanAttachs(val){
	var attach = new Array();
	 Ext.Ajax.request({//拿到grid的columns
		   url : basePath + 'common/getFilePaths.action',
		   async: false,
		   params: {
			   id:val
		   },
		   method : 'post',
		   callback : function(options,success,response){
			   var res = new Ext.decode(response.responseText);
			   if(res.exception || res.exceptionInfo){
				   showError(res.exceptionInfo);
				   return;
			   }
			   attach =  res.files != null ?  res.files : [];
		   }
	   });
    var items=new Array();
	items.push({
		height: 20,
		width: 600,
		style: 'background:#CDBA96;',
		html: '<h3>附件:</h3>',
	});
	Ext.each(attach, function(item){
		var path = item.fp_path;
		var name = '';
		if(contains(path, '\\', true)){
			name = path.substring(path.lastIndexOf('\\') + 1);
		} else {
			name = path.substring(path.lastIndexOf('/') + 1);
		}
		  items.push({

			   style: 'background:#C6E2FF;',
			   html: '<img src="' + basePath + 'resource/images/mainpage/things.png" width=16 height=16/>' + 
			   '<span>文件:' + name + '<a href="' + basePath + "common/download.action?path=" + path + '">下载</a></span>',
		   });
	});
	var win = new Ext.window.Window({
		title: '附件下载',
    	id : 'win',
		height: "60%",
		width: "40%",
		items: items,
		buttonAlign: 'center',
		buttons: [{
			text: $I18N.common.button.erpCloseButton,
	    	iconCls: 'x-button-icon-close',
	    	cls: 'x-btn-gray',
	    	handler: function(){
	    		Ext.getCmp('win').close();
	    	}
		}]
	});
	win.show();
}
</script>
</head>
<body style="size: 1">
</body>
</html>