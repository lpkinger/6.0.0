Ext.define('erp.view.oa.officialDocument.sod.query.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'north',         
	    	  xtype:'erpSODQueryFormPanel',  
	    	  anchor: '100% 30%'
	    },{
	    	  region: 'south',         
	    	  xtype:'erpDatalistGridPanel',  
	    	  anchor: '100% 70%',
//	    	  selModel: Ext.create('Ext.selection.CheckboxModel',{
//	    	  })
	    }]
		});
		me.callParent(arguments); 
	}
});