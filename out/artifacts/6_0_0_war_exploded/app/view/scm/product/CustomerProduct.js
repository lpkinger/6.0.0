Ext.define('erp.view.scm.product.CustomerProduct', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype: 'erpFormPanel',
				anchor: '100% 15%',
				saveUrl: 'scm/product/saveCustProd.action'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 85%'
			} ]
		});
		me.callParent(arguments);
	}
});