Ext.define('erp.view.fa.arp.ApkindArp',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'apkindArpViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpGridPanel4',
					anchor: '100% 100%', 
					necessaryField: 'ak_code',
					keyField: 'ak_id',

				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});