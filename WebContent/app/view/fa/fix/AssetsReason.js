Ext.define('erp.view.fa.fix.AssetsReason',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'assetsReasonViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpGridPanel4',
					anchor: '100% 100%', 
					necessaryField: 'ar_name',
					keyField: 'ar_id',

				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});