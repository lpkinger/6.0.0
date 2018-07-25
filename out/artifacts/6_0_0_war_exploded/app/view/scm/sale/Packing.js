Ext.define('erp.view.scm.sale.Packing', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 35%',
				saveUrl : 'scm/sale/savePacking.action',
				deleteUrl : 'scm/sale/deletePacking.action',
				updateUrl : 'scm/sale/updatePacking.action',
				auditUrl : 'scm/sale/auditPacking.action',
				resAuditUrl : 'scm/sale/resAuditPacking.action',
				submitUrl : 'scm/sale/submitPacking.action',
				resSubmitUrl : 'scm/sale/resSubmitPacking.action',
				getIdUrl : 'common/getId.action?seq=PACKING_SEQ',
				keyField : 'pa_id',
				codeField : 'pa_code',
				statusField : 'pa_statuscode'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 65%',
				detno : 'pad_detno',
				necessaryField : 'pad_prodcode',
				keyField : 'pad_id',
				mainField : 'pad_paid'
			} ]
		});
		me.callParent(arguments);
	}
});