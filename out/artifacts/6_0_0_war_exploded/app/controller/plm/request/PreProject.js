Ext.QuickTips.init();
Ext.define('erp.controller.plm.request.PreProject', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'plm.request.PreProject', 'core.grid.Panel2','core.toolbar.Toolbar', 'core.form.MultiField','core.button.TurnProject', 
			'core.button.Save', 'core.button.Add','core.button.Submit', 'core.button.Upload','core.button.ResAudit','core.button.Audit',
			'core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit', 'core.button.Export',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn', 'core.form.StatusField',
			'core.form.FileField','core.button.CopyAll','core.button.ResetSync', 'core.button.RefreshSync','core.button.ChangeResponsible','core.form.MultiField'],
	init : function() {
		var me = this;
		this.control({
			'erpGridPanel2' : {
				afterrender : function(grid) {
					var status = Ext.getCmp('pp_statuscode');
					if (status && status.value != 'ENTERING'
							&& status.value != 'COMMITED') {
						Ext.each(grid.columns, function(c) {
							c.setEditor(null);
						});
					}	
				},
				reconfigure:function(grid){
					var items = grid.store.data.items;
					Ext.each(items, function(item) {
						var ra_status = item.data['ra_status'];
						if(ra_status!='已完成'){
							item.data['ra_enddate']=null;
						}
					});
				},
				itemclick:function(grid,record){
					me.GridUtil.onGridItemClick(grid, record);					
					var status = Ext.getCmp('pp_statuscode');
					if (status && status.value == 'ENTERING') {
						if(record.index+1==grid.store.data.items.length){
							var data = new Array();
							for(var i=0;i<10;i++){
								var o = new Object();
								o['ppd_detno'] = record.data['ppd_detno'] + i + 1;
								data.push(o);
							}
							grid.store.loadData(data,true);
						}
					}
					if(record.data['ra_status']=='进行中'){
						var btn = Ext.getCmp('chanrespbtn');
						if(btn){
							btn.setDisabled(false);
							btn.oldValue=record.data['ppd_mancode']+";"+record.data['ppd_man'];
							btn.ppd_id = record.data['ppd_id'];				
						}
					}else{
						var btn = Ext.getCmp('chanrespbtn');
						if(btn){
							btn.setDisabled(true);
						}
					}
				}
			},
			'field[name=pp_prcode]' : {
				afterrender : function(f) {
					f.setFieldStyle({
						'color' : 'blue'
					});
					f.focusCls = 'mail-attach';
					var c = Ext.Function.bind(me.openInvoice, me);
					Ext.EventManager.on(f.inputEl, {
						mousedown : c,
						scope : f,
						buffer : 100
					});
				}
			},
			'erpChangeResponsibleButton':{
				click : function(btn) {
					btn.setDisabled(true);
					var win = new Ext.window.Window({
							id: 'win',
							title: '变更责任人',
							height: 200,
							width: 550,							
							layout:'fit',
							items: [{
								xtype:'form',
								layout: {
							        type: 'hbox',
							        pack: 'end',
							        align: 'middle'
					    		},
								border: false, 
								autoScroll:true,
								labelSeparator : ':',
								bodyStyle :'background:#F2F2F2;',							
								items : [{
									margin:'10 30 10 35',
									labelAlign : "left",							
									labelWidth:60,	
									columnWidth:0.25,
									fieldLabel : '责任人',					
									allowBlank:false,
									xtype:'multifield',
									labelStyle:'color:#FF0000',
									id:'ppd_mancode',
									name:'ppd_mancode',
									value:btn.oldValue,
									logic:'ppd_man',
									secondname:'ppd_man'
								}],
								buttonAlign: 'center',
								buttons: [{
									text: '确 认',
									iconCls: 'x-button-icon-save',
									cls: 'x-btn-gray',
									style:'margin-right:20px;',
									formBind: true,//form.isValid() == false时,按钮disabled
									handler: function(b) {
										var value = b.ownerCt.ownerCt.getValues();						
										var oldValue = btn.oldValue.split(';');										
										if(oldValue[0]!=value['ppd_mancode']||oldValue[1]!=value['ppd_man']){																	
											value = Ext.encode(value);
											Ext.Ajax.request({
												url : basePath + 'plm/request/changeResponsible.action',
												params: {caller:caller,id:btn.ppd_id,newman:value},
												method : 'post',
												callback : function(options,success,response){
													var res = new Ext.decode(response.responseText);
													if(res.success){
														window.location.reload();
														showMessage('变更成功','变更责任人成功!');
													} else if(res.exceptionInfo){					
														showError(res.exceptionInfo);
													} 
												}
											})
										}
										b.ownerCt.ownerCt.ownerCt.close();
									}
								},{
									text: '取 消',
									iconCls: 'x-button-icon-close',
									cls: 'x-btn-gray',
									style:'margin-left:20px;',
									handler: function(b) {
										b.ownerCt.ownerCt.ownerCt.close();
									}
								}]
							}]
							
						});
						win.show();
				}
			},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('pp_id').value);
				}
			},
			'erpUpdateButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('pp_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					var grid = Ext.getCmp('grid'), items = grid.store.data.items;
					var bool = true;
					// 计划完成日期不能小于计划开始日期
					Ext.each(items, function(item) {
						if (item.dirty
								&& item.data[grid.necessaryField] != null
								&& item.data[grid.necessaryField] != "") {
							var ppdate = item.data['ppd_end'];
							if (Ext.Date.format(ppdate, 'Y-m-d') < Ext.Date.format(
									item.data['ppd_start'], 'Y-m-d')) {
								bool = false;
								showError('明细表第' + item.data['ppd_detno']
										+ '行的计划完成日期小于计划开始日期');
								return;
							} else if (Ext.Date.format(item.data['ppd_start'], 'Y-m-d') < Ext.Date
									.format(new Date(), 'Y-m-d')) {		
								bool = false;
								showError('明细表第' + item.data['ppd_detno']
										+ '行的计划开始日期小于当前日期');
								return;
							}
						}
					});
					if (bool) {
						this.FormUtil.onUpdate(this);
					}
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('pp_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('pp_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('pp_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('pp_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('pp_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('pp_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('pp_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('pp_id').value);
				}
			},
			'erpTurnProject' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('pp_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					warnMsg("确定要转立项吗?", function(b){
    					if(b == 'yes'){
							var form = me.getForm(btn);
							me.turnProject(form);
    					}
					});
				}
			}
		})
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	},
	openInvoice : function(e, el, obj) {
		var f = obj.scope, form = f.ownerCt, i = form.down('#pp_prcode');
		if (i && i.value) {
			url = 'jsps/plm/request/require.jsp?formCondition=pr_codeIS'+ i.value;
			openUrl(url);
		}
	},
	turnProject:function(form){
		var me =this;
		var id = Ext.getCmp(form.keyField).getValue();
		var t = Ext.getCmp('pp_prjtitle');
		var title ='';
		if(t){
			title = t.getValue();
			if(!title||title==''){
				Ext.Msg.alert('警告','转立项请填上项目标题！');
				return;
			}
		}
		var url = form.turnProjectUrl;
		if(url.indexOf('caller=') == -1){
			url = url + "?caller=" + caller;
		}
		me.FormUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + url,
			params: {id:id,title:title},
			method : 'post',
			callback : function(options,success,response){
				me.FormUtil.getActiveTab().setLoading(false);
				var res = new Ext.decode(response.responseText);
				if(res.success){
					window.location.reload();
					showMessage('转立项成功',res.msg);
				} else if(res.exceptionInfo){					
					showError(res.exceptionInfo);
				} 
			}
		})
	}
});