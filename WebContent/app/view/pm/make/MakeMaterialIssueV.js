Ext.define('erp.view.pm.make.MakeMaterialIssueV',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpGridPanel6',
					anchor: '100% 32%',
					caller: 'Make!issue',
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
							text: '生成领料单',
							id: 'create',
							width: 95,
					    	iconCls: 'x-button-icon-add',
					    	style: {
					    		marginLeft: '10px'
					        },
					    	cls: 'x-btn-gray'
						},/*{
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
						},*/{
							text: '生成拨出单',
							id: 'createbc',
							width: 95,
					    	iconCls: 'x-button-icon-add',
					    	style: {
					    		marginLeft: '10px'
					        },
					    	cls: 'x-btn-gray',
				 			hidden:true	
						},'->',{ 
							xtype: 'checkbox',
							id: 'ifnullwhman',
							boxLabel: '包含空',
							labelAlign: 'right',
							checked: true
						},{
					    	xtype: 'dbfindtrigger',
						    hideLabel:true,
						    emptyText:'仓管员',
							width:100,
							name:'pr_whmancode',
							id: 'pr_whmancode',
							value: ''  							
						},{ 
							xtype: 'checkbox',
							id: 'ifnulllocation',
							boxLabel: '包含空',
							labelAlign: 'right',
							checked: true
						},{
					    	xtype: 'textfield',
						    hideLabel:true,
						    emptyText:'储位',
							width:80,
							name:'pr_location',
							id: 'pr_location',
							value: ''						
						},{
					    	xtype: 'dbfindtrigger',
						    hideLabel:true,
						    emptyText:'中心',
							width:100,
							name:'wccode',
							id: 'wccode',
							value: '', 
							triggerName:'wc_code',
							dbCaller:'WorkCenter',
							listeners:{
								aftertrigger:function(t, d){
									t.ownerCt.down('textfield[name=wccode]').setValue(d.get('wc_code')); 
								}
							}
						},{
					    	xtype: 'dbfindtrigger',
						    emptyText:'工序编号',
							width:100,
							name:'st_code',
							id: 'st_code',
							value: '', 
							hidden:true,
							dbCaller:'Step',
							triggerName:'st_code'
						},{
							margin: '3 0 0 0',
							xtype: 'checkbox',
							id: 'set',
							boxLabel: '按套数发料',
							labelAlign: 'right',
							checked: true
						},{
							xtype: 'checkbox',
							id: 'whcode',
							boxLabel: '按仓库分组',
							labelAlign: 'right',
							checked: true
						}]
					}
				},{
					xtype: 'erpEditorColumnGridPanel',
					caller: 'MakeMaterial!issue',
					condition: '1=2',
					anchor: '100% 68%',
					version:'4.2',
					ifOnlyShowUserFactoryWh:false,
					plugins: [Ext.create('erp.view.core.plugin.ProdOnhand'),
					          Ext.create('Ext.grid.plugin.CellEditing', {
					              clicksToEdit: 1
					          }), Ext.create('erp.view.core.plugin.CopyPasteMenu')]
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});