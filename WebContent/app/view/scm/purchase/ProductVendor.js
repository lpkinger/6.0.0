Ext.define('erp.view.scm.purchase.ProductVendor',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%',
				updateUrl: 'scm/purchase/updateProductVendor.action',
				getIdUrl: 'common/getId.action?seq=PRODUCT_SEQ',
				keyField: 'pr_id',
				codeField: 'pr_code'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 80%', 
				detno: 'pv_detno',
				necessaryField: 'pv_vendid',
				keyField: 'pv_id',
				mainField: 'pv_prodid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});