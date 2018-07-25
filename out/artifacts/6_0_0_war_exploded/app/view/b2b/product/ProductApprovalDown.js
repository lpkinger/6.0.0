Ext.define('erp.view.b2b.product.ProductApprovalDown',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	autoScroll : true,
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor : '99% 98%',				
				keyField: 'pa_id', 
				codeField: 'pa_code'				
			}]
		}); 
		me.callParent(arguments); 
	}
});