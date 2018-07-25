Ext.define('erp.view.scm.purchase.VendorContact',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				updateUrl: 'scm/purchase/updateVendorContact.action',
				getIdUrl: 'common/getId.action?seq=VENDOR_SEQ',
				keyField: 've_id',
				codeField: 've_code',
				statusField: 've_auditstatuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});