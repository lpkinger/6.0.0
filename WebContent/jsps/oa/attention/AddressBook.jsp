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
.x-livesearch-matchbase{
   font-size: 14px !important;
	font-weight: normal!important;
}
.x-tree-cls-node{
	background-image:  url('<%=basePath %>resource/ext/resources/themes/images/back.jpg') ;
	background-color: #f8f8f8;
	height: 21px !important;
	background:#f0f0f0 !important;
}
.x-tree-cls-node:hover,.x-tree-cls-parent:hover{
	font-size: 14px !important;
	font-weight: normal!important;
	color:black !important;
	background-image:url('<%=basePath %>resource/ext/resources/themes/images/background_1.jpg');
} 
.x-tree-cls-root:hover{
	font-size: 14px !important;
}
.btn-cls{
//  border:none;
  //background-image:  url('<%=basePath %>resource/ext/resources/themes/images/back.jpg') ;
  background:#F0F0F0;
}
.btn-cls:hover{
 boder:1px;
 background:#E6E6FA;
}
.btn-basecls{
margin-left:10px;background:#CFCFCF;border:1px solid #8B8386;
}
.x-livesearch-match {
    font-weight: lighter;
    background-color:#EED8AE;
}
.x-livesearch-matchbase{
   font-weight: bold;
   background-color:#EE6A50;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
var caller = 'AddressBook';
var gridid='AttentionGridPanel';
var basePath="<%=basePath %>";
var formCondition = '';
var gridCondition = '';
var emid=<%=session.getAttribute("em_id")%>;
      Ext.application({
        name: 'erp',//为应用程序起一个名字,相当于命名空间
        appFolder: basePath+'app',//app文件夹所在路径
       controllers: [//声明所用到的控制层
        'oa.attention.AddressBook'
       ],
       launch: function() {
    	Ext.create('erp.view.oa.attention.AddressBook');//创建视图
        }
      });
function openUrl(keyValue, keyField) {
  var win = new Ext.window.Window({
	    		   id : 'win',
	    		   height: '80%',
	    		   width: '60%',
	    		   title:'添加联系人',
	    		   maximizable : true,
	    		   buttonAlign : 'center',
	    		   layout : 'anchor',
	    		   items: [{
	    			   tag : 'iframe',
	    			   frame : true,
	    			   anchor : '100% 100%',	
	    			   xtype:'erpAttentionFormPanel',
	    			   caller:'AddressBook'	, 
	    			   formCondition:keyField+"="+keyValue,
	    			   updateUrl:'/oa/addressbook/updateAddressPerson.action', 				       
	    			   bbar:['->',{
	    				   xtype:'erpUpdateButton',
	    				   id:'updatebutton',
	    				   handler:function(){
	    				     Ext.getCmp('form').update();
	    				     Ext.getCmp('win').close();
	    				     var groupid=Ext.getCmp('groupid').getValue();
		                     var findcondition=(groupid==0) ?'ab_recorderid='+emid : 'ab_groupid='+groupid+'  AND ab_recorderid='+emid;
	                         var gridParam = {caller: caller, condition:findcondition };
	                         var grid=Ext.getCmp('AttentionGridPanel');
    	                     grid.loadNewStore(grid, gridParam);  
	    				   }
	    			   },{
	    				   xtype:'erpCloseButton',
	    				   handler:function(){
	    					   Ext.getCmp('win').close();
	    				   } 
	    			   },'->']         
	    		   }],

	    	   });
	    	   win.show();
		
}
</script>
</head>
<body >
</body>
</html>