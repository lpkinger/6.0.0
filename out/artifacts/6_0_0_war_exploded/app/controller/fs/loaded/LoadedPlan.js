Ext.QuickTips.init();
Ext.define('erp.controller.fs.loaded.LoadedPlan', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.loaded.LoadedPlan','core.button.Save', 'core.button.Add','core.button.Submit', 
			'core.button.Upload','core.button.ResAudit','core.button.Audit','core.button.Close',
			'core.button.Update','core.button.ResSubmit','core.button.Export','core.button.PrintByCondition',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn',
			'core.form.StatusField','core.form.FileField','core.form.MultiField','core.trigger.AddDbfindTrigger',
			'core.trigger.MultiDbfindTrigger', 'core.form.SeparNumber','core.button.FormsDoc'],
	init : function() {
		var me = this;
		this.control({
			'erpFormPanel': {
				afterload:function(form){
					var title = getUrlParam('title');
					if(title){
						form.setTitle(title);
					}
					var attach = Ext.getCmp('pt_attach');
					if(attach){
						attach.setReadOnly(true);
					}
				}
			},
			'erpSaveButton': {
    			click: function(btn){
    				var args = '{"pt_pid":'+parent.pid+',"pt_psid":'+getUrlParam('psid')+'}';
					this.beforeSave(args);				
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
					var status = Ext.getCmp('pt_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('pt_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('pt_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('pt_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('pt_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('pt_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('pt_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('pt_id').value);
				}
			}
		})
	},
	/**
	 * 保存之前的判断
	 * @param arg 额外参数
	 */
	beforeSave: function(arg){
		var mm = this.FormUtil;
		var form = Ext.getCmp('form');
		if(! mm.checkForm()){
			return;
		}
		if(form.keyField){
			if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
				mm.getSeqId(form);
			}
		}
		mm.onSave([], arg);
	}
});