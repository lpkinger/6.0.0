Ext.QuickTips.init();
Ext.define('erp.controller.fs.buss.FsPoundageIn', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.buss.FsPoundageIn', 'core.grid.Panel2','core.toolbar.Toolbar', 'core.form.SeparNumber',
			'core.button.Add','core.button.Save','core.button.Submit', 'core.button.Upload','core.button.ResAudit',
			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
			'core.button.Export','core.button.FormsDoc',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn',
			'core.form.StatusField','core.form.FileField','core.form.MultiField','core.trigger.AddDbfindTrigger'],
	init : function() {
		var me = this;
		this.control({
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('FsPoundageIn', '手续费入账单', 'jsps/fs/buss/fsPoundageIn.jsp');
    			}
        	},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('pi_id').value);
				}
			},
			'erpSaveButton' : {
				click : function(btn) {
					var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
					me.beforeUpdate(false);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					me.beforeUpdate(true);
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('pi_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('pi_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('pi_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('pi_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('pi_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('pi_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('pi_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('pi_id').value);
				}
			},
			'field[name=pi_handamount]': {
				beforerender:function(f){
					f.labelStyle = 'color:blue';
				}
			}
		})
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeUpdate: function(isUpdate){
		var me = this;
		if(Ext.isEmpty(Ext.getCmp('pi_custcode').value)){
        	showError('请选择客户编号！') ;  
			return; 
		}
    	if(isUpdate){
    		me.FormUtil.onUpdate(this);
    	} else {
    		me.FormUtil.beforeSave(this);		
    	}
	}
});