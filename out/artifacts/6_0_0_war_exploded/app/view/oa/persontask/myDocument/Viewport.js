Ext.define('erp.view.oa.persontask.myDocument.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'south',         
	    	  xtype:'erpDatalistGridPanel', 
//	    	  filterCondition: "in_leader_id='" + em_code + "'",
	    	  anchor: '100% 100%',
	    	  tbar:[{
	    	    	id: 'sod',
	    	    	iconCls: 'group-unread',
	    			text: "查看部门发文",
	    			cls: 'x-btn-gray'
	    	    },'-',{
	    	    	id: 'all',
	    	    	iconCls: 'group-unread',
	    			text: "查看所有发文",
	    			cls: 'x-btn-gray'
	    	    },'-',{
	    	    	id: 'in',
	    	    	iconCls: 'group-unread',
	    			text: "查看内部请示",
	    			cls: 'x-btn-gray'
	    	    }]
	    }]
		});
		me.callParent(arguments); 
	}
});