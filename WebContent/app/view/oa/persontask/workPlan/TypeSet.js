Ext.define('erp.view.oa.persontask.workPlan.TypeSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpDatalistGridPanel',
					anchor: '100% 100%',
					selModel: Ext.create('Ext.selection.CheckboxModel',{
			    	}),
			        tbar: [{
			        	iconCls: 'group-delete',
			        	id: 'delete',
			    		text: $I18N.common.button.erpDeleteButton
			        },'-',{
			        	iconCls: 'x-button-icon-add',
			        	id: 'add',
			    		text: '添加'
			        },'-',{
			        	iconCls: 'group-close',
			    		text: '修 改',
			    		id: 'update'
			        },'-',{
			        	iconCls: 'group-close',
			    		text: $I18N.common.button.erpCloseButton,
			    		handler: function(){
			    			parent.Ext.getCmp("content-panel").getActiveTab().close();
			    		}
			        },'->',{
			        	xtype: 'textfield',
			        	fieldLabel: '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b><font size="3">类型名称</font></b>',
			        	id: 'titlelike'
			        },{
			        	iconCls: 'x-button-icon-scan',
			        	text: '查询',
			    		id: 'search'
			        }],
				}],				
			}] 
		});
		me.callParent(arguments); 
	}
});