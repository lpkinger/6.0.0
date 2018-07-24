Ext.QuickTips.init();
Ext.define('erp.controller.oa.vehicle.teamInfo', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.vehicle.teamInfo','core.form.Panel','core.form.FileField','core.form.MultiField',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.AutoCodeTrigger',
    			'core.form.YnField','core.button.Scan'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
				afterrender: function(btn){
					var status = Ext.getCmp('ti_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('ti_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ti_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addTeamInfo', '新增车队信息', 'jsps/oa/vehicle/teamInfo.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ti_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('ti_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ti_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ti_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ti_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('ti_id').value);
				}
			},'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ti_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ti_id').value);
				}
			}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});