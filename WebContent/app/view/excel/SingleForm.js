Ext.define('erp.view.excel.SingleForm',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpExcelFormPanel', 
					anchor: '100% 100%',
				}]
			}] 
		}); 
		me.callParent(arguments);
	} 
});