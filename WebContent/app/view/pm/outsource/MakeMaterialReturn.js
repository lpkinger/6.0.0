Ext.define('erp.view.pm.outsource.MakeMaterialReturn',{ 
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
					caller: 'Make!OS!return',
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
							xtype: 'combo',
							fieldLabel:'供应类型',
							id: 'prsupplytype',
							width:150,
							labelWidth:75,
							store: Ext.create('Ext.data.Store', {
							   fields: ['display', 'value'],
							   data : [{"display": '全部', "value": ""},
							           {"display": '推式', "value": "pr_supplytype='PUSH'"},
							           {"display": '拉式', "value": "pr_supplytype='PULL'"},
							           {"display": '虚拟件', "value": "pr_supplytype='VIRTUAL'"},
							           {"display": '无', "value": "nvl(pr_supplytype,' ')=''"}]
						   }),
						   displayField: 'display',
						   valueField: 'value',
						   queryMode: 'local',
						   value:'',
						   editable:false
						   ,hidden:true						
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
						        text: '退料主表',
						        listeners: {
						        	click: function(m){
						        		var grid = Ext.getCmp('grid');
							    		grid.BaseUtil.exportexcel(grid);
						        	}
						        }
					        },{
					        	iconCls: 'main-msg',
						        text: '退料从表',
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
						},*/{
							text: '生成退料单',
							id: 'retreat',
							width: 95,
					    	iconCls: 'x-button-icon-add',
					    	style: {
					    		marginLeft: '10px'
					        },
					    	cls: 'x-btn-gray'
						},'->',{
							xtype: 'checkbox',
							id: 'showouttoint',
							boxLabel: '只显示水口料',
							labelAlign: 'right',
							checked: false,
							hidden:true			
						},{
							xtype: 'checkbox',
							boxLabel: '按仓库分组',
							id: 'whcode',
							labelAlign: 'right',
							checked: true
						} ,{
							id:'allowChangeAfterCom',
							xtype:'checkbox',
							boxLabel: '允许完工退料',
							checked: false,
							hidden:true	
						}]
					}
				},{
					xtype: 'erpEditorColumnGridPanel',
					caller: 'MakeMaterial!OS!return',
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