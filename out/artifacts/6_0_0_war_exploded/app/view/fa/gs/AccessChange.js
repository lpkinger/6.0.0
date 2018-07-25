Ext.define('erp.view.fa.gs.AccessChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'accessChangeViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpGridPanel4',
					anchor: '100% 100%', 
					necessaryField: 'ac_code',
					keyField: 'ac_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});