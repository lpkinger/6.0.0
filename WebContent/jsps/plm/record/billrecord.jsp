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
.search-item {
	font: normal 11px tahoma, arial, helvetica, sans-serif;
	padding: 3px 10px 3px 10px;
	border: 1px solid #fff;
	border-bottom: 1px solid #eeeeee;
	white-space: normal;
	color: #555;
}

.search-item h3 {
	display: block;
	font: inherit;
	font-weight: bold;
	color: #222;
}

.search-item h3 span {
	float: right;
	font-weight: normal;
	margin: 0 0 5px 5px;
	width: 150px;
	display: block;
	clear: none;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
var _i = getUrlParam('ra_id'), _c = getUrlParam('ra_type');
if(_i != null) {
	if(_c != 'billtask') {
		window.location.href = basePath + 'jsps/plm/record/workrecord.jsp?_noc=1&formCondition=ra_idIS' + _i + '&gridCondition=wr_raidIS' + _i;
	} else {
		window.location.href = basePath + 'jsps/plm/record/billrecord.jsp?_noc=1&formCondition=ra_idIS' + _i;
	}
} else {
	Ext.Loader.setConfig({
		enabled: true
	});//开启动态加载
	Ext.application({
	    name: 'erp',//为应用程序起一个名字,相当于命名空间
	    appFolder: basePath+'app',//app文件夹所在路径
	    controllers: [//声明所用到的控制层
	        'plm.record.BillRecord'
	    ],
	    launch: function() {
	    	Ext.create('erp.view.plm.record.BillRecord');//创建视图
	    }
	});
	var caller = 'ResourceAssignment!Bill';
	var recorder = '<%=session.getAttribute("em_name")%>';
	var emid = '<%=session.getAttribute("em_uu")%>';
	var formCondition = "";
}
</script>
</head>
<body >
</body>
</html>