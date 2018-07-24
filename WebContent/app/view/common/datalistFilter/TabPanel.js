Ext.define('erp.view.common.datalistFilter.TabPanel',{ 
	extend: 'Ext.tab.Panel', 
	alias: 'widget.FilterTabPanel',
	cls:'tabpanel',
	items:[{
		xtype:'panel',
		title:'查询条件',
		id:'searchPanel',
		cls:'searchPanel',
		autoScroll:true,
		items:[{
			xtype:'panel',
			width:'100%',
			cls:'clearconditionpanel',
			items:[{
				xtype:'button',
				text:'清空条件',
				iconCls:'clearconditionspico',
				cls:'clearconditionsp',
				id:'clearconditionsp',
				width:90,
				listeners:{
					click:function(){
						var conpanel=this.ownerCt.ownerCt.down('form');
						conpanel.removeAll();	
						Ext.getCmp("saveAsButton").disable();
					}
				}
			}]
		},{
			xtype:'form',
			width:560,
			id:'conditionpanel'
		},{
			xtype:'panel',
			width:560,
			items:[{
				xtype:'button',
				text:'新增行',
				width:70,				
				iconCls:'newaddpanelico',
			    cls:'newaddpanel',
			    id:'newaddpanel',
				handler:function(){
					var conpanel=this.ownerCt.ownerCt.down('form');
					conpanel.add({
						xtype:'concontainer',
						FieldStore:conpanel.FieldStore
					});	
					Ext.getCmp("saveAsButton").enable();
				}	
			}]
		}]
	},
	/*{
			xtype:'form',
			title:'高级查询',
			layout:'column',			
			items:[
					{						
						xtype: 'displayfield',
        				fieldLabel: '条件描述',
						columnWidth: 0.85
					},{
						xtype:'button',
						text:'清空条件',
						width:90,
						columnWidth: 0.15,
						id:"clearCondition",
						iconCls:'clearconditionspico',
						cls:'clearCondition',
					},{
						xtype:'textfield',
						id:'tfCondition',
						width:370,
						autoScroll:true,						
						columnWidth: 1
					},{
						xtype: 'displayfield',
        				fieldLabel: 'SQL语句',
        				columnWidth: 1
					},{
						xtype:'textareafield',
						id:'tfSql',
						width:370,
						autoScroll:true,
						columnWidth: 1

			}],
			listeners:{
				activate:function(){
					var me=this;
					me.doLayout(); 
				}
			},
		}*/],


		initComponent : function(){
			this.callParent(arguments); 

		}
});