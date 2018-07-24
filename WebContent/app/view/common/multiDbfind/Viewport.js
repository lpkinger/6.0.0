Ext.define('erp.view.common.multiDbfind.Viewport', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : "erpMultiDbfindGridPanel",
				anchor : '100% 100%',
				bbar : {
					xtype : 'erpMultiDbfindToolbar',
					id : 'pagingtoolbar',
					displayInfo : true
				}
			}, {
				xtype : "erpResultDbfindGridPanel",//选中数据grid
				anchor : '100% 100%'
			}]
		});
		me.callParent(arguments);
	}
});