Ext.define('erp.view.oa.publicAdmin.book.returnManage.ReturnApp',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'north',         
	    	  xtype:'erpReturnFormPanel',  
	    	  anchor: '100% 25%'
	    	  
	    },{
	    	  region: 'south',         
	    	  xtype:'erpDatalistGridPanel',  
	    	  anchor: '100% 75%',
        	  selModel: Ext.create('Ext.selection.CheckboxModel',{
	    	  }),
	    	  tbar:[{
	    			id: 'return',
	    			iconCls: 'x-button-icon-close',
	    	    	cls: 'x-btn-gray',
	    			text: "归还"
	    	    },{
	    			id: 'renew',
	    			iconCls: 'x-button-icon-close',
	    	    	cls: 'x-btn-gray',
	    			text: "续借"
	    	    }]
	    }]
		});
		me.callParent(arguments); 
	}
});