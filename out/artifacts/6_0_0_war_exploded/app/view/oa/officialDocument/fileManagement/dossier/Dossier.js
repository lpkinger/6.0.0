Ext.define('erp.view.oa.officialDocument.fileManagement.dossier.Dossier',{ 
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
					region:'west'
				},{
				    xtype:'erpDatalistGridPanel',
				    region:'center',
				    selModel: Ext.create('Ext.selection.CheckboxModel',{
			    	}),
			    	tbar:[{
		    	    	iconCls: 'group-delete',
		    	    	id: 'delete',
		    			text: $I18N.common.button.erpDeleteButton
		    	    }, '-' , {
		    	    	iconCls: 'x-button-icon-add',
		    	    	id: 'add',
		    			text: '添 加'
		    	    }, '-' , {
		    	    	iconCls: 'x-button-icon-submit',
		    	    	id: 'update',
		    			text: $I18N.common.button.erpUpdateButton
		    	    }, '-' , {
		    	    	iconCls: 'x-button-icon-add',
		    	    	id: 'cj',
		    			text: '拆 卷'
		    	    }, '-' , {
		    	    	iconCls: 'x-button-icon-add',
		    	    	id: 'fj',
		    			text: '封 卷'
		    	    }]			    	  
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});