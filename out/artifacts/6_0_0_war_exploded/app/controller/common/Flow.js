Ext.QuickTips.init();
Ext.define('erp.controller.common.Flow', {
	extend: 'Ext.app.Controller',
	views:['common.flow.FlowHeader','common.flow.FlowBody','common.flow.FlowBottom','core.trigger.DbfindTrigger','core.form.MultiField','core.form.FileField'],
	init:function(){
		var me = this;
		this.control({
			'#agree-task':{
				afterrender:function(btn){
					btn.setDisabled(true);
				},
				click: {
					fn: function(){
						me.agreeTask();
					},
					lock: 2000
				}
			},
			'#disagree-task':{
				click: {
					fn: function(btn){
						me.disAgreeTask();
					},
					lock: 2000
				}
			},
			'#skip-task':{
				click: {
					fn: function(btn){
						me.skipTask();
					},
					lock: 2000
				}		    	
			},
			'#changehandler':{
				click:function(btn){
					me.changeHandler();
				}		    	
			},
			'#notify':{
				click:function(btn){
					me.notify(btn);
				}
			}
		});
	},
	agreeTask:function(){
		var me=this;
		var confirmAutoPrinciple=false;
		Ext.Ajax.request({
			url: basePath + 'common/getFieldsDatas.action',
			async: false,
		    params: {
			   caller: 'JAUTOPRINCIPLE',
			   fields: 'count(1) as num',
			   condition: "JAP_PROCESSDEFID=(select JP_PROCESSINSTANCEID from jprocess where jp_nodeid="+ProcessData.jp_nodeId+") and jap_status=0 and JAP_NODENAME='"+ProcessData.jp_nodeName+"'"
		   },
			callback: function(options, success, response) {
				var r = new Ext.decode(response.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			} else if(r.success){
	   				if(new Ext.decode(r.data)[0].NUM>0){
	   					confirmAutoPrinciple = true;
	   				}
	   			}
			}
		});
		if(confirmAutoPrinciple){
			Ext.MessageBox.confirm('提示','驳回后：流程是否跳过中间节点审批',function(btn){
				if(btn=='yes'){
					me.dealWithAgreeTask(true);
				}else{
					me.dealWithAgreeTask(false);
				}
			});
		}else{
			me.dealWithAgreeTask(false);
		}
	},
	dealWithAgreeTask:function(AutoPrinciple){
        var me = this;
		var params=new Object();
		if(!me.getNodeInfo(params)) return; 
		var taskId = ProcessData.jp_nodeId;
		var nodeName = ProcessData.jp_nodeName;
		var mb = new Ext.window.MessageBox();
		mb.wait('系统正在处理', '请稍后');
		var startdealtime = new Date();
		var bool = true;

		var form = (iframe_maindetail.contentWindow||iframe_maindetail.window).Ext.getCmp('form'),values=form.getValues();
		if (!canexecute) {
			//触发保存事件  用于代码直接触发修改功能
			var isShowWin = true;//判断是否是自动触发保存  是则不显示之后的提示窗口
			if(form.dockedItems&&form.dockedItems.items&&form.dockedItems.items[0]&&form.dockedItems.items[0].items
			   &&form.dockedItems.items[0].items.items&&form.dockedItems.items[0].items.items[0]){
				var firstButton = form.dockedItems.items[0].items.items[0];
				var items = form.items.items;
				var canEdits = false;   //当按钮等于分组名称，就能自动触发按钮
				Ext.each(items,function(item) {
					if (item.groupName == firstButton.jb_fields) {
						canEdits = true;
					}
				});
				if (requiredFields != null||canEdits) {
					if(firstButton&&firstButton.xtype=='button'){
						firstButton.fireHandler();
						isShowWin = false;
					}
				}
			}
		}
		if (requiredFields != null) {
			//再次判断是否修改成功
			if(!canexecute){
				bool = false;
				mb.close();
				isShowWin?showError('请在同意之前先保存必填信息!'):null;
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
			params.taskId=taskId;
			params.nodeName=nodeName;
			params.holdtime=((startdealtime - basestarttime) / 1000).toFixed(0),
			params.result=true;
			params.master=master;
			params._center=_center;
			params._noc=1;
			params.autoPrinciple=AutoPrinciple;
			Ext.Ajax.request({
				url: basePath + 'common/review.action',
				params:params,
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
//								function showResult(btn){
								if (jsonData.after != null && jsonData.after != "") {
									var str = jsonData.after;
									if (str.trim().substr(0, 12) == 'AFTERSUCCESS') { //特殊情况:操作成功，但是出现警告,允许刷新页面
										str = str.replace('AFTERSUCCESS', '');											
										me.dealNextStep(nextnode,jsonData._tomaster);
										showError(jsonData.after);
									} else showError(str);
								} else me.dealNextStep(nextnode,jsonData._tomaster);
//								}
//								Ext.Msg.show({
//									title:'提示',
//									msg: '审批成功!',
//									buttons: Ext.Msg.OK,
//									closable: false,
//									fn: showResult
//								});	
								
								
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
	},
	disAgreeTask:function(){
		var grid = Ext.getCmp('historyGrid'),datas=grid.store.data,attachs = Ext.getCmp('attachs').items.items[0].value;
		var combodata = new Array();
		combodata.push({
			display: '制单人',
			value: 'RECORDER'
		});	
		if(forknode==0){//并行节点只能回退至制单人
			Ext.Array.each(datas.items,function(item) {
				if (item.data.jn_dealResult == '同意' && item.data.jn_attach == 'T') {
					if(ProcessData.jp_processInstanceId==item.data.jn_processInstanceId){
						combodata.push({
							display: item.data.jn_name,
							value: item.data.jn_name
						});
					}
				}
			});
			for(i = 0; i < combodata.length; i++){
			  for(j = i + 1; j < combodata.length; j++){
			   if(combodata[i].value == combodata[j].value){
				   combodata.splice(j,1);
				   j--;
			   }
			  }
			}
		}
		Ext.create('Ext.window.Window', {
			title: '指定回退节点',
			height: 200,
			width: 400,
			layout: 'column',
			id: 'win',
			modal:true,
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
			        mb.wait('系统正在处理','请稍后');
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
									} else {
										me.dealNextStep(nextnode,jsonData._tomaster);
									}
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
	},
	getNodeInfo:function(params){
		var dealMessage = Ext.getCmp('dealMessage').getValue(),customs = Ext.getCmp('customSetup'),attachs = Ext.getCmp('attachs').items.items[0].value;
		var obj={},val,arr=new Array(),text,flag = 0;
		params.nodeLog=dealMessage;
		params.attachs=attachs;
		if(customs){
			Ext.each(customs.items.items,function(cu,index) {
				val=cu.getValue();
				if (!cu.allowBlank && (val == null || val == '')) {
					Ext.Msg.alert('提示', "<b>" + cu.fieldLabel.fontcolor("Red") + "</b>为必填项！");
					flag++;
					return false;
				}
				index++;
				if(val !=null && val !=''){
					if(cu.xtype=='datefield'){
						val=Ext.Date.format(val,'Y-m-d');
					}
					text=index+'.'+cu.fieldLabel+"("+val+")";
					if(cu.logic!='') text+='@'+cu.logic+'@'; //部分审批要点字段作为审批流分支
					arr.push(text);
				}
			});
			if (flag > 0) {
				return false;
			}
			params.customDes=arr.join(";");
		}
		return true;
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
					} else {
						if (nextnode && nextnode != '-1') {
							me.loadNextTask(nextnode,toMaster);
						} else {
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
		var url="jsps/common/flow.jsp?formCondition=jp_nodeId=" + nextnode;
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
	skipTask: function() {
		var taskId = ProcessData.jp_nodeId;
		var mb = new Ext.window.MessageBox();
		mb.wait('系统正在跳过', '请稍后');
		Ext.Ajax.request({
			url: basePath + 'common/getNextProcess.action',
			params: {
				taskId: taskId,
				_noc: 1,
				_center:_center
			},
			callback: function(options, success, response) {
				mb.close();
				var data = response.responseText;
				var jsonData = Ext.decode(data);
				if (jsonData.success && jsonData.nodeId!=-1) {
					window.location.href = basePath + "jsps/common/flow.jsp?formCondition=jp_nodeId=" + jsonData.nodeId;
				} else {
					showMessage('提示','已无待审批的单据',0);
					parent.Ext.getCmp('content-panel').getActiveTab().close();
				}

			}
		});
	},
	changeHandler:function(btn){
		var me=this,value = Ext.getCmp('AssigneeComboxcode').getValue();
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
					description: Ext.getCmp('changedescription').getValue(),
					_noc: 1,
					_center:_center
				},
				callback : function(options, success, response) {
					var jsonData = Ext.decode(response.responseText);
					
					if (jsonData.exceptionInfo) {
						showError(jsonData.exceptionInfo);
						return;
					}
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
	},
	notify:function(btn){
		var me=this,peoples=Ext.getCmp('notifyPeopleid').value, groups=Ext.getCmp('notifyGroup').value,peoplesname=Ext.getCmp('notifyPeople').value,groupsname=Ext.getCmp('notifyGroupName').value;
		if(!peoples && !groups) showMessage('提示','先选择需要知会的岗位或人员!',1000); 
		else{
			var data={
					processInstanceId:ProcessData.jp_processInstanceId,
					nodeId:ProcessData.jp_nodeId,
					nodeName:ProcessData.jp_nodeName
			};
			if (notifyPeople)data.notifyPeopleid=notifyPeople;
			if (notifyGroup)data.notifyGroup=notifyGroup;
			if (peoplesname)data.notifyPeople=peoplesname;
			if (groupsname)data.notifyGroupName=groupsname;
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
						Ext.Array.each(btn.ownerCt.items.items,function(item){
							if(item.xtype!='button')item.reset();
						});
					} else {
						showError(data.exceptionInfo);
					}
				}
			});
		}
	},
	setLoading : function(b) {
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