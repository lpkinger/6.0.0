Ext.define('erp.view.fa.arp.vmQuery.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'south',         
	    	  xtype:'erpVmQueryGridPanel',  
	    	  anchor: '100% 100%'
	      }]
		});
		me.callParent(arguments); 
	}
});