Ext.define('erp.view.oa.flow.flowEditor.flowGrid.FlowNodeGrid',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.FlowNodeGrid',
	layout:{
	    type: 'hbox',
	    align: 'stretch',
	    padding: 5
    },
    id:'FlowNodeGrid',
	bodyStyle:'background:#f2f2f2;border-top:none;padding-top:5px;',
	autoScroll : true,
	requires:['erp.view.oa.flow.flowEditor.flowGrid.FlowUpdateTabGrid','erp.view.oa.flow.flowEditor.flowGrid.FlowAddTabGrid'],
	FormUtil: Ext.create('erp.util.FormUtil'),
	initComponent : function(){
		var me = this;	
		//获取参照页面
		var GroupStore = me.getSelectTab(shortName);
	    var items = [{
	       margin:'0 10 0 10',
		   xtype:'grid',
		   width:200,
		   multiSelect: true,
		   id: 'fromgrid',
		   title:'所有页面',
		   cls: 'custom-grid',	
		   tools:me.setTools(GroupStore),
		   store:Ext.create('Ext.data.Store', {
		   fields: [{name:'name',type:'string'},{name:'title',type:'string'}],
			   data: me.allData,
			   filterOnLoad: false 
		   }),
		   plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
		   viewConfig: {
			   plugins: {
				   ptype: 'gridviewdragdrop',
			   	   dragGroup: 'togrid',
			       dropGroup: 'togrid'
			   }		
		   },
		   listeners: {
		   	   itemdblclick : function(view, record){ 
		   	   		var addData = record.data;
		   	   		if(record.get('title')==''){
		   	   			addData.title = addData.name
		   	   		}
		   	   		Ext.getCmp('togrid').store.add(addData);
		   	   		Ext.getCmp('fromgrid').store.remove(record);
		   	   },
		   	   itemcontextmenu : function(view, record, item, index, e){
					//阻止浏览器触发事件
					e.preventDefault();
					var contextmenu = new Ext.menu.Menu({ 
						items:[{
							iconCls:'x-button-icon-change',
							text:'修改分组',
							handler:function(){
								var win = new Ext.Window({
									title: '<span style="color:#115fd8;">新增TAB页</span>',
									draggable:true,
									height: '90%',
									width: '80%',
									resizable:false,
									id:'updateTabWin',
							   		modal: true,
							   		layout:'fit',
							   		items:[{
										xtype:'panel',
										layout: 'column', 
										bbar: ['->',{
											xtype:'button',
											cls:'x-btn-gray',
											text:'更新',
											margin:'0 0 0 2',
											handler:function(btn){
												var tabName = Ext.getCmp('tabName');
												var togrid = Ext.getCmp('toTab');
												var toStroe = togrid.store;
												if(togrid.store.data.items.length<1){
													showInformation('至少选中一个字段', function(btn){});
													return;
												}
												if(tabName&&tabName.value&&tabName.value.trim()!=''){
													var saveJson = new Array();
													Ext.Array.each(togrid.store.data.items, function(item,index){
														saveJson.push({
															width:item.data.columnsWidth,
															field:item.data.field,
															isNew:item.data.isNew,
															text:item.data.text,
															detno:index+1
														});
													});
													Ext.Ajax.request({
														url : basePath + 'oa/flow/updateTab.action',
														params: {
										   					shortName:shortName,
										   					tabName:tabName.value.trim(),
										   					tabs:JSON.stringify(saveJson)
										   				},
														method : 'post',
														callback : function(options,success,response){
															var localJson = new Ext.decode(response.responseText);
															if(localJson.exceptionInfo){
																showError(localJson.exceptionInfo);return;
															}
															if(localJson.success){
																showInformation('Tab页保存成功！', function(btn){
//																	Ext.getCmp('updateTabWin').close();
																});
															}
														}
													});
												}else{
													showInformation('请检查TAB名称是否不为空且填写正确', function(btn){});
													return;
												}
											}
										},{xtype:'splitter',width:10},{
											xtype:'button',
											cls:'x-btn-gray',
											text:'关闭',
											margin:'0 5 0 0',
											handler:function(){
												var errInfo = '是否继续关闭？';
												warnMsg(errInfo, function(btn){
													if(btn == 'yes'){
														Ext.getCmp('updateTabWin').close()
													} else {
														return;
													}
												});
											}
										},'->'],
										items:[{
											fieldLabel: "TAB名称",
											name: 'TAB名称',
											id:'tabName',
											columnWidth:1,
											xtype:'textfield',
											labelAlign: 'left',
											readOnly:true,
											allowBlank: false,
											editable:false,
											value:record.data.name,
											cls: "form-field-allowBlank"
										},{
											fieldLabel: '参照',
											name: '参照',
											allowDecimals:true,
											columnWidth: 1,
											hideTrigger:false,
											cls: "form-field-allowBlank",
											labelAlign:"left",
											maxLength:50,
											maxHeight:250,
											maxLengthText:"字段长度不能超过50字符!",
											readOnly:false,
											xtype:"combobox",
											editable: false,
											store: Ext.create('Ext.data.Store',{
												fields: ['name','shortName'],
												data: GroupStore
											}),
											displayField: 'name',
											valueField: 'name',
											listeners:{
												afterrender:function(f){
													var GroupStore = new Array();
													Ext.Ajax.request({
														url : basePath + 'common/getFieldsDatas.action',
														async: false,
														params:{
															fields : 'fgc_groupname,min(fgc_id)',
															caller : 'flow_groupconfig',
															condition : 'fgc_fdshortname = \''+ shortName +'\' group by fgc_groupname order by min(fgc_id)'
														},
														callback : function(options,success,response){
															var rs = new Ext.decode(response.responseText);
															if(rs.exceptionInfo){
																showError(rs.exceptionInfo);return;
															}
															Ext.Array.each(Ext.decode(rs.data), function(item){
																GroupStore.push({
																	name:item.FGC_GROUPNAME,
																	shortName:shortName
																});
															});
														}
													});
													//更换store
													var store = Ext.create('Ext.data.Store',{
														fields: ['name','shortName'],
														data: GroupStore
													})
													f.store = store;
												},
												change:function(f,newValue){
													//加载参照页面字段
													var allField = Ext.getCmp('FlowAddTabGrid').allField;
													var selectTab = new Array();
													Ext.Ajax.request({
														url : basePath + 'oa/flow/getSelectTab.action',
														async:false,
														params:{
															shortName:shortName,
															groupName:newValue,
															caller:caller
														},
														callback : function(options,success,response){
															var rs = new Ext.decode(response.responseText);
															if(rs.exceptionInfo){
																showError(rs.exceptionInfo);return;
															}
															if(rs.groups.length>0){
																Ext.Array.each(rs.groups, function(item){
																	var isNew = item.FGC_NEW?item.FGC_NEW:false
																	if(isNew=='true'){
																		isNew = true
																	}else if(isNew=='false'){
																		isNew = false
																	}
																	selectTab.push({
																		text : item.FD_CAPTION,
																		field : item.FGC_FIELD,
																		isNew : isNew,
																		columnsWidth : item.FGC_WIDTH
																	});
																});
															}
														}
													});
													//剔除重复Group
													var toStroe = Ext.getCmp('toTab').store;
													var fromStore = Ext.getCmp('fromTab').store;
													var allData = allField.concat();
													var nowData = selectTab;
													if(allData.length>0&&nowData.length>0){
														Ext.Array.each(nowData, function(b_item){
															Ext.Array.each(allData, function(a_item,index){
																if(a_item&&a_item.field==b_item.field){
																	allData.splice(index,1)
																	return;
																}
															});
														});
													}
													//刷新usingGroups allGroups
													toStroe.loadData(selectTab);
													fromStore.loadData(allData);
												}
											}
										},{
											xtype:'FlowUpdateTabGrid',
											_groupName:record.data.name
										}]
							   		}]
								});
								win.show();
							}
						},{
							iconCls:'x-button-icon-close',
							text:'删除分组',
							handler:function(){
								Ext.MessageBox.show({
							     	title: $I18N.common.msg.title_prompt,
							     	msg: '是否确定删除分组--'+record.data.name+'？',
							     	buttons: Ext.Msg.YESNO,
							     	icon: Ext.Msg.INFO,
							     	fn: function(btn){
										if(btn == 'yes'){
											Ext.Ajax.request({
												url:basePath+'oa/flow/deleteTab.action',
												params:{
													shortName:shortName,
													groupName:record.data.name
												},
												callback : function(options,success,response){
													var rs = new Ext.decode(response.responseText);
													if(rs.exceptionInfo){
														showError(rs.exceptionInfo);return;
													}
													if(rs.success){
														window.location.reload();
													}
												}
											})
										}else{
											return;
										}
									
							     	}
								});
							}
						}]
					});
					contextmenu.showAt(e.getXY());
				}
		   },
		   stripeRows: false,
		   columnLines:true,
		   columns:[{
			   dataIndex:'name',
			   cls :"x-grid-header-1",
			   text:'页面',
			   flex:1,
			   filter: {
			   	xtype : 'textfield'
			   }
		   }]
	   },{
		   xtype:'grid',
		   margin:'0 10 0 10',
		   multiSelect: true,
		   width:400,
		   id: 'togrid',
		   stripeRows: true,
		   columnLines:true,
		   title:'使用页面',
		   store:Ext.create('Ext.data.Store', {
		   fields: [{name:'name',type:'string'},{name:'title',type:'string'}],
			            data:me.nowData,
			            filterOnLoad: false 
		   }),
		   necessaryField:'fullName',
		   plugins: [Ext.create('erp.view.core.grid.HeaderFilter'),
	         		 Ext.create('Ext.grid.plugin.CellEditing', {
	        	 		clicksToEdit: 1
	       })],
	       listeners: {
		   	   itemdblclick : function(view, record){ 
		   	   		var addData = record.data;
		   	   		Ext.getCmp('fromgrid').store.add(addData);
		   	   		Ext.getCmp('togrid').store.remove(record);
		   	   }
		   },
           viewConfig: {
        	 plugins: {
        		 ptype: 'gridviewdragdrop',
        		 dragGroup: 'togrid',
        		 dropGroup: 'togrid'
        	 },
        	 listeners:{
    		 	beforedrop:function(n,d){
    		 		Ext.Array.each(d.records, function(item){
    		 			if(item.get('title')==''){
    		 				item.data.title = item.get('name')
    		 			}
    		 		});
    		 	}
    		 }
           },
           columns:[{
        	 dataIndex:'name',
        	 text:'页面',
        	 cls :"x-grid-header-1",
        	 flex:1,
        	 filter: {
        		 xtype : 'textfield'
        	 }
           },{
        	 dataIndex:'title',
        	 text:'显示名称',
        	 cls :"x-grid-header-1",
        	 flex:1,
        	 filter: {
        		 xtype : 'textfield'
        	 },
        	 editor: {
			   	xtype: 'textfield',
			   	allowBlur : true,
			   	displayField : "display",
				valueField : "value",
				format:"",
				hideTrigger:true,
				positiveNum:false,
				queryMode:"local"
		   	 }
           }] 
	    },{
		   xtype:'grid',
		   margin:'0 10 0 10',
		   multiSelect: true,
		   flex:1,
		   id: 'flowgrid',
		   stripeRows: true,
		   columnLines:true,
		   title:'派生来源',
		   store:Ext.create('Ext.data.Store', {
		   fields: [{name:'nodename',type:'string'},{name:'shortname',type:'string'},{name:'define',type:'string'}],
			            data:me.flowData,
			            filterOnLoad: false 
		   }),
		   plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
           columns:[{
        	 dataIndex:'shortname',
        	 text:'派生版本号',
        	 cls :"x-grid-header-1",
        	 flex:1,
        	 filter: {
        		 xtype : 'textfield'
        	 }
           },{
        	 dataIndex:'nodename',
        	 text:'派生节点',
        	 cls :"x-grid-header-1",
        	 flex:1,
        	 filter: {
        		 xtype : 'textfield'
        	 }
           }] 
	    }]
	    Ext.apply(me, { 
			items:items 
		}); 
	    this.callParent(arguments);				   
	},
	getSelectTab:function(shortName){
		//获取参照页面
		var GroupStore = new Array();
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'fgc_groupname,min(fgc_id)',
				caller : 'flow_groupconfig',
				condition : 'fgc_fdshortname = \''+ shortName +'\' group by fgc_groupname order by min(fgc_id)'
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				Ext.Array.each(Ext.decode(rs.data), function(item){
					GroupStore.push({
						name:item.FGC_GROUPNAME,
						shortName:shortName
					});
				});
			}
		});
		return GroupStore;
	},
	setTools: function(GroupStore){
		return [{	
			xtype:'button',				
			text:'添加TAB页',
			id:'addTabButton',
			margin:'0 0 0 2',	
			listeners: {
				click:function(btn){	
					var win = new Ext.Window({
						title: '<span style="color:#115fd8;">新增TAB页</span>',
						draggable:true,
						height: '90%',
						width: '80%',
						resizable:false,
						id:'addTabWin',
				   		modal: true,
				   		layout:'fit',
				   		items:[{
							xtype:'panel',
							layout: 'column', 
							bbar: ['->',{
								xtype:'button',
								cls:'x-btn-gray',
								text:'保存',
								margin:'0 0 0 2',
								handler:function(btn){
									var tabName = Ext.getCmp('tabName');
									var togrid = Ext.getCmp('toTab');
									if(togrid.store.data.items.length<1){
										showInformation('至少选中一个字段', function(btn){});
										return;
									}
									if(tabName&&tabName.value&&tabName.value.trim()!=''){
										var saveJson = new Array();
										Ext.Array.each(togrid.store.data.items, function(item,index){
											saveJson.push({
												width:item.data.columnsWidth,
												field:item.data.field,
												isNew:item.data.isNew,
												text:item.data.text,
												detno:index+1
											});
										});
										Ext.Ajax.request({
											url : basePath + 'oa/flow/saveNewTab.action',
											params: {
							   					shortName:shortName,
							   					tabName:tabName.value.trim(),
							   					tabs:JSON.stringify(saveJson)
							   				},
											method : 'post',
											callback : function(options,success,response){
												var localJson = new Ext.decode(response.responseText);
												if(localJson.exceptionInfo){
													showError(localJson.exceptionInfo);return;
												}
												if(localJson.success){
													showInformation('Tab页保存成功！', function(btn){
														Ext.getCmp('addTabWin').close();
														//刷新allGroups
														var allGroups = new Array();
														Ext.Array.each(Ext.getCmp('fromgrid').store.data.items, function(item){
															allGroups.push({
																name : item.data.name
															});
														});
														allGroups.push({
															name : tabName.value.trim()
														});
														Ext.getCmp('fromgrid').store.loadData(allGroups);
													});
												}
											}
										});
									}else{
										showInformation('请检查TAB名称是否不为空且填写正确', function(btn){});
										return;
									}
								}
							},{xtype:'splitter',width:10},{
								xtype:'button',
								cls:'x-btn-gray',
								text:'关闭',
								margin:'0 5 0 0',
								handler:function(){
									var errInfo = 'Tab未保存，是否继续关闭？';
									warnMsg(errInfo, function(btn){
										if(btn == 'yes'){
											Ext.getCmp('addTabWin').close()
										} else {
											return;
										}
									});
								}
							},'->'],
							items:[{
								fieldLabel: "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>TAB名称",
								name: 'TAB名称',
								id:'tabName',
								columnWidth:1,
								xtype:'textfield',
								labelAlign: 'left',
								readOnly:false,
								allowBlank: false,
								editable:true,
								cls: "form-field-allowBlank"
							},{
								fieldLabel: '参照',
								name: '参照',
								allowDecimals:true,
								columnWidth: 1,
								hideTrigger:false,
								cls: "form-field-allowBlank",
								labelAlign:"left",
								maxLength:50,
								maxHeight:250,
								maxLengthText:"字段长度不能超过50字符!",
								readOnly:false,
								xtype:"combobox",
								editable: false,
								store: Ext.create('Ext.data.Store',{
									fields: ['name','shortName'],
									data: GroupStore
								}),
								displayField: 'name',
								valueField: 'name',
								listeners:{
									afterrender:function(f){
										var GroupStore = new Array();
										Ext.Ajax.request({
											url : basePath + 'common/getFieldsDatas.action',
											async: false,
											params:{
												fields : 'fgc_groupname,min(fgc_id)',
												caller : 'flow_groupconfig',
												condition : 'fgc_fdshortname = \''+ shortName +'\' group by fgc_groupname order by min(fgc_id)'
											},
											callback : function(options,success,response){
												var rs = new Ext.decode(response.responseText);
												if(rs.exceptionInfo){
													showError(rs.exceptionInfo);return;
												}
												Ext.Array.each(Ext.decode(rs.data), function(item){
													GroupStore.push({
														name:item.FGC_GROUPNAME,
														shortName:shortName
													});
												});
											}
										});
										//更换store
										var store = Ext.create('Ext.data.Store',{
											fields: ['name','shortName'],
											data: GroupStore
										})
										f.store = store;
									},
									change:function(f,newValue){
										//加载参照页面字段
										var allField = Ext.getCmp('FlowAddTabGrid').allField;
										var selectTab = new Array();
										Ext.Ajax.request({
											url : basePath + 'oa/flow/getSelectTab.action',
											async:false,
											params:{
												shortName:shortName,
												groupName:newValue,
												caller:caller
											},
											callback : function(options,success,response){
												var rs = new Ext.decode(response.responseText);
												if(rs.exceptionInfo){
													showError(rs.exceptionInfo);return;
												}
												if(rs.groups.length>0){
													Ext.Array.each(rs.groups, function(item){
														var isNew = item.FGC_NEW?item.FGC_NEW:false
														if(isNew=='true'){
															isNew = true
														}else if(isNew=='false'){
															isNew = false
														}
														selectTab.push({
															text : item.FD_CAPTION,
															field : item.FGC_FIELD,
															isNew : isNew,
															columnsWidth : item.FGC_WIDTH
														});
													});
												}
											}
										});
										//剔除重复Group
										var toStroe = Ext.getCmp('toTab').store;
										var fromStore = Ext.getCmp('fromTab').store;
										var allData = allField.concat();
										var nowData = selectTab;
										if(allData.length>0&&nowData.length>0){
											Ext.Array.each(nowData, function(b_item){
												Ext.Array.each(allData, function(a_item,index){
													if(a_item&&a_item.field==b_item.field){
														allData.splice(index,1)
														return;
													}
												});
											});
										}
										//刷新usingGroups allGroups
										toStroe.loadData(selectTab);
										fromStore.loadData(allData);
									}
								}
							},{
								xtype:'FlowAddTabGrid'
							}]
				   		}]
					});
					win.show();
				}
			}
		}];
	}
});