<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"
    import="com.uas.erp.model.Employee"
    %>
    
<%

String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
/* String sob = SpObserver.getSp(); */
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<style type="text/css">
.x-tree-panel .x-grid-row-over .x-grid-cell,.x-tree-panel .x-grid-row-over .x-grid-rowwrap-div {
    background-color: #F5F5F5;
	color: #3285ff;
}
.x-actioncol-commonuse {
	height: 100%;
    width: 16px;
    background-position-y: center;
    background-repeat: no-repeat;
	background-image: url(<%=basePath %>resource/images/upgrade/bluegray/mainicon/lock.png);
}
.x-actioncol-nocommonuse {
	height: 100%;
    width: 16px;
    background-position-y: center;
    background-repeat: no-repeat;
	background-image: url(<%=basePath %>resource/images/upgrade/bluegray/mainicon/unlock.png);
}
.x-module-parent {
	background-color: rgb(242, 242, 242);
	border: 1px solid blue;
	width:200px;/*表示容器的高度*/
	height:1133px;/*表示容器的宽度*/
	overflow-y: scroll;
	padding:0px;margin:0px;
	-webkit-transform-origin:0px 0px;
	-ms-transform-origin:0px 0px;
	-moz-transform-origin:0px 0px;
	-o-transform-origin:0px 0px;
	transform-origin:0px 0px;
	-webkit-transform:rotate(-90deg) translate(-200px/*这个值为负的width*/);
	-ms-transform:rotate(-90deg) translate(-200px);
	-moz-transform:rotate(-90deg) translate(-200px);
	-o-transform:rotate(-90deg) translate(-200px);
    transform:rotate(-90deg) translate(-200px);
    padding: 10px 5px;
}
.x-module-item {
	width:136px;
	height:26px;
  	margin-right:-110px;/*该值  = lineHeight - width*/
  	margin-bottom: 150px;/*设置列间隔，实际效果值 = 该值 + margin-right*/
  	padding-left: 5px;
  	padding-right: 24px;
  	line-height:26px; /*该值 = height*/
  	list-style-position:inside;float:right;
  	-webkit-transform-origin:0px 0px;
	-ms-transform-origin:0px 0px;
	-moz-transform-origin:0px 0px;
	-o-transform-origin:0px 0px;
	transform-origin:0px 0px;
  	-webkit-transform:rotate(90deg) translateY(-26px/*该值 = lineHeight*/);
  	-ms-transform:rotate(90deg) translateY(-26px);
	-moz-transform:rotate(90deg) translateY(-26px);
	-o-transform:rotate(90deg) translateY(-26px);
  	transform:rotate(90deg) translateY(-26px);
  	overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}
.x-module-title {
    font-family: MicrosoftYaHei;
    font-weight: bold;
    font-size: 15px;
    color: #1f23fa;
    margin-right: -100px;
}
.x-module-ungroup-title {
	/* color: darkgray; */
}
.x-module-child {
	font-family: MicrosoftYaHei;
	font-weight: normal;
	font-size: 13px;
	color: #333333;
	padding-left: 16px;
	cursor: pointer;
}
.x-module-over {
	background-color: #C9DCEB;
    border-radius: 4px;
}
.x-module-delete-icon{
	display: none;
	opacity: .5;
	background-image: url(<%=basePath %>resource/images/upgrade/bluegray/icon/maindetail/tbar/refresh.png);
    width: 16px;
    height: 100%;
    background-repeat: no-repeat;
    line-height: 100%;
    background-position-y: center;
    right: 5px;
    cursor: pointer;
    position: absolute;
    top: 0px;
}
.x-module-delete-icon:HOVER {
	opacity: 1;
}
.x-module-over .x-module-delete-icon {
	display: block;
}
.x-module-delete-icon::HOVER {
	opacity: 1;
}
.x-item-selected {
	border-radius: 4px;
	border: 1px solid #BDBDBD;
}
.x-dd-drag-ghost .x-module-item {
	-webkit-transform:rotate(0deg) translateY(0);
  	-ms-transform:rotate(0deg) translateY(0);
	-moz-transform:rotate(0deg) translateY(0);
	-o-transform:rotate(0deg) translateY(0);
  	transform:rotate(0deg) translateY(0);
}
.x-dd-drop-nodrop .x-dd-drop-icon {
	height: 100%;
    top: 0;
}
.x-dd-drop-ok .x-dd-drop-icon {
	height: 100%;
    top: 0;
}
.x-target-drag {
	opacity: .5;
}
.x-target-hover-below::AFTER{
	content: ' ';
    width: 100%;
    height: 4px;
    position: absolute;
    left: 0px;
    top: 23px;
    background-color: #878484;
}
.x-target-hover-above::AFTER{
	content: ' ';
    width: 100%;
    height: 4px;
    position: absolute;
    left: 0px;
    top: 0px;
    background-color: #878484;
}
.x-checker-on {
	height: 16px;
    width: 16px;
    background-image: url(<%=basePath %>resource/images/upgrade/bluegray/icon/maindetail/checked.png);
}
.x-checker-off {
	height: 16px;
    width: 16px;
    background-image: url(<%=basePath %>resource/images/upgrade/bluegray/icon/maindetail/unchecked.png);
}

</style>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'common.ButtonGroupSet'
    ],
    launch: function() {
    	Ext.create('erp.view.common.ButtonGroupSet');//创建视图
    }
});
var caller = getUrlParam('caller');
var button4rw = parent.Ext.getCmp('buttonGroupSetWin').button4rw;
function union_array(a,b) {
    for (var i = 0, j = 0, ci, r = {}, c = []; ci = a[i++] || b[j++]; ) {
        if (r[ci]) continue;
        r[ci] = 1;
        c.push(ci);
    }
    return c;
}
</script>
</head>
<body >
</body>
</html>