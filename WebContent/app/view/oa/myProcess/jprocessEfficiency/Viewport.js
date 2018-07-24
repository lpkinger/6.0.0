Ext.define('erp.view.oa.myProcess.jprocessEfficiency.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'north',         
	    	  xtype:'erpJProcessEfficiencyFormPanel',  
	    	  anchor: '100% 30%'
	    },{
	    	  region: 'south',         
	    	  xtype:'erpJProcessEfficiencyGridPanel',  
	    	  anchor: '100% 70%'
	    }]
		});
		me.callParent(arguments); 
	}
});