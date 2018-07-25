Ext.define('erp.view.fa.arp.VendorArp',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fa/ars/saveVendorArp.action',
					deleteUrl: 'fa/ars/deleteVendorArp.action',
					updateUrl: 'fa/ars/updateVendorArp.action',
					bannedUrl: 'fa/ars/bannedVendorArp.action',
					resBannedUrl: 'fa/ars/resBannedVendorArp.action',
					getIdUrl: '',
					keyField: 've_id',
					codeField: 've_code'/*,
					statuscodeField: 'cr_statuscode'*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});