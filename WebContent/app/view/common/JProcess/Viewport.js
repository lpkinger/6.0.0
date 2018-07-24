Ext.define('erp.view.common.JProcess.Viewport', {
	extend: 'Ext.Viewport',
	layout: 'fit',
	id: 'viewPort',
	hideBorders: true,
	autoScroll: false,
	style: {
		background: '#D3D3D3'
	},
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	initComponent: function() {
		var me = this;
		formCondition = this.BaseUtil.getUrlParam('formCondition'); //从url解析参数
		formCondition = (formCondition == null) ? "": formCondition.replace(/IS/g, "=");
		nodeId = nodeId != null ? nodeId: this.BaseUtil.getUrlParam('nodeId');
		if (formCondition != "") {
			nodeId = formCondition.split("=")[1];
		}
		var condition = formCondition == "" ? "jp_nodeId='" + nodeId + "'": formCondition;
		var panel = new Ext.panel.Panel({
			html: '<div style="background-color:#f0f0f0;font-size:15px"><h2>待审批单据信息</h2></div>'

		});
		var param = {
				caller: caller,
				condition: condition,
				_noc: 1
		};
		if (master) {
			param.master = master;
		}
		var nodesgrid = Ext.create('erp.view.common.JProcess.GridPanel', {
			anchor: '100%',
			deferLoadData:true
		});
		Ext.apply(me, {
			items: [{
				id: 'JProcessViewport',
				layout: 'auto',
				autoScroll: true,
				xtype: 'panel',
				style: {
					background: '#FFFFFF'
				},
				items: [{
					xtype: 'form',
					anchor: '100%',
					bodyStyle: {
						background: '#E3E3E3'
					},
					id: 'mainForm',
					items: [{
						id: 'app-header',
						xtype: 'box',
						height: 5,
						style: 'color: #596F8F;font-size: 16px;font-weight: 200;padding: 5px 5px;text-shadow: 0 1px 0 #fff'
					},{
						xtype: 'toolbar',
						id: 'currentNodeToolbar',
						layout: {
							type: 'hbox',
							align: 'right'
						},
						style: {
							background: 'transparent',
							border: 'none'
						},
						items: [{
							xtype: 'tbtext',
							id: 'processname'							
						},
						'->', {
							xtype: 'tbtext',
							id: 'label1',
							text: ''
						},
						'->', {
							xtype: 'tbtext',
							id: 'label2',
							text: ''
						},
						{
							xtype: 'tbtext',
							id: 'label3',
							text: ''
						}]

					},
					{
						xtype: 'fieldcontainer',
						labelWidth: 250,
						layout: 'column',
						id: 'container',
						items: [{
							xtype: 'checkboxfield',
							boxLabel: '<span style="font-weight: bold !important;">变更办理人</span>',
							columnWidth: 0.4,
							readOnly:ISexecuted,
							disabled:ISexecuted,
							checked: false,
							id: 'alterAssignee',
							handler: function(checkBox, checked) {
								this.ownerCt.ownerCt.ownerCt.ownerCt.getAssigneeCombox(checkBox, checked);
							}

						},
						{
							xtype: 'toolbar',
							width: '78%',	
							columnWidth: 0.6,
							layout: {
								type: 'hbox',
								align: 'right'
							},
							style: {
								background: 'transparent',
								border: 'none'
							},
							items: ['->', {
								xtype: 'tbtext',
								id: 'currentnode'
							},
							'-', {
								xtype: 'tbtext',
								id: 'launchername'
							},
							'-', {
								xtype: 'tbtext',
								id: 'launchtime'
							}]
						},
						{
							xtype: 'fieldcontainer',
							columnWidth: 1,
							id: 'container2',
							labelWidth: 100,
							layout: 'hbox'
						}]
					},
					{
						xtype: 'fieldcontainer',
						id: 'container3',
						labelWidth: 100,
						layout: 'column',
						labelSeparator: '',
						margin: '0 0 0 5',
						fieldLabel: '<h2>审批意见</h2>',
						items: [{
							xtype: 'textfield',
							name: 'dealMessage',
							id: 'dealMessage',
							hideLabel: true,
							columnWidth: 0.65,
							labelAlign: 'right',
							fieldStyle: 'background:#FFFAFA;color:#515151;'
						}]
					},{
						xtype: 'fieldcontainer',
						id: 'customSetup',
						labelWidth: 100,
						labelSeparator: '',
						fieldLabel: '<h2>审批要点</h2>',
						layout: 'column',
						hidden: true,
						margin: '0 72 0 5'
					},                   
					{
						xtype: 'fieldcontainer',
						id: 'container4',
						labelWidth: 100,
						layout: 'anchor'
					},
					{
						xtype:'mfilefield',
						id:'attachs',
						name:'attachs',
						collapsed: true,
						collapsible: true,
						frame:true,
						modify:true,
						style: '',
						title: '<img src="' + basePath + 'resource/images/icon/attach.png" width=20/>&nbsp;&nbsp;<span style="font-weight:bold;font-size:13px;">附件</span>',
						listeners : {
							afterrender: function(f){
								if(f.value != null && f.value.toString().trim() != ''){
									f.download(f.value);
								}
							}
						}
					},{
						xtype: 'fieldset',
						margin: '2 2 2 2',
						collapsible: true,
						collapsed: true,
						layout:'column',
						title: '<img src="' + basePath + 'resource/images/mainpage/info.png" width=20/>&nbsp;&nbsp;<span style="font-weight:bold;font-size:13px;">知会</span>',
						items: [Ext.create('erp.view.core.trigger.AddDbfindTrigger',{
							anchor:'100% 30%',
							emptyText:'选择知会人',
							fieldStyle: 'background:#FAFAFA;color:#515151;',
							hideLabel:true,
							name:'notifyPeople',
							id:'notifyPeople',
							group:0,
							columnWidth:0.4
						}),{
							name:'notifyPeopleid',
							id:'notifyPeopleid',
							xtype:'textfield',
							hidden:true
						},Ext.create('erp.view.core.trigger.AddDbfindTrigger',{
							anchor:'100% 30%',
							emptyText:'选择知会岗位',
							name:'notifyGroupName',
							id:'notifyGroupName',
							group:0,
							fieldStyle: 'background:#FAFAFA;color:#515151;padding-left:5px',
							hideLabel:true,
							columnWidth:0.4
						}),{
							name:'notifyGroup',
							id:'notifyGroup',
							xtype:'textfield',
							hidden:true
						},{
							text : '清空',
							xtype:'button',
							handler : function(b) {
								Ext.Array.each(b.ownerCt.items.items,function(item){
									item.reset();
								});
							}
						},{
							text : '确定',
							xtype:'button',
							handler : function(b) {
								var peoples=Ext.getCmp('notifyPeopleid').value;
								var groups=Ext.getCmp('notifyGroup').value;
								var peoplesname=Ext.getCmp('notifyPeople').value;
								var groupsname=Ext.getCmp('notifyGroupName').value;
								if((peoples==null || peoples=='' )&& (groups==null || groups=='')) showMessage('提示','先选择需要知会的岗位或人员!',1000); 
								else me.saveNotify(peoples,groups,peoplesname,groupsname,me,b);
							}
						}]

					},{
						xtype: 'fieldset',
						margin: '2 2 2 2',
						collapsible: true,
						collapsed: true,
						layout:'column',
						title: '<img src="' + basePath + 'resource/images/icon/communicate.png" width=20/>&nbsp;&nbsp;<span style="font-weight:bold;font-size:13px;text-align:top;height:30px;">沟通</span>',
						items: [{
							name      : 'com_record',
							id        :'com_record',
							hideLabel : true,
							autoHeight : true,
							height:250,
							columnWidth:0.6,
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
							fieldStyle: 'border-bottom: none;'
						},{
							xtype:'form',
							columnWidth:0.4,
							height    :250,
							layout:'anchor',
							frame:true,
							items:[{
								xtype:'textareafield',
								name :'communicaterecord',
								id:'communicaterecord',
								hideLabel:true,
								allowBlank:false,
								anchor:'100% 70%',
								fieldStyle: 'background:#FAFAFA;color:#515151;'
							},Ext.create('erp.view.core.trigger.MultiDbfindTrigger',{
								anchor:'100% 30%',
								emptyText:'选择沟通人',
								fieldStyle: 'background:#FAFAFA;color:#515151;',
								id:'communicator',	
								name:'communicator',
								allowBlank:false,
								labelWidth : 50
							}),{
								id:'communicatorid',
								name:'communicatorid',
								xtype:'textfield',
								hidden:true
							}],
							buttonAlign : 'center',
							buttons : [{
								text : '重置',
								handler : function(b) {
									b.ownerCt.ownerCt.getForm().reset();
								}
							},{
								text : '确定',
								formBind: true,
								handler : function(b) {
									me.CommunicateWithOther(b.ownerCt.ownerCt,me);
								}
							}]							
						}]
					},{
						xtype: 'toolbar',
						style: {
							background: 'transparent',
							border: 'none'
						},
						id: 'container5',
						anchor: '100%',
						layout: 'hbox',
						readOnly: ISexecuted,
						disabled: ISexecuted,
						items: [{
							xtype: 'splitter',
							width: 20
						},
						{
							xtype: 'button',
							text: '同&nbsp;&nbsp;&nbsp;&nbsp;意',
							iconCls: 'x-button-icon-agree',
							cls: 'x-btn-gray',
							width: 90,
							handler: function() {
								this.ownerCt.ownerCt.ownerCt.ownerCt.executeTask(1);
							}
						},
						{
							xtype: 'splitter',
							width: 10
						},
						{
							xtype: 'button',
							id:'disagree',
							text: '不 &nbsp;同 &nbsp;意',
							iconCls: 'x-button-icon-unagree',
							cls: 'x-btn-gray',
							width: 90,
							handler: function() {
								this.ownerCt.ownerCt.ownerCt.ownerCt.executeTask(2);
							}
						},
						{
							xtype: 'splitter',
							width: 10
						},{
							xtype: 'button',
							iconCls: 'x-button-icon-turn',
							text: '结束沟通',
							cls: 'x-btn-gray',
							iconCls:'x-button-icon-talk',
							width: 90,
							handler: function() {
								this.ownerCt.ownerCt.ownerCt.ownerCt.endcommunicateTask(me);
							}
						},{
							xtype: 'splitter',
							width: 10
						},{
							xtype: 'button',
							text: '结束流程',
							width: 90,
							iconCls: 'x-button-icon-end',
							cls: 'x-btn-gray',
							handler: function() {
								this.ownerCt.ownerCt.ownerCt.ownerCt.endProcess();
							}
						},{
							xtype: 'splitter',
							width: 10
						},{
							xtype: 'button',
							iconCls: 'x-button-icon-turn',
							text: '下一条',
							cls: 'x-btn-gray',
							width: 90,
							handler: function() {
								this.ownerCt.ownerCt.ownerCt.ownerCt.skipTask();
							}
						},
						{
							xtype: 'splitter',
							width: 10
						},
						/*{
							xtype: 'button',
							text: '结束流程',
							width: 90,
							iconCls: 'x-button-icon-end',
							cls: 'x-btn-gray',
							handler: function() {
								this.ownerCt.ownerCt.ownerCt.ownerCt.endProcess();
							}
						},
						{
							xtype: 'splitter',
							width: 10
						},
						{
							xtype: 'button',
							text: '删除流程',
							iconCls: 'tree-delete',
							disabled: true,
							width: 90,
							cls: 'x-btn-gray',
							handler: function() {
								this.ownerCt.ownerCt.ownerCt.ownerCt.deleteProcess();
							}
						},
						{
							xtype: 'splitter',
							width: 10
						},
						{
							xtype: 'button',
							text: '重置流程',
							iconCls: 'x-button-icon-reset',
							width: 90,
							cls: 'x-btn-gray',
							handler: function() {
								this.ownerCt.ownerCt.ownerCt.ownerCt.backProcess();
							}
						},
						{
							xtype: 'splitter',
							width: 10
						},*/
						{
							xtype: 'button',
							text: '关&nbsp;&nbsp;&nbsp;&nbsp;闭',
							iconCls: 'x-button-icon-close',
							cls: 'x-btn-gray',
							id:'closeProcess',
							width: 90,
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
						}
						]
					}]
				},
				panel, nodesgrid]
			}]
		});
		me.callParent(arguments);
	},
	getProcessData: function(params) {
		Ext.Ajax.request({
			url: basePath + form.saveUrl,
			params: params,
			method: 'post',
			async: false,
			callback: function(options, success, response) {
				var localJson = new Ext.decode(response.responseText);
				ProcessData = localJson.data;
			}
		});
	},
	getAssigneeCombox: function(checkBox, checked) {
		var processInstanceId = ProcessData.jp_processInstanceId,me=this;
		var button = Ext.create('Ext.Button', {
			text: '确定',
			cls: 'x-btn-gray',
			iconCls: 'x-button-icon-save',
			margin: '0 0 0 10',
			handler: function() {
				var value = Ext.getCmp('AssigneeComboxcode').getValue();
			/*	var customs = Ext.getCmp('customSetup').items.items;
				var flag = 0;
				Ext.each(customs,
						function(cu) {
					if (!cu.items.items[1].allowBlank && (cu.items.items[1].getValue() == null || cu.items.items[1].getValue() == '')) {
						Ext.Msg.alert('提示', "<b>" + cu.items.items[0].value.fontcolor("Red") + "</b>为必填项！");
						flag++;
						return;
					}
				});
				if (flag > 0) {
					return;
				}
				var cValues = [];
				Ext.each(customs,
						function(cu) {
					if (cu.items.items[1].getValue() != null && cu.items.items[1].getValue() != '') {
						var value = cu.items.items[1].getValue() instanceof Date ? me.getStringByDate(cu.items.items[1].getValue()) : cu.items.items[1].getValue();
						var labelValue = cu.items.items[0].getFieldLabel() + "(" + value + ")";
						if(cu.items.items[1].logic!='') labelValue+='@'+cu.items.items[1].logic+'@';
						cValues.push(labelValue);
					}
				});
				var opd = cValues.join(";");*/
				if (!value) {
					Ext.Msg.alert('提示', '请先选择变更办理人!');
					return;
				} else {
					Ext.Ajax.request({
						url: basePath + 'common/setAssignee.action',
						params: {
							taskId: ProcessData.jp_nodeId,
							assigneeId: value,
							processInstanceId: processInstanceId,
							//customDes: opd,
							description: Ext.getCmp('changedescription').getValue(),
							_noc: 1,
							_center:_center
						},
						success: function(response) {
							var jsonData = Ext.decode(response.responseText);
							if (jsonData.result) {
								var nextnode = jsonData.nextnode;										
								     function showResult(btn){
											 me.dealNextStep(nextnode,jsonData._tomaster);
										}
								        Ext.Msg.show({
								            title:'提示',
								            msg: '变更成功',
								            buttons: Ext.Msg.OK,
								            closable: false,
								            fn: showResult
								        });								
									
									if (parent && parent.Ext.getCmp('content-panel')) {
										var firstGrid = parent.Ext.getCmp('content-panel').items.items[0].firstGrid;
										if (firstGrid && firstGrid != null) {
											firstGrid.loadNewStore();
										}
									}
									return;
							
							} else {
								Ext.Msg.alert('提示', "该任务不存在，无法变更!");
							}

						}
					});
				}
			}
		});
		var container = Ext.getCmp('container2');
		if (checked) {
			if(!ISexecuted){
				container.add([{
					fieldLabel: '变更办理人',
					xtype: 'multifield',
					name: 'AssigneeComboxcode',
					editable: false,
					width: 350,
					fieldStyle: 'background:#fffac0;color:#515151;',
					id: 'AssigneeComboxcode',
					secondname: 'em_name'
				},
				{
					xtype: 'textareatrigger',
					name: 'changedescription',
					id: 'changedescription',
					fieldStyle: 'background:#FFFAFA;color:#515151;',
					width: 350,
					labelAlign: 'right',
					fieldLabel: '变更描述'
				},
				button]);	
			}
		} else {
			container.removeAll(true);
		}
	},
	deleteProcess: function() {
		Ext.Ajax.request({
			url: basePath + 'common/deleteProcess.action',
			params: {
				processInstanceId: ProcessData.jp_processInstanceId
			},
			success: function(response) {
				var text = response.responseText;
				jsonData = Ext.decode(text);
				if (jsonData.success) {
					Ext.Msg.alert('提示', "流程已删除!");
				}
				if (jsonData.exceptionInfo) {
					showError("该流程实例不存在");
				}
			}
		});

	},
	backProcess: function() {
		var mb = new Ext.window.MessageBox();
		mb.wait('系统正在处理', '请稍后');
		Ext.Ajax.request({
			url: basePath + 'common/backToLastNode.action',
			params: {
				processInstanceId: ProcessData.jp_processInstanceId,
				jnodeId: ProcessData.jp_nodeId
			},
			success: function(response) {
				mb.close();
				var text = response.responseText;
				jsonData = Ext.decode(text);
				if (jsonData.success) {
					Ext.Msg.alert('提示', '流程重置成功!');
				}
				if (jsonData.exceptionInfo) {
					showError(jsonData.exceptionInfo);
				}
			}
		});
	},
	endProcess: function() {
		var me=this,startdealtime = new Date(),taskId = ProcessData.jp_nodeId;
		Ext.Ajax.request({
			url: basePath + 'common/endProcessInstance.action',
			params: {
				processInstanceId: ProcessData.jp_processInstanceId,
				holdtime: ((startdealtime - basestarttime) / 1000).toFixed(0),
				nodeId: taskId
			},
			callback: function(options, success, response) {
				var text = response.responseText;
				jsonData = Ext.decode(text);
				if (jsonData.success) {
					Ext.Msg.alert('提示', "流程已结束!");				
				}
				if (jsonData.exceptionInfo) {
					showError(jsonData.exceptionInfo);
					Ext.Msg.alert('提示', "该流程实例不存在!");
				}
			}
		});

	},

	dealNextStep: function(nextnode,toMaster) {
		var nextNodeId = null,tab=null,me=this;		
		if(parent.Ext.getCmp('content-panel')) tab=parent.Ext.getCmp('content-panel').getActiveTab();
		function processResult() {
			var btn = arguments[0];
			if (btn == 'yes') {
				var confirm = new Ext.button.Button({
					text: '确定',
					handler: function(btn) {
						var items = Ext.ComponentQuery.query('window >tabpanel>panel>radio');
						var params = new Array();
						Ext.each(items,function(item){
							if(item.getValue()){
								var param = new Object();
								var label = item.boxLabel;//em_name(em_code) 2013-3-8 10:18:11 
								var em_code = label.substring(label.lastIndexOf('(')+1,label.length-1);
								param.em_code = em_code;
								param.nodeId=item.name;
								params.push(JSON.stringify(param));						
							}
						});
						Ext.Ajax.request({
							url: basePath + 'common/takeOverTask.action',
							async: false,
							params: {
								params:unescape(params),
								_noc: 1
							},
							callback: function(options, success, response) {
								var text = response.responseText;
								jsonData = Ext.decode(text);
								if (jsonData.success) {
									Ext.Msg.alert('提示', "指派成功!");
									me.dealNextStep(nextnode,toMaster);
									Ext.getCmp('assignwin').close();
									
								} else {
									Ext.Msg.alert('提示', "指派失败!");
									Ext.getCmp('assignwin').close();
								}
								

							}
						});						
					}
				});
				var cancel = new Ext.button.Button({
					text: '取消',
					handler: function() {		
						if (nextnode && nextnode != '-1') {
							me.loadNextTask(nextnode,toMaster);
						} else {
							//if (tab!=null )tab.close();
							var closeBtn = Ext.getCmp('closeProcess');
							closeBtn.handler();
						}
					}
				});

				var searchKey = new Object();
				var win = Ext.create('Ext.window.Window', {
					title: '指定下一步任务审批人',
					height: 450,
					width: 650,
					id: 'assignwin',
					modal:true,
					layout:'border',
					closable:false,
					buttons: [confirm, cancel],
					buttonAlign: 'center',
					items: []
				});
				win.add([{
					xtype:'textfield',
					margin:'10 20 10 20',
					fieldLabel:'快速搜索',
					labelStyle:'font-weight:bold;',
					id:'searchtextfield',
					region:'north',
					enableKeyEvents:true,
					listeners:{
						keydown:function(field,e){
							if(e.getKey()==Ext.EventObject.ENTER){	
								searchKey[Ext.getCmp('assignTab').getActiveTab().id]=field.value;
								var results=Ext.Array.filter(jsonData.actorUsers[Ext.getCmp('assignTab').getActiveTab().id].JP_CANDIDATES,function(JP_CANDIDATE){
									if(field.value==undefined || JP_CANDIDATE.indexOf(field.value)!=-1) return JP_CANDIDATE;
								});
								Ext.Array.each(Ext.getCmp('assignTab').getActiveTab().personUsers,function(item){
									Ext.getCmp('assignTab').getActiveTab().remove(item);
								});						
								addUserItems(Ext.getCmp('assignTab').getActiveTab(),jsonData.actorUsers[Ext.getCmp('assignTab').getActiveTab().id].JP_NODEID,results);

							}
						}
					}
				}]);
				var assignTab = new Ext.TabPanel({
					id : 'assignTab',
					enableTabScroll : true,
					closeAll : true,				   				  
					region:'center',
					minTabWidth :80,
					autoHeight:true,  
					resizeTabs : true,
					listeners:{
						'tabchange':function(tabPanel,newCard,oldCard,eOpts){											
							Ext.getCmp('searchtextfield').setValue(searchKey[newCard.id]);
						}
					}
				});
				win.add(assignTab);				
				for (var i = 0; i < jsonData.actorUsers.length; i++) {
					var panel=new Ext.Panel({
						id:i.toString(),
						width: 480, 
						autoHeight:true,  
						autoScroll:true,
						layout:'column',
						bodyStyle: 'background:#e0e0e0',
						title:jsonData.actorUsers[i].JP_NODENAME
					});
					assignTab.add(panel);						
					addUserItems(panel,jsonData.actorUsers[i].JP_NODEID,jsonData.actorUsers[i].JP_CANDIDATES);
				}
				assignTab.setActiveTab(0);
				win.show();

			} else {
				//if (tab!=null )tab.close();
				var closeBtn = Ext.getCmp('closeProcess');
				closeBtn.handler();
			}
		}
		function addUserItems(panel,jp_nodeid,jp_candidates){
			var me=this;
			var maxSize=jp_candidates.length>24?24:jp_candidates.length,personUsers=new Array(),user=null,more=Ext.getCmp('more'+panel.id);		
			if(more)more.destroy();
			for(var j=0;j<maxSize;j++){
				user=Ext.create('Ext.form.field.Radio',{
					name:jp_nodeid,
					boxLabel:jp_candidates[j],
					columnWidth: 0.33,
					fieldCls:'x-myradio',
					checked: j==0?true:false
				});
				personUsers.push(user);			
			}			 
			panel.add(personUsers);
			panel.personUsers=personUsers;
			if(jp_candidates.length>maxSize){
				panel.add({ xtype: 'textfield',
					readOnly:true,
					labelSeparator:'',
					columnWidth:1,
					id:'more'+panel.id,
					fieldStyle : 'background:#e0e0e0;border-bottom:none;vertical-align:middle;border-top:none;border-right:none;border-bottom:none;border-left:none;',
					fieldLabel: '『<a href="#" class="terms">全部</a>』',
					listeners: {
						click: {
							element: 'labelEl',
							fn: function(e,el) {
								var target = e.getTarget('.terms');
								Ext.getCmp('more'+panel.id).destroy();
								if (target) {
									Ext.Array.each(panel.personUsers,function(item){
										panel.remove(item);
									});	
									var personUsers=new Array();
									for(var i=0;i<jp_candidates.length;i++){
										user=Ext.create('Ext.form.field.Radio',{
											name:jp_nodeid,
											boxLabel:jp_candidates[i],
											columnWidth: 0.33,
											fieldCls:'x-myradio',
											checked: i==0?true:false
										});
										personUsers.push(user);			
									}
									panel.add(personUsers);
									panel.personUsers=personUsers;
									e.preventDefault();
								}
							}
						}
					}
				});	
			}			
		}
		Ext.Ajax.request({
			url: basePath + 'common/dealNextStepOfPInstance.action',
			params: {
				processInstanceId: ProcessData.jp_processInstanceId,
				_noc: 1
			},
			callback: function(options, success, response) {
				var text = response.responseText;
				jsonData = Ext.decode(text);
				if (jsonData.hasNext) {
					if (jsonData.actorUsers.length > 0) {
						Ext.Msg.show({
							title: '提示',
							msg: '下一步审批节点有多位处理人,现在指定>>>',
							buttons: Ext.Msg.YESNO,
							icon: Ext.window.MessageBox.QUESTION,
							closable: false,
							fn: processResult

						});
						//nextNodeId = jsonData.nodeId;
					} else {
						if (nextnode && nextnode != '-1') {
							me.loadNextTask(nextnode,toMaster);
						} else {
							//if (tab!=null )tab.close();
							var closeBtn = Ext.getCmp('closeProcess');
							if(closeBtn){
								closeBtn.handler();
							}
						}
						return;
					}

				} else {
					tab.close();
				}

			}
		});
	},
	loadNextTask:function(nextnode,toMaster){
		var url="jsps/common/jprocessDeal.jsp?whoami=JProcess!Me&formCondition=jp_nodeId=" + nextnode;
		if(toMaster!=null){
			//changemaster
			url+='&_center=1';
			var modalwin=parent.Ext.getCmp('modalwindow');
			if(modalwin && modalwin.relateMaster!=toMaster){
				Ext.Ajax.request({
					url: basePath + 'common/changeMaster.action',
					params: {
						to: toMaster
					},
					async:false,
					callback: function(opt, s, r) {
						var localJson = new Ext.decode(r.responseText);
						var modalwin=parent.Ext.getCmp('modalwindow');
						modalwin.relateMaster=toMaster;
						modalwin.setTitle('创建到账套' + localJson.currentMaster  + '的临时会话');
					}
				});	
			}										
		}
		window.location.href = basePath + url;
	},
	executeTask: function(value) {
		var me = this;
		var dealMessage = Ext.getCmp('dealMessage').getValue(); //处理信息 2013-3-6 20:58:14 
		var customs = Ext.getCmp('customSetup').items.items;	
		var attachs = Ext.getCmp('attachs').items.items[0].value;
		var flag = 0;
		Ext.each(customs,
				function(cu) {
			if (!cu.items.items[1].allowBlank && (cu.items.items[1].getValue() == null || cu.items.items[1].getValue() == '')) {
				Ext.Msg.alert('提示', "<b>" + cu.items.items[0].value.fontcolor("Red") + "</b>为必填项！");
				flag++;
				return;
			}
		});
		if (flag > 0) {
			return;
		}
		var cValues = [];
		Ext.each(customs,
				function(cu) {
			if (cu.items.items[1].getValue() != null && cu.items.items[1].getValue() != '') {
				var value =cu.items.items[1].getValue() instanceof Date ? me.getStringByDate(cu.items.items[1].getValue()) : cu.items.items[1].getValue();  
				var labelValue = cu.items.items[0].value + "(" + value + ")";
				if(cu.items.items[1].logic!='') labelValue+='@'+cu.items.items[1].logic+'@';
				cValues.push(labelValue);
			}
		});
		var opd = cValues.join(";");
		switch (value) {
		case 1:
		{
			var taskId = ProcessData.jp_nodeId;
			var nodeName = ProcessData.jp_nodeName;
			var mb = new Ext.window.MessageBox();
			mb.wait('系统正在处理', '请稍后');
			var startdealtime = new Date();
			var form = (iframe_maindetail.contentWindow||iframe_maindetail.window).Ext.getCmp('form'),values=form.getValues();
			var bool = true;
			if (requiredFields != null) {
				if (!canexecute) {
					bool = false;
					mb.close();
					showError('请在同意之前先保存必填信息!');
					return false;
				}
				var fields = requiredFields.split(",");
				Ext.Array.each(fields,function(field) {
					if (form.down('#'+field) && (values[field] == null || values[field] == "" || conditionValidation==1)) {
						bool = false;
						mb.close();
						showError('请在同意之前先保存必填信息!');
						return false;
					}
				});				
			}
			if (bool) {
				Ext.Ajax.request({
					url: basePath + 'common/review.action',
					params: {
						taskId: taskId,
						nodeName: nodeName,
						nodeLog: dealMessage,
						holdtime: ((startdealtime - basestarttime) / 1000).toFixed(0),
						customDes: opd,
						result: true,
						master: master,
						attachs:attachs,
						_center:_center,
						_noc: 1
					},
					callback: function(options, success, response) {
						try {
							var text = response.responseText;
							var jsonData = Ext.decode(text);
							if (jsonData.exceptionInfo != null) {
								mb.close();
								showError("<div style='color:red;font-size:15px;'>无法审批</div>" + jsonData.exceptionInfo);
								return;
							}
							else if (jsonData.success) {
								var nextnode = jsonData.nextnode;
								if (nextnode == '0') {
									mb.close();
									Ext.Msg.alert('提示', nodeName + '节点已审批!');
									return;
								} else {
									mb.close();						
								     function showResult(btn){
								
											if (jsonData.after != null && jsonData.after != "") {
												var str = jsonData.after;
												if (str.trim().substr(0, 12) == 'AFTERSUCCESS') { //特殊情况:操作成功，但是出现警告,允许刷新页面
													str = str.replace('AFTERSUCCESS', '');											
													me.dealNextStep(nextnode,jsonData._tomaster);
													showError(jsonData.after);
												} else showError(str);
											} else me.dealNextStep(nextnode,jsonData._tomaster);
										}
								        Ext.Msg.show({
								            title:'提示',
								            msg: '审批成功!',
								            buttons: Ext.Msg.OK,
								            closable: false,
								            fn: showResult
								        });								
									
									if (parent && parent.Ext.getCmp('content-panel')) {
										var firstGrid = parent.Ext.getCmp('content-panel').items.items[0].firstGrid;
										if (firstGrid && firstGrid != null) {
											firstGrid.loadNewStore();
										}
									}
									return;
								}
							} else {
								mb.close();
								Ext.Msg.alert('提示', "该任务已提交,不能重复操作！");
								parent.Ext.getCmp('content-panel').getActiveTab().close();
							}
						} catch(e) {
							showError(Ext.decode(response.responseText).exceptionInfo);
						}
					}
				});
			}

		};
		break;
		case 2:
		{
			//不同意 去选择退回节点
			//取处理历史记录
			var grid = Ext.getCmp('historyGrid');
			var nodegriddata = grid.store.data;
			var combodata = new Array();
			combodata.push({
				display: '制单人',
				value: 'RECORDER'
			});	
			if(forknode==0){//并行节点只能回退至制单人
			Ext.Array.each(nodegriddata.items,
					function(item) {
				if (item.data.jn_dealResult == '同意' && item.data.jn_attach == 'T') {
					combodata.push({
						display: item.data.jn_name,
						value: item.data.jn_name
					});
				}
			});
			}
			Ext.create('Ext.window.Window', {
				title: '指定回退节点',
				height: 200,
				width: 400,
				layout: 'column',
				id: 'win',
				buttonAlign: 'center',
				defaults: {
					fieldStyle: 'background:#FFFAFA;color:#515151;',
					columnWidth: 0.9
				},
				allowDrag: false,
				items: [{
					xtype: 'combo',
					fieldLabel: '回退节点',
					name: 'backtask',
					id: 'backtask',
					isFormField : true,
					listConfig: {
						maxHeight: 180
					},
					fieldStyle: 'background:#fffac0;color:#515151;',
					store: {
						fields: ['display', 'value'],
						data: [{
							display: '制单人',
							value: 'RECORDER'
						}]
					},
					displayField: 'display',
					valueField: 'value',
					queryMode: 'local',
					allowBlank: false,
					value: 'RECORDER',
					onTriggerClick: function(trigger) {
						var me = this;
						this.getStore().loadData(combodata);
						if (!me.readOnly && !me.disabled) {
							if (me.isExpanded) {
								me.collapse();
							} else {
								me.expand();
							}
							me.inputEl.focus();
						}
					}
				},
				{
					xtype: 'textarea',
					fieldLabel: '回退原因',
					name: 'unagreedescription',
					id: 'unagreedescription',
					fieldStyle: 'background:#fffac0;color:#515151;',
					allowBlank: false,
					isFormField : true
				}],
				buttons: [{
					text: $I18N.common.button.erpConfirmButton,
					xtype: 'button',
					formBind: true,
					handler: function() {
						var taskId = ProcessData.jp_nodeId;
						var nodeName = ProcessData.jp_nodeName;
						var mb = new Ext.window.MessageBox();
						var startdealtime = new Date();
						var backTaskName = Ext.getCmp('backtask').value;
						var dealMessage = Ext.getCmp('unagreedescription').value;
						if(dealMessage==null || dealMessage==''){
							showMessage('提示', '请先填写回退原因!', 1000);
							return;
						}
						mb.wait('系统正在处理', '请稍后');
						Ext.Ajax.request({
							url: basePath + 'common/review.action',
							params: {
								taskId: taskId,
								nodeName: nodeName,
								backTaskName: backTaskName,
								nodeLog: dealMessage,
								holdtime: ((startdealtime - basestarttime) / 1000).toFixed(0),
								result: false,
								master: master,
								attachs:attachs,
								_noc: 1
							},
							callback: function(options, success, response) {
								try{
									var text = response.responseText;
									var jsonData = Ext.decode(text);
									mb.close();
									if (jsonData.exceptionInfo != null) {
										showError("<div style='color:red;font-size:15px;'>无法审批</div>" + jsonData.exceptionInfo);
										return;
									}
									if (jsonData.success) {
										var nextnode = jsonData.nextnode;
										showMessage('提示', '你已不同意!', 1000);
										Ext.getCmp('win').close();
										if (jsonData.after != null && jsonData.after != "") {
											showError(jsonData.after);
										} else me.dealNextStep(nextnode,jsonData._tomaster);
										if (parent && parent.Ext.getCmp('content-panel')) {
											var firstGrid = parent.Ext.getCmp('content-panel').items.items[0].firstGrid;
											if (firstGrid && firstGrid != null) {
												firstGrid.loadNewStore();
											}
										}
										return;
									} else {
										Ext.Msg.alert('提示', "该任务已处理,不能重复操作！");
										parent.Ext.getCmp('content-panel').getActiveTab().close();
									}

								}catch(e) {
									showError(Ext.decode(response.responseText).exceptionInfo);
								}}
						});
					}
				},
				{
					text: $I18N.common.button.erpCancelButton,
					handler: function() {
						Ext.getCmp('win').close();
					}
				}]
			}).show();
			return;
		};
		break;
		default:
			break;
		}
	},
	nextTask: function() {
		window.location.href = basePath + "jsps/common/jprocessDeal.jsp?whoami=JProcess!Me&formCondition=jp_nodeId=" + nextnodeId;
	},
	skipTask: function() {
		var taskId = ProcessData.jp_nodeId;
		var mb = new Ext.window.MessageBox();
		mb.wait('系统正在跳过', '请稍后');
		Ext.Ajax.request({
			url: basePath + 'common/getNextProcess.action',
			params: {
				taskId: taskId,
				_noc: 1
			},
			callback: function(options, success, response) {
				mb.close();
				var data = response.responseText;
				var jsonData = Ext.decode(data);
				if (jsonData.success && jsonData.nodeId!=-1) {
					window.location.href = basePath + "jsps/common/jprocessDeal.jsp?whoami=JProcess!Me&formCondition=jp_nodeId=" + jsonData.nodeId;
				} else {
					showMessage('提示','已无待审批的单据',0);
					parent.Ext.getCmp('content-panel').getActiveTab().close();
				}

			}
		});
	},
	saveNotify:function(notifyPeople,notifyGroup,peoplesname,groupnames,me,b){
		var data={
				processInstanceId:ProcessData.jp_processInstanceId,
				nodeId:ProcessData.jp_nodeId,
				nodeName:ProcessData.jp_nodeName
		};
		if (notifyPeople!=null && notifyPeople!='')data.notifyPeopleid=notifyPeople;
		if (notifyGroup !=null && notifyGroup!='')data.notifyGroup=notifyGroup;
		if (peoplesname !=null && peoplesname!='')data.notifyPeople=peoplesname;
		if (groupnames !=null && groupnames!='')data.notifyGroupName=groupnames;
		me.setLoading(true);
		Ext.Ajax.request({
			url: basePath + 'common/saveProcessNotify.action',
			params: {
				data:unescape(Ext.JSON.encode(data).replace(/\\/g, "%")),
				_noc: 1
			},
			callback: function(options, success, response) {
				me.setLoading(false);
				var data = response.responseText;
				var jsonData = Ext.decode(data);
				if (jsonData.success) {		
					showMessage('提示', '设置知会人员成功!', 1000);
					Ext.Array.each(b.ownerCt.items.items,function(item){
						item.reset();
					});
				} else {
					showError(data.exceptionInfo);
				}

			}
		});
	},
	endcommunicateTask:function (me){
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
					/*me.getCommunicates(taskId,ProcessData.jp_processInstanceId);
					form.reset();*/
					showMessage('提示', '已成功结束沟通!', 1000);
				} else {
					showError(data.exceptionInfo);
				}

			}
		});
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
					showError(data.exceptionInfo);
				}

			}
		});
	},
	getCommunicates:function(nodeId,processInstanceId){
		Ext.Ajax.request({
			url: basePath + 'common/communicateTask.action',
			params: {
				taskId: nodeId,
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
	getFieldByType: function(type, i,cs,label,necessary) {
		var logic='';
		var bool=necessary=='Y';
		var fieldStyle=bool?"background:#F5FFFA;":"background:#FFFAFA;";
		if(cs.indexOf("@")>0)
			logic=cs.substring(cs.indexOf("@")+1);
		switch (type) {
		case "S":
			return Ext.create('Ext.form.FieldContainer',{
				defaults: {
					hideLabel: true
				},
				hideLabel:true,
				columnWidth:0.8,
				layout: {
					type: 'table',
					columns:2,
					defaultMargins: {top: 0, right: 5, bottom: 0, left: 0}
				},
				items: [{
					xtype: 'displayfield',
					maxWidth :window.innerWidth * 0.5,
					value: (i+1)+'.'+label
				},{
					id:i,
					labelSeparator:'',
					padding:'0 0 0 10',
					xtype:'textfield',
					allowBlank:!bool,
					logic:logic}]

			});
			break;
		case "D":
			return Ext.create('Ext.form.FieldContainer',{
				defaults: {
					hideLabel: true
				},
				hideLabel:true,
				columnWidth:0.8,
				layout: {
					type: 'table',
					columns:2,
					defaultMargins: {top: 0, right: 5, bottom: 0, left: 0}
				},
				items: [{
					xtype: 'displayfield',
					maxWidth :window.innerWidth * 0.5,
					value: (i+1)+'.'+label
				},{
					id:i,
					format: 'Y-m-d',
					labelSeparator:'',
					padding:'0 0 0 10',
					logic:logic,
					allowBlank:!bool,
					xtype:'datefield'
				}]
			});
			break;
		case "N":
			return Ext.create('Ext.form.FieldContainer',{
				defaults: {
					hideLabel: true
				},
				hideLabel:true,
				columnWidth:0.8,
				layout: {
					type: 'hbox',
					defaultMargins: {top: 0, right: 5, bottom: 0, left: 0}
				},
				items: [{
					xtype: 'displayfield',
					maxWidth :window.innerWidth * 0.5,
					value: (i+1)+'.'+label
				},{
					id: i,
					labelSeparator:'',
					padding:'0 0 0 10',
					logic:logic,
					allowBlank:!bool,
					hideTrigger:true,
					fieldStyle:fieldStyle,
					xtype:'numberfield'
				}]
			});
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
				return Ext.create('Ext.form.FieldContainer',{
					defaults: {
						hideLabel: true
					},
					hideLabel:true,
					columnWidth:0.8,
					layout: {
						type: 'table',
						columns:2,
						defaultMargins: {top: 0, right: 5, bottom: 0, left: 0}
					},
					items: [{
						xtype: 'displayfield',
						maxWidth :window.innerWidth * 0.5,
						value: (i+1)+'.'+label
					},{
						store: comStore,
						queryMode: 'local',
						editable: false,
						displayField: 'value',
						padding:'0 0 0 10',
						valueField: 'value',
						logic:logic,					
						labelSeparator:'',
						fieldStyle:fieldStyle, 
						xtype:'combo',
						allowBlank:!bool
					}]
				});
			}
			break;
		case "B":
			var comStore = Ext.create('Ext.data.Store', {
				fields: ['value'],
				data: [{
					"value": "是"
				},{
					"value": "否"
				},{
					"value": "不执行"
				}]
			});
			return Ext.create('Ext.form.FieldContainer',{
				defaults: {
					hideLabel: true
				},
				hideLabel:true,
				columnWidth:0.8,
				layout: {
					type: 'table',
					columns:2,
					defaultMargins: {top: 0, right: 5, bottom: 0, left: 0}
				},
				items: [{
					xtype: 'displayfield',
					maxWidth :window.innerWidth * 0.3,
					value: (i+1)+'.'+label
				},{
					store: comStore,
					queryMode: 'local',
					editable: false,
					displayField: 'value',
					valueField: 'value',
					columnWidth: 0.3,
					padding:'0 0 0 10',
					logic:logic,
					fieldStyle:fieldStyle ,
					blankText: "不允许为空",
					allowBlank:!bool,
					hideLabel:true,
					xtype:'combo',
					labelSeparator:''
				}
				]
			});
		}

	},
	getStringByDate: function() {
		var date = arguments[0];
		var m = date.getMonth();
		var month = m + 1;
		return date.getFullYear() + "-" + month + "-" + date.getDate();
	},
	listeners: {
		beforerender: function() {
			var me = this;
			formCondition = this.BaseUtil.getUrlParam('formCondition'); //从url解析参数
			formCondition = (formCondition == null) ? "": formCondition.replace(/IS/g, "=");
			var strArr = formCondition.split("=");
			Ext.Ajax.request({
				url: basePath + 'common/getCustomSetupOfTask.action',
				params: {
					nodeId: strArr[1],
					master: master,
					_noc: 1
				},
				success: function(response, options) {
					var localJson = Ext.decode(response.responseText);
					cs = localJson.cs;
					var arr = null;
					if (localJson.data != null) {
						arr = localJson.data.split(";");
					}
					if(localJson.isApprove==1){
						Ext.getCmp('disagree').setDisabled(true);
					}
					var customSetup = Ext.getCmp('customSetup');
					if (cs != null && cs.length > 0) {
						customSetup.show();
						var csstr='';
						for (var i = 0; i < cs.length; i++) {
							csstr=cs[i].toString();
							var i1 = csstr.indexOf('^');
							var i2 = csstr.indexOf('$');
							var value = csstr.substring(0, i1);
							var type = csstr.substring(i1 + 1, i2);
							var neccesary = csstr.substring(i2 +1,i2+2);
							var field = me.getFieldByType(type, i,csstr,value,neccesary);
							if (arr != null) {
								field.items.items[1].setValue(arr[i].substring(arr[i].indexOf("(") + 1, arr[i].indexOf(")")));
							}
							customSetup.add(field);
						}
					}
				}
			});
		},
		afterrender: function() {
			var me = this;
			formCondition = this.BaseUtil.getUrlParam('formCondition'); //从url解析参数
			formCondition = (formCondition == null) ? "": formCondition.replace(/IS/g, "=");
			var strArr = formCondition.split("=");
			nodeId = nodeId != null ? nodeId: this.BaseUtil.getUrlParam('nodeId');
			nodeId =nodeId !=null?nodeId :strArr[1];
			Ext.Ajax.request({ 
				url: basePath + 'common/getProcessInstanceId.action',
				params: {
					jp_nodeId: nodeId,
					master: master,
					_noc: 1
				},
				success: function(response) {
					var res = response.responseText;
					processInstanceId = Ext.decode(res).processInstanceId;
					me.getCommunicates(nodeId,processInstanceId);
					Ext.getCmp('historyGrid').getOwnStore(processInstanceId);
					Ext.Ajax.request({ //获取当前节点对应的JProcess对象
						url: basePath + 'common/getCurrentNode.action',
						params: {
							jp_nodeId: nodeId,
							master: master,
							_noc: 1
						},
						success: function(response) {
							var res = new Ext.decode(response.responseText);
							ProcessData = res.info.currentnode;
							forknode=res.info.forknode;
							if(ProcessData.jp_pagingid && parent.Ext){
								var win=parent.Ext.getCmp('msg-win-'+ProcessData.jp_pagingid);
								if(win){
									win.close();
								}
							}
							Ext.getCmp('currentnode').setText('当前节点:<font size=2 color="red">' + ProcessData.jp_nodeName + '</font>');
							Ext.getCmp('launchername').setText('发起人:<font size=2 color="red">' + ProcessData.jp_launcherName + '</font>');
							Ext.getCmp('launchtime').setText('发起时间:<font size=2 color="red">' + Ext.Date.format(new Date(ProcessData.jp_launchTime), "Y-m-d H:i:s") + '</font>');
							Ext.getCmp('label1').setText('<span style="font-weight: bold !important;font-size:18px">' + ProcessData.jp_name + '</span>');
							var formCondition = ProcessData.jp_keyName + "IS" + ProcessData.jp_keyValue;
							var gridCondition = '';
							if (ProcessData.jp_keyName) {
								gridCondition = ProcessData.jp_formDetailKey + 'IS' + ProcessData.jp_keyValue;
							}

							var caller = ProcessData.jp_caller;
							var url = basePath + ProcessData.jp_url;
							var queryType='form';
							var myurl;
							if (me.BaseUtil.contains(url, '?', true)) {
								myurl = url + '&formCondition=' + formCondition + '&gridCondition=' + gridCondition;
							} else {
								myurl = url + '?formCondition=' + formCondition + '&gridCondition=' + gridCondition;
							}
							myurl += '&_noc=1&datalistId=NaN'; // 不限制权限
							if (master) {
								myurl += '&newMaster=' + master;
							}
							if(myurl.indexOf('jsps/ma/jprocess/AutoJprocess.jsp?type=1')>0){
								myurl+='&caller='+caller;
								queryType='tabpanel';
							}
							panel = new Ext.panel.Panel({
								id: 'mm',
								style: {
									background: '#f0f0f0',
									border: 'none'
								},
								frame: true,
								border: false,
								layout: 'fit',
								height: window.innerHeight,
								iconCls: 'x-tree-icon-tab-tab',
								//html: '<iframe id="iframe_maindetail" name = "iframe_maindetail" src="' + myurl + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',						
								items: {xtype: 'component',
									   id:'iframe_maindetail',									
									   autoEl: {
									   tag: 'iframe',
									   style: 'height: 100%; width: 100%; border: none;',
									   src: myurl},
								listeners: {
									load: {
									   element: 'el',
									   fn: function (e) {
									      TaskId= window.setInterval(findToolbar, 1000);
									   }}
									}}
							});
						      
							var viewport = Ext.getCmp("JProcessViewport");							
							viewport.insert(2, panel);
							//TaskId= window.setInterval(findToolbar, 1000);
							function findToolbar() {
								var childpanel,childtoolbar;
								var w = iframe_maindetail.contentWindow||iframe_maindetail.window;
								if (w.Ext) {
									//childpanel = w.Ext.ComponentQuery.query(queryType)[0];
									childpanels = w.Ext.ComponentQuery.query(queryType);
									Ext.Array.each(childpanels,function(item,index){
										if(item.dockedItems){
											if(item.dockedItems.items.length>0){
												childpanel = item;
												return false;
											}
										}
									});
									childtoolbar = w.Ext.ComponentQuery.query(queryType+'>toolbar')[0];
									var grid=w.Ext.ComponentQuery.query('grid')[0];
									if (!childpanel || !childpanel.dockedItems) return;
									Ext.Array.each(childpanel.dockedItems.items,
											function(item) {
										if (item.dock == 'bottom') {
											item.removeAll();
											toolbar = item;
										}
									});
									if (childtoolbar != null && (grid==null || (grid!=null && grid.columns && grid.columns.length>0))) {
										window.clearInterval(TaskId);										
										var button = res.info.button;
										if (button != null && !ISexecuted) {
											//带XTYPE的BUTTON 
											var buttontype = button.jb_fields;
											var neccessaryField = button.jt_neccessaryfield;
											if(buttontype=='updatedetail'){
												grid=w.Ext.ComponentQuery.query('grid')[0];
												if (neccessaryField != null) {
													grid.readOnly=false;
													grid.NoAdd=true;
													var fields = neccessaryField.split(","),addItems=new Array(),fieldtype=null,editable=false;														
													Ext.Array.each(grid.columns,function(column){
														editable=false;
														Ext.Array.each(fields,function(field) {
															var f =column.xtype;													
															if(column.dataIndex==field){
																column.getEl().applyStyles('color:#FF0000');
																column.neccessaryField=true;
																editable=true;
																if (f=="numbercolumn") {																
																	column.editor={
																			xtype:'numberfield',
																			format:'0',
																			hideTrigger:true
																	};
																} else if (f=="floatcolumn") {
																	column.editor={
																			xtype:'numberfield',
																			format:'0.00',
																			hideTrigger:true
																	};
																} else if (f.indexOf("floatcolumn")>-1) {							
																	var format = "0.";
																	var length =parseInt(f.substring(11));
																	for (var i = 0; i < length; i++) {
																		format += "0";
																	}
																	column.editor={
																			xtype:'numberfield',
																			format:format,
																			hideTrigger:true
																	};
																} else if (f =="datecolumn") {
																	column.editor={
																			xtype:'datefield',
																			format:"Y-m-d",
																			hideTrigger:false
																	};
																} else if (f =="datetimecolumn") {
																	column.editor={
																			xtype:'datetimefield',
																			format:"Y-m-d H:i:s",
																			hideTrigger:false
																	};
																} else if (f =="timecolumn") {
																	column.editor={
																			xtype:'timefield',
																			format:"H:i",
																			hideTrigger:false
																	};
																} else if (f =="monthcolumn") {
																	column.editor={
																			xtype:'monthdatefield',
																			hideTrigger:false
																	};
																} else if (f =="textcolumn" || f=="textfield" || f=="text") {	
																	column.editor={
																			xtype:'textfield'
																	};
																} else if (f =="textareafield") {			
																	column.editor={
																			xtype:'textareafield'
																	};
																} else if (f=="textareatrigger") {
																	column.editor={
																		xtype:'textareatrigger',
																		hideTrigger:false
																	};
																} else if (f=="dbfindtrigger") {					
																	column.editor={
																			xtype:'dbfindtrigger',
																			hideTrigger:false
																		};
																} else if (f =="multidbfindtrigger") {
																	column.editor={
																			xtype:'multidbfindtrigger',
																			hideTrigger:false
																		};
																} else if (f=="datehourminutefield") {					
																	column.editor={
																			xtype:'datehourminutefield',
																			hideTrigger:false
																		};
																} else if (f=="checkbox") {
																	column.editor={
																			xtype:'checkbox',
																			cls:'x-grid-checkheader-editor',
																			hideTrigger:false
																		};
																}
																return false;
															}
														});
														if(!editable) {
															column.editor=null;
														}
													});
													toolbar.add(['->', {
														xtype: 'button',
														text:'修改明细',
														iconCls: 'x-button-icon-save',
														cls: 'x-btn-gray',
														handler:function(btn){
															var values = {};													
															var jsonGridData = new Array();
															var s = grid.getStore().data.items;//获取store里面的数据
															var dd;
															for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
																	var data = s[i].data;
																	dd = new Object();
																	if(s[i].dirty && !grid.GridUtil.isBlank(grid, data)){
																		Ext.each(grid.columns, function(c){																		
																			if((c.neccessaryField && c.logic!='ignore')|| c.logic=='keyField'){
																				if(c.xtype == 'datecolumn'){
																					c.format = c.format || 'Y-m-d';
																					if(Ext.isDate(data[c.dataIndex])){
																						dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
																					} 
																				} else if(c.xtype == 'datetimecolumn'){
																					if(Ext.isDate(data[c.dataIndex])){
																						dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');
																					} 
																				} else if(c.xtype == 'numbercolumn'){																					
																						dd[c.dataIndex] = "" + s[i].data[c.dataIndex];																					
																				} else {
																					dd[c.dataIndex] = s[i].data[c.dataIndex];
																				}
																			}
																		});															
																		jsonGridData.push(Ext.JSON.encode(dd));
																	}
																}																												
															childpanel.getForm().getFields().each(function(field) {
																if (field.isDirty() &&  field.logic!='ignore' ) {
																	var data = field['getSubmitData'](true);
																	if (Ext.isObject(data)) {
																		Ext.iterate(data,
																				function(name, val) {
																			if (true && val === '') {
																				val = field.emptyText || '';
																			}
																			if (name in values) {
																				var bucket = values[name],
																				isArray = Ext.isArray;
																				if (!isArray(bucket)) {
																					bucket = values[name] = [bucket];
																				}
																				if (isArray(val)) {
																					values[name] = bucket.concat(val);
																				} else {
																					bucket.push(val);
																				}
																			} else {
																				values[name] = val;
																			}
																		});
																	}
																}
															});
															values[ProcessData.jp_keyName] = ProcessData.jp_keyValue;
															Ext.Ajax.request({
																url: basePath + '/common/processUpdate.action',
																method: 'post',
																params: {
																	caller: caller,
																	processInstanceId:ProcessData.jp_processInstanceId,
																	formStore: unescape(Ext.JSON.encode(values).replace(/\\/g, "%")),
																	param: unescape(jsonGridData.toString()),
																	_noc: 1
																},
																callback: function(options, success, response) {
																	var localJson = new Ext.decode(response.responseText);
																	canexecute = true;
																	if (localJson.success) {
																		conditionValidation=0;
																		updateSuccess();
																		grid.GridUtil.loadNewStore(grid,{
																			caller:caller,
																			condition:ProcessData.jp_formDetailKey + '=' + ProcessData.jp_keyValue
																		});
																	} else if (localJson.exceptionInfo) {
																		var str = localJson.exceptionInfo;
																		if (str.trim().substr(0, 12) == 'AFTERSUCCESS') { //特殊情况:操作成功，但是出现警告,允许刷新页面
																			str = str.replace('AFTERSUCCESS', '');
																			conditionValidation=0;
																			updateSuccess();
																		}else conditionValidation=1;
																		showError(str);
																		return;
																	} else {																	
																		updateFailure();
																	}
																}
															});
														}
													},
													'->']);
												}

											}else if (buttontype.indexOf('#') > 0) {
												if(buttontype.indexOf(',') > 0){
													var btns = buttontype.replace(/xtype#/,'').split(',');
													toolbar.add(['->']);
													Ext.Array.each(btns,function(btn){
														toolbar.add({
															xtype: btn,
														    fireHandler: function(e){
														        var me = this,
														            handler = me.handler;
														        canexecute=true;    
														        
														        if(btn!='erpEditDetailButton'){
														        	var updateStatus = function(from,to){
														        		var form = me.ownerCt.ownerCt;
																		var statusCodeField = form.statuscodeField;
																		var tablename = form.tablename;
																		var keyField = form.keyField;
																		if(statusCodeField&&tablename&&keyField){
																			var w = window.frames['iframe_maindetail'].contentWindow;
																			tablename = tablename.toUpperCase();
																			if(tablename.indexOf('LEFT JOIN')>-1){
																				tablename = tablename.substring(0,tablename.indexOf('LEFT JOIN'));
																			}else if(tablename.indexOf('RIGHT JOIN')>-1){
																				tablename = tablename.substring(0,tablename.indexOf('RIGHT JOIN'));
																			}
																			if(w.Ext&&tablename){
																				var key = w.Ext.getCmp(keyField);
																				if(key&&key.value){
																					Ext.Ajax.request({
																						url:basePath + 'common/updateByCondition.action',
																						method:'post',
																						async:false,
																						params:{
																							table:tablename,
																							update:statusCodeField + "='"+to+"'",
																							condition:statusCodeField + "='"+from+"' and " +keyField + "='"+key.value+"'"
																						},
																						callback:function(options,success,response){
																							var res = Ext.decode(response.responseText);
																							if(res.exceptionInfo){
																								showError(res.exceptionInfo);
																							}
																						}
																					});
																				}
																			}
																		}
															        };
														        	updateStatus('COMMITED','ENTERING');
																	me.fireEvent('click', me, e);
																	Ext.defer(function(){
																		updateStatus('ENTERING','COMMITED');
																	},500);
														        }else{
														        	me.fireEvent('click', me, e,true);
														        }
														        if (handler) {
														            handler.call(me.scope || me, me, e,w.Ext.ComponentQuery.query('grid'),button.jt_neccessaryfield);
														        }
														        me.onBlur();
														        
														    },
														    listeners:{
														    	afterrender:function(btn){
														    		Ext.defer(function(){
														    			if(btn.hidden){
														    				btn.show();
														    			}
														    		},500);														    			
														    	}
														    }
														});	

													});
													toolbar.add(['->']);
												}else{
													toolbar.add(['->', {
														xtype: buttontype.split('#')[1],
														text: button.jb_buttonname,
													    fireHandler: function(e){
													        var me = this,
													            handler = me.handler;
													        canexecute=true;    
													        me.fireEvent('click', me, e);
													        if (handler) {
													            handler.call(me.scope || me, me, e);
													        }
													        me.onBlur();
													    }
													},
													'->']);
												}

											} else {
												childtoolbar.add(['->', {
													xtype: 'button',
													text: button.jb_buttonname,
													id: button.jb_id,
													group: button.jb_fields,
													iconCls: 'x-button-icon-save',
													cls: 'x-btn-gray',
													formBind: true,
													handler: function() {
														var values = {};
														var necessaryValues = (iframe_maindetail.contentWindow||iframe_maindetail.window).Ext.getCmp('form').getValues();
														var bool = true;
														if (requiredFields != null) {
															var fields = requiredFields.split(",");
															Ext.Array.each(fields,
																	function(field) {
																if (necessaryValues[field] == null || necessaryValues[field] == "") {
																	bool = false;
																	showError('保存之前请先填写必填的信息!');
																	return false;
																}
															});
														}
														if (bool) {
															childpanel.getForm().getFields().each(function(field) {
																//&& field.groupName==button.jb_fields 有些组件写的有问题
																if (field.isDirty() &&  field.logic!='ignore' ) {
																	var data = field['getSubmitData'](true);
																	if (Ext.isObject(data)) {
																		Ext.iterate(data,
																				function(name, val) {
																			if (true && val === '') {
																				val = field.emptyText || '';
																			}
																			if (name in values) {
																				var bucket = values[name],
																				isArray = Ext.isArray;
																				if (!isArray(bucket)) {
																					bucket = values[name] = [bucket];
																				}
																				if (isArray(val)) {
																					values[name] = bucket.concat(val);
																				} else {
																					bucket.push(val);
																				}
																			} else {
																				values[name] = val;
																			}
																		});
																	}
																}
															});
															var grids = w.Ext.ComponentQuery.query('itemgrid');
															Ext.each(grids,function(g,index){
																if(g.xtype=='itemgrid' && !g.readOnly){
																	g.saveValue();
																}
															});
															values[ProcessData.jp_keyName] = ProcessData.jp_keyValue;
															Ext.Ajax.request({
																url: basePath + '/common/processUpdate.action',
																method: 'post',
																params: {
																	caller: caller,
																	processInstanceId:ProcessData.jp_processInstanceId,
																	formStore: unescape(Ext.JSON.encode(values).replace(/\\/g, "%")),
																	_noc: 1
																},
																callback: function(options, success, response) {
																	var localJson = new Ext.decode(response.responseText);
																	canexecute = true;
																	if (localJson.success) {
																		conditionValidation=0;
																		updateSuccess();
																	} else if (localJson.exceptionInfo) {
																		var str = localJson.exceptionInfo;
																		if (str.trim().substr(0, 12) == 'AFTERSUCCESS') { //特殊情况:操作成功，但是出现警告,允许刷新页面
																			str = str.replace('AFTERSUCCESS', '');
																			conditionValidation=0;
																			updateSuccess();
																		}else conditionValidation=1;
																		showError(str);
																		return;
																	} else {																		
																		updateFailure();
																	}
																}
															});
														}
													}
												},
												'->']);
											}
											var items = w.Ext.ComponentQuery.query('form')[0].items.items;
											var forms = w.Ext.ComponentQuery.query('form');
											Ext.Array.each(forms,function(form){
												if(form.dockedItems){
													if(form.dockedItems.items.length>0){
														items = form.items.items;
														return false;
													}
												}
											});
											//var nameArray=button.jb_buttonname.split(";");
											var fieldsArray=button.jb_fields.split(";");
											requiredFields = neccessaryField;
											var necFields=neccessaryField!=null?neccessaryField.split(","):[];
											Ext.each(items,function(item) {
												if (item.groupName == button.jb_buttonname && button.jb_fields.indexOf("#") > 0) {
													if(item.xtype =='itemgrid'){
														item.readOnly=false;
													}else{
													    item.setReadOnly(false);
													}
												}
												if (Ext.Array.contains(fieldsArray,item.groupName)) {
													if(item.xtype =='itemgrid'){
														item.readOnly=false;
													}else{
														item.setReadOnly(false);
													}
													if (item.xtype!='checkbox' && item.xtype!='itemgrid' && !Ext.Array.contains(necFields,item.name)) {
														 item.setFieldStyle("background:#FFFAFA;color:#515151;");
													} 
												}
                                                if(Ext.Array.contains(necFields,item.name)){
                                                	if(item.xtype =='itemgrid'){
														item.readOnly=false;
													}else{
														item.setReadOnly(false);
													}
                                                	if(item.xtype!='checkbox')
                                                	item.setFieldStyle("background:#fffac0;color:#515151;");
                                                }
											});
										}
									}
								}
							};
						}
					});

				}
			});
		}
	},
	setLoading : function(b) {// 原this.getActiveTab().setLoading()换成此方法,解决Window模式下无loading问题
		var mask = this.mask;
		if (!mask) {
			this.mask = mask = new Ext.LoadMask(Ext.getBody(), {
				msg : "处理中,请稍后...",
				msgCls : 'z-index:10000;'
			});
		}
		if (b)
			mask.show();
		else
			mask.hide();
	}

});