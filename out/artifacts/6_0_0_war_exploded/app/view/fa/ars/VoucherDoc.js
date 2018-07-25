Ext.define('erp.view.fa.ars.VoucherDoc',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'makeViewport', 
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
					getIdUrl: 'common/getId.action?seq=VOUCHER_SEQ',
					keyField: 'vo_id',
					codeField: 'vo_code',
					statusField: 'vo_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'vd_detno',
					necessaryField: 'vd_doc',
					keyField: 'vd_id',
					mainField: 'vd_voucherid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});