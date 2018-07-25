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
	 #ruleSearch .x-form-element{
	 	padding:2px 0 0 2px!important
	 }
    </style>
    
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
		var joborgnorelation = '<%=session.getAttribute("joborgnorelation")%>';
    </script>
    
    <script type="text/javascript" >
       /*  var xmlInfo=''; */
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
    	function getUrlParam(name) {
			var reg=new RegExp("(^|&)"+name+"=([^&]*)(&|$)");   
	    	var r=window.location.search.substr(1).match(reg);   
	    	if(r!=null)   
	    		return decodeURI(r[2]); 
	    	return null; 
		};
		function loadingData(jdId)
		{  
			/* var jdId =getUrlParam('jdId'); */
			var xmlInfo='';
			Ext.Ajax.request({
		    url: basePath + 'common/getJProcessDeployInfo.action',
		    params: {
		        jdId: jdId,
		        _noc:1
		    },
		    success: function(response){
		    	var text = new Ext.decode(response.responseText);
		         xmlInfo = text.xmlInfo;
		         }
			});
			return xmlInfo; /* 回调函数 不执行 这行代码 **/
		};
		var comboxdata=""; 
		var formCaller = "";
 	</script> 
 	
  </head>
  

<body id="body" >
    <link rel="stylesheet" type="text/css" href="scripts/ux/ext-patch.css" />
	<script type="text/javascript" src="scripts/gef/scripts/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="scripts/gef/scripts/all-core.js"></script>
    
    <script type="text/javascript">
		Gef.IMAGE_ROOT = 'scripts/gef/images/activities/48/';
    </script>
    
    <script type="text/javascript" src="scripts/gef/all-editor.js"></script>
    <script type="text/javascript" src="scripts/all-workflow2.js"></script>
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
    <!-- <script type='text/javascript' src='../app/util/BaseUtil.js'></script> -->
    <script type='text/javascript'>
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
	<!-- <script type="text/javascript">
	/* 	var xmlInfo = "<process xmlns='http://jbpm.org/4.4/jpdl'>"+
	"<start g='253,67,48,48' name='start 1'>"+
	"<transition to='end 1'/></start>"+
	"<end g='247,239,48,48' name='end 1'/></process>"; */
	 /* var xmlInfo = "<process xmlns='http://jbpm.org/4.4/jpdl'>"+
		  "<start g='253,67,48,48' name='start 1'>" +
	    "<transition name='同意' to='审批'/>"+
	  "</start>"+
	  "<end g='213,330,48,48' name='end 1'/>"+
	  "<task assignee='王锡爵' g='95,161,90,50' name='审批'>"+
	    "<description>鸟才</description>"+
	    "<transition name='同意' to='查询'/>"+
	  "</task>"+
	  "<sql var='v' unique='true' g='312,191,90,50' name='查询'>"+
	    "<query>select *</query>"+
	    "<parameters><object name='hah'; expr='dddd'></object></parameters>"+
	    "<description>雕塑</description>"+
	    "<transition name='同意' to='end 1'/>"+
	  "</sql>"+
	"</process>"; */
	/*  var xmlInfo = "<process xmlns='http://jbpm.org/4.4/jpdl' name='Purchase'><start g='241,61,48,48' name='start 1'><transition to='task 1'/></start> <end g='250,298,48,48' name='end 1'/><task g='266,157,90,50' name='task 1'><transition name='同意' to='end 1'/><transition name='不同意' to='cancel 1'/></task><end-cancel g='125,181,48,48' name='cancel 1'/></process>"; */
	function getUrlParam(name) {
		var reg=new RegExp("(^|&)"+name+"=([^&]*)(&|$)");   
	    var r=window.location.search.substr(1).match(reg);   
	    if(r!=null)   
	    	return decodeURI(r[2]); 
	    return null; 
	};
	var jdId =getUrlParam('jdId'); 
	console.log(jdId);
	console.log(typeof(jdId));
	console.log(jdId);
	var xmlInfo='';
	Ext.Ajax.request({
    url: basePath + 'common/getJProcessDeployInfo.action',
    params: {
        jdId: jdId
    },
    success: function(response){
    	var text = new Ext.decode(response.responseText);
        console.log(text);
        xmlInfo = text.xmlInfo;
        console.log(xmlInfo);
    }
});
	</script>
	 -->
  </body>
</html>