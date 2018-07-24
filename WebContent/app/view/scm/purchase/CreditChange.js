Ext.define('erp.view.scm.purchase.CreditChange', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 100%',
				saveUrl : 'common/saveCommon.action?caller=' + caller,
				deleteUrl : 'common/deleteCommon.action?caller=' + caller,
				updateUrl : 'common/updateCommon.action?caller=' + caller,
				auditUrl : 'scm/purchase/auditCreditChange.action',
				printUrl : 'common/printCommon.action?caller=' + caller,
				resAuditUrl : 'common/resAuditCommon.action?caller=' + caller,
				submitUrl : 'common/submitCommon.action?caller=' + caller,
				resSubmitUrl : 'common/resSubmitCommon.action?caller=' + caller,
				getIdUrl : 'common/getId.action?seq=VENDCREDITCHANGE_SEQ',
				keyField : 'vc_id',
				codeField : 'vc_code',
				statusField : 'vc_status',
				statuscodeField : 'vc_statuscode'
			} ]
		});
		me.callParent(arguments);
	}
});