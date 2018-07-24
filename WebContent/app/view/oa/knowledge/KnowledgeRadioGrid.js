Ext.define('erp.view.oa.knowledge.KnowledgeRadioGrid',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'center',         
	    	  xtype: "erpKnowledgeGridPanel",
	    	  selModel: Ext.create('Ext.selection.CheckboxModel',{
	    	  
	    	  }),
	    	  anchor: '100% 100%',
	    	  layout : 'fit',
	    	  items : [{xtype: 'erpEditorColumnGridPanel'}]
	    }]
		});
		me.callParent(arguments); 
	}
});