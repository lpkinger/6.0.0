Ext.define('erp.view.oa.publicAdmin.book.bookManage.BookManage',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'north',         
	    	  xtype:'erpBookFormPanel',  
	    	  anchor: '100% 25%'
	    	  
	    },{
	    	  region: 'south',         
	    	  xtype:'erpDatalistGridPanel',  
	    	  anchor: '100% 75%',
         	  selModel: Ext.create('Ext.selection.CheckboxModel',{
	    	  }),
	    	  tbar:[{
	    			id: 'vastdelete',
	    	    	iconCls: 'group-delete',
	    	    	cls: 'x-btn-gray',
	    			text: $I18N.common.button.erpDeleteButton
	    	    }]
	    }]
		});
		me.callParent(arguments); 
	}
});