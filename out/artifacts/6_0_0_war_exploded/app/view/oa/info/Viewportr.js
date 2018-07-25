Ext.define('erp.view.oa.info.Viewportr',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'north',         
	    	  xtype:'erpPagingFormPanelr',  
	    	  anchor: '100% 20%'
	    },{
	    	  region: 'south',         
	    	  xtype:'erpDatalistGridPanel',  
	    	  cls: 'custom',
	    	  anchor: '100% 80%',
	    	  _noc: 1,
	    	  selModel: Ext.create('Ext.selection.CheckboxModel',{
	    	  })
	    }]
		});
		me.callParent(arguments); 
	}
});