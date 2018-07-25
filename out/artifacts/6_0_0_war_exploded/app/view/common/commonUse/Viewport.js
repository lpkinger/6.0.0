Ext.define('erp.view.common.commonUse.Viewport',{
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
		    	  xtype:'commonusegrid',  
		    	  anchor: '100% 100%'
			}]
		});
		me.callParent(arguments); 
	}
});