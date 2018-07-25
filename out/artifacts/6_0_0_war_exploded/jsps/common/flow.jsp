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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style type="text/css">
.custom-rest{
	background:  url('<%=basePath %>/resource/images/x.png') no-repeat center
	center !important;
	width: 16px !important;
	background-image: url('<%=basePath %>/resource/ext/4.2/resources/ext-theme-gray/images/form/text-bg.gif'); 
    height: 16px;
    border: 0!important;
    visibility:hidden;
    margin: 2px 0px 0px 0px;
}
.x-grid-cell .x-grid-cell-inner {
	white-space: normal;
	height: auto !important;
	border-color: #ededed;
	border-style: solid;
	border-width: 0 !important;
	border-top:none;
	border-bottom: none;
/* 	//color:gray; */
}
.x-grid-cell-inner{
 line-height: 19px!important;

}
.nodedealman{
 padding-left:5px;
}

#win-body{
	background-color: white;
}

.addcommonwordsbtncls{
	background-image: url("jprocessDeal/images/add.png");
	margin-bottom: 2px;
 }
/* #AssigneeComboxcode-labelEl{
	margin-right:-14px !important;

 }
#notifyPeople-labelEl{
 	margin-right:-20px !important;
 } */

#com_record .x-toolbar{
border:none!important;
padding:0!important;
}

#historyGrid .x-grid-cell .x-grid-cell-inner{
	white-space: normal!important;
	/* font:13px arial,sans-serif; */
	word-break:break-all!important; 
}

.dealMessageCls{
	background-image:url(../../resource/images/icon/editpen.png)!important;
    background-repeat: no-repeat!important;
    background-position: left center!important;
    padding-left: 20px!important;
    padding-right: 20px!important;
}

#dealMessage .x-form-trigger{
	border-bottom:none;
}

.x-field-formbody{
	background-color: #f7f7f7 !important;
}

 .iframe-background{
	background:rgb(255, 250, 192);
} 
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>/resource/ux/PreviewPlugin.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true,
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'common.Flow'
    ],
    launch: function() {
         Ext.create('erp.view.common.flow.Flow');
    }
});
var caller = "JProcess";
var dbfinds = [];
var en_uu = '<%=session.getAttribute("en_uu")%>';
var formCondition = getUrlParam('formCondition');
var gridCondition = '';
var basestarttime=new Date();
var nodeId='<%=request.getAttribute("jp_nodeId")%>';
var nextnodeId=null;
var requiredFields=null; 
var conditionValidation=null;
var canexecute=false;
var ISexecuted=getUrlParam("_do")==1;
var master=getUrlParam("newMaster");
var _center=getUrlParam("_center");
var TaskId=null;
var ProcessData=null;
var forknode=0;//判断当前审批节点是否并行节点
var disagree = getUrlParam('_disagree')==1;;

