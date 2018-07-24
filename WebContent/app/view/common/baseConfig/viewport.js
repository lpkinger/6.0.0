Ext.define('erp.view.common.baseConfig.viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
		    	  xtype:'baseConfigForm',  
		    	  anchor: '100% 100%'
			}]
		});
		me.callParent(arguments); 
	}
});