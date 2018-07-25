Ext.QuickTips.init();
Ext.define('erp.controller.oa.vehicle.Vehiclearchives', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	views : ['oa.vehicle.Vehiclearchives', 'core.form.Panel',
			'core.grid.Panel2', 'core.toolbar.Toolbar', 'core.button.Add',
			'core.button.Save', 'core.button.Close',
			'core.button.DeleteDetail', 'core.button.Submit',
			'core.button.ResAudit', 'core.button.Audit',
			'core.button.ResSubmit', 'core.button.Update',
			'core.button.Delete', 'core.form.YnField','core.form.FileField',
			'core.trigger.TextAreaTrigger', 'core.trigger.DbfindTrigger'],
	init : function() {
		var me = this;
		this.control({
					'field[name=va_isused]' : {
						afterrender : function(field) {
							if (caller == 'Vehiclearchives'
									&& Ext.getCmp('va_id').value!='') {	
								Ext.Ajax.request({
											url : basePath + 'oa/vehicle/checkVehiclearchives.action',
											Async : false,
											params : {
											caller:caller,
											id:Ext.getCmp('va_id').value
											},
											method : 'post',
											callback : function(options,
													success, response) {
												var rs = Ext.decode(response.responseText);
												if (rs.isused=='T') {
													field.setValue("使用中");
												}
											}

										})
							}
						}
					},
					'field[name=ma_tasktype]' : {
						afterrender : function(field) {
							if (field.value == 'OS' && caller == 'Make!Base') {
								window.location.href = window.location.href
										.replace('whoami=Make!Base',
												'whoami=Make');
							}
						}
					},
					'erpSaveButton' : {
						click : function(btn) {
							this.FormUtil.beforeSave(this);
						}
					},
					'erpCloseButton' : {
						click : function(btn) {
							this.FormUtil.beforeClose(this);
						}
					},
					'erpUpdateButton' : {
						click : function(btn) {
							this.FormUtil.onUpdate(this);
						}
					},
					'erpDeleteButton' : {
						afterrender : function(btn) {
							var status = Ext.getCmp('va_statuscode');
							if (status && status.value != 'ENTERING') {
								btn.hide();
							}
						},
						click : function(btn) {
							me.FormUtil.onDelete((Ext.getCmp('va_id').value));
						}
					},
					'erpAuditButton' : {
						afterrender : function(btn) {
							var status = Ext.getCmp('va_statuscode');
							if (status && status.value != 'COMMITED') {
								btn.hide();
							}
						},
						click : function(btn) {
							this.FormUtil.onAudit(Ext.getCmp('va_id').value);
						}
					},
					'erpResAuditButton' : {
						afterrender : function(btn) {
							var status = Ext.getCmp('va_statuscode');
							if (status && status.value != 'AUDITED') {
								btn.hide();
							}
						},
						click : function(btn) {
							this.FormUtil.onResAudit(Ext.getCmp('va_id').value);
						}
					},
					'erpSubmitButton' : {
						afterrender : function(btn) {
							var status = Ext.getCmp('va_statuscode');
							if (status && status.value != 'ENTERING') {
								btn.hide();
							}
						},
						click : function(btn) {
							this.FormUtil.onSubmit(Ext.getCmp('va_id').value);

						}
					},
					'erpResSubmitButton' : {
						afterrender : function(btn) {
							var status = Ext.getCmp('va_statuscode');
							if (status && status.value != 'COMMITED') {
								btn.hide();
							}
						},
						click : function(btn) {
							this.FormUtil
									.onResSubmit(Ext.getCmp('va_id').value);
						}
					},
					'erpAddButton' : {
						click : function() {
							me.FormUtil.onAdd('addVehiclearchives', '新增车辆档案',
									'jsps/oa/vehicle/vehiclearchives.jsp');
						}
					}
				});
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	}
});