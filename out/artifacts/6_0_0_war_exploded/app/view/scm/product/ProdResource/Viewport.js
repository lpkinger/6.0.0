Ext.define('erp.view.scm.product.ProdResource.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'north',         
	    	  xtype:'erpQueryFormPanel',  
	    	  anchor: '100% 25%'
	    },{
	    	  region: 'south',         
	    	  xtype:'erpQueryGridPanel',  
	    	  anchor: '100% 75%'
	    }]
		});
		me.callParent(arguments); 
	}
});