<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String url = request.getRequestURL().toString();
	String basePath = url.substring(0, url.length()
			- request.getRequestURI().length())
			+ request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css"
	type="text/css"></link>  	
<link rel="stylesheet"
	href="<%=basePath%>resource/css/opensys.css"
	type="text/css"></link>
<style type="text/css">
  .x-panel-header-default-top {
    box-shadow: none;
  }
  .x-panel-body-default {
    background: white;
   } 
  .x-panel-header-default{
        background: white;
        padding:2px 0px 0px 0px ;
  }
  .x-panel-header-text-container-default {
      padding: 0 2px 0px;
  }
  .x-panel-header-text-container{
    border-bottom: solid 6px #DFE2E2;
  }
   .x-panel-header-text-default{
    background: url(<%=basePath%>resource/images/title.png) no-repeat;
    border: none;
	height: 22px;
	width: 108px;
    line-height: 22px;
	float: left;
	margin-left: -5px !important;
	text-decoration: none;
	border-top-left-radius:12px;
	overflow: hidden;
    outline: none;
    padding-left: 9px;
    display: block;
  } 

   .x-toolbar-default{
     background: white;
   }
 .x-toolbar-item {
    margin: 0 8px 0 0;
}
.x-btn-default-toolbar-small {
    -webkit-border-radius: 3px;
    -moz-border-radius: 3px;
    -ms-border-radius: 3px;
    -o-border-radius: 3px;
    border-radius: 3px;
    padding: 3px 3px 3px 3px;
    border-width: 1px;
    border-style: solid;
    background-image: none;
    background-color: #f5f5f5;
    background-image: -webkit-gradient(linear,50% 0,50% 100%,color-stop(0%,#f6f6f6),color-stop(50%,#f5f5f5),color-stop(51%,#e8e8e8),color-stop(100%,#f5f5f5));
    background-image: -webkit-linear-gradient(top,#f6f6f6,#f5f5f5 50%,#e8e8e8 51%,#f5f5f5);
    background-image: -moz-linear-gradient(top,#f6f6f6,#f5f5f5 50%,#e8e8e8 51%,#f5f5f5);
    background-image: -o-linear-gradient(top,#f6f6f6,#f5f5f5 50%,#e8e8e8 51%,#f5f5f5);
    background-image: linear-gradient(top,#f6f6f6,#f5f5f5 50%,#e8e8e8 51%,#f5f5f5);
}
.x-btn-default-toolbar-small {
    border-color: #e1e1e1;
}
.x-btn {
    display: inline-block;
    position: relative;
    zoom: 1;
    outline: 0;
    cursor: pointer;
    white-space: nowrap;
    vertical-align: middle;
    text-decoration: none;
}
.x-box-item {
    position: absolute!important;
    left: 0;
    top: 0;
}
.x-btn-default-toolbar-small-over {
  background-image: none;
  background-color: #ebebeb;
  background-image: -webkit-gradient(linear, 50% 0%, 50% 100%, color-stop(0%, #ededed), color-stop(50%, #ebebeb), color-stop(51%, #dfdfdf), color-stop(100%, #ebebeb));
  background-image: -webkit-linear-gradient(top, #ededed, #ebebeb 50%, #dfdfdf 51%, #ebebeb);
  background-image: -moz-linear-gradient(top, #ededed, #ebebeb 50%, #dfdfdf 51%, #ebebeb);
  background-image: -o-linear-gradient(top, #ededed, #ebebeb 50%, #dfdfdf 51%, #ebebeb);
  background-image: linear-gradient(top, #ededed, #ebebeb 50%, #dfdfdf 51%, #ebebeb);
}

.x-btn-default-toolbar-small-focus {
  background-image: none;
  background-color: #ebebeb;
  background-image: -webkit-gradient(linear, 50% 0%, 50% 100%, color-stop(0%, #ededed), color-stop(50%, #ebebeb), color-stop(51%, #dfdfdf), color-stop(100%, #ebebeb));
  background-image: -webkit-linear-gradient(top, #ededed, #ebebeb 50%, #dfdfdf 51%, #ebebeb);
  background-image: -moz-linear-gradient(top, #ededed, #ebebeb 50%, #dfdfdf 51%, #ebebeb);
  background-image: -o-linear-gradient(top, #ededed, #ebebeb 50%, #dfdfdf 51%, #ebebeb);
  background-image: linear-gradient(top, #ededed, #ebebeb 50%, #dfdfdf 51%, #ebebeb);
}   
   .x-border-layout-ct {
    background-color:white;
  }   
</style>
<script type="text/javascript"
	src="<%=basePath%>resource/ext/4.2/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [
        'opensys.Home'
    ],
    launch: function() {
    	Ext.create('erp.view.opensys.home.ViewPort');//创建视图
    }
});
var enUU  = '<%=session.getAttribute("enUU")%>';
var em_code ='<%=session.getAttribute("em_code")%>';
var em_name ='<%=session.getAttribute("em_name")%>'; 
var cu_name ='<%=session.getAttribute("cu_name")%>'; 
var em_id ='<%=session.getAttribute("em_id")%>'; 
var emUU  = '<%=session.getAttribute("em_uu")%>';
var cu_code  = '<%=session.getAttribute("cu_code")%>';
</script>
</head>
<body>
	<div id="legalese" style="display: none;">
		<h2>使用条款</h2>
	</div>
</body>
</html>