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
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'pm.bom.ProdReplace'
    ],
    launch: function() {
    	Ext.create('erp.view.pm.bom.ProdReplace');//创建视图
    }
});
var caller = 'ProdReplace';
var formCondition = '';
var gridCondition = '';
function getProductFileWindow(id){
	var me = this;
	var win = new Ext.window.Window({
		id : 'win',
		height : 400,
		width : 902,
		maximizable : true,
		border:false,
		buttonAlign : 'center',
		layout : 'anchor',
		title : '附件信息',
		bodyStyle : 'background:#F2F2F2;',
		items : [{
			xtype:'gridpanel',
			id:'versionGrid',
			width:'100%',
			height:'100%',				
			columns:[{
				text:'ID',
				dataIndex:'fp_id',
				width:0
			},{
				cls : "x-grid-header-1",
				header: '阅读',
				xtype:'actioncolumn',		
				align:'center',
				width:100,
				icon: basePath + 'resource/images/icon/read.png',
				tooltip: '阅读',
				handler: function(grid, rowIndex, colIndex) {	
					var select=grid.getStore().getAt(rowIndex);
					var folderId = select.data.fp_id;
					var name = unescape(select.data.fp_name);
					var type = name.substring(name.lastIndexOf('.') + 1);
					console.log(folderId + " "+ type );
					readFile(folderId,type);
				}
			},{
				cls : "x-grid-header-1",
				header: '下载',
				xtype:'actioncolumn',		
				align:'center',
				width:100,
				icon: basePath + 'resource/images/icon/download.png',
				tooltip: '下载' ,
				handler: function(grid, rowIndex, colIndex) {	
					var select=grid.getStore().getAt(rowIndex);
					var folderId = select.data.fp_id;
					downFile(folderId);
				} 
			},{
				cls : "x-grid-header-1",
				text: '文件名称',
				dataIndex: 'fp_name',
				width:400,
				readOnly:true				
			}],
			store:Ext.create('Ext.data.Store', {
				fields:[{
					name: 'fp_id',
					type: 'number'
				},{
					name:'fp_man',
					type:'string'
				},{
					name:'fp_name',
					type:'string'
				},{
					name: 'fp_path',
					type: 'string'
				}],
				data:[]
			}),
			listeners:{
				 afterrender:function(grid){
					me.loadNewStore(grid,id)							
				} 
			} 
		}]
	});	
	win.show();
//	console.log(win);
}
function loadNewStore(grid, id){
	var me = this;
	Ext.Ajax.request({
		url : basePath + 'common/getFilePaths.action?id='+id,
		method:'post',	
		callback : function (opt, s, res){
			var r = new Ext.decode(res.responseText);
			var d = r.files;
			console.log(d);
			if(r.exceptionInfo){
				showError(r.exceptionInfo);
			} else if(r.success){
				grid.store.loadData(d);
			}
		}
	});
}
function readFile(folderId,type){
	if (type == 'doc'|| type =='docx'|| type == 'xls'|| type == 'xlsx'||type == 'png' || type =='jpg') {
		
		Ext.Ajax.request({
			url : basePath + 'common/getHtml.action',
			params: {
				folderId:folderId,
				type:type
			},
			method : 'post',
			async:false,
			callback : function(opt, s, res){
				var r = new Ext.decode(res.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);
				} else if(r.success){
					var url=basePath;
					var path=r.newPath;
					if(type == 'doc'|| type =='docx'|| type == 'xls'|| type == 'xlsx'){
						url += 'jsps/oa/doc/readWordOrExcel.jsp?path='+basePath + path;
					}else if (type == 'png' || type == 'jpg') {
						url += 'jsps/oa/doc/readPngOrJpg.jsp?path=' + path + '&folderId='+ folderId +'&type=' + type;
					}
					window.open(url);
				} 
			}
		});	
	} else {
		showMessage('提示','当前文件类型不支持在线预览，请先下载!');
	}
}
function downFile(folderId){
	if (!Ext.fly('ext-attach-download')) {  
		var frm = document.createElement('form');  
		frm.id = 'ext-attach-download';  
		frm.name = id;  
		frm.className = 'x-hidden';
		document.body.appendChild(frm);  
	}
	Ext.Ajax.request({
		url:basePath + 'common/downloadbyId.action',
		params : {
			id:folderId
		},
		method: 'post',
		async:false,
		form: Ext.fly('ext-attach-download'),
		isUpload: true,
		callback:function(options,success,resp){
			var begin = resp.responseText.indexOf('{"exceptionInfo":"');
			if(begin>-1){
				var end = resp.responseText.indexOf("\"}");
				var str = resp.responseText.substring(begin+'{"exceptionInfo":"'.length,end);
				showError(str);	
			}
		}
	});
}
</script>
</head>
<body >
</body>
</html>