Ext.define('erp.view.scm.purchase.Payments',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'scm/purchase/savePayments.action',
				deleteUrl: 'scm/purchase/deletePayments.action',
				updateUrl: 'scm/purchase/updatePayments.action',
				auditUrl: 'scm/purchase/auditPayments.action',
				resAuditUrl: 'scm/purchase/resAuditPayments.action',
				submitUrl: 'scm/purchase/submitPayments.action',
				resSubmitUrl: 'scm/purchase/resSubmitPayments.action',
				bannedUrl: 'scm/purchase/bannedPayments.action',
				resBannedUrl: 'scm/purchase/resBannedPayments.action',
				getIdUrl: 'common/getId.action?seq=PAYMENTS_SEQ',
				keyField: 'pa_id',
				codeField: 'pa_code',
				statusField: 'pa_auditstatuscode'
			},{
				xtype : 'erpGridPanel2',
				anchor : '100% 70%',
				detno : 'pad_detno',
				keyField : 'pad_id',
				mainField : 'pad_paid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});