Ext.define('erp.view.common.deallist.Viewport', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				region : 'north',
				xtype : 'erpBatchDealFormPanel',
				anchor : '100% 15%',
				_noc:1
			}, {
				region : 'south',
				//_noc : 1,
				xtype : 'erpDatalistGridPanel',
				noSpecialQuery:true,
				anchor : '100% 85%',
				autoQuery : false
			} ]
		});
		me.callParent(arguments);
	}
});