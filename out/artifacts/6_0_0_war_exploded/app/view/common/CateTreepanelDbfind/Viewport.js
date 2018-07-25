Ext.define('erp.view.common.CateTreepanelDbfind.Viewport',{ 
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
	    	  items : [{xtype: 'cateStrDbfindTree'}]
	    }/*,{
	    	  region: 'south',         
	    	  xtype: "panel",  
	    	  anchor: '100% 5%',
	    	  layout : 'fit',
	    	  items : [{xtype: 'erpMultiDbfindToolbar'}]
	    }*/]
		});
		me.callParent(arguments); 
	}
});