Ext.define('erp.view.oa.officialDocument.instruction.manage.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
		    	  region: 'north',         
		    	  xtype:'erpInstructionFormPanel',  
		    	  anchor: '100% 30%'
		    },{
		    	  region: 'south',         
		    	  xtype:'erpDatalistGridPanel',  
		    	  anchor: '100% 70%',
		    	  selModel: Ext.create('Ext.selection.CheckboxModel',{
		    	  }),
		    	  tbar:[ {
		    	    	iconCls: 'group-delete',
		    	    	id: 'delete',
		    			text: $I18N.common.button.erpDeleteButton
		    	  }]
		    }]
		});
		me.callParent(arguments); 
	}
});