<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
request.setCharacterEncoding("utf-8");
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" />
<style>
	.deleted .x-grid-cell{
		font-style: italic;
		color: gray;
		text-decoration: line-through;
	}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'ma.MultiGrid'
    ],
    launch: function() {
    	Ext.create('erp.view.ma.MultiGrid');//创建视图
    }
});
	var caller = "Form";
	var whoami = "";
	var dbfinds = [];
	var formCondition = '';
	var gridCondition = '';
	var em_type="<%=session.getAttribute("em_type")%>";
	function Delete(){
		var grid=Ext.getCmp('dbgrid');
	    var lastselected=grid.getSelectionModel().getLastSelected();
	    grid.getStore().remove(lastselected);
	}
	function DeleteGrid(){
		var grid=Ext.getCmp('dbGridgrid');
		 var lastselected=grid.getSelectionModel().getLastSelected();
		 var id=lastselected.data.ds_id;
		 if(id!=null){
			 //存在ID 则后台删除
			 Ext.Ajax.request({
					url : basePath + 'common/deleteDbFindSetGrid.action',
					params : {
						id:id,
					},
					method : 'post',
					callback : function(options,success,response){
						var res=new Ext.decode(response.responseText);
						if(res.exceptionInfo != null){
							showError(res.exceptionInfo);return;
						}
						if(res.success){
							Ext.Msg.alert('提示','删除成功!');				                 
						}
					}
				});
		 }
		 grid.getStore().remove(lastselected);
	}
</script>
</head>
<body >
</body>
</html>