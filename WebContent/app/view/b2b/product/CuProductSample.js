Ext.define('erp.view.b2b.product.CuProductSample',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'CuProductSample', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					turnSampleUrl:'b2b/product/turnSendSample.action',
					keyField: 'cps_id',
					codeField: 'cps_code',
					statusField: 'cps_status',
					statuscodeField: 'cps_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});