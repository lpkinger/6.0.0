Ext.define('erp.view.crm.chance.ShowFunnel.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  id:'panel',
	    	  xtype:'panel',
	    	  autoScroll : true,
	    	  anchor: '100% 50%'
	      },{
	    	  //region: 'south',         
	    	  xtype:'erpShowFunnelGridPanel',  
	    	  anchor: '100% 50%'
	      }]
		});
		me.callParent(arguments); 
	}
});