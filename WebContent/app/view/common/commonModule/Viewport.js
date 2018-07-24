Ext.define('erp.view.common.commonModule.Viewport',{
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpTreePanel',
		    	region: 'west',
		    	width: '30%'
			}, {
				xtype: 'centerpanel',
				region: 'center'
			}]
		});
		me.callParent(arguments); 
	}
});