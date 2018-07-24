Ext.define('erp.view.oa.knowledge.Recknowledge',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'north',         
	    	  xtype:'erpBatchDealFormPanel',
	    	  anchor: '100% 20%'
	    },{
	    	  region: 'south',         
	    	  xtype:'erpDatalistGridPanel',  
	    	  anchor: '100% 80%',
	    	  selModel: Ext.create('Ext.selection.CheckboxModel',{
	    	  })
	    }]
		});
		me.callParent(arguments); 
	}
});