Ext.define('erp.view.oa.info.Viewports',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'north',         
	    	  xtype:'erpPagingFormPanels',  
	    	  anchor: '100% 20%',
	    	  _noc:1
	    },{
	    	  region: 'south',         
	    	  xtype:'erpDatalistGridPanel',  
	    	  anchor: '100% 80%',
	    	  _noc: 1,
	    	  selModel: Ext.create('Ext.selection.CheckboxModel',{
	    	  })
	    }]
		});
		me.callParent(arguments); 
	}
});