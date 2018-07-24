Ext.define('erp.view.oa.knowledge.KnowledgeRank',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'north',         
	    	  xtype:'erpKnowledgeRankFormPanel',
	    	  style:'background:#CDCDB4',
			  bodyStyle: 'background:#CDCDB4; padding:0px;',
	    	  anchor: '100% 13%'
	    },{
	    	  region: 'south',         
	    	  xtype:'erpDatalistGridPanel',  
	    	  anchor: '100% 87%',
	    }]
		});
		me.callParent(arguments); 
	}
});