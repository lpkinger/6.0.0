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
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" />
<style type="text/css">

 .custom-turned .x-grid-cell {
	background: #ffdab9 !important;
	font-style: italic !important;
	border-color: #ededed;
	border-style: solid;
	border-width: 1px 0;
	border-top-color: #fafafa;
	height: 26px;
	line-height: 26px
}

.custom-turned .x-grid-cell-inner {
	background: #ffdab9 !important;
	font-style: italic !important;
}
 
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/zebraBrowserPrint/BrowserPrint-1.0.4.min.js"></script> 
<script type="text/javascript" src="<%=basePath %>resource/zebraBrowserPrint/zebraPrint.js"></script> 
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/data/PagingMemoryProxy.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'common.BatchDealer'
    ],
    launch: function() {
        Ext.create('erp.view.common.batchDeal.ViewPorter');
    }
});
	var caller = getUrlParam('whoami');
	var dataCount = 0;//结果总数
	caller = caller.replace(/'/g, "");
	var urlcondition = getUrlParam('urlcondition');
	var em_id ='<%=session.getAttribute("em_id")%>'; 
	var height = window.innerHeight;
	if(Ext.isIE){//ie不支持window.innerHeight;document.documentElement.clientHeight == 0
		height = screen.height*0.73;
	}
	var pageSize = parseInt(height*0.7/23);
	var page = 1;
	var value = 0;
	var total = 0;
	var page = 1;
	var pageSize = 50;
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
</script>
</head>
<body>
</body>
</html>