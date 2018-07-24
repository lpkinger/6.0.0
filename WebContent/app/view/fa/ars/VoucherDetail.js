Ext.define('erp.view.fa.ars.VoucherDetail',{ 
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
					anchor: '100% 30%',
					saveUrl: 'fa/ars/saveVoucherDetail.action',
					deleteUrl: 'fa/ars/deleteVoucherDetail.action',
					updateUrl: 'fa/ars/updateVoucherDetail.action',
					auditUrl: 'fa/ars/auditVoucherDetail.action',
					resAuditUrl: 'fa/ars/resAuditVoucherDetail.action',
					submitUrl: 'fa/ars/submitVoucherDetail.action',
					resSubmitUrl: 'fa/ars/resSubmitVoucherDetail.action',
					getIdUrl: 'common/getId.action?seq=VOUCHER_SEQ',
					keyField: 'vo_id',
					codeField: 'vo_code',
					statusField: 'vo_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					detno: 'vd_detno',
					necessaryField: 'vd_yearmonth',
					keyField: 'vd_id',
					mainField: 'vd_void'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});