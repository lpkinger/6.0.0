Ext.define('erp.view.scm.product.datalist.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'north',         
	    	  xtype: "panel",  
	    	  anchor: '100% 100%',
	    	  layout : 'fit',
	    	  items : [{
	    		  xtype:'erpDatalistGridPanel'
	    	  }]
	    }]
		});
		me.callParent(arguments); 
	}
});