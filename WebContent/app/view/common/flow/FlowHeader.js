Ext.define('erp.view.common.flow.FlowHeader',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.flowheader',
	style:'border-bottom:1px solid #bdbdbd;',
	defaults:{
		frame:true
	},
	layout:'auto',
	hideBorders: true,
	bodyStyle: {
		background: '#f7f7f7'
	},
	hidden:disagree,
	items:[{
		cls:'x-flow-header',
		layout: {
			type: 'hbox',
			padding:'5',
			align:'top'
		},
		defaults:{margins:'0 5 0 0'},
		items:[{
			xtype:'tbspacer',
			flex:1
		},{
			xtype: 'tbtext',
			id:'processtitle'
		},{
			xtype:'tbspacer',
			flex:1
		}]
	},{ 
		border:'0 0 0 0',
		bodyStyle:'border:none',
		xtype: 'toolbar',
		id:'flow_tbar',
		layout: {
			type: 'hbox',
			pack:'start',
			align:'middle'
		},
		style: {
			border: 'none'
		},
		defaults:{
			margins:'0 5 0 0',
			xtype:'tbtext'
		},
		items:[{
			readOnly: ISexecuted,
			disabled: ISexecuted,
			xtype: 'button',
			text: '同意',
			id:'agree-task',
			iconCls: 'x-button-icon-confirm',
			cls: 'x-btn-gray',
			width: 70
		},{
			readOnly: ISexecuted,
			disabled: ISexecuted,
			xtype: 'button',
			id:'disagree-task',
			text: '不同意',
			iconCls: 'x-button-icon-unagree',
			cls: 'x-btn-gray',
			width: 70
		},{
			readOnly: ISexecuted,
			disabled: ISexecuted,
			xtype: 'button',
			id:'skip-task',
			iconCls: 'x-button-icon-turn',
			text: '下一条',
			cls: 'x-btn-gray',
			width: 70
		},{
			readOnly: ISexecuted,
			disabled: ISexecuted,
			xtype:'button',
			text: '智能审批',
			iconCls: 'x-button-icon-autodeal',
			cls: 'x-btn-gray',
			id:'autoaudit',
			width: 90,
			handler: function() {
				this.ownerCt.ownerCt.ownerCt.down('flowbody').autoAudit();						
			}
		},{
			readOnly: ISexecuted,
			disabled: ISexecuted,
			xtype: 'button',
			text: '关闭',
			iconCls: 'x-button-icon-newclose',
			cls: 'x-btn-gray',
			id:'closeProcess',
			width: 70,
			handler: function() {			
				if(parent.Ext.getCmp('modalwindow')){
					Ext.Ajax.request({
						url: basePath + 'common/changeMaster.action',
						params: {
							to: parent.Ext.getCmp('modalwindow').historyMaster
						},
						callback: function(opt, s, r) {
							if (s) {
								var formUtil = Ext.create('erp.util.FormUtil');
								var tab = formUtil.getActiveTab();
								if(tab){
									tab.fireEvent('activate',tab);
								}
								parent.Ext.getCmp('modalwindow').close();
							} else {
								alert('切换到原账套失败!');
							}
						}
					}); 							  									
				}								
				else parent.Ext.getCmp('content-panel').getActiveTab().close();
			}
		},'->',{
			hidden:true,
			id:'flow_histroy',
			xtype:'button',
			text:'审批历史',
			cls:'x-btn-history',
			iconCls:'x-button-history',
			handler:function(btn){
				var win = Ext.getCmp('histroyWin');
				if(!win) {
					win = new Ext.window.Window({
	        			title: '审批历史',
	        			draggable:true,
	        			height: '80%',
	        			width: '80%',
	        			resizable:false,
	        			id:'histroyWin',
	        	   		modal: true,
	        	   		layout: 'fit',
	        		   	items: [{
	        		   		xtype:'flowbottom',
	        		   		title: '',
	        		   		emptyText: '无数据',
	        				deferLoadData:false,
	        				id: 'historyGrid2'
	        			}]
	        		});
				}
				win.show();				
			}
		},{xtype: 'splitter',width: 1},{
			id:'currentnode'
		},{xtype: 'splitter',width: 1},{
			id:'currentnodename'
		},{xtype: 'splitter',width: 1},{
			id:'launchername'
		},{xtype: 'splitter',width: 1},{
			id:'launchtime'
		}]
	},{
		xtype:'container',
		layout: {
			type: 'hbox'
		},
		items:[
			{ 
			xtype:'combo',
			emptyText: '审批意见',
			fieldCls: 'x-form-approve-textfield',
			labelWidth:80,
			labelSeparator:'',
			triggerCls:'x-form-textarea-trigger',
		    onTriggerClick: function() {
		        var trigger = this;
		        var value = this.lastValue;
		        Ext.MessageBox.minPromptWidth = 600;
		        Ext.MessageBox.defaultTextHeight = 200;
		        Ext.MessageBox.style= 'background:#e0e0e0;';
		        Ext.MessageBox.draggable = false;//不可拖动
		        Ext.MessageBox.prompt("详细内容", '',
			        function(btn, text) {
			            if (trigger.editable && btn == 'ok') {
		                    trigger.setValue(text);
			            }
			        },
			        this, true, //表示文本框为多行文本框    
		        value);
		    },
			border:true,
			width:700,
			height:30,
			margin:'10 0 10 22',			
			name: 'dealMessage',
			id: 'dealMessage',
			store:Ext.create('Ext.data.Store', {
				fields: [{name: 'TEXT_'},
				         {name: 'USERID_'},
				         {name:'ID_'}
				         ]
				//data:[{content_:'ok1',id_:100},{content_:'好的',id_:50}]
			}),
			queryMode: 'local',
			enableKeyEvents:true,
			valueField:'TEXT_',
			displayField:'TEXT_',
			listConfig: {
				maxHeight : 90,
				//常用审批语
				getInnerTpl: function() {
					return '<div style="padding: 2px 2px;">' + 
					'<span style="font-size:110%;">' +
					'{TEXT_}' +
					'</span>' + 
					'<span style="float:right;"><div onclick="javascript:deleteCommonWords(\'{ID_}\',event)"><img src="' + basePath + 'jsps/common/jprocessDeal/images/delete.png"></div></span>' + 
					'</div>';
				}
			},
			listeners :{
				focus:function(){
					var me=this;
					me.doQuery();	
				},
				change:function(t,newValue,oldValue){
					if(newValue==null){
						Ext.getCmp("addtocommonusewords").hide();
					}
				}
			},
			doQuery: function(val) {	
				var me=this;
				var data=me.ownerCt.ownerCt.getSearchData(em_uu,val);
				if(data!=null){
					if(data.length>0){
						me.store.loadData(data,false);
				 		me.expand();
					}else{
						if(val){
							Ext.getCmp("addtocommonusewords").show();
						}
						me.collapse( );
					}
				}else{
					if(val){
						Ext.getCmp("addtocommonusewords").show();
					}
					me.collapse( );						
				}					
		    }
		},
	{
		xtype:'button',
		text:'添加常用语',
		id:'addtocommonusewords',
		height:25,
		margin:'10 0 10 10 ',
		//iconCls:'addcommonwordsbtncls',
		hidden:true,
		handler:function(btn){
			var me=btn.ownerCt.ownerCt;
			var value=Ext.getCmp('dealMessage').rawValue;								
			me.saveToCommonWords(value);
		}
	}
		]				
	}
	],
	initComponent : function(){
		var bases=this.getBases();
		this.callParent(arguments);
		this.loadPoints();
		if(bases) this.add(bases);
	},
	saveToCommonWords:function(value){
		Ext.Ajax.request({
			url: basePath + 'common/saveToCommonWords.action',
			params: {
				value:value
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
					alert('添加成功！');
				}else{
					alert('添加失败！');
				}
			}
		});
	},
	getSearchData:function(userid,text){
		var data;
		Ext.Ajax.request({
			url: basePath + 'common/getComboBoxTriggerData.action',
			params: {
				userid:userid,
				text:text
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
					 data=res.data;
					return data;
				}
			}
		});
		return data;	
	},

	loadPoints:function(){
		var me = this;
		formCondition = (formCondition == null) ? "": formCondition.replace(/IS/g, "=");
		var nodeId = formCondition.split("=")[1];
		Ext.Ajax.request({
			url: basePath + 'common/getCustomSetupOfTask.action',
			params: {
				nodeId: nodeId,
				master: master,
				_noc: 1
			},
			success: function(response, options) {
				var localJson = Ext.decode(response.responseText);
				cs = localJson.cs;
				var arr = null,items=new Array();
				if (localJson.data != null) {
					arr = localJson.data.split(";");
				}
				if(localJson.isApprove==1){
					Ext.getCmp('disagree-task').setDisabled(true);
				}
				if (cs != null && cs.length > 0) {
					var csstr='';
					for (var i = 0; i < cs.length; i++) {
						csstr=cs[i].toString();
						var i1 = csstr.indexOf('^');
						var i2 = csstr.indexOf('$');
						var value = csstr.substring(0, i1);
						var type = csstr.substring(i1 + 1, i2);
						var neccesary = csstr.substring(i2 +1,i2+2);
						var field = me.getFieldByType(type,i,csstr,value,neccesary);
						if (arr != null) {
							field.items.items[1].setValue(arr[i].substring(arr[i].indexOf("(") + 1, arr[i].indexOf(")")));
						}
						items.push(field);
					}
				}
				if(items.length>0){
					me.insert(3,{
						xtype: 'fieldset',
						margin: '2 20 2 20',
						collapsible: true,
						collapsed: false,
						layout:'column',
						id:'customSetup',
						title: '<img src="' + basePath + 'jsps/common/jprocessDeal/images/attach.png" width=20 style="vertical-align:middle;margin:-4px 0 0 0;">&nbsp;&nbsp;<span style="font-weight:bold;font-size:13px;display:inline-block">审批要点</span></img>',
						defaults:{
							columnWidth:0.4,
							margin:'5 0 5 20'
						},
						items:items
					});
				}
			}
		});
	},
	getBases:function(){
		return {
			xtype: 'fieldset',
			id: 'extraCompoent',
			margin: '20 20 20 20',
			collapsible: true,
			collapsed: true,
			layout:'column',
			title:'<img src="' + basePath + 'jsps/common/jprocessDeal/images/normal.png" width=20 style="vertical-align:middle;margin:-4px 0 0 0;">&nbsp;<span style="margin-left:4px;font-weight:bold;font-size:13px;">基本操作<font size="1" color="gray">(变更办理人、设置知会人、上传附件、发起沟通)</font></span></img>',
			items:[{
				layout:'hbox',
				columnWidth:1,
				margin:'5 0 0 0',
				padding:'8 10 10 15 ',
				defaults:{
					margin:'3 0 0 0'
				},
				frame:true,
				defaults:{
					readOnly:ISexecuted,
					disabled:ISexecuted
				},
				style:'background:#f7f7f7',
				bodyStyle:'background:#f7f7f7',
				items:[{
					fieldLabel: '变更处理人',
					xtype: 'multifield',
					name: 'AssigneeComboxcode',
					editable: true,
					labelWidth : 80,
					width: 350,
					fieldStyle: 'background:#fff;color:#515151;',
					id: 'AssigneeComboxcode',
					secondname: 'em_name'
				},{
					xtype: 'textfield',
					name: 'changedescription',
					id: 'changedescription',
					fieldStyle: 'background:#fff;color:#515151;',
					width: 350,
					labelAlign: 'right',
					emptyText:'变更描述'
				},{
					margin:'0 0 0 5',
					height:24,
					xtype:'button',
					text:'确定',
					id:'changehandler'
				}]
			},{
				columnWidth:1,
				layout:'hbox',
				margin:'5 0 0 0',
				padding:'8 10 10 15 ',
				style:'background:#f7f7f7',
				bodyStyle:'background:#f7f7f7',
				frame:true,
				defaults:{
					labelAlign:'left',
					margin:'3 0 0 0'
				},
				items: [Ext.create('erp.view.core.trigger.AddDbfindTrigger',{
					anchor:'100% 30%',
					emptyText:'选择个人',
					fieldStyle: 'background:#fff;color:#515151;',
					name:'notifyPeople',
					id:'notifyPeople',
					group:0,
					width: 350,
					fieldLabel:'知会',
					labelWidth : 80
				}),{
					name:'notifyPeopleid',
					id:'notifyPeopleid',
					xtype:'textfield',
					hidden:true
				},Ext.create('erp.view.core.trigger.AddDbfindTrigger',{
					anchor:'100% 30%',
					emptyText:'选择岗位',
					name:'notifyGroupName',
					id:'notifyGroupName',
					group:0,
					fieldStyle: 'background:#FAFAFA;color:#515151;padding-left:5px',
					width: 350
				}),{
					name:'notifyGroup',
					id:'notifyGroup',
					xtype:'textfield',
					hidden:true
				},{
					margin:'3 0 0 5',
					height:24,
					xtype:'button',
					text:'确定',
					id:'notify'
				}]

			},{
				xtype:'mfilefield',
				id:'attachs',
				name:'attachs',
				columnWidth:1,
				collapsible: false,
				//title:'上传附件',
				title: '<img src="' + basePath + 'jsps/common/jprocessDeal/images/fujian.png" width=20/>&nbsp;&nbsp;<span style="font-weight:normal;font-size:13px;">上传附件</span>',
				margin:'5 0 5 0',
				listeners : {
					afterrender: function(f){
						if(f.value != null && f.value.toString().trim() != ''){
							f.download(f.value);
						}
					}
				}
			},
			{
				xtype: 'mfilefield',
				columnWidth:1,
				collapsible: false,
				margin:'5 0 5 0',
				layout:'column',
				//title:'沟通',
				title: '<img src="' + basePath + 'jsps/common/jprocessDeal/images/communicate.png" width=20/>&nbsp;&nbsp;&nbsp;<span style="font-weight:normal;font-size:13px;text-align:top;height:30px;">沟通</span>',
				items: [{
					xtype:'form',
					margin:'5 0 0 0',
					columnWidth:0.5,
					height    :100,
					layout:'anchor',
					frame:true,
					/*style:'background:#f1f1f1',*/
					bodyStyle:'background:#f7f7f7',
					items:[{
						xtype:'textareafield',
						name :'communicaterecord',
						id:'communicaterecord',
						fieldLabel:'沟通内容',
						labelAlign :'left',
						labelWidth : 80,
						hideLabel:false,
						allowBlank:false,
						anchor:'100% 70%',
						fieldStyle: 'background:#fff;color:#515151;'
					},{
						margin:'3 0 0 0',
						xtype:'container',
						anchor:'100% 30%',
						layout:'hbox',
						items:[
							Ext.create('erp.view.core.trigger.MultiDbfindTrigger',{
							flex:1,
							//emptyText:'选择沟通人',
							fieldLabel:'选择沟通人',
							labelAlign :'left',
							fieldStyle: 'background:#fff;color:#515151;',
							id:'communicator',	
							name:'communicator',
							allowBlank:false,
							labelWidth : 80
						}),{
							xtype:'button',
							//columnWidth:0.10,
							margin:'0 5 0 5',
							width:42,
							height:24,
							text : '重置',
							handler : function(b) {
								b.ownerCt.ownerCt.getForm().reset();
							}				
						},{
							xtype:'button',							
							margin:'0 5 0 0',
							width:42,
							height:24,
							text : '发送',
							formBind: true,
							handler : function(b) {
								var me=this;
								var f=me.ownerCt.ownerCt.ownerCt.ownerCt.ownerCt;
								f.CommunicateWithOther(b.ownerCt.ownerCt,f);
							}
						},{
							xtype: 'button',
							text: '结束沟通',
							height:24,
							handler: function(b) {
								var me=this;
								var f=me.ownerCt.ownerCt.ownerCt.ownerCt.ownerCt;
								f.endcommunicateTask(b.ownerCt.ownerCt,f);
							}
						},{
							id:'communicatorid',
							name:'communicatorid',
							xtype:'textfield',
							hidden:true
						}
					]	
					}					
					]							
				},{
					name      : 'com_record',
					id        :'com_record',
					hideLabel : false,
					margin:'7 0 0 0',
					autoHeight : true,
					height:80,
					columnWidth:0.5,
					readOnly:true,
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
					fieldStyle: 'border-bottom: none;',
					//fieldStyle: 'border:1px solid red',
					fieldLabel:'沟通历史消息',
					labelAlign :'left'
				}]
			}		
			]
		};
	},
	getFieldByType: function(type, i,cs,label,necessary) {
		var logic='';
		var bool=necessary=='Y';
		var fieldStyle=bool?"background:#F5FFFA;":"background:#FFFAFA;";
		if(cs.indexOf("@")>0)
			logic=cs.substring(cs.indexOf("@")+1);
		var conf={};
		switch (type) {
		case "S":
			conf={
				xtype:'textfield'
		};
			break;
		case "D":
			conf={
				format: 'Y-m-d',	
				logic:logic,
				xtype:'datefield'
		};
			break;
		case "N":
			conf={
				xtype:'numberfield',
				hideTrigger:true
		};
			break;
		case "C":
			if(cs.indexOf('[')>0){
				var comstr=cs.substring(cs.indexOf('[')+1,cs.indexOf(']'));
				var str=comstr.split(";");
				var arr=new Array();
				Ext.Array.each(str,function(s){
					arr.push({"value":s});
				});
				var comStore = Ext.create('Ext.data.Store', {
					fields: ['value'],
					data: arr
				});
				conf={
						store: comStore,
						queryMode: 'local',
						editable: false,
						displayField: 'value',
						valueField: 'value',					
						xtype:'combo'};
			}
			break;
		case "B":
			conf={
				store: Ext.create('Ext.data.Store', {
					fields: ['value'],
					data: [{
						"value": "是"
					},{
						"value": "否"
					},{
						"value": "不执行"
					}]
				}),
				queryMode: 'local',
				editable: false,
				displayField: 'value',
				valueField: 'value',
				xtype:'combo'
		};
			break;
		}
		var width=window.innerWidth*0.2;
		Ext.apply(conf,{
			allowBlank:!bool,
			fieldLabel:label,
			logic:logic,
			labelAlign:'right',
			labelStyle:'padding-right:10px;'+(bool?"color:red;":""),
			labelWidth:width,
			id:i
		})
		return conf;
	},
	CommunicateWithOther: function(owner,me){
		var form=owner.getForm();
		var data=form.getValues();
		var taskId = ProcessData.jp_nodeId;
		me.setLoading(true);
		Ext.Ajax.request({
			url: basePath + 'common/communicateWithOther.action',
			params: {
				taskId: taskId,
				processInstanceId:ProcessData.jp_processInstanceId,
				data:unescape(Ext.JSON.encode(data).replace(/\\/g, "%")),
				_noc: 1
			},
			callback: function(options, success, response) {
				me.setLoading(false);
				var data = response.responseText;
				var jsonData = Ext.decode(data);
				if (jsonData.success) {
					me.getCommunicates(taskId,ProcessData.jp_processInstanceId);
					form.reset();
					showMessage('提示', '已成功发起沟通!', 1000);
				} else {
					showError(jsonData.exceptionInfo);
				}

			}
		});
	},
	getCommunicates:function(nodeId,processInstanceId){
		Ext.Ajax.request({
			url: basePath + 'common/getCommunicates.action',
			params: {
				processInstanceId:processInstanceId,
				_noc: 1
			},
			callback: function(options, success, response) {
				var data = response.responseText;
				var jsonData = Ext.decode(data);
				if (jsonData.success) {
					Ext.getCmp('com_record').setValue('<ul style="font-size:90%;color:#3B3B3B;list-style-type:none;margin:0;padding:0;margin-left:10px;overflow:hidden;">'+jsonData.msg+'</ul>');
				} else {
					showError(data.exceptionInfo);
				}

			}
		});
	},
	endcommunicateTask:function (owner,me){
		var form=owner.getForm();
		var taskId = ProcessData.jp_nodeId;
		me.setLoading(true);
		Ext.Ajax.request({
			url: basePath + 'common/endCommunicateTask.action',
			params: {
				taskId: taskId,
				processInstanceId:ProcessData.jp_processInstanceId,
				_noc: 1
			},
			callback: function(options, success, response) {
				me.setLoading(false);
				var data = response.responseText;
				var jsonData = Ext.decode(data);
				if (jsonData.success) {
					me.getCommunicates(taskId,ProcessData.jp_processInstanceId);
					form.reset();
					showMessage('提示', '已成功结束沟通!', 1000);
				} else {
					showError(data.exceptionInfo);
				}

			}
		});
	}
});