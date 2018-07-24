Ext.define('erp.view.oa.meeting.MeetingRoom',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {
			layout: 'anchor',
			items: [{
		    	  //region: 'north',         
		    	  xtype:'erpQueryFormPanel',  
		    	  anchor: '100% 20%'
		    },{
		    	  //region: 'south',         
		    	  xtype:'erpDatalistGridPanel',  
		    	  anchor: '100% 80%',
		    	  selModel: Ext.create('Ext.selection.CheckboxModel',{
		    	  }),
		    	  tbar:[ {
		    	    	iconCls: 'x-button-icon-add',
		    	    	id: 'add',
		    			text: '添&nbsp;加'
		    	  },{
		    	    	iconCls: 'group-delete',
		    	    	id: 'delete',
		    			text: $I18N.common.button.erpDeleteButton
		    	  }]
		    }]
		});
		me.callParent(arguments); 
	}
});