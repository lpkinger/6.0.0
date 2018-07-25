Ext.QuickTips.init();
Ext.define('erp.controller.fs.loaded.LoadedPlanSet', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.loaded.LoadedPlanSet', 'core.grid.Panel2','core.toolbar.Toolbar','core.button.Add',
			'core.button.Save','core.button.Update','core.button.Close','core.button.Delete','core.trigger.DbfindTrigger',
			'core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn'],
	init : function() {
		var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd(caller, '逾期贷后方案设置', 'jsps/fs/loaded/loadedPlanSet.jsp');
    			}
        	},
			'erpSaveButton': {
    			click: function(btn){
					this.FormUtil.beforeSave(this);				
    			}
        	},
        	'erpUpdateButton': {
    			click: function(btn){
					this.FormUtil.onUpdate(this);				
    			}
        	},
        	'erpDeleteButton': {
    			click: function(btn){
					this.FormUtil.onDelete(Ext.getCmp('ps_id'));				
    			}
        	},
        	'dbfindtrigger[name=psd_caller]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				t.dbBaseCondition = "UPPER(fo_table)='FSLOADEDPLANTABLE'";
    			}
    		}
		})
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	}
});