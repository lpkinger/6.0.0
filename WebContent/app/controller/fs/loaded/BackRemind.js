Ext.QuickTips.init();
Ext.define('erp.controller.fs.loaded.BackRemind', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.loaded.BackRemind', 'core.grid.Panel2','core.toolbar.Toolbar',
			'core.button.Save', 'core.button.Close','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger',
			'core.form.YnField', 'core.grid.YnColumn','core.trigger.AddDbfindTrigger'],
	init : function() {
		var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		}
		})
	},
	onGridItemClick: function(selModel, record){//grid行选择
		var readOnly = record.get('lrd_iscloseoff')=='是'?1:0;
		var type = record.get('lrd_type');
		this.FormUtil.onAdd('LoadedPlans'+record.get('lrd_id'), type, 'jsps/fs/loaded/loadedPlans.jsp?pCaller='+caller+'&pid='+record.get('lrd_id')+'&readOnly='+readOnly+'&type='+type);
	}
});