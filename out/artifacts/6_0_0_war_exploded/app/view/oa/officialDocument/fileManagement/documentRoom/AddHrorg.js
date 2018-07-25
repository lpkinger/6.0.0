Ext.define('erp.view.oa.officialDocument.fileManagement.documentRoom.AddHrorg',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
		    	  region: 'south',         
		    	  xtype:'erpDatalistGridPanel',  
		    	  anchor: '100% 80%',
		    	  selModel: Ext.create('Ext.selection.CheckboxModel',{
		    	  }),
		    	  tbar:[ {
		    	    	iconCls: 'x-button-icon-add',
		    	    	id: 'add',
		    			text: '确&nbsp;定'
		    	  },{
		    	    	iconCls: 'group-delete',
		    	    	id: 'cancel',
		    			text: '取&nbsp;消'
		    	  }]
		    }]
		});
		me.callParent(arguments); 
	}
});