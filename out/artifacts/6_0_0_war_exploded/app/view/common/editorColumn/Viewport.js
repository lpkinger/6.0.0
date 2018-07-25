Ext.define('erp.view.common.editorColumn.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'center',         
	    	  xtype: "panel",  
	    	  anchor: '100% 100%',
	    	  layout : 'fit',
	    	  items : [{xtype: 'erpEditorColumnGridPanel'}]
	    }]
		});
		me.callParent(arguments); 
	}
});