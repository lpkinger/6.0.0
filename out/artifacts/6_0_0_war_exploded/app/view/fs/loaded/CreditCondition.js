Ext.define('erp.view.fs.loaded.CreditCondition',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit',
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype : 'erpGridPanel2',
				plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				onExport: function(caller, type, condition){
					this.BaseUtil.createExcel(caller, type, condition,'授信业务状况'+ Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
				},
				keyField : 'cd_id',
				mainField : 'cd_liid'
			}]
		}); 
		this.callParent(arguments); 
	}
});