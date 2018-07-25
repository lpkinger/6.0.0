Ext.define('erp.view.b2b.product.ProductSampleDown',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 90%',	
				turnUrl: 'b2b/product/turnCustSendSample.action',
				keyField: 'ps_id',
				codeField: 'ps_code',
				statusField: 'ps_status',
				statuscodeField: 'ps_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});