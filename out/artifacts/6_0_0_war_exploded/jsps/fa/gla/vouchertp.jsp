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
.x-grid-rowwrap-div {
	border-width: 0;
}
.u-table>tbody>tr:nth-child(odd) {
	background-color: #f9f9f9;
}
.u-table>tbody>tr>td {
	padding: 5px;
	line-height: 1.4;
	vertical-align: top;
	border: solid #ddd;
	border-width: 1px 1px 0 1px;
	position: relative;
}
.u-icon {
	display: none;
	cursor: pointer;
	position: absolute;
	background-repeat: no-repeat;
	width: 16px;
	height: auto;
	top: 6px;
	left: auto;
	bottom: 0;
	right: 5px;
}
.u-table>tbody>tr:hover .u-icon {
	display: block;
}
.text-right {
	text-align: right;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/RowExpander.js"></script>
<script type="text/javascript">
Ext.Array.findBy = function(array, fn, scope) {
    var i = 0,
        len = array.length;

    for (; i < len; i++) {
        if (fn.call(scope || array, array[i], i)) {
            return array[i];
        }
    }
    return null;
};
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'fa.gla.VoucherTP'
    ],
    launch: function() {
    	Ext.create('erp.view.fa.gla.VoucherTP');//创建视图
    }
});
var caller = 'Voucher!TP';
var formCondition = '';
var gridCondition = '';
</script>
</head>
<body >
</body>
</html>