Ext.define('erp.view.common.bench.Bench',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				region:'north',
				xtype:'erpBenchFormPanel'
			},{
				region:'center',
				xtype:'panel',
				layout:'card',
				id:'businesses',
				activeItem: 0
			}]
		}); 
		me.callParent(arguments); 
	} 
});