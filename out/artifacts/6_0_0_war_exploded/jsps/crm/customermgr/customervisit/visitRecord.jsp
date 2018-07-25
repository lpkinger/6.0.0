<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<!--[if IE]>
	<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-ie-scoped.css" type="text/css"></link>
<![endif]-->

<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/layout/component/form/ItemSelector.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/layout/component/form/MultiSelect.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/form/MultiSelect.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'crm.customermgr.customervisit.VisitRecord'
    ],
    launch: function() {
    	Ext.create('erp.view.crm.customermgr.customervisit.VisitRecord');//创建视图
    }
});
var caller = 'VisitRecord';
var em_uu = '<%=session.getAttribute("em_uu")%>';
var em_code = '<%=session.getAttribute("em_code")%>';
var formCondition = '';
var gridCondition = '';
var height = window.innerHeight*0.3; 
function showchancestatus(code){
	 Ext.create('Ext.window.Window', {
		    height: 200,
		    title:'<h2>进展明细</h2>',
		    width: 900,
		    layout: 'fit',
		    items:[/* {
               xtype:'erpBatchDealGridPanel',
              condition:"ch_code='"+code+"'",
              caller:'ChanceManage'
			    } */
             Ext.create('erp.view.crm.chance.ChanceManage',{
            	 condition:"ch_code='"+code+"'",
                 caller:'ChanceManage',
                 height:200
                 })
				    ] 
		}).show();
}
</script>
</head>
<body >
</body>
</html>