Ext.QuickTips.init();
Ext.define('erp.controller.common.MoreFlow', {
	extend : 'Ext.app.Controller',
	requires : ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
	views : ['common.DeskTop.MoreFlow', 'common.datalist.GridPanel',
			'common.datalist.Toolbar', 'core.button.VastAudit',
			'core.button.VastDelete', 'core.button.VastPrint',
			'core.button.VastReply', 'core.button.VastSubmit',
			'core.button.ResAudit', 'core.form.FtField', 'core.grid.TfColumn',
			'core.grid.YnColumn', 'core.trigger.DbfindTrigger',
			'core.form.FtDateField', 'core.form.FtFindField',
			'core.form.FtNumberField', 'core.form.MonthDateField',
			'core.button.ProcessRemind','core.grid.HeaderFilter','common.DeskTop.DeskTabPanel'],
	init : function() {
		var me = this;
		this.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.FormUtil = Ext.create('erp.util.FormUtil');
		this.GridUtil = Ext.create('erp.util.GridUtil');
		this.control({
			'#alreadyLaunch' : {
				cellclick : function(view, td, colIdx, record, tr, rowIdx, e) {					
					var field = view.ownerCt.columns[colIdx].dataIndex;
					if (field == 'jp_codevalue') {
						this.onCellItemClick(record);
					};
				}
			},
			'#toDo' : {
				 itemclick: this.onGridItemClick
			},
			'#alreadyDo':{
				itemclick: this.onAlreadyDoItemClick
			},
 		   'checkbox[name=only_todo]':{
			   change:function(field,newvalue){
				   var grid=Ext.getCmp('alreadyLaunch');
				   if(newvalue){                      
					   grid.defaultCondition=' jp_launcherid=\''+ em_code + '\' and jp_status=\'待审批\'';
					   page=1;
					   grid.getCount(); 					  
				   }else {
					   grid.defaultCondition=' jp_launcherid=\''+ em_code + '\'';
					   page=1;
					   grid.getCount();  
				   } 
			   }  
		   },
			'erpProcessRemindButton' : {
				click : function(btn) {
					var multiselected = [];
					var items = Ext.getCmp('alreadyLaunch').selModel.getSelection();
					Ext.each(items, function(item, index) {
								if (item.data['jp_status'] == '待审批')
									multiselected.push(item);
							});
					var records = Ext.Array.unique(multiselected);
					if (records.length > 0) {
						var params = new Object();
						params.caller = 'Process!Remind';
						var data = new Array();
						var bool = false;
						Ext.each(records, function(record, index) {
							var o = new Object();
							o['jp_nodeId'] = record.data['jp_nodeId'];
							o['dealpersoncode'] = record.data['jp_nodedealman'];
							o['jp_name']= record.data['jp_name'];
							o['jp_codevalue']= record.data['jp_codevalue'];
							data.push(o);
							bool = true;
						});
						if (bool && !me.dealing) {
							params.data = unescape(Ext.JSON.encode(data)
									.replace(/\\/g, "%"));
							me.dealing = true;
							Ext.getCmp('alreadyLaunch').setLoading(true);// loading...
							Ext.Ajax.request({
								url : basePath + 'common/remindProcess.action',
								params : params,
								method : 'post',
								callback : function(options, success, response) {
									Ext.getCmp('alreadyLaunch').setLoading(false);
									me.dealing = false;
									var localJson = new Ext.decode(response.responseText);
									if (localJson.exceptionInfo) {
										var str = localJson.exceptionInfo;
										if (str.trim().substr(0, 12) == 'AFTERSUCCESS') {
											str = str.replace('AFTERSUCCESS',
													'');
											multiselected = [];
										}
										showError(str);
										return;
									}
									if (localJson.success) {
										if (localJson.log) {
											showMessage("提示", localJson.log);
										}
										multiselected = [];
									}
								}
							});
						} else {
							showError("没有需要处理的数据!");
						}
					} else {
						showError("请勾选待审批的明细!");
					}
				}
			}
		});
	},
	onGridItemClick: function(selModel, record){//待办
		if(record.get('TYPECODE')=='procand'){
			Ext.getCmp('toDo').url='jsps/common/jtaketask.jsp';			
		}else if(record.get('TYPECODE')=='unprocess'){
			Ext.getCmp('toDo').url='jsps/common/jprocessDeal.jsp?_do=1';
		}else if(record.get('TYPECODE')=='process'){
			Ext.getCmp('toDo').url='jsps/common/jprocessDeal.jsp';
		}				
	Ext.getCmp('desktabpanel').onGridItemClick(selModel, record);
     },	 
     
    onAlreadyDoItemClick: function(selModel, record){//已处理			
    	Ext.getCmp('desktabpanel').onGridItemClick(selModel, record);
     },	
     
	onCellItemClick : function(record) {// 已发起
		var id = record.data['jp_keyvalue'];
		Ext.Ajax.request({
			url : basePath + 'common/getJProcessByForm.action',
			async : false,
			params : {
				caller : record.data['jp_caller'],
				keyValue : id,
				_noc : 1
			},
			method : 'post',
			callback : function(options, success, response) {
				var localJson = new Ext.decode(response.responseText);
				if (localJson.exceptionInfo) {
					showError(localJson.exceptionInfo);
					return;
				}
				if (localJson.node && localJson.node != -1) {
					// 再根据nodeId调取流程信息
					if (Ext.getCmp('win-flow' + id)) {
						Ext.getCmp('win-flow' + id).show();
					} else {
						var grid = Ext.create(
								"erp.view.common.JProcess.GridPanel", {
									anchor : '100% 80%',
									nodeId : localJson.node
								});
						var form = Ext.create('Ext.form.Panel', {
							layout : 'column',
							defaultType : 'textfield',
							anchor : '100% 20%',
							bodyStyle : 'background:#f1f1f1;',
							fieldDefaults : {
								columnWidth : 0.33,
								readOnly : true,
								cls : "form-field-allowBlank",
								fieldStyle : 'background:#f0f0f0;border: 1px solid #8B8970;'
							},
							items : [{
										id : 'jp_name',
										name : 'jp_name',
										fieldLabel : '流程名称',
										columnWidth : 0.33
									}, {
										columnWidth : 0.33,
										xtype : 'textfield',
										fieldLabel : '发起时间',
										name : 'jp_launchTime',
										id : 'jp_launchTime',
										readOnly : true,
										fieldStyle : 'background:#f0f0f0;border: 1px solid #8B8970;'
									}, {
										fieldLabel : '发起人',
										columnWidth : 0.33,
										xtype : 'textfield',
										id : 'jp_launcherName',
										name : 'jp_launcherName',
										readOnly : true,
										fieldStyle : 'background:#f0f0f0;border: 1px solid #8B8970;'
									}, {
										fieldLabel : '节点名称',
										id : 'jp_nodeName',
										name : 'jp_nodeName',
										xtype : 'textfield',
										readOnly : true,
										fieldStyle : 'background:#f0f0f0;border: 1px solid #8B8970;'
									}, {
										fieldLabel : '处理人',
										id : 'jp_nodeDealMan',
										name : 'jp_nodeDealMan',
										xtype : 'textfield',
										fieldStyle : 'background:#f0f0f0;border: 1px solid #8B8970;',
										readOnly : true,
										listeners : {
											change : function(field) {
												var em = Ext
														.getCmp('jp_nodeDealMan')
														.getValue();
												var btn = Ext
														.getCmp('dealbutton');
												if (em != em_code)
													btn.setDisabled(true);
											}
										}
									}, {
										fieldLabel : '审批状态',
										id : 'jp_status',
										name : 'jp_status',
										xtype : 'textfield',
										fieldStyle : 'background:#f0f0f0;border: 1px solid #8B8970;',
										readOnly : true
									}],
							loader : {
								url : basePath + 'common/getCurrentNode.action',
								renderer : function(loader, response, active) {
									var res = Ext.decode(response.responseText);
									if (res.info.currentnode.jp_nodeDealMan) {
										res.info.currentnode.jp_nodeDealMan = res.info.dealmanname
												+ "("
												+ res.info.currentnode.jp_nodeDealMan
												+ ")";
									} else
										res.info.currentnode.jp_nodeDealMan = res.info.dealmanname
												+ "("
												+ res.info.currentnode.jp_candidate
												+ ")";
									res.info.currentnode.jp_launchTime = Ext.Date
											.format(
													new Date(res.info.currentnode.jp_launchTime),
													'Y-m-d H:i:s');
									this.target.getForm()
											.setValues(res.info.currentnode);
									return true;
								},
								autoLoad : true,
								params : {
									jp_nodeId : localJson.node,
									_noc : 1
								}
							},
							buttonAlign : 'center',
							buttons : [{
								text : $I18N.common.button.erpFlowButton,
								iconCls : 'x-button-icon-scan',
								cls : 'x-btn-gray',
								id : 'dealbutton',
								handler : function(btn) {
									me.FormUtil.onAdd(caller + '_flow', '流程处理',
											'jsps/common/jprocessDeal.jsp?formCondition=jp_nodeidIS'
													+ localJson.node);
								}
							}, {
								text : '关  闭',
								iconCls : 'x-button-icon-close',
								cls : 'x-btn-gray',
								handler : function() {
									Ext.getCmp('win-flow' + id).close();
								}
							}]
						});
						Ext.create('Ext.window.Window', {
							id : 'win-flow' + id,
							title : '<span style="color:#CD6839;">流程处理情况</span>',
							iconCls : 'x-button-icon-set',
							closeAction : 'destroy',
							height : "100%",
							width : "90%",
							maximizable : true,
							buttonAlign : 'center',
							layout : 'fit',
							items : [{
								xtype : 'tabpanel',
								frame : true,
								layout : 'fit',
								items : [{
											title : '处理明细',
											layout : 'anchor',
											frame : true,
											items : [form, grid]
										}, {
											title : '节点信息',
											items : [{
												tag : 'iframe',
												style : {
													background : '#f0f0f0',
													border : 'none'
												},
												frame : true,
												border : false,
												layout : 'fit',
												height : window.innerHeight
														* 0.9,
												iconCls : 'x-tree-icon-tab-tab',
												html : '<iframe id="iframe_maindetail_" src="'
														+ basePath
														+ 'workfloweditor/workfloweditorscan.jsp?jdId='
														+ localJson.jd
														+ "&type="
														+ localJson.type
														+ "&nodeId="
														+ localJson.node
														+ '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
											}]
										}, {
											title : '知会信息',
											frame : true,
											layout : 'anchor',
											items : [{
												xtype : 'gridpanel',
												anchor : '100% 100%',
												columnLines : true,
												store : Ext.create(
														'Ext.data.Store', {
															fields : [
																	'jn_notify',
																	'jn_notifyname',
																	'jn_nodename',
																	{
																		name : 'jn_type',
																		type : 'string'
																	},
																	'jn_man', {
																		name : 'jn_date',
																		type : 'date'
																	}]
														}),
												columns : [{
															text : '知会编号',
															dataIndex : 'jn_notify',
															width : 120

														}, {
															text : '知会个人/岗位',
															dataIndex : 'jn_notifyname',
															width : 120
														}, {
															text : '设置节点',
															dataIndex : 'jn_nodename',
															width : 120
														}, {
															text : '知会类型',
															dataIndex : 'jn_type',
															renderer : function(
																	val) {
																var res = val;
																if (val == 'people')
																	return '个人';
																else
																	return '岗位';
															},
															width : 120
														}, {
															text : '设置人',
															dataIndex : 'jn_man',
															width : 120
														}, {
															text : '设置时间',
															dataIndex : 'jn_date',
															xtype : "datecolumn",
															format : "Y-m-d H:i:s",
															flex : 1
														}]

											}],
											listeners : {
												activate : function(tab) {
													var grid = tab.items.items[0];
													var gridUtil = Ext
															.create('erp.util.GridUtil');
													gridUtil.loadNewStore(grid,
															{
																caller : 'JProcessNotify',
																condition : "jn_processinstanceid='"
																		+ localJson.instanceId
																		+ "'"
															});
												}
											}

										},{
											title:'历史处理明细',
											layout:'anchor',
											frame:true,
											items:[Ext.create("erp.view.common.JProcess.AllHistoryGridPanel",{
												anchor: '100% 100%' ,
												nodeId: localJson.node
											})]																	
										}]

							}]
						}).show();
					}
				} else {
					showMessage("提示", "当前单据无流程处理!");
				}
			}
		});
	}
});