Ext.define('erp.view.pm.make.MakeMaterialGive',{ 
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
					caller: 'Make!Give',
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
						        text: '补料主表',
						        listeners: {
						        	click: function(m){
						        		var grid = Ext.getCmp('grid');
							    		grid.BaseUtil.exportexcel(grid);
						        	}
						        }
					        },{
					        	iconCls: 'main-msg',
						        text: '补料从表',
						        listeners: {
						        	click: function(m){
						        		var grid = Ext.getCmp('editorColumnGridPanel');
							    		grid.BaseUtil.exportexcel(grid);
						        	}
						        }
					        }]
					    },{
							text: '生成补料单',
							id: 'create',
							width: 95,
					    	iconCls: 'x-button-icon-add',
					    	style: {
					    		marginLeft: '10px'
					        },
					    	cls: 'x-btn-gray'
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
					    },'->',{
							xtype: 'combo',
							fieldLabel:'大类',
							id: 'filterByPrKind',
							width:150,
							labelWidth:40,
							selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录  		
						    displayField: 'PK_NAME',
						    valueField: 'PK_NAME',
						    defaultListConfig:{//取消loading的Mask
			                     loadMask: false
			                },
						    store: Ext.create('Ext.data.Store', {
							    fields: ['PK_NAME'],
							    proxy: {
						             type: 'ajax',
								     url : basePath + 'scm/product/getPrKind.action',			
								     extraParams:{tablename:"productkind",fields:"pk_name", condition:"pk_level=1"},
								     reader: {
								          type: 'json',
								          root: 'data'
								     },
								     headers: {
						                 'Content-Type': 'application/json;charset=utf-8'
						             }		                   
						           },
						       listeners:{    
						            load : function(store, records, options ){        
						                store.insert(0,{"PK_NAME": "全部"});    
						            }    
					           } 
							}),
						    value:'',
						    editable:false,
						    hidden:true					
					    }, { 
							xtype: 'checkbox',
							id: 'ifnulllocation',
							boxLabel: '包含空',
							labelAlign: 'right',
							checked: true
						},{
					    	xtype: 'textfield',
						    hideLabel:true,
						    emptyText:'储位',
							width:100,
							name:'pr_location',
							id: 'pr_location',
							value: ''							
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
							xtype: 'checkbox',
							id: 'whcode',
							boxLabel: '按仓库分组',
							labelAlign: 'right',
							checked: true
						}]
					}
				},{
					xtype: 'erpEditorColumnGridPanel',
					caller: 'MakeMaterial!Give',
					condition: '1=2',
					anchor: '100% 68%',
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