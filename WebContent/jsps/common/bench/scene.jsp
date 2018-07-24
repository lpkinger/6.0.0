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
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/data/PagingMemoryProxy.js"></script>
<style type="text/css">
.x-grid-row-checker, .x-column-header-checkbox .x-column-header-text {
    background-repeat: no-repeat;
    background-color: transparent;
    height: 24px;
    width: 19px;
    padding: 0px !important;
    margin: 0px !important;
    margin-top: 5px !important;
    margin-left: 2px !important;
    background-size: contain;
}
</style>
<style type="text/css">
.form .x-form-display-field {
    font-weight: 500;
    font-size: 13px;
    color: black;
    width: 60px;
    padding: 10px 0px 0px;
}
.x-grid-row-over .x-grid-cell,.x-grid-row-over .x-grid-rowwrap-div {
	color: black;
	border: 1px solid #6c6cff !important; 
	border-left-color: #c5c5c5 !important;
	border-right-color: #c5c5c5 !important;
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
        'common.bench.Scene'
    ],
    launch: function() {
        Ext.create('erp.view.common.bench.Scene');
    }
});
var Scene = getUrlParam('Scene');
var condition = '';
var page = 1;
var value = 0;
var total = 0;
var dataCount = 0;//结果总数
var repeatCount = false;
var msg = '';

var height = window.innerHeight;
if(Ext.isIE){//ie不支持window.innerHeight;document.documentElement.clientHeight == 0
	height = screen.height*0.73;
}

//var pageSize = parseInt(height*0.7/23);
var pageSize = parseInt((height-133)/27);  //减去头尾高度

function selectRecord(grid,store){//数据回显
	var datachecked=new Array();
	Ext.each(Ext.Object.getKeys(grid.selectObject),function(k){
		datachecked.push(grid.selectObject[k]);
	});
	if(datachecked.length>0){
		var selectArr=new Array();
		if(!store){
			store = grid.store;
		}
		Ext.each(store.data.items, function(item){
			delete item.data.RN;
			Ext.each(datachecked,function(checked){
				var checkflag=true;
				var keys = new Array();
				if(grid.keyField != null && grid.keyField != ''){
	    		   	if(grid.keyField.indexOf('+') > 0) {
	    		   		var arr = grid.keyField.split('+'), ff = [];
					   	Ext.Array.each(arr, function(r){
						   ff = r.split('@');
						   keys.push(ff[1]);
					   });
	    		   	} else {
	    		   		keys.push(grid.keyField);
	    		   	}
	        	}else{
	        		keys=Ext.Object.getKeys(item.data);
	        	}
				for(var i=0;i<keys.length && checkflag;i++){
					var k=keys[i];
					if(checked[k] instanceof Date){
						if(Ext.Date.format(item.data[k], 'Y-m-d')!=Ext.Date.format(checked[k], 'Y-m-d')){
							checkflag=false;
							break;
						}
					}else{
						if(item.data[k]!=checked[k]){
							checkflag=false;
							break;
						}
					}
					
					if(i==keys.length-1&&checkflag){
						selectArr.push(item);
					}
				} 					
			});
		});
		grid.selModel.select(selectArr);
	}
}

function getStringParam(str,name){
	var reg=new RegExp("(^|&|\\?)"+name+"=([^&]*)(&|$)"); 
    var r=str.match(reg); 
    if  (r!=null)   
		return decodeURI(r[2]);
    return null;
}

//给datalist加上ctrl+alt+s键盘事件,自动跳转datalist配置界面
function onDatalistKeyDown(){
	if(Ext.isIE && !Ext.isIE11){
		document.body.attachEvent('onkeydown', function(){//ie的事件名称不同,也不支持addEventListener
			if(window.event.altKey && window.event.ctrlKey && window.event.keyCode == 83){
				openTable('场景维护(' + Scene + ')', "jsps/ma/bench/sceneSet.jsp?bench="+parent.bench+"&formCondition=bs_codeIS'" + Scene + "'" + 
						"&gridCondition=sg_bscodeIS'" + Scene + "'");
			}
		});
	} else {
		document.body.addEventListener("keydown", function(e){
			if(Ext.isFF5){//firefox不支持window.event
				e = e || window.event;
			}
			if(e.altKey && e.ctrlKey && e.keyCode == 83){
				openTable('场景维护(' + Scene + ')', "jsps/ma/bench/sceneSet.jsp?bench="+parent.bench+"&formCondition=bs_codeIS'" + Scene + "'" + 
						"&gridCondition=sg_bscodeIS'" + Scene + "'");
			}			
			
    	});
	}
}
function openTable(title, url){
	var main = parent.Ext.getCmp("content-panel");
	var panel = null;
	if(!main){
		var main = parent.parent.Ext.getCmp("content-panel");
		//@goua 针对问题反馈2017010379,通过activeTab.id来设置唯一id 
		if(main){
			var id = main.activeTab.id+"_"+Scene;
			panel = parent.parent.Ext.getCmp(id); 
		}
	}else{
		//@goua 针对问题反馈2017010379,通过activeTab.id来设置唯一id 
		var id = main.activeTab.id+"_"+Scene;
		panel = parent.Ext.getCmp(id); 
	}
	
	if(!panel){ 
    	panel = { 
    			id:id,
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
<body onload="onDatalistKeyDown()">
</body>
</html>