Ext.define('erp.view.scm.sale.CustomerRelive', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 50%',
				saveUrl : 'scm/sale/saveCustomerRelive.action',
				deleteUrl : 'scm/sale/deleteCustomerRelive.action',
				updateUrl : 'scm/sale/updateCustomerRelive.action',
				auditUrl : 'scm/sale/auditCustomerRelive.action',
				submitUrl : 'scm/sale/submitCustomerRelive.action',
				resSubmitUrl : 'scm/sale/resSubmitCustomerRelive.action',
				getIdUrl : 'common/getId.action?seq=CUSTOMERRELIVE_SEQ',
				printUrl : 'scm/sale/printCustomerRelive.action',
				keyField : 'cr_id',
				codeField : 'cr_code',
				statuscodeField : 'cr_statuscode'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 50%',
				detno : 'crd_detno',
				necessaryField : 'crd_custcode',
				keyField : 'crd_id',
				mainField : 'crd_crid',
				allowExtraButtons:true
			} ]
		});
		me.callParent(arguments);
	}
});