Ext.define('erp.view.oa.flow.FlowList',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFlowListTree',
				anchor: '100% 100%'
			}]
		});
		me.callParent(arguments); 
	}
});