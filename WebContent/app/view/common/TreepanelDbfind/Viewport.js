Ext.define('erp.view.common.TreepanelDbfind.Viewport',{ 
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
	    	  items : [{xtype: 'hrOrgStrDbfindTree'}]
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