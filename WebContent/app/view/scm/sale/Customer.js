Ext.define('erp.view.scm.sale.Customer', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 100%',
				saveUrl : 'scm/sale/saveCustomer.action',
				updateUrl : 'scm/sale/updateCustomer.action',
				getIdUrl : 'common/getId.action?seq=CUSTOMER_SEQ',
				keyField : 'cu_id',
				codeField : 'cu_code'
			} ]
		});
		me.callParent(arguments);
	}
});