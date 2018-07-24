Ext.QuickTips.init();
Ext.define('erp.controller.hr.employee.Department', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.employee.Department','core.form.Panel','core.button.Sync',
    		'core.button.Add','core.button.Save','core.button.Close',
    			'core.button.Update','core.button.Delete','core.form.YnField',
    			'core.button.Submit','core.button.Audit','core.button.ResAudit',
				'core.button.ResSubmit','core.button.Banned','core.button.ResBanned',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSyncButton': {
    			afterrender: function(btn){
    				btn.autoClearCache = true;
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('dp_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addDepartment', '新增岗位资料', 'jsps/hr/employee/department.jsp');
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dp_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('dp_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('dp_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('dp_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dp_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('dp_id').value);
				}
			},
			'erpBannedButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dp_statuscode');
					if(status && status.value != 'AUDITED' ){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onBanned(Ext.getCmp('dp_id').value);
    			}
    		},
    		'erpResBannedButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('dp_statuscode');
					if(status && status.value != 'DISABLE'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResBanned(Ext.getCmp('dp_id').value);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});