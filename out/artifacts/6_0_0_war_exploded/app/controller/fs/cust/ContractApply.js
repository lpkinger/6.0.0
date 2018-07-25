Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.ContractApply', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.cust.ContractApply', 'core.grid.Panel2','core.toolbar.Toolbar',
			'core.button.Save', 'core.button.Add','core.button.Submit', 'core.button.Upload','core.button.ResAudit',
			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
			'core.button.Export',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn',
			'core.form.StatusField','core.form.FileField','core.form.MultiField'],
	init : function() {
		var me = this;
		this.control({
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('ContractApply', '业务申请', 'jsps/fs/cust/contractApply.jsp');
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
					me.FormUtil.onDelete(Ext.getCmp('ca_id').value);
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
					var status = Ext.getCmp('ca_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('ca_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ca_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('ca_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ca_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('ca_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ca_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('ca_id').value);
				}
			},
			//放款成数(%)
    		'field[name=ca_factoramount]': {
    			change: function(f) {
    				var a = f.ownerCt.down('#ca_saamount'), r = f.ownerCt.down('#ca_lendrate');
    				if(a.value != 0 && f.value != 0) {
    					var rate = Ext.Number.toFixed(f.value/a.value*100, 2);
    					if(r.value != rate)
    						r.setValue(rate);
    				}
    				if(a.value == 0 || f.value == 0){
    					r.setValue(1);
    				}
    			}
    		},
    		'field[name=ca_saamount]': {
    			change: function(f) {
    				var v = f.ownerCt.down('#ca_factoramount'), r = f.ownerCt.down('#ca_lendrate');
    				if(v.value != 0 && f.value != 0) {
    					var rate = Ext.Number.toFixed(v.value/f.value*100, 2);
    					if(r.value != rate)
    						r.setValue(rate);
    				}
    				if(v.value == 0 || f.value == 0){
    					r.setValue(1);
    				}
    			}
    		}
		})
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});