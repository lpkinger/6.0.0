Ext.define('erp.view.scm.sale.CreditChange', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 100%',
				saveUrl : 'scm/sale/saveCreditChange.action',
				deleteUrl : 'common/deleteCommon.action?caller=' + caller,
				updateUrl : 'scm/sale/updateCreditChange.action',
				auditUrl : 'scm/sale/auditCreditChange.action',
				printUrl : 'common/printCommon.action?caller=' + caller,
				resAuditUrl : 'common/resAuditCommon.action?caller=' + caller,
				submitUrl : 'common/submitCommon.action?caller=' + caller,
				resSubmitUrl : 'common/resSubmitCommon.action?caller=' + caller,
				getIdUrl : 'common/getId.action?seq=CREDITCHANGE_SEQ',
				keyField : 'cc_id',
				codeField : 'cc_code',
				statusField : 'cc_status',
				statuscodeField : 'cc_statuscode'
			} ]
		});
		me.callParent(arguments);
	}
});