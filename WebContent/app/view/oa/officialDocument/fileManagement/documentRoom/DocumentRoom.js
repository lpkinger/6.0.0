Ext.define('erp.view.oa.officialDocument.fileManagement.documentRoom.DocumentRoom',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'desk', 
				layout: 'border', 
				items: [{
					xtype:'erpDocumentRoomTreePanel',
					region:'west',
					tbar:[{
		    	    	iconCls: 'group-delete',
		    	    	id: 'deldr',
		    	    	disabled: true,
		    			text: '删 除'
		    	    }, '-', {
		    	    	iconCls: 'x-button-icon-add',
		    	    	id: 'adddr',
		    	    	disabled: false,
		    			text: '添 加'
		    	    }, '-', {
		    	    	iconCls: 'x-button-icon-add',
		    	    	id: 'updatedr',
		    	    	disabled: true,
		    			text: '修 改'
		    	    }]
				},{
				    xtype:'erpDatalistGridPanel',
				    region:'center',
				    selModel: Ext.create('Ext.selection.CheckboxModel',{
			    	}),
			    	tbar:[{
		    	    	iconCls: 'group-delete',
		    	    	id: 'delete',
		    			text: '删除部门'
		    	    }, '-', {
		    	    	iconCls: 'x-button-icon-add',
		    	    	id: 'add',
		    			text: '添加部门'
		    	    }]			    	  
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});