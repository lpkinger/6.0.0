Ext.define('erp.view.scm.sale.Payments', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [{
				xtype : 'erpFormPanel',
				anchor : '100% 30%',
				saveUrl : 'scm/sale/savePayments.action',
				deleteUrl : 'scm/sale/deletePayments.action',
				updateUrl : 'scm/sale/updatePayments.action',
				auditUrl : 'scm/sale/auditPayments.action',
				resAuditUrl : 'scm/sale/resAuditPayments.action',
				submitUrl : 'scm/sale/submitPayments.action',
				resSubmitUrl : 'scm/sale/resSubmitPayments.action',
				bannedUrl : 'scm/sale/bannedPayments.action',
				resBannedUrl : 'scm/sale/resBannedPayments.action',
				getIdUrl : 'common/getId.action?seq=PAYMENTS_SEQ',
				keyField : 'pa_id',
				codeField : 'pa_code',
				statusField : 'pa_auditstatuscode'
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