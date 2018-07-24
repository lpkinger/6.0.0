Ext.define('erp.view.scm.sale.customerUnit', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 20%',
				updateUrl : 'scm/sale/updateCustomerUnit.action',
				getIdUrl : 'common/getId.action?seq=CUSTOMER_SEQ',
				keyField : 'cu_id',
				codeField : 'cu_code',
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 80%',
				keyField : 'cs_id',
				mainField : 'cs_cuid',
				detno : 'cs_detno',
				autoSetSequence : true,
				necessaryField : 'cs_name'
			} ]
		});
		me.callParent(arguments);
	}
});