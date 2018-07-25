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
#sidebar a:link { 
color:#1C86EE; 
text-decoration:none; 
} 
#sidebar a:visited { 
color:#1C86EE; 
text-decoration:none; 
} 
#sidebar a:hover { 
color:#CD2626; 
text-decoration:none; 
} 
#sidebar a:active { 
color:#1C86EE; 
text-decoration:none; 
} 
.x-livesearch-matchbase{
   font-weight: bold;
   //background-color:#EE6A50;
   color:#EE6A50;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/data/PagingMemoryProxy.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/Export.js"></script>
<script type="text/javascript" src="<%=basePath%>jsps/oa/knowledge/Knowledge.js"></script>
<script type="text/javascript">	
function order(i){
 if(i==1){
  Ext.getCmp('rankfield').setValue('最新知识');
  var data=Ext.Array.sort(BaseData.items, function(a, b){
        	return new Date(b.data.kl_addtime)-new Date(a.data.kl_addtime);
        });
   BaseStore.loadData(data); 
   change('kl_addtime');    
 }else if(i==2){
  Ext.getCmp('rankfield').setValue('热门点击');
  var data=Ext.Array.sort(BaseData.items, function(a, b){
        	return parseInt(b.data.kl_scantimes)-parseInt(a.data.kl_scantimes);
        });
   BaseStore.loadData(data); 
   change('kl_scantimes'); 
 }else if(i==3){
  Ext.getCmp('rankfield').setValue('强力推荐');
  var data=Ext.Array.sort(BaseData.items, function(a, b){
        	return parseInt(b.data.kl_recommonedtimes)-parseInt(a.data.kl_recommonedtimes);
        });
   BaseStore.loadData(data); 
   change('kl_recommonedtimes'); 
 }else if(i==4){
  Ext.getCmp('rankfield').setValue('最佳知识');
  var data=Ext.Array.sort(BaseData.items, function(a, b){
        	return parseInt(b.data.kl_point)-parseInt(a.data.kl_point);
        });
   BaseStore.loadData(data); 
   change('kl_point');
 }
}
function change(header){
     var me = Ext.getCmp('grid'),
             count = 0;
         me.view.refresh();
         me.indexes = [];
         var cellIndex=0;
         for(var i=0;i<me.columns.length;i++){
            if(me.columns[i].dataIndex==header) cellIndex=i;
         }
             me.store.each(function(record, idx) {
                 var td = Ext.fly(me.view.getNode(idx)).down('td'),
                     cell, cellHTML;               
                 while(td) {
                     cell = td.down('.x-grid-cell-inner');            
                     if(cell.dom.offsetParent.cellIndex==cellIndex) { 
                     cell.dom.innerHTML ='<span class=" x-livesearch-matchbase">' +cell.dom.innerText + '</span>';}
                     else cell.dom.innerHTML=cell.dom.innerHTML;
                     td = td.next();
                 }
             }, me);
}
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'oa.knowledge.KnowledgeRank'
    ],
    launch: function() {
    	Ext.create('erp.view.oa.knowledge.KnowledgeRank');//创建视图
    }
});
var page = 1;
var height = window.innerHeight;
if(Ext.isIE){//ie不支持window.innerHeight;document.documentElement.clientHeight == 0
	height = screen.height*0.75;
}
var pageSize = parseInt(height*0.7/28);
var pageSize = 13;
var dataCount = 0;
var url = '';
var msg = '';
var caller = 'RecKnowledge';
var BaseStore='';
var BaseData='';
var emid='<%=session.getAttribute("em_id")%>';
</script>
</head>
<body >
</body>
</html>