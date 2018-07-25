Ext.define('erp.view.common.messageCenter.JProcessCenter',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', //fit
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				region:'north',
				xtype:'erpJProcessCenterFormPanel',
				width:'100%'
			},{
				region:'center',
				xtype:'erpJProcessCenterGridPanel'
			}]
		}); 
		me.callParent(arguments); 
	} 
});