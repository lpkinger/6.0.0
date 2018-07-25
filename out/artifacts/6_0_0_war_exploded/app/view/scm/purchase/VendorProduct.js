Ext.define('erp.view.scm.purchase.VendorProduct',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%',
				updateUrl: 'scm/purchase/updateVendorProduct.action',
				getIdUrl: 'common/getId.action?seq=VENDOR_SEQ',
				keyField: 've_id',
				codeField: 've_code'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 80%', 
				detno: 'pv_detno',
				necessaryField: 'pv_prodid',
				keyField: 'pv_id',
				mainField: 'pv_vendid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});