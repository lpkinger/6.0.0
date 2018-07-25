Ext.define('erp.view.scm.sale.CustomerCredit', {
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
				auditUrl : 'scm/sale/auditCustomerCredit.action',
				printUrl : 'common/printCommon.action?caller=' + caller,
				resAuditUrl : 'common/resAuditCommon.action?caller=' + caller,
				submitUrl : 'common/submitCommon.action?caller=' + caller,
				resSubmitUrl : 'common/resSubmitCommon.action?caller=' + caller,
				getIdUrl : 'common/getId.action?seq=CustomerCredit_SEQ',
				keyField : 'cuc_id',
				statusField : 'cuc_status',
				statuscodeField : 'cuc_statuscode'
			} ]
		});
		me.callParent(arguments);
	}
});