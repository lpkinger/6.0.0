Ext.define('erp.view.oa.officialDocument.fileManagement.noFile.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
		    	  region: 'north',         
		    	  xtype:'erpNoFileFormPanel',  
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
		    	  },{
		    	    	iconCls: 'x-button-icon-print',
		    	    	text: $I18N.common.button.erpPrintButton,
		    			id: 'print'
		    	  },{
		    	    	iconCls: 'x-button-icon-add',
		    	    	id: 'file',
		    			text: $I18N.common.button.erpFileButton
		    	  },{
		    	    	iconCls: 'x-button-icon-submit',
		    	    	id: 'back',
		    			text: '退回流程'
		    	  }]
		    }]
		});
		me.callParent(arguments); 
	}
});