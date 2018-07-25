Ext.define('erp.view.common.queryDetail.Viewport', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				region : 'north',
				xtype : 'erpQueryFormPanel',
				anchor : '100% 20%'
			}, {
				region : 'south',
				_noc : 1,
				xtype : 'erpDatalistGridPanel',
				noSpecialQuery:true,
				anchor : '100% 80%',
				autoQuery : false
			} ]
		});
		me.callParent(arguments);
	}
});