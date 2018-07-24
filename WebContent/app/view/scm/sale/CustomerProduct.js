Ext.define('erp.view.scm.sale.CustomerProduct', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 20%',
				updateUrl : 'scm/sale/updateCustomerProduct.action',
				getIdUrl : 'common/getId.action?seq=CUSTOMER_SEQ',
				keyField : 'cu_id',
				codeField : 'cu_code'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 80%',
				detno : 'pc_detno',
				necessaryField : 'pc_prodcode',
				keyField : 'pc_id',
				mainField : 'pc_custid'
			} ]
		});
		me.callParent(arguments);
	}
});