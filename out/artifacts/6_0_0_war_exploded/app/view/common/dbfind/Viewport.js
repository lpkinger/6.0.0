Ext.define('erp.view.common.dbfind.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'north',         
	    	  xtype: "panel",  
	    	  anchor: '100% 100%',
	    	  layout : 'fit',
	    	  items : [{xtype: 'erpDbfindGridPanel'}]
	    }]
		});
		me.callParent(arguments); 
	}
});