Ext.define('erp.view.oa.flow.flowCenter.viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				region:'north',
				xtype:'erpFlowCenterFormPanel',
				width:'100%'
			},{
				region:'center',
				xtype:'erpFlowCenterGridPanel'
			}]
		}); 
		me.callParent(arguments);
	}
});