Ext.define('erp.view.fs.cust.CustomerPayTaxes', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 20%',
				updateUrl : 'fs/cust/updateCustomerPayTaxes.action',
				deleteUrl: 'fs/cust/deleteCustomerPayTaxes.action',
				submitUrl: 'fs/cust/submitCustomerPayTaxes.action',
				resSubmitUrl: 'fs/cust/resSubmitCustomerPayTaxes.action',
				auditUrl: 'fs/cust/auditCustomerPayTaxes.action',
				resAuditUrl: 'fs/cust/resAuditCustomerPayTaxes.action',
				getIdUrl : 'common/getId.action?seq=CUSTOMERPAYTAXES_SEQ',
				keyField : 'cu_id',
				codeField : 'cu_code'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 80%',
				detno : 'ct_detno',
				keyField : 'ct_id',
				mainField : 'ct_custid'
			} ]
		});
		me.callParent(arguments);
	}
});