Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.CreditInformation', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'fa.fp.CreditInformation','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.form.FileField',
		'core.button.Add','core.button.Save','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
		'core.button.Upload','core.button.Update','core.button.Delete','core.button.Close','core.trigger.TextAreaTrigger',
		'core.trigger.DbfindTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.form.MonthDateField'
	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ci_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addCreditInformation', '新增贷款公司资料维护', 'jsps/fa/fp/CreditInformation.jsp');
    			}
    		},
    		'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			}
    	});
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});
