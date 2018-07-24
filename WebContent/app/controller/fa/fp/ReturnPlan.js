Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.ReturnPlan', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'fa.fp.ReturnPlan','core.form.Panel','core.grid.Panel2','core.form.MultiField','core.form.FileField','core.form.YnField','core.form.MonthDateField',
		'core.button.Add','core.button.Save','core.button.Close','core.button.Update','core.button.Delete','core.button.Submit','core.button.ResSubmit',
		'core.button.Audit','core.button.ResAudit','core.button.Upload','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.toolbar.Toolbar'
	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': {                
                itemclick: this.onGridItemClick
            },
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ccr_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
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
	},
	onGridItemClick: function(selModel, record){//grid行选择
    	this.gridLastSelected = record;    	
    	this.GridUtil.onGridItemClick(selModel, record);
    }
});
