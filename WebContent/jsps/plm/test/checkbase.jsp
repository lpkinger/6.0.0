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
<style type="text/css">
.x-form-field-cir-focus{
	-moz-box-shadow:1px 1px 2px rgba(0,0,0,.2); 
	-webkit-box-shadow:1px 1px 2px rgba(0,0,0,.2); 
	box-shadow:1px 1px 2px rgba(0,0,0,.2); 
	-moz-border-radius:5px; 
	-webkit-border-radius:5px; 
	border-radius:5px;
	background: #ffffff !important;
	border: 1px solid #CD950C !important;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'plm.test.CheckBase'
    ],
    launch: function() {
    	Ext.create('erp.view.plm.test.CheckBase');//创建视图
    }
});
/**
 *  生成BUG的按钮
 */
function updateCheckBase(){
	var form  = Ext.getCmp("form");
	var r=form.getValues();
	console.log(r);
	var formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));	
	Ext.MessageBox.confirm('提示','确定生成BUG单吗',function(btn){
		if(btn=='yes'){
			Ext.Ajax.request({
				url:basePath + 'plm/check/checkBaseToBug.action',
				method:'post',
				params:{formStore:formStore},
				callback:function(options,success,response){
		   			var localJson = new Ext.decode(response.responseText);
	    			if(localJson.success){
	    				showMessage('提示', '生成成功!', 1000);
	    				window.location.reload();
		   			} else if(localJson.exceptionInfo){
		   				showMessage(localJson.exceptionInfo);
	        		} 					
				}
			});
		}
	})
}


var caller = 'CheckListBaseDetail';
var emid = '<%=session.getAttribute("em_uu")%>';
var emname='<%=session.getAttribute("em_name")%>';
var formCondition = "" ;
var gridCondition="";
</script>
</head>
<body >
</body>
</html>