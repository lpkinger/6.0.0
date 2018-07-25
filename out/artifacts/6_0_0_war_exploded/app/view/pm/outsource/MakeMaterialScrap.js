Ext.define('erp.view.pm.outsource.MakeMaterialScrap',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpGridPanel5',
					anchor: '100% 32%',
					caller: 'Make!OS!Scrap',
					necessaryField: 'ma_code',
					bbar: {
						xtype: 'toolbar',
						items: [{
							xtype: 'tool',
							type: 'restore',
							tooltip: '最大化',
							listeners:{
								click: function(btn){
									window.open(window.location.href);
								}
							}
						},'->',{
							name: 'query',
							id: 'query',
							text: $I18N.common.button.erpQueryButton,
							iconCls: 'x-button-icon-query',
					    	cls: 'x-btn-gray'
						},{							
							xtype: 'combo',
							fieldLabel:'集团采购料',
							id: 'groupPurs',
							width:150,
							labelWidth:75,
							store: Ext.create('Ext.data.Store', {
							   fields: ['display', 'value'],
							   data : [{"display": '是', "value": 'pr_isgrouppurc<>0'},
							           {"display": '否', "value": 'NVL(pr_isgrouppurc,0)=0'},
							           {"display": '全部', "value": ''}]
						   }),
						   displayField: 'display',
						   valueField: 'value',
						   queryMode: 'local',
						   value:'',
						   editable:false,
						   hidden:true											
						},{
					    	name: 'export',
							text: $I18N.common.button.erpExportButton,
							iconCls: 'x-button-icon-excel',
					    	cls: 'x-btn-gray',
					    	style: {
					    		marginLeft: '10px'
					        },
					        menu: [{
					        	iconCls: 'main-msg',
						        text: '发料主表',
						        listeners: {
						        	click: function(m){
						        		var grid = Ext.getCmp('grid');
							    		grid.BaseUtil.exportexcel(grid);
						        	}
						        }
					        },{
					        	iconCls: 'main-msg',
						        text: '发料从表',
						        listeners: {
						        	click: function(m){
						        		var grid = Ext.getCmp('editorColumnGridPanel');
							    		grid.BaseUtil.exportexcel(grid);
						        	}
						        }
					        }]
					    },{
					    	name: 'close',
							text: $I18N.common.button.erpCloseButton,
							iconCls: 'x-button-icon-close',
					    	cls: 'x-btn-gray',
					    	style: {
					    		marginLeft: '10px'
					        },
					    	handler: function(btn){
					    		var main = parent.Ext.getCmp("content-panel"); 
								if(main){
									main.getActiveTab().close();
								} else {
									window.close();
								}
					    	}
					    },/*{
							text: '打印备料单',
							id: 'print',
							width: 95,
					    	iconCls: 'x-button-icon-print',
					    	style: {
					    		marginLeft: '10px'
					        },
					    	cls: 'x-btn-gray'
						},*/
					    {
							id:'allowChangeAfterCom',
							xtype:'checkbox',
							boxLabel: '允许完工退料',
							checked: false,
							hidden:true	
						},{
							text: '生成报废单',
							id: 'retreat',
							width: 95,
					    	iconCls: 'x-button-icon-add',
					    	style: {
					    		marginLeft: '10px'
					        },
					    	cls: 'x-btn-gray'
						},'->']
					}
				},{
					xtype: 'erpEditorColumnGridPanel',
					caller: 'MakeMaterial!OS!Scrap',
					condition: '1=2',
					anchor: '100% 68%',
					ifOnlyShowUserFactoryWh:false,
					pluginConfig: [Ext.create('erp.view.core.plugin.ProdOnhand')]
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});