Ext.define('erp.view.oa.persontask.myAgenda.agQuery.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'north',         
	    	  xtype:'erpAgendaQueryFormPanel',  
	    	  anchor: '100% 30%'
	    },{
	    	  region: 'south',         
	    	  xtype:'erpDatalistGridPanel', 
	    	  filterCondition: "ag_arrange_id=" + em_uu + " OR ag_executor_id like '%" + em_uu +"%'",
	    	  anchor: '100% 70%'	    	  
	    }]
		});
		me.callParent(arguments); 
	}
});