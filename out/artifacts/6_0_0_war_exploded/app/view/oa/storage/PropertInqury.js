Ext.define('erp.view.oa.storage.PropertInqury',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
		    	  region: 'north',         
		    	  xtype:'erpBookFormPanel',  
		    	  anchor: '100% 35%'
		    	  
		    },{
	    	  region: 'south',         
	    	  xtype:'erpDatalistGridPanel',  
	    	  anchor: '100% 65%'
		    }]
		});
		me.callParent(arguments); 
	}
});