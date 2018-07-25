Ext.define('erp.view.oa.knowledge.KnowledgeSearch',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'south',         
	    	  xtype:'erpSearchGridPanel',  
	    	  anchor: '100% 100%',
	    }]
		});
		me.callParent(arguments); 
	}
});