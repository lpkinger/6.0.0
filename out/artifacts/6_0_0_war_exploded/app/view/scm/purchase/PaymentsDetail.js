Ext.define('erp.view.scm.purchase.PaymentsDetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 28%',
				saveUrl: 'scm/purchase/savePaymentsDetail.action',
				deleteUrl: 'scm/purchase/deletePaymentsDetail.action',
				updateUrl: 'scm/purchase/updatePaymentsDetail.action',
				auditUrl: 'scm/purchase/auditPaymentsDetail.action',
				resAuditUrl: 'scm/purchase/resAuditPaymentsDetail.action',
				submitUrl: 'scm/purchase/submitPaymentsDetail.action',
				resSubmitUrl: 'scm/purchase/resSubmitPaymentsDetail.action',
				bannedUrl: 'scm/purchase/bannedPaymentsDetail.action',
				resBannedUrl: 'scm/purchase/resBannedPaymentsDetail.action',
				getIdUrl: 'common/getId.action?seq=PAYMENTS_SEQ',
				keyField: 'pa_id',
				codeField: 'pa_code',
				statusField: 'pa_auditstatuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 72%', 
				detno: 'pad_detno',
				keyField: 'pad_id',
				mainField: 'pad_paid',
				necessaryField: 'pad_subpaid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});