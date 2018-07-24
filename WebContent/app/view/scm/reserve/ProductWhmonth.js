Ext.define('erp.view.scm.reserve.ProductWhmonth', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpBatchDealFormPanel',
				anchor : '100% 20%'
			}, {
				xtype : 'erpDatalistGridPanel',
				noSpecialQuery:true,
				caller : caller,
				anchor : '100% 80%',
				_noc : 1,
				autoQuery : false
			} ]
		});
		me.callParent(arguments);
	}
});