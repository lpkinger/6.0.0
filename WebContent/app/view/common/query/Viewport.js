Ext.define('erp.view.common.query.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'north',         
	    	  xtype:'erpQueryFormPanel',  
	    	  anchor: '100% 30%'
	    },{
	    	  region: 'south',         
	    	  xtype:'erpQueryGridPanel',  
	    	  anchor: '100% 70%'
	    }]
		});
		me.callParent(arguments); 
	}
});