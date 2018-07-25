Ext.define('erp.view.common.reportsQuery.viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
		    	  xtype:'reportsGrid',  
		    	  anchor: '100% 100%'
			}]
		});
		me.callParent(arguments); 
	}
});