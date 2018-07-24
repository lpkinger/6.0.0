Ext.define('erp.view.scm.sale.PaymentsDetail', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 50%',
				saveUrl : 'scm/sale/savePaymentsDetail.action',
				deleteUrl : 'scm/sale/deletePaymentsDetail.action',
				updateUrl : 'scm/sale/updatePaymentsDetail.action',
				auditUrl : 'scm/sale/auditPaymentsDetail.action',
				resAuditUrl : 'scm/sale/resAuditPaymentsDetail.action',
				submitUrl : 'scm/sale/submitPaymentsDetail.action',
				resSubmitUrl : 'scm/sale/resSubmitPaymentsDetail.action',
				bannedUrl : 'scm/sale/bannedPaymentsDetail.action',
				resBannedUrl : 'scm/sale/resBannedPaymentsDetail.action',
				getIdUrl : 'common/getId.action?seq=PAYMENTS_SEQ',
				keyField : 'pa_id',
				codeField : 'pa_code',
				statusField : 'pa_auditstatuscode'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 50%',
				detno : 'pad_detno',
				keyField : 'pad_id',
				mainField : 'pad_paid',
				necessaryField : 'pad_subpaid'
			} ]
		});
		me.callParent(arguments);
	}
});