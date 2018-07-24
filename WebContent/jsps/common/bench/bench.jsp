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
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css"/>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>jsps/common/bench/css/centerform1.css"  type="text/css"></link>
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" />
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
        'common.bench.Bench'
    ],
    launch: function() {
        Ext.create('erp.view.common.bench.Bench');
    }
});
var bench = getUrlParam('bench');
var business = getUrlParam('business');
var scene = getUrlParam('scene');
var em_name = '<%=session.getAttribute("em_name")%>';
var em_uu = '<%=session.getAttribute("em_uu")%>';
var em_code = '<%=session.getAttribute("em_code")%>';
var en_email ='<%=session.getAttribute("en_email")%>';
var em_type = '<%=session.getAttribute("em_type")%>';
var em_id ='<%=session.getAttribute("em_id")%>';
var em_defaulthsid = '<%=session.getAttribute("em_defaulthsid")%>';

function parseUrl(url) {
	if(!url){
		return null;
	}	
    var id = url.substring(url.lastIndexOf('?') + 1); //将作为新tab的id
    if (id == null) {
        id = url.substring(0, url.lastIndexOf('.'));
    }
    if (contains(url, 'session:em_uu', true)) { //对url中session值的处理
        url = url.replace(/session:em_uu/g, em_uu);
    }
    if (contains(url, 'session:em_code', true)) { //对url中em_code值的处理
        url = url.replace(/session:em_code/g, "'" + em_code + "'");
    }
    if (contains(url, 'sysdate', true)) { //对url中系统时间sysdate的处理
        url = url.replace(/sysdate/g, "to_date('" + Ext.Date.toString(new Date()) + "','yyyy-mm-dd')");
    }
    if (contains(url, 'session:em_name', true)) {
        url = url.replace(/session:em_name/g, "'" + em_name + "'");
    }
    if (contains(url, 'session:em_type', true)) {
        url = url.replace(/session:em_type/g, "'" + em_type + "'");
    }
    if (contains(url, 'session:em_id', true)) {
        url = url.replace(/session:em_id/g,em_id);
    }
    if (contains(url, 'session:em_depart', true)) {
        url = url.replace(/session:em_depart/g,em_id);
    }
    if (contains(url, 'session:em_defaulthsid', true)) {
        url = url.replace(/session:em_defaulthsid/g,em_defaulthsid);
    }
    return url;
}

function getStringParam(str,name){
	var reg=new RegExp("(^|&|\\?)"+name+"=([^&]*)(&|$)"); 
    var r=str.match(reg); 
    if  (r!=null)   
		return decodeURI(r[2]);
    return null;
}

function setActiveScene(bbcode, bscode) {
	var switchBtns = Ext.getCmp('switch');
	scene = bscode;
	Ext.Array.each(switchBtns.items.items, function(btn) {
		if (btn.data.bb_code == bbcode) {
			switchBtns.setActive(btn);
			return;
		}
	});			
}
</script>
</head>
<body>
</body>
</html>