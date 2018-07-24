Ext.define('erp.view.scm.purchase.VendContact',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'scm/purchase/saveVendContact.action',
					deleteUrl: 'common/deleteCommon.action?caller='+caller,
					updateUrl: 'scm/purchase/updateVendContact.action',
					getIdUrl: 'common/getId.action?seq=VendorCONTACT_SEQ',
					keyField: 'vc_id',
					//codeField: 'ct_code',
//					statusField: 'ch_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});