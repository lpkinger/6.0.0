/**
 * 此toolbar用于明细表grid
 */
Ext.define('erp.view.hr.kpi.Kpitoolbar', {
	extend : 'Ext.Toolbar',
	alias : 'widget.Kpitoolbar',
	FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
	dock : 'top',
	requires : ['erp.view.core.trigger.DbfindTrigger','erp.view.hr.kpi.KpiPanel'],
	initComponent : function() {
		var me=this;
		var f=this.id;
		Ext.apply(this, {
			items : [{
						xtype : 'tbtext',
						text : '基础数据'
					},{
						xtype:'button',
						iconCls: 'x-button-icon-add',
				    	cls: 'x-btn-gray',
				    	id:f+"-KpiAdd",
				    	text: '添加',
				        width: 60,
				    	style: {
				    		marginLeft: '10px'
				        },
				        listeners: {
							click:function(btn){
								var form=btn.ownerCt.ownerCt.ownerCt.previousSibling();
								var k=form.keyField;
								var fid=Ext.getCmp(k).value;
								if(!fid){
									showError("请先保存");
								}else{
									var grid=btn.ownerCt.ownerCt;
									var caller=grid.id;
									if(caller=='grid'){
										caller='KpidesignItem';
									}
									var win;
									var formCondition='';
									if(caller=='KpidesignItem'){
										win =new Ext.window.Window({
											id: 'kpi-win1',
											title: '考核项目维护',
											height:350,
											width: 1000,
											layout:'fit',
											resizable:false,
											defaults: {
									            anchor: '100%'
									        },
									        items:[{
									        	xtype: 'erpKpiPanel',
									        	id:'kpi-form',
												anchor: '100% 50%',
												fixedlayout:true,
												caller:'KpidesignItem',
												formCondition:formCondition,
												saveUrl: 'hr/kpi/saveDetail.action?caller=' +caller,
												updateUrl:'hr/kpi/updateDetail.action?caller=' +caller,
												deleteUrl: 'hr/kpi/deleteDetail.action?caller=' +caller,
												getIdUrl: 'common/getCommonId.action?caller=' +caller,
												keyField: '',
												codeField: '',
												buttons: [{
													text:'保存',
													cls: 'x-btn-gray',
													handler: function(btn) {
													Ext.getCmp('ki_kdid').setValue(Ext.getCmp('kd_id').value);
													    var bool=me.checkScore();
													    if(bool!=false){
													    	me.beforeSave();	
													    }
													}
												}, {
													text: $I18N.common.button.erpCloseButton,
													cls: 'x-btn-gray',
													handler: function(btn) {
														btn.ownerCt.ownerCt.ownerCt.close();
													}
												}]
									        }],
											listeners:{
									              'beforeclose':function(view ,opt){
									            	   //grid  刷新一次
									            	var   grid=Ext.getCmp('grid');
									            	var   fid=Ext.getCmp('kd_id').value;
									            	var   gridParam = {caller: 'Kpidesign', condition: 'ki_kdid='+fid};
									            	grid.GridUtil.loadNewStore(grid,gridParam);
									            	}	
									        }
										});
									}else if(caller=='KpidesigngradeLevel_F'){
										win =new Ext.window.Window({
						    				id: 'kpi-win2',
						    				title: '评分等级维护',
						    				height: 200,
						    				width: 400,
						    				layout:'fit',
						    				resizable:false,
						    				defaults: {
						    		            anchor: '100%'
						    		        },
						    		        items:[{
						    		        	xtype:'form',
						    		        	id:'kpi-form',
						    		        	baseCls : "x-plain",
						    		        	getIdUrl: 'common/getId.action?seq=KpidesigngradeLevel_SEQ',
						    		        	updateUrl:'hr/kpi/updateDetail.action?caller=' +caller,
						    		        	saveUrl: 'hr/kpi/saveDetail.action?caller=KpidesigngradeLevel_F',
						    		        	buttonAlign: 'center',
						    		        	keyField:'kl_id',
						    		        	margin:'10 10 10 10',
							    				layout: {
							    					type: 'vbox',
							    					align: 'center'
							    				},
							    				defaults:{
							    					bodyStyle:'padding:10 10 10 10'
							    				},
						    		        	items: [{
							    					xtype: 'hidden',
							    					fieldLabel: 'ID',
							    					name:'kl_id',
							    					id:'kl_id',
							    					allowBlank: false,
							    					value: ''
							    				},{
							    					xtype: 'hidden',
							    					fieldLabel: '关联主表',
							    					name:'kl_kdid',
							    					id:'kl_kdid',
							    					value: ''
							    				},{
							    					xtype: 'hidden',
							    					fieldLabel: '序号',
							    					name:'kl_detno',
							    					id:'kl_detno',
							    					value: ''
							    				},{
							    					xtype: 'dbfindtrigger',
							    					fieldLabel: '名称',
							    					name:'kl_name',
							    					id:'kl_name',
							    					allowBlank: false,
							    					labelStyle:'color:#FF0000',
							    					fieldStyle: 'background:#E0E0FF;color:#515151',
							    					value: ''
							    				},{
							    					xtype: 'numberfield',
							    					fieldLabel: '最低分',
							    					name:'kl_score_from',
							    					id:'kl_score_from',
							    					labelStyle:'color:#FF0000',
							    					fieldStyle: 'background:#E0E0FF;color:#515151',
							    					allowBlank: false,
							    					maxValue :100,
							    					minValue:0,
							    					value: ''
							    				},{
							    					xtype: 'numberfield',
							    					fieldLabel: '最高分',
							    					labelStyle:'color:#FF0000',
							    					allowBlank: false,
							    					name:'kl_score_to',
							    					fieldStyle: 'background:#E0E0FF;color:#515151',
							    					id:'kl_score_to',
							    					maxValue :100,
							    					minValue:0,
							    					value: ''
							    				}],
							    				buttons: [{
							    					text:'保存',
							    					cls: 'x-btn-gray',
							    					handler: function(btn) {
							    						Ext.getCmp('kl_kdid').setValue(Ext.getCmp('kd_id').value);	
							    						me.beforeSave(btn);
							    					}
							    				}, {
							    					text: $I18N.common.button.erpCloseButton,
							    					cls: 'x-btn-gray',
							    					handler: function(btn) {
							    						btn.ownerCt.ownerCt.ownerCt.close();
							    					}
							    				}]
						    		        }],
							    				listeners:{
							                          'beforeclose':function(view ,opt){
							                        	   //grid  刷新一次
							                        	var   grid=Ext.getCmp('KpidesigngradeLevel_F');
							                        	var   fid=Ext.getCmp('kd_id').value;
							                        	var   gridParam = {caller: 'KpidesigngradeLevel', condition: 'kl_kdid='+fid};
							                        	grid.GridUtil.loadNewStore(grid,gridParam);
							                        	}	
							                    }
						    			});
									}else{
										win = new Ext.window.Window({
											id: 'kpi-win3',
						    				title: '评分设计维护',
						    				height:500,
						    				width: 1000,
						    				layout:'anchor',
						    				resizable:false,
						    				defaults: {
						    		            anchor: '100%'
						    		        },
						    		        items:[{
						    		        	xtype: 'erpKpiPanel',
						    		        	id:'kpi-form',
						    					anchor: '100% 50%',
						    					fixedlayout:true,
						    					caller:'Kpidesignpoint_F',
						    					formCondition:'',
						    					saveUrl: 'hr/kpi/saveDetail.action?caller=' +caller,
						    					updateUrl:'hr/kpi/updateDetail.action?caller=' +caller,
						    					deleteUrl: 'hr/kpi/deleteDetail.action?caller=' +caller,
						    					getIdUrl: 'common/getCommonId.action?caller=' +caller,
						    					keyField: '',
						    					codeField: '',
						    					buttons: [{
								    				text:'保存',
								    				cls: 'x-btn-gray',
								    				handler: function(btn) {
													Ext.getCmp('kp_kdid').setValue(Ext.getCmp('kd_id').value);	
							   						me.beforeSave(btn);
							    					}
							    				}, {
							    					text: $I18N.common.button.erpCloseButton,
							    					cls: 'x-btn-gray',
							    					handler: function(btn) {
							    						btn.ownerCt.ownerCt.ownerCt.close();
							    					}
								    			}]
						    		        },{
						    		        	id:'kpi-grid',
						    		        	xtype: 'erpGridPanel2',
						    					selModel: {
						    					    injectCheckbox: 0,
						    					    mode: "MULTI",     //"SINGLE"/"SIMPLE"/"MULTI"
						    					    checkOnly: true     //只能通过checkbox选择
						    					},
						    					condition:'ki_kdid ='+fid,
						    					caller:'Kpidesignpoint_F',
						    					selType: "checkboxmodel",
						    					bbar:[],
						    					anchor: '100% 50%'
						    		        }],
						    				listeners:{
						                          'beforeclose':function(view ,opt){
						                        	   //grid  刷新一次
						                        	var   grid=Ext.getCmp('Kpidesignpoint_F');
						                        	var   fid=Ext.getCmp('kd_id').value;
						                        	var   gridParam = {caller: 'Kpidesignpoint', condition: 'kp_kdid='+fid};
						                        	grid.GridUtil.loadNewStore(grid,gridParam);
						                        	}	
						                    }
										});
									}
									 win.show(); 
								}
							}
						}					
				     },{
							xtype:'button',
							iconCls: 'x-button-icon-submit',
					    	cls: 'x-btn-gray',
					    	id:f+"-KpiUpdate",
					    	text: '修改',
					    	disabled:true,
					        width: 60,
					    	style: {
					    		marginLeft: '10px'
					        },
					    	handler: function(btn){
					    			var grid=btn.ownerCt.ownerCt;
					    			var records = grid.selModel.getSelection();
					    			if(records.length > 0){
					    					var fid=Ext.getCmp('kd_id').value;
											var grid=btn.ownerCt.ownerCt;
											var caller=grid.id;
											if(caller=='grid'){
												caller='KpidesignItem';
											}
											var win;
											var formCondition='';
											if(caller=='KpidesignItem'){
												formCondition='ki_id='+records[0].data['ki_id'];
												win =new Ext.window.Window({
													id: 'kpi-win1',
													title: '考核项目维护',
													height:350,
													width: 1000,
													layout:'fit',
													resizable:false,
													defaults: {
											            anchor: '100%'
											        },
											        items:[{
											        	xtype: 'erpKpiPanel',
											        	id:'kpi-form',
														anchor: '100% 50%',
														fixedlayout:true,
														caller:'KpidesignItem',
														formCondition:formCondition,
														saveUrl: 'hr/kpi/saveDetail.action?caller=' +caller,
														updateUrl:'hr/kpi/updateDetail.action?caller=' +caller,
														deleteUrl: 'hr/kpi/deleteDetail.action?caller=' +caller,
														getIdUrl: 'common/getCommonId.action?caller=' +caller,
														keyField: '',
														codeField: '',
														buttons: [{
															text:'更新',
															cls: 'x-btn-gray',
															handler: function(btn) {
															Ext.getCmp('ki_kdid').setValue(Ext.getCmp('kd_id').value);	
																var bool=me.checkScore();
																if(bool!=false){	
																	me.beforeUpdate();
																}
															}
														}, {
															text: $I18N.common.button.erpCloseButton,
															cls: 'x-btn-gray',
															handler: function(btn) {
																btn.ownerCt.ownerCt.ownerCt.close();
															}
														}]
											        }],
													listeners:{
											              'beforeclose':function(view ,opt){
											            	   //grid  刷新一次
											            	var   grid=Ext.getCmp('grid');
											            	var   fid=Ext.getCmp('kd_id').value;
											            	var   gridParam = {caller: 'Kpidesign', condition: 'ki_kdid='+fid};
											            	grid.GridUtil.loadNewStore(grid,gridParam);
											            	}	
											        }
												});
											}else if(caller=='KpidesigngradeLevel_F'){
												win =new Ext.window.Window({
								    				id: 'kpi-win2',
								    				title: '评分等级维护',
								    				height: 200,
								    				width: 400,
								    				layout:'fit',
								    				resizable:false,
								    				defaults: {
								    		            anchor: '100%'
								    		        },
								    		        items:[{
								    		        	xtype:'form',
								    		        	id:'kpi-form',
								    		        	baseCls : "x-plain",
								    		        	getIdUrl: 'common/getId.action?seq=KpidesigngradeLevel_SEQ',
								    		        	updateUrl:'hr/kpi/updateDetail.action?caller=' +caller,
								    		        	saveUrl: 'hr/kpi/saveDetail.action?caller=KpidesigngradeLevel_F',
								    		        	buttonAlign: 'center',
								    		        	keyField:'kl_id',
								    		        	margin:'10 10 10 10',
									    				layout: {
									    					type: 'vbox',
									    					align: 'center'
									    				},
									    				defaults:{
									    					bodyStyle:'padding:10 10 10 10'
									    				},
								    		        	items: [{
									    					xtype: 'hidden',
									    					fieldLabel: 'ID',
									    					name:'kl_id',
									    					id:'kl_id',
									    					allowBlank: false,
									    					value: records[0].data['kl_id']
									    				},{
									    					xtype: 'hidden',
									    					fieldLabel: '关联主表',
									    					name:'kl_kdid',
									    					id:'kl_kdid',
									    					value: records[0].data['kl_kdid']
									    				},{
									    					xtype: 'hidden',
									    					fieldLabel: '序号',
									    					name:'kl_detno',
									    					id:'kl_detno',
									    					value: records[0].data['kl_detno']
									    				},{
									    					xtype: 'dbfindtrigger',
									    					fieldLabel: '名称',
									    					name:'kl_name',
									    					id:'kl_name',
									    					allowBlank: false,
									    					labelStyle:'color:#FF0000',
									    					fieldStyle: 'background:#E0E0FF;color:#515151',
									    					value: records[0].data['kl_name']
									    				},{
									    					xtype: 'numberfield',
									    					fieldLabel: '最低分',
									    					name:'kl_score_from',
									    					id:'kl_score_from',
									    					labelStyle:'color:#FF0000',
									    					fieldStyle: 'background:#E0E0FF;color:#515151',
									    					allowBlank: false,
									    					value: records[0].data['kl_score_from']
									    				},{
									    					xtype: 'numberfield',
									    					fieldLabel: '最高分',
									    					labelStyle:'color:#FF0000',
									    					allowBlank: false,
									    					name:'kl_score_to',
									    					fieldStyle: 'background:#E0E0FF;color:#515151',
									    					id:'kl_score_to',
									    					value: records[0].data['kl_score_to']
									    				}],
									    				buttons: [{
									    					text:'更新',
									    					cls: 'x-btn-gray',
									    					handler: function(btn) {
									    						Ext.getCmp('kl_kdid').setValue(Ext.getCmp('kd_id').value);	
									    						me.beforeUpdate(btn);
									    					}
									    				}, {
									    					text: $I18N.common.button.erpCloseButton,
									    					cls: 'x-btn-gray',
									    					handler: function(btn) {
									    						btn.ownerCt.ownerCt.ownerCt.close();
									    					}
									    				}]
								    		        }],
									    				listeners:{
									                          'beforeclose':function(view ,opt){
									                        	   //grid  刷新一次
									                        	var   grid=Ext.getCmp('KpidesigngradeLevel_F');
									                        	var   fid=Ext.getCmp('kd_id').value;
									                        	var   gridParam = {caller: 'KpidesigngradeLevel', condition: 'kl_kdid='+fid};
									                        	grid.GridUtil.loadNewStore(grid,gridParam);
									                        	}	
									                    }
								    			});
											}else{
												formCondition='kp_id='+records[0].data['kp_id'];
												win = new Ext.window.Window({
													id: 'kpi-win3',
								    				title: '评分设计维护',
								    				height:500,
								    				width: 1000,
								    				layout:'anchor',
								    				resizable:false,
								    				defaults: {
								    		            anchor: '100%'
								    		        },
								    		        items:[{
								    		        	xtype: 'erpKpiPanel',
								    		        	id:'kpi-form',
								    					anchor: '100% 50%',
								    					fixedlayout:true,
								    					caller:'Kpidesignpoint_F',
								    					formCondition:formCondition,
								    					saveUrl: 'hr/kpi/saveDetail.action?caller=' +caller,
								    					updateUrl:'hr/kpi/updateDetail.action?caller=' +caller,
								    					deleteUrl: 'hr/kpi/deleteDetail.action?caller=' +caller,
								    					getIdUrl: 'common/getCommonId.action?caller=' +caller,
								    					keyField: '',
								    					codeField: '',
								    					buttons: [{
										    				text:'更新',
										    				cls: 'x-btn-gray',
										    				handler: function(btn) {
															Ext.getCmp('kp_kdid').setValue(Ext.getCmp('kd_id').value);	
									   						me.beforeUpdate(btn);
									    					}
									    				}, {
									    					text: $I18N.common.button.erpCloseButton,
									    					cls: 'x-btn-gray',
									    					handler: function(btn) {
									    						btn.ownerCt.ownerCt.ownerCt.close();
									    					}
										    			}]
								    		        },{
								    		        	id:'kpi-grid',
								    		        	xtype: 'erpGridPanel2',
								    					selModel: {
								    					    injectCheckbox: 0,
								    					    mode: "MULTI",     //"SINGLE"/"SIMPLE"/"MULTI"
								    					    checkOnly: true     //只能通过checkbox选择
								    					},
								    					condition:'ki_kdid ='+fid,
								    					caller:'Kpidesignpoint_F',
								    					selType: "checkboxmodel",
								    					bbar:[],
								    					anchor: '100% 50%'
								    		        }],
								    				listeners:{
								                          'beforeclose':function(view ,opt){
								                        	   //grid  刷新一次
								                        	var   grid=Ext.getCmp('Kpidesignpoint_F');
								                        	var   fid=Ext.getCmp('kd_id').value;
								                        	var   gridParam = {caller: 'Kpidesignpoint', condition: 'kp_kdid='+fid};
								                        	grid.GridUtil.loadNewStore(grid,gridParam);
								                        	}	
								                    }
												});
											}
											 win.show(); 
									}else{
										showError("请选择明细行");
									}	
					    		}
				},{
					xtype:'button',
					iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	id:f+"-KpiDel",
			    	text: '删除',
			    	disabled:true,
			        width: 60,
			    	style: {
			    		marginLeft: '10px'
			        },
			    	handler: function(btn){
			    			var grid=btn.ownerCt.ownerCt;
			    			var records = grid.selModel.getSelection();
			    			var caller=grid.id;
							if(caller=='grid'){
								caller='KpidesignItem';
							}
							var id=0;
							var   fid=Ext.getCmp('kd_id').value;
                        	var   gridParam = {};
                        	if(records.length > 0){
                        		if(caller=='KpidesignItem'){
                        			id=records[0].data['ki_id'];
                        			gridParam = {caller: 'Kpidesign', condition: 'ki_kdid='+fid};
                        		}else if(caller=='KpidesigngradeLevel_F'){
                        			id=records[0].data['kl_id'];
                        			gridParam = {caller: 'KpidesigngradeLevel', condition: 'kl_kdid='+fid};
                        		}else{
                        			id=records[0].data['kp_id'];
                        			gridParam = {caller: 'Kpidesignpoint', condition: 'kp_kdid='+fid};
                        		}
			    					warnMsg($I18N.common.msg.ask_del, function(btn){
									if(btn == 'yes'){
										Ext.Ajax.request({
											url : basePath + 'hr/kpi/deleteDetail.action?caller=' +caller,
											params: {
												id: id
											},
											method : 'post',
											callback : function(options,success,response){
												var localJson = new Ext.decode(response.responseText);
												if(localJson.exceptionInfo){
													showError(localJson.exceptionInfo);return;
												}
												if(localJson.success){
													Ext.Msg.alert('提示','删除成功',function(){
														grid.GridUtil.loadNewStore(grid,gridParam);
														});
												} else {
													delFailure();
												}
											}
										});
									}
								});
			    			}else{
							
							}	
			    	}
				},{
					xtype:'button',
					iconCls: 'x-button-icon-save',
			    	cls: 'x-btn-gray',
			    	id:f+"-KpiLook",
			    	text: '查看',
			        width: 60,
			    	style: {
			    		marginLeft: '10px'
			        },
			    	handler: function(btn){
			    			var grid=btn.ownerCt.ownerCt;
			    			var records = grid.selModel.getSelection();
			    			if(records.length > 0){
			    					var fid=Ext.getCmp('kd_id').value;
									var grid=btn.ownerCt.ownerCt;
									var caller=grid.id;
									if(caller=='grid'){
										caller='KpidesignItem';
									}
									var win;
									var formCondition='';
									if(caller=='KpidesignItem'){
										formCondition='ki_id='+records[0].data['ki_id'];
										win =new Ext.window.Window({
											id: 'kpi-win1',
											title: '考核项目',
											height:350,
											width: 1000,
											layout:'fit',
											resizable:false,
											defaults: {
									            anchor: '100%'
									        },
									        items:[{
									        	xtype: 'erpKpiPanel',
									        	id:'kpi-form',
												anchor: '100% 50%',
												fixedlayout:true,
												caller:'KpidesignItem',
												formCondition:formCondition,
												keyField: '',
												codeField: '',
												buttons: [{
													text: $I18N.common.button.erpCloseButton,
													cls: 'x-btn-gray',
													handler: function(btn) {
														btn.ownerCt.ownerCt.ownerCt.close();
													}
												}]
									        }]
										});
									}else if(caller=='KpidesigngradeLevel_F'){
										win =new Ext.window.Window({
						    				id: 'kpi-win2',
						    				title: '评分等级',
						    				height: 200,
						    				width: 400,
						    				layout:'fit',
						    				resizable:false,
						    				defaults: {
						    		            anchor: '100%'
						    		        },
						    		        items:[{
						    		        	xtype:'form',
						    		        	id:'kpi-form',
						    		        	baseCls : "x-plain",
						    		        	buttonAlign: 'center',
						    		        	keyField:'kl_id',
						    		        	margin:'10 10 10 10',
							    				layout: {
							    					type: 'vbox',
							    					align: 'center'
							    				},
							    				defaults:{
							    					bodyStyle:'padding:10 10 10 10'
							    				},
						    		        	items: [{
							    					xtype: 'hidden',
							    					fieldLabel: 'ID',
							    					name:'kl_id',
							    					id:'kl_id',
							    					allowBlank: false,
							    					value: records[0].data['kl_id']
							    				},{
							    					xtype: 'hidden',
							    					fieldLabel: '关联主表',
							    					name:'kl_kdid',
							    					id:'kl_kdid',
							    					value: records[0].data['kl_kdid']
							    				},{
							    					xtype: 'hidden',
							    					fieldLabel: '序号',
							    					name:'kl_detno',
							    					id:'kl_detno',
							    					value: records[0].data['kl_detno']
							    				},{
							    					xtype: 'dbfindtrigger',
							    					fieldLabel: '名称',
							    					name:'kl_name',
							    					id:'kl_name',
							    					allowBlank: false,
							    					labelStyle:'color:#FF0000',
							    					fieldStyle: 'background:#E0E0FF;color:#515151',
							    					value: records[0].data['kl_name']
							    				},{
							    					xtype: 'numberfield',
							    					fieldLabel: '最低分',
							    					name:'kl_score_from',
							    					id:'kl_score_from',
							    					labelStyle:'color:#FF0000',
							    					fieldStyle: 'background:#E0E0FF;color:#515151',
							    					allowBlank: false,
							    					value: records[0].data['kl_score_from']
							    				},{
							    					xtype: 'numberfield',
							    					fieldLabel: '最高分',
							    					labelStyle:'color:#FF0000',
							    					allowBlank: false,
							    					name:'kl_score_to',
							    					fieldStyle: 'background:#E0E0FF;color:#515151',
							    					id:'kl_score_to',
							    					value: records[0].data['kl_score_to']
							    				}],
							    				buttons: [{
							    					text: $I18N.common.button.erpCloseButton,
							    					cls: 'x-btn-gray',
							    					handler: function(btn) {
							    						btn.ownerCt.ownerCt.ownerCt.close();
							    					}
							    				}]
						    		        }]
						    			});
									}else{
										formCondition='kp_id='+records[0].data['kp_id'];
										win = new Ext.window.Window({
											id: 'kpi-win3',
						    				title: '评分设计',
						    				height:500,
						    				width: 1000,
						    				layout:'anchor',
						    				resizable:false,
						    				defaults: {
						    		            anchor: '100%'
						    		        },
						    		        items:[{
						    		        	xtype: 'erpKpiPanel',
						    		        	id:'kpi-form',
						    					anchor: '100% 50%',
						    					fixedlayout:true,
						    					caller:'Kpidesignpoint_F',
						    					formCondition:formCondition,
						    					keyField: '',
						    					codeField: '',
						    					buttons: [{
							    					text: $I18N.common.button.erpCloseButton,
							    					cls: 'x-btn-gray',
							    					handler: function(btn) {
							    						btn.ownerCt.ownerCt.ownerCt.close();
							    					}
								    			}]
						    		        },{
						    		        	id:'kpi-grid',
						    		        	xtype: 'erpGridPanel2',
						    					selModel: {
						    					    injectCheckbox: 0,
						    					    mode: "MULTI",     //"SINGLE"/"SIMPLE"/"MULTI"
						    					    checkOnly: true     //只能通过checkbox选择
						    					},
						    					condition:'ki_kdid ='+fid,
						    					caller:'Kpidesignpoint_F',
						    					selType: "checkboxmodel",
						    					bbar:[],
						    					anchor: '100% 50%'
						    		        }]
										});
									}
									 win.show(); 
							}else{
								showError("请选择明细行");
							}	
			    		}
		
				}]
		});
		this.callParent(arguments);
	},
	checkForm: function(){
		var s = '';
		var form = Ext.getCmp('kpi-form');
		form.getForm().getFields().each(function (item, index, length){
			if(!item.isValid()){
				if(s != ''){
					s += ',';
				}
				if(item.fieldLabel || item.ownerCt.fieldLabel){
					s += item.fieldLabel || item.ownerCt.fieldLabel;
				}
			}
		});
		if(s == ''){
			return true;
		}
		showError($I18N.common.form.necessaryInfo1 + '(<font color=green>' + s.replace(/&nbsp;/g,'') + 
				'</font>)' + $I18N.common.form.necessaryInfo2);
		return false;
	},
	checkScore:function(){
		var a=Ext.getCmp('ki_score_from').value-0;//最低分
		var b=Ext.getCmp('ki_score_to').value-0;//最高分
		var c=Ext.getCmp('ki_standardscore').value-0;//标准分
		if(a>b){
			showError('起始分数不能大于截止分数');
			return false;
		}
		if(c<a||c>b){
			showError('标准分要在起始分数和截止分数之间');
			return false;	
		}
	},
	beforeSave: function(){
		var me = this;
		var form = Ext.getCmp('kpi-form');
		if(!me.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var grid = Ext.getCmp('kpi-grid');
		var jsonGridData = new Array();
		if(grid) {
			var s=grid.selModel.getSelection();//获取多行
			var dd;
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				dd = new Object();
					Ext.each(grid.columns, function(c){
						if((!c.isCheckerHd)&&(c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
							if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
								if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
									dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
								} else {
									dd[c.dataIndex] = "" + s[i].data[c.dataIndex];
								}
							} else {
								dd[c.dataIndex] = s[i].data[c.dataIndex];
							}
							if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
								dd[c.dataIndex] = c.defaultValue;
							}
						}
					});
					if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
						dd[grid.mainField] = Ext.getCmp(form.keyField).value;
					}
					jsonGridData.push(Ext.JSON.encode(dd));
				}
			}
		param1 = jsonGridData == null ? [] : "[" + jsonGridData.toString().replace(/\\/g,"%") + "]";
		if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				me.save(r, param1);
			}else{
				me.FormUtil.checkForm();
			}		
		},
	save: function(){
		var params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});	
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param = unescape(arguments[1].toString().replace(/\\/g,"%"));
		var me = this;
		var form = Ext.getCmp('kpi-form');
		Ext.Ajax.request({
	   		url : basePath + form.saveUrl,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){	   			
	   			var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					Ext.Msg.alert('提示','添加成功',function(){
						form.ownerCt.close();
					});
				} else if(localJson.exceptionInfo){
	   					var str = localJson.exceptionInfo;
	   					showError(str);
		   				return;
	   			} else{
	   				saveFailure();//@i18n/i18n.js
	   			}
	   		}
	   		
		});
	},
	beforeUpdate: function(){
		var me = this;
		var form = Ext.getCmp('kpi-form');
		if(!me.checkForm){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var grid = Ext.getCmp('kpi-grid');
		var jsonGridData = new Array();
		if(grid) {
			var s=grid.selModel.getSelection();//获取多行
			var dd;
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				dd = new Object();
					Ext.each(grid.columns, function(c){
						if((!c.isCheckerHd)&&(c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
							if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
								if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
									dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
								} else {
									dd[c.dataIndex] = "" + s[i].data[c.dataIndex];
								}
							} else {
								dd[c.dataIndex] = s[i].data[c.dataIndex];
							}
							if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
								dd[c.dataIndex] = c.defaultValue;
							}
						}
					});
					if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
						dd[grid.mainField] = Ext.getCmp(form.keyField).value;
					}
					jsonGridData.push(Ext.JSON.encode(dd));
				}
			}
			param = jsonGridData == null ? [] : "[" + jsonGridData.toString().replace(/\\/g,"%") + "]";
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				me.update(r, param);
			}else{
			me.FormUtil.checkForm();
		}	
	},
	update:function(){
		var params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param = unescape(arguments[1].toString().replace(/\\/g,"%"));
		var me = this;
		var form = Ext.getCmp('kpi-form');
		Ext.Ajax.request({
	   		url : basePath + form.updateUrl,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					Ext.Msg.alert('提示','添加成功',function(){
						form.ownerCt.close();
					});
				} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				showError(str);
		   				return;
	   			} else{
	   				saveFailure();//@i18n/i18n.js
	   			}
	   		} 		
		});
	}
});