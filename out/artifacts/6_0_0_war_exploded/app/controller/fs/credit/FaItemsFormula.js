Ext.QuickTips.init();
Ext.define('erp.controller.fs.credit.FaItemsFormula', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.credit.FaItemsFormula', 'core.grid.Panel2','core.toolbar.Toolbar', 'core.form.MultiField',
			'core.button.Save', 'core.button.Add','core.button.Submit', 'core.button.Upload','core.button.ResAudit','core.button.Audit',
			'core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit', 'core.button.Export',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn', 'core.form.StatusField',
			'core.form.FileField','core.button.CopyAll','core.button.ResetSync', 'core.button.RefreshSync'],
	init : function() {
		var me = this;
		this.control({
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('FaItemsFormula', '财务项目计算公式', 'jsps/fs/credit/faItemsFormula.jsp');
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
					me.FormUtil.onDelete(Ext.getCmp('fif_id').value);
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
			}
		})
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});