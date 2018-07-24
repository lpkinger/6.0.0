Ext.define('erp.view.fa.gs.BankReconciliation',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 24%'
			},{
				xtype : 'grid',
				anchor: '100% 38%',
				title: '银行对账单',
				columnLines : true,
				id : 'grid1',
				plugins: [Ext.create('erp.view.core.grid.HeaderFilter', {
					remoteFilter: false
				}),Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToEdit: 1
				}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				listeners : {
					afterrender: function(grid){
						grid.plugins[1].on('afteredit',function(e){
	    					grid.selModel.countAmount();
	    				});
					}
				},
				selModel: Ext.create('Ext.selection.CheckboxModel',{
			    	checkOnly : true,
					ignoreRightMouseSelection : false,
				    getEditor: function(){
				    	return null;
				    },
				    listeners : {
						selectionchange : function(selModel, selected, options) {
							var me = this, grid = selModel.view.ownerCt;
			            	me.countAmount();
						}
					},
					countAmount: function(record){
			        	var me = this;
		            	var grid = Ext.getCmp('grid1'), form = Ext.getCmp('form');
		            	var items = grid.selModel.selected.items;
		            	var debit = 0, credit = 0;
		            	Ext.each(items,function(item,index){
		            		debit = debit + Number(item.data['ACD_DEBIT']);
		            		credit = credit + Number(item.data['ACD_CREDIT']);
	                	});
		               	Ext.getCmp('accredit').setValue(credit.toFixed(2));
		               	Ext.getCmp('acdebit').setValue(debit.toFixed(2));
			        }
				}),
				store: Ext.create('Ext.data.Store', {
			        fields:[{
			        	name: 'ACD_DATE',
			        	type: 'number'
			        },{
			        	name: 'ACD_EXPLANATION',
			        	type: 'string'
			        },{
			        	name: 'ACD_DEBIT',
			        	format: '0.00',
			        	type: 'number'
			        },{
			        	name: 'ACD_CREDIT',
			        	format: '0.00',
			        	type: 'number'
			        },{
			        	name: 'ACD_CATECODE',
			        	type: 'string'
			        },{
			        	name: 'CA_DESCRIPTION',
			        	type: 'string'
			        },{
			        	name: 'ACD_CHECKSTATUS',
			        	type: 'string'
			        },{
			        	name: 'ACD_ID',
			        	type: 'number'
			        }],
			        data: []
			    }),
				columns : [ {
					text : '日期',
					cls : 'x-grid-header-1',
					dataIndex: 'ACD_DATE',
					width: 120,
					filter: {
	    				xtype : 'datefield'
	    			},
	    			renderer: function(val) {return Ext.Date.format(new Date(val), 'Y-m-d');}
				}, {
					text : '摘要',
					cls : 'x-grid-header-1',
					dataIndex: 'ACD_EXPLANATION',
					width: 250,
					filter: {
	    				xtype : 'textfield'
	    			}
				}, {
					text : '借方',
					cls : 'x-grid-header-1',
					dataIndex: 'ACD_DEBIT',
					width: 120,
					filter: {
	    				xtype : 'textfield'
	    			},
	    			xtype:'numbercolumn',
	    			editor:{
	    				  xtype:'numberfield',
	    				  format:'0',
	    				  hideTrigger: true
	    			}
				}, {
					text : '贷方',
					cls : 'x-grid-header-1',
					dataIndex: 'ACD_CREDIT',
					width: 120,
					filter: {
	    				xtype : 'textfield'
	    			},
	    			xtype:'numbercolumn',
	    			editor:{
	    				  xtype:'numberfield',
	    				  format:'0',
	    				  hideTrigger: true
	    			}
				},{
					text : '账户编号',
					cls : 'x-grid-header-1',
					dataIndex: 'ACD_CATECODE',
					width: 100,
					filter: {
	    				xtype : 'textfield'
	    			}
				},{
					text : '账户名称',
					cls : 'x-grid-header-1',
					dataIndex: 'CA_DESCRIPTION',
					width: 300,
					filter: {
	    				xtype : 'textfield'
	    			}
				}, {
					text : '对账状态',
					cls : 'x-grid-header-1',
					dataIndex: 'ACD_CHECKSTATUS',
					width: 80,
					filter: {
	    				xtype : 'textfield'
	    			}
				}, {
					text : 'ID',
					cls : 'x-grid-header-1',
					dataIndex: 'ACD_ID',
					width: 0,
					filter: {
	    				xtype : 'textfield'
	    			}
				}]
    		},{
				xtype : 'grid',
				anchor: '100% 38%',
				title: '银行日记账',
				columnLines : true,
				id : 'grid2',
				plugins: [Ext.create('erp.view.core.grid.HeaderFilter', {
					remoteFilter: false
				}),Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToEdit: 1
				}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				listeners : {
					afterrender: function(grid){
						grid.plugins[1].on('afteredit',function(e){
	    					grid.selModel.countAmount();
	    				});
					}
				},
				selModel: Ext.create('Ext.selection.CheckboxModel',{
			    	checkOnly : true,
					ignoreRightMouseSelection : false,
				    getEditor: function(){
				    	return null;
				    },
				    listeners : {
						selectionchange : function(selModel, selected, options) {
							var me = this, grid = selModel.view.ownerCt;
			            	me.countAmount();
						}
					},
					countAmount: function(record){
						var me = this;
		            	var grid = Ext.getCmp('grid2'), form = Ext.getCmp('form');
		            	var items = grid.selModel.selected.items;
		            	var debit = 0, credit = 0;
		            	Ext.each(items,function(item,index){
		            		debit = debit + Number(item.data['AR_DEPOSIT']);
		            		credit = credit + Number(item.data['AR_PAYMENT']);
	                	});
		               	Ext.getCmp('arcredit').setValue(credit.toFixed(2));
		               	Ext.getCmp('ardebit').setValue(debit.toFixed(2));
			        }
				}),
				store: Ext.create('Ext.data.Store', {
			        fields:[{
			        	name: 'AR_DATE',
			        	type: 'number'
			        },{
			        	name: 'AR_MEMO',
			        	type: 'string'
			        },{
			        	name: 'AR_DEPOSIT',
			        	format: '0.00',
			        	type: 'number'
			        },{
			        	name: 'AR_PAYMENT',
			        	format: '0.00',
			        	type: 'number'
			        },{
			        	name: 'AR_ACCOUNTCODE',
			        	type: 'string'
			        },{
			        	name: 'CA_DESCRIPTION',
			        	type: 'string'
			        },{
			        	name: 'AR_CHECKSTATUS',
			        	type: 'string'
			        },{
			        	name: 'AR_CODE',
			        	type: 'string'
			        },{
			        	name: 'AR_ID',
			        	type: 'number'
			        }],
			        data: []
			    }),
				columns : [ {
					text : '日期',
					cls : 'x-grid-header-1',
					dataIndex: 'AR_DATE',
					width: 120,
					filter: {
	    				xtype : 'datefield'
	    			},
	    			renderer: function(val) {return Ext.Date.format(new Date(val), 'Y-m-d');}
				}, {
					text : '摘要',
					cls : 'x-grid-header-1',
					dataIndex: 'AR_MEMO',
					width: 250,
					filter: {
	    				xtype : 'textfield'
	    			}
				}, {
					text : '借方',
					cls : 'x-grid-header-1',
					dataIndex: 'AR_DEPOSIT',
					width: 120,
					filter: {
	    				xtype : 'textfield'
	    			},
	    			xtype:'numbercolumn',
	    			editor:{
	    				  xtype:'numberfield',
	    				  format:'0',
	    				  hideTrigger: true
	    			}
				}, {
					text : '贷方',
					cls : 'x-grid-header-1',
					dataIndex: 'AR_PAYMENT',
					width: 120,
					filter: {
	    				xtype : 'textfield'
	    			},
	    			xtype:'numbercolumn',
	    			editor:{
	    				  xtype:'numberfield',
	    				  format:'0',
	    				  hideTrigger: true
	    			}
				},{
					text : '账户编号',
					cls : 'x-grid-header-1',
					dataIndex: 'AR_ACCOUNTCODE',
					width: 100,
					filter: {
	    				xtype : 'textfield'
	    			}
				},{
					text : '账户名称',
					cls : 'x-grid-header-1',
					dataIndex: 'CA_DESCRIPTION',
					width: 300,
					filter: {
	    				xtype : 'textfield'
	    			}
				}, {
					text : '对账状态',
					cls : 'x-grid-header-1',
					dataIndex: 'AR_CHECKSTATUS',
					width: 80,
					filter: {
	    				xtype : 'textfield'
	    			}
				}, {
					text : '银行登记单号',
					cls : 'x-grid-header-1',
					dataIndex: 'AR_CODE',
					width: 110,
					filter: {
	    				xtype : 'textfield'
	    			}
				}, {
					text : 'ID',
					cls : 'x-grid-header-1',
					dataIndex: 'AR_ID',
					width: 0,
					filter: {
	    				xtype : 'textfield'
	    			}
				}]
    		}]
		}); 
		me.callParent(arguments); 
	} 
});