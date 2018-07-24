Ext.define('erp.view.common.JProcess.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpHistoryProcessGridPanel',
	//layout : 'auto',
	id: 'historyGrid', 
	emptyText : '无数据',
	title: '<h1 style="color:black ! important;">审批历史</h1>',
	columnLines : true,
	autoScroll : true,
	//store: [],
	columns: [],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	nodeId: null,
	plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1
	})],
	requires: ['erp.view.core.trigger.TextAreaTrigger'],
	initComponent : function(){ 
		formCondition = this.BaseUtil.getUrlParam('formCondition');//从url解析参数
		formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
		var strArr = formCondition.split("=");
		var acountmaster=getUrlParam('newMaster'),nodeId=this.nodeId || strArr[1]; 
		if(!this.deferLoadData){
			var processInstanceId=this.processInstanceId?this.processInstanceId:this.getProcessInstanceId(nodeId,acountmaster);
			this.getOwnStore(processInstanceId); 
		}  
		this.callParent(arguments); 
	},
	getProcessInstanceId:function (nodeId,acountmaster){
		var processInstanceId=null;
		Ext.Ajax.request({
			url: basePath + 'common/getProcessInstanceId.action',
			params: {
				jp_nodeId : nodeId,
				master:acountmaster,
				_noc:1
			},
			async:false,
			success: function(response){
				var text = response.responseText;
				var jsonData = Ext.decode(text);
				processInstanceId= jsonData.processInstanceId;

			}
		});
		return processInstanceId;
	},
	getOwnStore: function(processInstanceId){		
		var me = this;		
		Ext.Ajax.request({
			url : basePath + 'common/getAllHistoryNodes.action',
			params: {

				processInstanceId:processInstanceId  ,
				_noc:1
			},
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				var store = Ext.create('Ext.data.Store', {
					storeId: 'gridStore',
					fields: [{name: 'jn_id', type: 'string'},
					         {name: 'jn_name', type: 'string'},
					         {name: 'jn_dealManId', type: 'string'},
					         {name: 'jn_dealManName', type: 'string'},
					         {name: 'jn_dealManName', type: 'string'},
					         {name: 'jn_dealTime', type: 'string'},
					         {name:'jn_holdtime',type:'int'},
					         {name: 'jn_dealResult', type: 'string'},
					         {name: 'jn_operatedDescription', type: 'string'},
					         {name: 'jn_nodeDescription', type: 'string'},
					         {name: 'jn_infoReceiver', type: 'string'},
					         {name: 'jn_processInstanceId', type: 'string'},
					         {name: 'jn_attachs', type: 'string'},
					         {name: 'jn_attach', type: 'string'}//是否回退节点
					         ],
					         data: res.nodes
				});

				var columns = [{header: '节点名称',  dataIndex: 'jn_name',width:100},
				               {header: '处理人',  dataIndex: 'jn_dealManId',width:80},
				               {header: '处理人姓名', dataIndex: 'jn_dealManName',width:100},
				               {header: '处理日期',  dataIndex: 'jn_dealTime',width:150},        		              
				               {header: '处理结果',  dataIndex: 'jn_dealResult',width:80},
				               {header: '管理要点',  dataIndex: 'jn_operatedDescription',width:150,editor:{xtype:'textareatrigger',hideTrigger:false,editable:false,onTriggerClick:me.onTriggerClick},renderer:function (val, meta, record){
				            	   return '<span style="color:green;padding-left:2px">' + val + '</span>';
				               }},
				               {header: '审批意见',  dataIndex: 'jn_nodeDescription',flex:1,width:140,editor:{xtype:'textareatrigger',hideTrigger:false,editable:false}},
				               {header: '备注信息',  dataIndex: 'jn_infoReceiver',width:120},
				               /*{header: '停留(s)',  dataIndex: 'jn_holdtime',align:'right',width:50},*/
				               {header: '附件',  dataIndex: 'jn_attachs',align:'right',width:80,renderer:function(val){
				            	   if(val){
				            		   return  '<a href="javascript:scanAttachs(\'' + val + '\');">查看附件</a>';
				            	   }else return '无附件';
				               }}
				               ];
				Ext.getCmp("historyGrid").reconfigure(store, columns);
				nodes = res.nodes;
				var toolbar=Ext.getCmp('nodeToolbar');
				if(toolbar){
					toolbar.add('->');
					for(var i=0;i<nodes.length;i++){//显示在页面左上角一排信息 2013-3-8 15:48:53 
						var ti = Ext.create('Ext.toolbar.TextItem',{
							text: nodes[i]['jn_name']+nodes[i]['jn_dealManId']
						});
						toolbar.add(ti);
						toolbar.add('-');
					}	
				}
			}
		});
	},
	download:function(val,meta,record){
		var value=Ext.getCmp('wr_taskpercentdone').getValue();
		var unit=Ext.getCmp('wr_assignpercent').getValue();
		var percent=Ext.getCmp('wr_percentdone');
		percent.setReadOnly(true);
		value=value+(percent.value)*unit/100;
		Ext.getCmp('wr_progress').updateProgress(value/100,'当前任务进度:'+Math.round(value)+'%');   			
		Ext.getCmp('wr_redcord').setHeight(320);
		var form=me.getForm(btn);
		var attachs=Ext.getCmp("wr_attachs").getValue();
		if(attachs!=null){
			Ext.Ajax.request({//拿到grid的columns
				url : basePath + 'common/getFilePaths.action',
				async: false,
				params: {
					id:attachs
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
			form.add({
				title:'相关文件',
				id:'container',
				style: {borderColor:'green', borderStyle:'solid', borderWidth:'0px'},
				xtype:'container',
				columnWidth:1
			});
			var items = new Array();
			items.push({
				style: 'background:#CDBA96;',
				html: '<h1>相关文件:</h1>'
			});
			Ext.each(attach, function(){
				var path = this.fp_path;
				var name = '';
				if(contains(path, '\\', true)){
					name = path.substring(path.lastIndexOf('\\') + 1);
				} else {
					name = path.substring(path.lastIndexOf('/') + 1);
				}
				items.push({

					style: 'background:#C6E2FF;',
					html: '<img src="' + basePath + 'resource/images/mainpage/things.png" width=16 height=16/>' + 
					'<span>文件:' + name + '<a href="' + basePath + "common/download.action?path=" + path + '">下载</a></span>'
				});
			});
			Ext.getCmp('container').add(items);
		}   			
	},
	onTriggerClick:function(){
		var trigger = this,
		value = this.value;
		var arr=value.split(";");
		var realValue="";
		for(var i=0;i<arr.length;i++){
			if(arr[i].indexOf('(否)')>0){
				realValue+='<font size=3 color="red">'+arr[i]+';<font></br>';
			}else realValue+=arr[i]+';</br>';	 
		}
		var win = new Ext.window.Window(
				{  
					id : 'win',
					height : 200,
					width : 600,
					maximizable : false,
					buttonAlign : 'center',
					layout : 'anchor',
					title:'详细信息',
					items : [ {
						xtype: 'htmleditor',
						enableColors: false,
						enableAlignments: false,
						enableFont: false,
						enableFontSize: false,
						enableFormat: false,
						enableLinks: false,
						enableLists: false,
						enableSourceEdit: false,
						frame: false,
						height: 140,
						width: 600,
						fieldStyle: 'border-bottom: none;',
						value:realValue
					}],
					buttons:[{
						text:'关闭',
						handler:function(btn){
							win.close();
						}
					}]

				});
		win.show(); 
	},
	showTrigger:function(val){//明细行文本框
		val = unescape(val);
		Ext.MessageBox.minPromptWidth = 600;
        Ext.MessageBox.defaultTextHeight = 200;
        Ext.MessageBox.style= 'background:#e0e0e0;';
        Ext.MessageBox.prompt("详细内容", '',
        function(btn, text) {},
        this, true, //表示文本框为多行文本框    
        val);
	}


});