function scanAttachs(val,jnname,jndealManName){
	var attach = new Array();
	 Ext.Ajax.request({//拿到grid的columns
		   url : basePath + 'common/getFilePaths.action',
		   async: false,
		   params: {
			   id:val
		   },
		   method : 'post',
		   callback : function(options,success,response){
			   var res = new Ext.decode(response.responseText);
			   if(res.exception || res.exceptionInfo){
				   showError(res.exceptionInfo);
				   return;
			   }
			   attach =  res.files != null ?  res.files : [];
		   }
	   });
    var data=new Array();
	Ext.each(attach, function(item){
		var path = item.fp_path;
		var name = '';
		if(contains(path, '\\', true)){
			name = path.substring(path.lastIndexOf('\\') + 1);
		} else {
			name = path.substring(path.lastIndexOf('/') + 1);
		}
		   data.push(item);
	});
	
	var win = new Ext.window.Window({
		title: '查看附件',
    	id : 'attchWin',
		width:500,
		height:400,
		modal:true,
		layout:'column',
		bodyStyle:'background:white!important',
		items: [{
			xtype:'displayfield',					
			fieldLabel:'节点名称',
			value:jnname,
			columnWidth:1,
			border:0,
			margin:'5 0 0 5',
			labelWidth:75,
			style:{
				'background':'#ffffff'
			},
		},{
			xtype:'displayfield',
			fieldLabel:'上传者',					
			value:jndealManName,
			border:0,
			margin:'5 0 0 5',
			labelWidth:75,
			columnWidth:1,
			style:{
				'background':'#ffffff'
			},
		},{
			xtype:'form',
			columnWidth:1,
			border:false,
			padding:'10 0 0 0',
			items:[{
				xtype: 'fieldset',
				title: '<span style="font-weight:bold;font-size:13px;">附件明细</span>',
				collapsible: false,
				collapsed: false,
				layout:'fit',
				padding:'10 0 0 0',
				items:[{
					xtype:'grid',
					id:'fjgrid',
					//layout:'fit',
					border:false,
					columns: [{
						flex:0.8,
						header: '附件类型',
						dataIndex:'fp_name',
						align:'center',												
						renderer:function(val,meta,record){
							var type=Ext.util.Format.uppercase(val.substring(val.indexOf("."),val.length));
							var urlhead='<img  style="vertical-align:middle;"src="' + basePath + 'jsps/common/jprocessDeal/images/';
							var urlend='.png" width=25 height=25/>'
							
							switch(type){
							  	case '.JPG': case '.BMP': case '.GIF': case '.JPEG':
							  	case '.TIFF': case '.PNG': case '.SWF':
								  	return urlhead+'jpg'+urlend;
								  	break;
							  	case '.MP3': case '.WAV': case '.MP4': case '.WMA': 
							  	case '.OGG': case '.APE': case '.RMVB': case '.MID': 								
							  		return urlhead+'mp3'+urlend;
								  	break;
							  	case '.DOC': case  '.DOCX': 
							  		return urlhead+'office'+urlend;														  		
								  	break;
							  	case '.XLS': case '.XLSX':
							  		return urlhead+'excel'+urlend;														  		
								  	break;
							  	case '.PPT': case '.PPTX':
							  		return urlhead+'ppt'+urlend;										  
								  	break;
							  	case '.WPS': case '.WPT': case '.DOT': case '.DPS': case '.DPT': 
							  	case '.POT': case '.ET': case '.ETT':  case '.XLT':	
							  		return urlhead+'wps'+urlend;	
								  	break;
							  	case '.PDF':
							  		return urlhead+'pdf'+urlend;	
								  	break;
							  	case '.RAR': case '.ZIP':case '.CAB': case '.GZIP':
							  		return urlhead+'rar'+urlend;	
								  	break;
							  	case '.TXT':
							  		return urlhead+'txt'+urlend;	
								  	break;
								 default :
									 return urlhead+'other'+urlend;	
									 break;															  	
							}
						}
					},{
						header: '附件名称',  dataIndex: 'fp_name',flex:1.3,align:'center',	
					},{
						header: '文件大小',  dataIndex: 'fp_size'	,flex:0.9,align:'center',
						renderer:function(val,meta,record){
							return val/1000+'K';
						}
					},{
						header: '操作',
						flex:0.8,
						align:'center',	
						dataIndex:'fp_path',
						renderer:function(val,meta,record){
							var fp_id = record.get('fp_id');
							if(fp_id){
								return  '<span><a href="' + basePath + "common/downloadbyId.action?id=" + fp_id + '"><img src="' + basePath + 'jsps/common/jprocessDeal/images/upload.png" width=20 height=20/></a></span>' 	
							}
							return '';
						}
					}],
					store:new Ext.data.Store({
						fields: ['fp_date', 'fp_id', 'fp_man','fp_name','fp_path','fp_size']
					}),
				}]
			}]
		}], 
		buttonAlign: 'center',
		buttons: [{
			text: $I18N.common.button.erpCloseButton,
	    	iconCls: 'x-button-icon-close',
	    	cls: 'x-btn-gray',
	    	handler: function(){
	    		Ext.getCmp('attchWin').close();
	    	}
		}]
	});
	Ext.getCmp('fjgrid').store.loadData(data);
	win.show();
}

function deleteCommonWords(id,event){
 	Ext.Ajax.request({
		url: basePath + 'common/deleteCommonWords.action',
		params: {
			id:id
		},
		async: false,
		method: 'post',
		callback: function(options, success, response) {
			var res = new Ext.decode(response.responseText);
			if (res.exceptionInfo) {
				showError(res.exceptionInfo);
				return;
			}
			if (res.success) {
				alert('删除成功！');
				var combo = Ext.getCmp('dealMessage');
				combo.doQuery();	
			}else{
				alert('删除失败！');
				}
			
		}
	}); 
	var e = event?event:window.event;
	if (window.event) {  
		e.cancelBubble=true;  
	} else {   
		e.stopPropagation();  
	}	
}

</script>
</head>
</html>