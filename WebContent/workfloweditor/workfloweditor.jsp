<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
    <title>UAS-ERP工作流设计器</title>
    <style>
     .x-grid3-row{border-width:1px 0;border-color:#ededed;border-style:solid;border-top-color:#fafafa;overflow:hidden}
.x-grid-rowwrap-div{border-width:1px 0;border-color:#ededed;border-style:solid;border-top-color:#fafafa;overflow:hidden}
.x-grid3-row-alt .x-grid3-cell,.x-grid-row3-alt .x-grid3-rowwrap-div{background-color:#EAEAEA}
.x-grid3-row-over .x-grid3-cell,.x-grid3-row-over .x-grid3-rowwrap-div{border-color:#EAEAEA;background-color:#BCD2EE}
.x-grid3-row-focus .x-grid3-cell,.x-grid3-row-focused .x-grid3-rowwrap-div{border-color:#dddddd;background-color:#efefef}
    </style>
  </head>
 <script type="text/javascript" >
 var joborgnorelation = '<%=session.getAttribute("joborgnorelation")%>';
 </script>
<body id="body" >
    <link rel="stylesheet" type="text/css" href="./scripts/loading/loading.css" />
    <div id="loading-mask"></div>
    <div id="loading">
        <div class="loading-indicator"><img src="scripts/loading/extanim32.gif" align="absmiddle"/>正在加载数据...</div>
    </div>
    <link rel="stylesheet" type="text/css" href="scripts/ext-2.0.2/resources/css/ext-all.css" />
    <script type="text/javascript" src="scripts/ext-2.0.2/ext-base.js"></script>
    <script type="text/javascript" src="scripts/ext-2.0.2/ext-all.js"></script>
    <script type="text/javascript" src="scripts/ext-2.0.2/ext-lang-zh_CN.js"></script> 
    <script type="text/javascript">
Ext.BLANK_IMAGE_URL = 'scripts/ext-2.0.2/resources/images/default/s.gif';
    </script>
    <link rel="stylesheet" type="text/css" href="scripts/ux/ext-patch.css" />

	<script type="text/javascript" src="scripts/gef/scripts/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="scripts/gef/scripts/all-core.js"></script>
    <script type="text/javascript">
Gef.IMAGE_ROOT = 'scripts/gef/images/activities/48/';
    </script>
    <script type="text/javascript" src="scripts/gef/all-editor.js"></script>
    <script type="text/javascript" src="scripts/all-workflow.js"></script>
    <script type="text/javascript" src="scripts/validation/all-validation.js"></script>
    <script type="text/javascript" src="scripts/form/all-forms.js"></script>
    <script type='text/javascript' src='scripts/property/all-property.js'></script>
    <script type='text/javascript' src='scripts/ux/checkboxtree/Ext.lingo.JsonCheckBoxTree.js'></script>
    <link rel='stylesheet' type='text/css' href='scripts/ux/checkboxtree/Ext.lingo.JsonCheckBoxTree.css' />
    <script type="text/javascript" src="scripts/org/OrgField.js"></script>
    <link rel="stylesheet" type="text/css" href="./styles/jbpm4.css" />
    <link rel="stylesheet" type="text/css" href="./styles/org.css" />
    <script type='text/javascript' src='scripts/ux/treefield/Ext.lingo.TreeField.js'></script>
    <script type='text/javascript' src='scripts/ux/localXHR.js'></script>
    
   
    <script type='text/javascript'>
    function getUrlParam(name) {
		var reg=new RegExp("(^|&)"+name+"=([^&]*)(&|$)");   
    	var r=window.location.search.substr(1).match(reg);   
    	if(r!=null)   
    		return decodeURI(r[2]); 
    	return null; 
	};
	var basePath = (function() {
		var fullPath = window.document.location.href;
		var path = window.document.location.pathname;
		var subpos = fullPath.indexOf('//');
		var subpath = subpos > -1 ? fullPath.substring(0, subpos + 2) : '';
		if (subpos > -1)
			fullPath = fullPath.substring(subpos + 2);
		var pos = fullPath.indexOf(path), sname = path.substring(0, path.substr(1).indexOf('/') + 1);
		sname = (['/jsps','/workfloweditor','/resource'].indexOf(sname) > -1 ? '/' : sname);
		return subpath + fullPath.substring(0, pos) + sname + (sname == '/' ? '' : '/');
	})();
    var comboxdata="";
    Ext.Ajax.request({
		url : basePath +"common/getAllJrocessButton.action",
		method : 'post',
		callback : function(options,success,response){    	    	   		
			var localJson = new Ext.decode(response.responseText);	
			comboxdata=localJson.buttons;
		}
	});	 
Gef.ORG_URL = 'org.json';
var en_uu = '<%=session.getAttribute("en_uu")%>';
    </script>
    <style type="text/css">
	    #pageh1{
		    font-size:36px;
			font-weight:bold;
			background-color:#C3D5ED;
			padding:5px;
		}
	</style>
  </body>
</html>