Ext.QuickTips.init();
Ext.define('erp.controller.fs.loaded.GuaranteeCheck', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.loaded.GuaranteeCheck', 'core.grid.Panel2','core.toolbar.Toolbar',
			'core.button.Save', 'core.button.Upload','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger',
			'core.form.YnField', 'core.grid.YnColumn','core.trigger.AddDbfindTrigger','core.button.Modify'],
	init : function() {
		var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
			'erpSaveButton': {
				afterrender:function(btn){
					var status = Ext.getCmp('li_statuscode');
					if(status&&status.value!='ENTERING'){
						btn.hide();
					}
				},
    			click: function(btn){
					this.FormUtil.onUpdate(this);				
    			}
        	}
		})
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	}
});