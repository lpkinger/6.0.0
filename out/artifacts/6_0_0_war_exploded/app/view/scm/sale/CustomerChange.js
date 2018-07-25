Ext.define('erp.view.scm.sale.CustomerChange', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 100%',
				saveUrl : 'scm/sale/saveCustomerChange.action',
				deleteUrl : 'scm/sale/deleteCustomerChange.action',
				updateUrl : 'scm/sale/updateCustomerChange.action',
				auditUrl : 'scm/sale/auditCustomerChange.action',
				resAuditUrl : 'scm/sale/resAuditCustomerChange.action',
				submitUrl : 'scm/sale/submitCustomerChange.action',
				resSubmitUrl : 'scm/sale/resSubmitCustomerChange.action',
				getIdUrl : 'common/getId.action?seq=CustomerChange_SEQ',
				keyField : 'cc_id',
				codeField : 'cc_code',
				statusField : 'cc_statuscode'
			} ]
		});
		me.callParent(arguments);
	}
});