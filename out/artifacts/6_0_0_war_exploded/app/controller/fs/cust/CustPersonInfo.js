Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.CustPersonInfo', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.cust.CustPersonInfo', 'core.form.MultiField','core.button.Save',
			'core.button.Add','core.button.Submit', 'core.button.Upload','core.button.ResAudit','core.button.Audit','core.button.Close',
			'core.button.Delete','core.button.Update','core.button.ResSubmit', 'core.button.Export',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.StatusField',
			'core.form.FileField','core.button.CopyAll','core.button.ResetSync', 'core.button.RefreshSync'],
	init : function() {
		var me = this;
		this.control({
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('CustPersonInfo', '客户个人信息', 'jsps/fs/cust/custPersonInfo.jsp');
    			}
        	},
			'erpSaveButton': {
    			click: function(btn){	
    				if(me.check()){
    					me.FormUtil.beforeSave();	
    				}
    			}
        	},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('cp_id').value);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					if(me.check()){
    					me.FormUtil.onUpdate();
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
					var status = Ext.getCmp('cp_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('cp_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cp_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('cp_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cp_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('cp_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cp_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('cp_id').value);
				}
			}
		})
	},
	check: function(){
		var cp_papertype = Ext.getCmp('cp_papertype').value,
			cp_papercode = Ext.getCmp('cp_papercode').value;
		if(!Ext.isEmpty(cp_papertype) && cp_papertype != '其他'){
			Ext.getCmp('cp_papercode').setValue(cp_papercode.toUpperCase());
			cp_papercode = Ext.getCmp('cp_papercode').value;
		}
		if(!Ext.isEmpty(cp_papertype) && cp_papertype == '居民身份证'){
			if(!Ext.isEmpty(cp_papercode) && cp_papercode.length != 18){
				showError('居民身份证号码应为18位！');
				return false;
			}
			var S = 0,check = ['1','0','X','9','8','7','6','5','4','3','2'];
			for(var i=0;i<17;i++){
				S += parseInt(cp_papercode.charAt(i))*(Math.pow(2,17-i)%11)
			}
			var t = S%11;
			if(cp_papercode.charAt(17)!=check[t]){
				showError('居民身份证号码有误！');
				return false;
			}
		}
		return true;
	}
});