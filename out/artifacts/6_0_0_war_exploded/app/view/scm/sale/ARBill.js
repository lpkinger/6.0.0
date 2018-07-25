Ext.define('erp.view.scm.sale.ARBill', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 50%',
				saveUrl : 'scm/sale/saveARBill.action',
				deleteUrl : 'scm/sale/deleteARBill.action',
				updateUrl : 'scm/sale/updateARBill.action',
				auditUrl : 'scm/sale/auditARBill.action',
				resAuditUrl : 'scm/sale/resAuditARBill.action',
				submitUrl : 'scm/sale/submitARBill.action',
				resSubmitUrl : 'scm/sale/resSubmitARBill.action',
				getIdUrl : 'common/getId.action?seq=APBILL_SEQ',
				keyField : 'ab_id',
				codeField : 'ab_code',
				statusField : 'ab_statuscode'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 50%',
				detno : 'abd_detno',
				necessaryField : 'abd_prodcode',
				keyField : 'abd_id',
				mainField : 'abd_abid'
			} ]
		});
		me.callParent(arguments);
	}
});