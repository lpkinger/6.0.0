Ext.define('erp.view.scm.sale.CustomerPayments', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 20%',
				updateUrl : 'scm/sale/updateCustomerPayments.action',
				getIdUrl : 'common/getId.action?seq=CUSTOMER_SEQ',
				keyField : 'cu_id',
				codeField : 'cu_code',
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 80%',
				keyField : 'cp_id',
				mainField : 'cp_cuid',
				detno : 'cp_detno',
				autoSetSequence : true,
				necessaryField : 'cp_paymentcode'
			} ]
		});
		me.callParent(arguments);
	}
});