<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String company ="优软科技有限公司>";
String ma_name = session.getAttribute("ma_name") == null?"欢迎 ":session.getAttribute("ma_name").toString();%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="icon" href="<%=basePath %>resource/images/icon_title.png" type="image/x-icon"/>

<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/fontawesome/css/font-awesome.min.css" type="text/css"></link> 

<link rel="stylesheet" href="resources/css/main-doc.css" type="text/css"></link>
<link rel="stylesheet" href="resources/css/main-doc-header.css" type="text/css"></link>
<link rel="stylesheet" href="resources/css/main-doc-tree.css" type="text/css"></link>
<link rel="stylesheet" href="resources/css/main-doc-panel.css" type="text/css"></link>
<link rel="stylesheet" href="resources/css/main-doc-tab.css" type="text/css"></link>

<link rel="stylesheet" href="<%=basePath %>resource/ux/css/TabScrollerMenu.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script> 
<script type="text/javascript" src="<%=basePath %>resource/ux/TabScrollerMenu.js"></script>
<script type="text/javascript" src="document.js"></script>

<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/data/PagingMemoryProxy.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/Export.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/DataView/LabelEditor.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/DataView/DragSelector.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/layout/component/form/ItemSelector.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/layout/component/form/MultiSelect.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/form/MultiSelect.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<script type="text/javascript">
	removeCss(basePath + 'resource/css/upgrade/gray/main.css');
	function removeCss(filename) {
		var targetelement = "link";
	    var targetattr = "href";
	    var allsuspects = document.getElementsByTagName(targetelement);
	    for (var i = allsuspects.length; i >= 0; i--) {
	        if (allsuspects[i] && allsuspects[i].getAttribute(targetattr) != null && allsuspects[i].getAttribute(
	                targetattr).indexOf(filename) != -1)
	            allsuspects[i].parentNode.removeChild(allsuspects[i])
	    }
	}
</script>
<style type="text/css">
*{
 padding:0;
 margin:0;
}
</style>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',
    controllers: [//声明所用到的控制层
        'oa.doc.DOCManage'
    ],
    launch: function() {
    	Ext.create('erp.view.oa.doc.DOCManage');//创建视图
    }
});
var page = 1;
var height = window.innerHeight;
if(Ext.isIE){//ie不支持window.innerHeight;document.documentElement.clientHeight == 0
	height = screen.height*0.75;
}
var msgCt;
function showResult(title,format,btn){
	  if(!msgCt){
          msgCt = Ext.DomHelper.insertFirst(document.body, {id:'msg-div'}, true);
      }
      var s = Ext.String.format.apply(String, Array.prototype.slice.call(arguments, 1));
      var m = Ext.DomHelper.append(msgCt, createBox(title, s), true);
      m.hide();
      m.slideIn('t').ghost("t", { delay: 1000, remove: true});
};
function createBox(t, s){
    return '<div class="msg"><h3>' + t + '</h3><p>' + s + '</p></div>';
 }
 
var pageSize = parseInt(height*0.7/28);
var pageSize = 13;
var dataCount = 0;
var url = '';
var msg = '';
var caller = 'DOCManage';
var CurrentFolderId=0;
var CurrentKind='folder';
var CurrentShowStyle='list';
var activeId=0;
var company = '<%=company%>';
var ma_name = '<%=ma_name%>';
</script>
</head>
<body>
</body>
</html>