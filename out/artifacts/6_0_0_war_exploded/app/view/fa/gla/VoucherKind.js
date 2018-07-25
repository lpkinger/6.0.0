Ext.define('erp.view.fa.gla.VoucherKind',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'voucherKindViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'fa/ars/saveVoucherKind.action',
					deleteUrl: 'fa/ars/deleteVoucherKind.action',
					updateUrl: 'fa/ars/updateVoucherKind.action',
					auditUrl: 'fa/ars/auditVoucherKind.action',
					resAuditUrl: 'fa/ars/resAuditVoucherKind.action',
					submitUrl: 'fa/ars/submitVoucherKind.action',
					resSubmitUrl: 'fa/ars/resSubmitVoucherKind.action',
					getIdUrl: 'common/getId.action?seq=VoucherKind_SEQ',
					keyField: 'vk_id',
				/*	codeField: 'abb_code',*/
					/*statusField: ''*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});