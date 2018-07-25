<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style type="text/css">
#sidebar a:link { 
color:#00688B; 
text-decoration:none; 
} 
#sidebar a:visited { 
color:#00688B; 
text-decoration:none; 
} 
#sidebar a:hover { 
color:#CD2626; 
text-decoration:none; 
} 
#sidebar a:active { 
color:#00688B; 
text-decoration:none; 
} 
.x-livesearch-matchbase{
   font-weight: bold;
   //background-color:#EE6A50;
   color:#EE6A50;
}
  .process {
    background-image: url("<%=basePath%>resource/images/add.png") ;
    float:left;
    color:gray;
    font-size:13px;
    padding-left:2px;
    cursor:pointer;
    width:23px;
    height:23px;
    border: none
   }
   
 .custom .x-grid-cell{
		background-color: #EEE8CD;
	}
	.custom-alt .x-grid-cell{
		background-color: #EAEAEA;
	}
	.custom-first .x-grid-cell{
		border-top-color: #999; 
		border-top-style: dashed;
		background-color: #EEE8CD;
	}
	.custom-alt-first .x-grid-cell{
		border-top-color: #999; 
		border-top-style: dashed;
		background-color: #EAEAEA;
	}
	.custom-grid .x-grid-row-over .x-grid-cell { 
	    background-color: #BCD2EE; 
	    border-bottom-color: #999; 
	    border-top-color: #999; 
	} 
	 
	.custom-grid .x-grid-row-selected .x-grid-cell { 
	    background-color: #BCD2EE !important; 
	}
.x-grid-row-selected .x-grid-cell,.x-grid-row-selected .x-grid-rowwrap-div
	{
	border-style: dotted;
	border-color: #a3bae9;
	background-color:#C1CDC1 !important;
	color: #C1CDC1;
	font-weight: normal
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>app/util/FormUtil.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'common.LaunchProcess'
    ],
    launch: function() {
         Ext.create('erp.view.common.JProcess.LaunchProcess');        
    }
});
var height = window.innerHeight;
var width=window.innerWidth;
var canAdd=getUrlParam('canAdd');
function open(val,meta,record){
    return '<div id="sidebar"><a  style="text-decoration: none;text-align:left;" href="javascript:opennewpanel(\''+ record.data['jd_caller'] + '\',' +'\''+ record.data['js_formurl']+'\''+');">' + val + '</a></div>';
 }
 function opennewpanel(caller,url){
	 Ext.create('erp.util.FormUtil').onAdd(caller, '发起流程('+caller+')',url);
 }
 function operate(val, meta, record) {
	 meta.tdCls = "cell";
	   if(!Ext.isEmpty(val)){
		   var sd_id = 0;
		   return  "<input type='button' name='detailbutton' " + 
		   	"class='process' stlye='background-color:"+meta.tdCls+";' onClick='window.open(\"" + basePath + 
		   	"jsps/scm/sale/saleDetail.jsp?formCondition=sd_id="+sd_id+"&gridCondition=sdd_sdid="+sd_id+"\", \"测试\", \"width=800,height=600,top=30,left=200\")'/>";
	   }
 }
 function openTable(title, url,caller){
		var panel = Ext.getCmp('datalist' + caller); 
		var main = parent.Ext.getCmp("content-panel");
		if(!panel){ 
	    	panel = { 
	    			title : title,
	    			tag : 'iframe',
	    			tabConfig:{tooltip: title},
	    			frame : true,
	    			border : false,
	    			layout : 'fit',
	    			iconCls : 'x-tree-icon-tab-tab',
	    			html : '<iframe id="iframe_maindetail_pageSet" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
	    			closable : true,
	    			listeners : {
	    				close : function(){
	    			    	main.setActiveTab(main.getActiveTab().id); 
	    				}
	    			} 
	    	};
			var p = main.add(panel); 
			main.setActiveTab(p);
		}else{ 
	    	main.setActiveTab(panel);
		} 
	}

</script>
</head>
<body>
</body>
</html>