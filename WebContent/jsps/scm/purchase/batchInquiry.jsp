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
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'scm.purchase.BatchInquiry'
    ],
    launch: function() {
    	Ext.create('erp.view.scm.purchase.BatchInquiry');//创建视图
    }
});
/**
 *  查看具体报价
 */
function productDetail(c,code){
	var code = Ext.getCmp('bi_code').value;
	var url='jsps/common/datalist.jsp?whoami=ProdAutoDetail&urlcondition=id_prodcode=\''+c+'\' and id_incode=\''+code+'\'';
	Ext.create('Ext.window.Window',{
		title: '<span style="color:#CD6839;">具体报价</span>',
		iconCls: 'x-button-icon-set',
		closeAction: 'destory',
		height: "100%",
		width: "80%",
		maximizable : true,
		buttonAlign : 'center',
		layout : 'fit',
		items : [{    
			header:false, 
			html : '<iframe src="'+basePath+url+'"  id="setframe" name="setframe" width="100%" height="100%"></iframe>', 
			border:false 
		}],
	}).show();
}
var caller = 'BatchInquiry';
var formCondition = '';
var gridCondition = ''
var condition = getUrlParam('gridCondition');
</script>
</head>
<body >
</body>
</html>