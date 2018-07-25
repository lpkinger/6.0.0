Ext.define('erp.view.scm.sale.CustomerBase', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 100%',
				saveUrl : 'scm/sale/saveCustomerBase.action',
				deleteUrl : 'scm/sale/deleteCustomerBase.action',
				updateUrl : 'scm/sale/updateCustomerBase.action',
				auditUrl : 'scm/sale/auditCustomerBase.action',
				resAuditUrl : 'scm/sale/resAuditCustomerBase.action',
				submitUrl : 'scm/sale/submitCustomerBase.action',
				resSubmitUrl : 'scm/sale/resSubmitCustomerBase.action',
				bannedUrl : 'scm/sale/bannedCustomerBase.action',
				resBannedUrl : 'scm/sale/resBannedCustomerBase.action',
				getIdUrl : 'common/getId.action?seq=CUSTOMER_SEQ',
				keyField : 'cu_id',
				codeField : 'cu_code',
				statusField : 'cu_auditstatuscode'
			} ]
		});
		me.callParent(arguments);
	}
});