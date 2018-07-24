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
/**
 * 凭证制作、取消
 */
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'fa.VoucherCreate'
    ],
    launch: function() {
    	Ext.create('erp.view.fa.VoucherCreate');//创建视图
    }
});
//制作 or 取消
var isCreate = Number(getUrlParam('ic') || 1) == 1;
//VoucherStyle--vs_code
var vs_code = getUrlParam('vs');
//caller
var caller = getUrlParam('caller');
//类型
var cls = getUrlParam('cls');
//single--单张制作;merge--合并制作
var c_type = getUrlParam('ct') || 'merge';
//AR,AP,...
var vo_type = getUrlParam('vt') || '';
</script>
</head>
<body>
</body>
</html>