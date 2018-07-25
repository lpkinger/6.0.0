Ext.define('erp.view.common.batchDeal.Viewport', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : "erpBatchDealFormPanel",
				anchor : '100% 35%',
			}, {
				xtype : "erpBatchDealGridPanel",
				anchor : '100% 65%',
			} ]
		});
		me.callParent(arguments);
	}
});