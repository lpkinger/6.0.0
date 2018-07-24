Ext.define('erp.view.oa.officialDocument.receiveODManagement.query.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'north',         
	    	  xtype:'erpRODQueryFormPanel',  
	    	  anchor: '100% 30%'
	    },{
	    	  region: 'south',         
	    	  xtype:'erpRODQueryGridPanel',  
	    	  anchor: '100% 70%'
	    }]
		});
		me.callParent(arguments); 
	}
});