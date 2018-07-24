Ext.define('erp.view.scm.sale.CustomerAddress', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 30%',
				updateUrl : 'scm/sale/updateCustomerAddress.action',
				getIdUrl : 'common/getId.action?seq=CUSTOMER_SEQ',
				keyField : 'cu_id',
				codeField : 'cu_code',
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 70%',
				keyField : 'ca_id',
				mainField : 'ca_cuid',
				detno : 'ca_detno',
				autoSetSequence : true,
				necessaryField : 'ca_address'
			} ]
		});
		me.callParent(arguments);
	}
});