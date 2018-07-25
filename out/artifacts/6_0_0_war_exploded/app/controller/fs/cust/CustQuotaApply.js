Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.CustQuotaApply', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.cust.CustQuotaApply', 'core.grid.Panel2','core.toolbar.Toolbar', 'core.form.MultiField',
			'core.button.TurnProject','core.button.Save', 'core.button.Add','core.button.Submit', 'core.button.Upload',
			'core.button.ResAudit','core.button.Audit','core.button.Close','core.button.Delete','core.button.Update',
			'core.button.DeleteDetail','core.button.ResSubmit', 'core.button.Export', 'core.button.InfoPerfect','core.button.Sync',
			'core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn', 'core.form.StatusField','core.form.FileField',
			'core.button.CopyAll','core.button.ResetSync', 'core.button.RefreshSync','core.form.MultiField','core.button.PrintByCondition','core.button.FormsDoc'],
	init : function() {
		var me = this;
		this.control({
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('CustQuotaApply', '核心客户保理额度申请', 'jsps/fs/cust/custQuotaApply.jsp');
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
			'erpSyncButton':{
    			afterrender: function(btn){
					var status = Ext.getCmp('ca_statuscode');
					if(status && status.value != 'AUDITED' && status.value != 'BANNED' && status.value != 'DISABLE'){
						btn.hide();
					}
				}
    		},
			'erpInfoPerfectButton':{
				click : function(btn) {
					var caid = Ext.getCmp('ca_id').value, custname = Ext.getCmp('ca_custname').value;
					var readOnly = 1;
					var status = Ext.getCmp('ca_statuscode');
					if (status && status.value == 'ENTERING') {
						readOnly = 0;
					}
					me.FormUtil.onAdd('HXInfoPerfect'+caid, custname+'额度调查报告', 'jsps/fs/cust/hxInfoPerfect.jsp?caid='+caid+'&readOnly='+readOnly+'&custname='+custname);
				}
        	},
        	'field[name=ca_type]': {
    			beforerender : function(f) {
    				var ca_pcucode = Ext.getCmp('ca_pcucode');
    				if(ca_pcucode){
    					if(Ext.isEmpty(f.value) || f.value == '一级额度'){
    						ca_pcucode.setReadOnly(true);
    					} else {
    						ca_pcucode.setReadOnly(false);
    					}
    				}
				},
				change : function(f) {
					var ca_pcucode = Ext.getCmp('ca_pcucode');
    				if(ca_pcucode){
    					if(Ext.isEmpty(f.value) || f.value == '一级额度'){
    						ca_pcucode.setReadOnly(true);
    					} else {
    						ca_pcucode.setReadOnly(false);
    					}
    				}
				}
    		}
		})
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});