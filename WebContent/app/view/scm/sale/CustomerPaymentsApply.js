Ext.define('erp.view.scm.sale.CustomerPaymentsApply', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 38%',
				saveUrl : 'scm/sale/saveCustomerPaymentsApply.action',
				deleteUrl : 'scm/sale/deleteCustomerPaymentsApply.action',
				updateUrl : 'scm/sale/updateCustomerPaymentsApply.action',
				getIdUrl : 'common/getId.action?seq=CustomerPaymentsApply_SEQ',
				auditUrl : 'scm/sale/auditCustomerPaymentsApply.action',
				resAuditUrl : 'scm/sale/resAuditCustomerPaymentsApply.action',
				submitUrl : 'scm/sale/submitCustomerPaymentsApply.action',
				resSubmitUrl : 'scm/sale/resSubmitCustomerPaymentsApply.action',
				keyField : 'ca_id',
				codeField : 'ca_code'
			// statusField: 'as_status'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 62%',
				detno : 'cad_detno',
				// necessaryField: 'cad_sellercode',
				keyField : 'cad_id',
				mainField : 'cad_caid'
			} ]
		});
		me.callParent(arguments);
	}
});