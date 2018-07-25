Ext.define('erp.view.oa.flow.Flow',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		//取group信息
		if(nodeId){
			var groups = me.getActivatepanel(caller,nodeId);
			groups = Ext.JSON.decode(groups);
		}
		if(formCondition&&status!='text'){
			//获取当前流程实例
			var title = me.getMainTitle(caller,formCondition,'getUsingInstance');
			me.padding = '2',
			//点击列表后跳转界面
			Ext.apply(me, { 
				items: [{ 
					title:title,
					id:'flow_mainpanel',
					xtype:'panel',
					layout: 'fit', 
					cls:'flow_mainpanel',
					tools:me.setTools(),
					items: [{
						xtype: 'tabpanel',
						id:'flow_tab',
						plugins:[Ext.create('Ext.ux.TabReorderer')],//可拖动
						listeners:{
							tabchange:function(tba,nowPanel,oldPanel){
								//解决切换tab太快的bug
								setTimeout(function() {
									var p = Ext.getCmp('flow_mainpanel');
									if(p){p.setLoading(false);}
									if(nowPanel.items.length==0){
										nowPanel.fireEvent('beforerender', nowPanel);
									}
			                    }, 500);
			                    if(nowPanel.title == '关联'){
			                    	if(Ext.getCmp('taskgrid')){
			                    		Ext.getCmp('taskgrid').store.load();
			                    	}
			                    	if(Ext.getCmp('flowgrid')){
			                    		Ext.getCmp('flowgrid').store.load();
			                    	}
			                    }
			                    if(nowPanel.title == '操作日志'){
			                    	if(Ext.getCmp('operation')){
			                    		Ext.getCmp('operation').store.load();
			                    	}
			                    }
			                    if(nowPanel.title == '附件'){
			                    	if(Ext.getCmp('fieldgrid')){
			                    		Ext.getCmp('fieldgrid').store.load();
			                    	}
			                    }
							}
						}
					}]
				}] 
			});
			me.callParent(arguments);
			me.insertPanel(groups,caller);//导入panel
		}else if(status!='text'){
			me.padding = '2',
			//新增界面
			Ext.apply(me, { 
				items: [{ 
					id:'addPanel',
					title:'新增流程',
					caller:caller,
					xtype:'FlowPanel',
					_group:groups[0],
					_add:true,
					tools:me.setTools()
				}] 
			});
			
			me.callParent(arguments);
		}else if(status=='text'){
			me.padding = '2',
			//草稿界面
			Ext.apply(me, { 
				items: [{ 
					id:'addPanel',
					title:'草稿单据',
					caller:caller,
					xtype:'FlowPanel',
					_group:groups[0],
					_model:true,
					_add:true,
					tools:me.setTools()
				}] 
			});
			
			me.callParent(arguments);
		}
	},
	insertPanel : function(groups,caller){
		formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
		var id = formCondition.split('=')[1];
		var tab = Ext.getCmp('flow_tab');
		var panels = new Array();
		//data赋值到panel属性
		Ext.each(groups, function(group,index){
			panels.push({
				_group:group,
				xtype:'FlowPanel',
				title: group.title,
				_first: index==0?true:false,
				reorderable: index==0?false:true //不可拖动
			})
		});
		//取固定静态panel
		panels.push({
			xtype:'FlowOperationPanel',
			title:'操作日志',
			_id:id
		})
		panels.push({
			xtype:'FlowFieldPanel',
			title:'附件',
			_id:id
		})
		panels.push({
			xtype:'FlowViewPanel',
			title:'流程视图',
			_codevalue:intanceCodeValue
		})
		panels.push({
			xtype:'FlowRelativePanel',
			title:'关联',
			_codevalue:intanceCodeValue
		})
		//一次加载tab 点击渲染panel
		tab.add(panels);
		tab.setActiveTab(0);
	},
	getActivatepanel : function(caller,nodeId){
		var data;
		Ext.Ajax.request({
			url : basePath + 'oa/flow/getActivatepanel.action',
			params: {
				caller: caller,
				nodeId : nodeId
			},
			async:false,
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);
					return false;
				}
				data = res.data;
			}
		});
		return data;
	},
	getMainTitle : function(caller,formCondition,condition){
		var title;
		Ext.Ajax.request({
			url : basePath + 'oa/flow/getIntance.action',
			params: {
				caller: caller,
				id : formCondition,
				condition : condition
			},
			async:false,
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);
					return false;
				}
				if(res.data.length!=0){
					title = res.data[0].FD_NAME + '-' +res.data[0].FI_TITLE;
				}
			}
		});
		return title;
	},
	setTools: function(){
		return [{	
			xtype:'button',				
			text:'选项',
			id:'buttons',
			margin:'0 0 0 2',	
			listeners:{
				mouseover:function(btn){
					btn.showMenu();	
				},
				mouseout: function(btn) {
					setTimeout(function() {
						if(!btn.menu.over) {
							btn.hideMenu();
						}
                    }, 20);
				},
				afterrender:function(btn){
					formCondition = getUrlParam('formCondition');
					if(formCondition==null||formCondition==''){
						btn.hide();
					}
				}
			},
			menu: {
				listeners: {
					mouseover: function() {
						this.over = true;
					},
					mouseleave: function() {
						this.over = false;
						this.hide();
					}
				},
				items:[{
					iconCls: 'x-nbutton-icon-download',
					text: '导出',
					listeners:{
						click:function(){
							var id = formCondition.split('IS')[1] ? formCondition.split('IS')[1] : formCondition.split('=')[1];
							window.location.href = basePath + 'oa/flow/downLoadAsExcel.action?nodeId='+nodeId+'&caller='+caller+'&id='+id;
						}
					}							
				},{
					iconCls: 'x-nbutton-icon-download',
					text: '回退',
					listeners:{
						click:function(btn){
							btn.ownerCt.floatParent.ownerCt.ownerCt.ownerCt.showBackWin();
						}
					}
				}]
			}
		}];
	},
	showBackWin:function(){
		formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
		var id = formCondition.split('=')[1];
		var data;
		//得到可回退的节点
		Ext.Ajax.request({
			url: basePath + 'oa/flow/getRollbackNodename.action',
			params: {
				caller: caller,
				nodeId: nodeId,
				id: id
			},
			async:false,
			callback: function(options, success, response){
				var res = Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);
					return;
				}
				if(res.data == null || res.data ==''){
					showError('当前流程没有可回退的节点');
				}else{
					data = res.data;
				}
			}
		});
		var win =new Ext.window.Window({
		title: '<span style="color:#115fd8;">选择回退节点</span>',
		draggable:true,
		height: '30%',
		width: '30%',
		resizable:false,
		id:'FlowBackWin',
		cls:'FlowBackWin',
		iconCls:'x-button-icon-set',
   		modal: true,
   		bbar:['->',{
   			cls:'x-btn-gray',
   			xtype:'button',
   			text:'确认',
   			handler:function(btn){
   				var db = Ext.getCmp('rollbackNode');
   				if(!db.value){
   					Ext.Msg.alert('提示','请选择回退节点再进行确认操作');
   					return;
   				}
   				//收集单据id信息
   				var newNodeId = Ext.getCmp('rollbackNode').getValue();
   				var params = {
   					caller: caller,
   					id: id,
   					newNodeId: newNodeId
   				}
				 warnMsg('确定要回退所选流程吗？', function(btn){
				 	if(btn == 'yes'){
						Ext.Ajax.request({
							url : basePath + 'oa/flow/versionRollback.action',
							params: params,
							method : 'post',
							callback : function(options,success,response){
								var localJson = new Ext.decode(response.responseText);
								if(localJson.exceptionInfo){
									showError(localJson.exceptionInfo);return;
								}
								if(localJson.success){
									showInformation('流程回退成功！', function(btn){
										window.location.reload();
									});
								}
							}
						});
					}
				 });
   			}
   		},{xtype:'splitter',width:10},{
   			cls:'x-btn-gray',
   			xtype:'button',
   			text:'取消',
   			handler:function(btn){
   				var win = Ext.getCmp('FlowBackWin');
   				win.close();
   			}
   		},'->'],
	   	items: [{
	   		id:'rollbackNode',
	   		allowBlank:false,
			allowDecimals:true,
			checked:false,
			padding:'10 0 0 0',
			fieldLabel:"回退节点",
			fieldStyle:"background:#fff;",
			hideTrigger:false,
			labelAlign:"left",
			labelStyle:"color:black",							
			maxLength:50,
			maxLengthText:"字段长度不能超过50字符!",
			name:"nodeName",
			readOnly:false,
			table:"CUSTOMTABLE",
			xtype:"combobox",
			editable: false,
			store: Ext.create('Ext.data.Store',{
				fields: ['FI_NODENAME','FI_NODEID'],
				data: data
			}),
			displayField: 'FI_NODENAME',
			valueField: 'FI_NODEID'
	    }]
	  });
	  win.show();
	}
});