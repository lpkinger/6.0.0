Ext.QuickTips.init();
Ext.define('erp.controller.fs.buss.ReimbursementApply', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.buss.ReimbursementApply', 'core.grid.Panel2','core.toolbar.Toolbar',
			'core.button.Save', 'core.button.Add','core.button.Submit', 'core.button.Upload','core.button.ResAudit',
			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
			'core.button.Export',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn',
			'core.form.StatusField','core.form.FileField','core.form.MultiField', 'core.trigger.MultiDbfindTrigger', 
			'core.form.SeparNumber','core.button.TurnBankRegister'],
	init : function() {
		var me = this;
		this.control({
			'erpFormPanel' : {
    			afterload : function(form) {
    				this.hidecolumns(form.down('#ra_kind'));
				}
    		},
    		'combo[name=ra_kind]': {
    			beforerender: function(field){
					if(Ext.getCmp('ra_code')&&Ext.getCmp('ra_code').value){
						field.readOnly=true;
					}
				},
    			delay: 200,
    			change: function(m){
					this.hidecolumns(m);
				}
    		},
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('ReimbursementApply', '还款申请', 'jsps/fs/buss/reimbursementApply.jsp');
    			}
        	},
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
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('ra_id').value);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					me.FormUtil.onUpdate(this);
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ra_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('ra_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ra_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('ra_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ra_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('ra_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ra_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('ra_id').value);
				}
			}
		})
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	hidecolumns:function(m){
		if(!Ext.isEmpty(m.getValue())) {
			var form = m.ownerCt;
			if(m.value == '出账单'){
				form.down('#ra_odcode').hide();
				form.down('#ra_aacode').show();
			} else if(m.value == '逾期单'){
				form.down('#ra_odcode').show();
				form.down('#ra_aacode').hide();
			}
		}
	}
});