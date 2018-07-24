Ext.define('erp.view.plm.project.TaskInterceptor',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				region: 'center',         
				xtype: "panel",  
				anchor: '100% 100%',
				layout : 'fit',
				items : [{xtype: 'erpTaskPanel',codeField:'pc_code',keyField:'pc_id'}],
				dockedItems: [{
					xtype: 'toolbar',
	    		    dock: 'top',
	    		    style: 'padding:0 0 5px 0',
	    		    items: [{ 
	    		    	iconCls: 'tree-save',
						cls: 'x-btn-gray',
						text: $I18N.common.button.erpSaveButton,
						xtype: 'button',
						id:'save-btn',
						margin:'0 5 0 0'
		    		},{
		    			iconCls: 'x-button-icon-help',
				    	cls: 'x-btn-gray',
				    	id: 'checkRuleSql',
				    	text: $I18N.common.button.erpCheckRuleSqlButton
	    			},'->',{
	    				iconCls: 'tree-close',
						xtype: 'button',
						id:'delete-btn',
						text:$I18N.common.button.erpDeleteButton,
						cls: 'x-btn-gray',
						margin:'0 5 0 5'
				    }]
				}]
			}]
		});
		me.callParent(arguments); 
	}
});