Ext.define('erp.view.common.bench.Scene',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				region:'north',
				xtype:'erpSceneFormPanel'
			},{
				region:'center',
				xtype:'erpSceneGridPanel',
				selectObject:new Object()
			}]
		}); 
		me.callParent(arguments); 
	} 
});