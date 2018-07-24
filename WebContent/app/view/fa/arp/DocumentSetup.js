Ext.define('erp.view.fa.arp.DocumentSetup',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'documentSetupArpViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpGridPanel4',
					anchor: '100% 100%', 
					keyField: 'ds_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});