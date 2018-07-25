Ext.define('erp.view.fa.arp.ApLienReasonArp',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'apLienReasonArpViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpGridPanel4',
					anchor: '100% 100%', 
					necessaryField: 'ar_code',
					keyField: 'ar_id',

				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});