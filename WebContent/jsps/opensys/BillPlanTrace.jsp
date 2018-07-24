<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>resource/ext/resources/css/ext-all-gray.css" />
<link rel="stylesheet" href="<%=basePath%>resource/css/main.css"
	type="text/css"></link>
<script type="text/javascript"
	src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/ux/grid/Export.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/ux/data/PagingMemoryProxy.js"></script>
<style type="text/css">
	.x-tree-panel .x-grid-row .x-grid-cell {
    	border: 1px solid #e1e1e1;
    }
    .x-module-item{
		width: 120px;
		height: 30px;
		text-align: center;
	}
	.path{
		clip-path: polygon(0 0, 80% 0, 100% 50%, 80% 100%, 0 100%);
	}
	.finish{
		background-color: rgb(3,159,216);
	} 
	.running{
		background-color: rgb(245,124,0);
	} 
	.font-style{
		color: white;
		line-height: 30px;
	    padding-right: 10%;
	}
	.gray{
		background-color: gray;
	}
</style>
<script type="text/javascript">
	Ext.Loader.setConfig({
		enabled : true
	});//开启动态加载
	Ext.application({
		name : 'erp',//为应用程序起一个名字,相当于命名空间
		appFolder : basePath + 'app',//app文件夹所在路径
		controllers : [//声明所用到的控制层
		'opensys.BillPlanTrace' ],
		launch : function() {
			Ext.create('erp.view.opensys.billPlanTrace.BillPlanTrace');
		}
	});
	var caller = 'BillPlanTrace';
	var condition = getUrlParam('urlcondition');
	var page = 1;
	var value = 0;
	var total = 0;
	var dataCount = 0;//结果总数
	var msg = '';
	var height = window.innerHeight;
	if (Ext.isIE) {//ie不支持window.innerHeight;document.documentElement.clientHeight == 0
		height = screen.height * 0.73;
	}
	var pageSize = parseInt(height * 0.7 / 26);
	var keyField = "";
	var pfField = "";
	var url = "";
	var relative = null;
	var Contextvalue = "";
	var LastValue = "";
</script>
</head>
<body>
</body>
</html>