Ext.define('erp.view.b2b.product.VendorSendSample',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'VendorSendSample', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					keyField: 'vs_id',
					codeField: 'vs_code',
					statusField: 'vs_status',
					statuscodeField: 'vs_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});