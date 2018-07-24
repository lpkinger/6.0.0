Ext.define('erp.view.common.messageCenter.TaskCenter',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', //fit
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				region:'north',
				xtype:'erpTaskCenterFormPanel',
				width:'100%'
			},{
				region:'center',
				xtype:'erpTaskCenterGridPanel',
			}]
		}); 
		me.callParent(arguments); 
	} 
});