Ext.define('erp.view.vendbarcode.batchDelivery.Viewport', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : "erpBatchDeliveryFormPanel",
				anchor : '100% 25%',
			}, {
				xtype : "erpBatchDeliveryGridPanel",
				anchor : '100% 75%',
			} ]
		});
		me.callParent(arguments);
	}
});