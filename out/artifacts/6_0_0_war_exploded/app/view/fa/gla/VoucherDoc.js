Ext.define('erp.view.fa.gla.VoucherDoc',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'voucherDocViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'fa/ars/saveVoucherDoc.action',
					deleteUrl: 'fa/ars/deleteVoucherDoc.action',
					updateUrl: 'fa/ars/updateVoucherDoc.action',
					auditUrl: 'fa/ars/auditVoucherDoc.action',
					resAuditUrl: 'fa/ars/resAuditVoucherDoc.action',
					submitUrl: 'fa/ars/submitVoucherDoc.action',
					resSubmitUrl: 'fa/ars/resSubmitVoucherDoc.action',
					getIdUrl: 'common/getId.action?seq=VoucherDoc_SEQ',
					keyField: 'cd_id',
				/*	codeField: 'abb_code',*/
					/*statusField: ''*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});