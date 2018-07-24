Ext.define('erp.view.co.cost.DefaultCategory',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'defaultCategoryViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpGridPanel4',
					anchor: '100% 100%'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});