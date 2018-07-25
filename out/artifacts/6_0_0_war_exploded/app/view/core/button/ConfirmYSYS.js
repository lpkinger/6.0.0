/**
 * 预收-应收明细确认
 */	
Ext.define('erp.view.core.button.ConfirmYSYS',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConfirmYSYSButton',
		//iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'confirmysys',
    	text: $I18N.common.button.erpConfirmYSYSButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 140,
		initComponent : function(){ 
			this.callParent(arguments); 
			this.BaseUtil = Ext.create('erp.util.BaseUtil');
		},
		listeners: {
	        click: function(m){
	        	var grid = Ext.getCmp('batchDealGridPanel'), record = grid.getSelectionModel().getLastSelected();
	        	if(record != null){
					Ext.getCmp('confirmysys').turn(record);
				} else {
					showMessage('提示', '请选择需要确认的明细!');
					return;
				}
	        }
	    },
		turn: function(record){
			var me = this, win = Ext.getCmp('win');
			me.BaseUtil.getSetting('sys', 'useBillOutAR', function(editable) {
				win = new Ext.window.Window({
		    		id : 'win',
				    height: "100%",
				    width: "80%",
				    maximizable : true,
					buttonAlign : 'center',
					layout : 'anchor',
				    items: [{
			    		xtype:'form',
			    		id: 'form',
			    		layout:'column',
			    		anchor : '100% 20%',
			    		frame:true,
			    		defaults:{
			    			margin: '5 15 0 5',
			    			xtype:'textfield',
			    			columnWidth:0.5
			    		},
			    		items:[{
			    			fieldLabel:'客户编号',
			    			value:record.data.cm_custcode,
			    			readOnly: true,
			    			fieldStyle:'background:#f0f0f0;border: 0px solid #8B8970;color:blue;',
			    			id:'cucode'
			    		},{
			    			fieldLabel:'客户名称'	,
			    			value:record.data.cu_name,
			    			fieldStyle:'background:#f0f0f0;border: 0px solid #8B8970;color:blue;',
			    			readOnly: true
			    		},{
			    			fieldLabel:'币别',
			    			value:record.data.cm_currency,
			    			fieldStyle:'background:#f0f0f0;border: 0px solid #8B8970;color:blue;',
			    			readOnly: true,
			    			id:'currency'
			    		},{
			    		    fieldLabel:'冲账金额',
			    		    xtype:'separnumberfield',
			    		    value:record.data.cm_prepaybalance,
			    		    readOnly: false,
			    		    id:'cm_prepaybalance'
			    		},{
			    		    fieldLabel:'预收金额',
			    		    fieldStyle:'background:#f0f0f0;border: 0px solid #8B8970;color:blue;',
			    		    value:'0',
			    		    readOnly: true,
			    		    id:'pramount'
			    		},{
			    		    fieldLabel:'发票金额',
			    		    fieldStyle:'background:#f0f0f0;border: 0px solid #8B8970;color:blue;',
			    		    value:'0',
			    		    readOnly: true,
			    		    id:'abamount'
			    		},{
			    		    fieldLabel:'VMID',
			    		    value:record.data.cm_id,
			    		    readOnly: true,
			    		    hidden:true,
			    		    id:'cm_id'
			    		}]
				    },{
				    	xtype : 'grid',
						anchor: '100% 35%',
						columnLines : true,
						id : 'grid1',
						plugins: [Ext.create('erp.view.core.grid.HeaderFilter', {
							remoteFilter: false
						}),Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1,
						})],
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
				            	var taxsum = 0;
				            	Ext.each(items,function(item,index){
			                		taxsum = taxsum + Number(item.data['PR_THISAMOUNT']);
			                	});
				               	Ext.getCmp('pramount').setValue(taxsum.toFixed(2));
					        }
						}),
						columns : [ {
							text : '预收单号',
							cls : 'x-grid-header-1',
							dataIndex: 'PR_CODE',
							width: 100,
							filter: {
			    				xtype : 'textfield'
			    			}
						}, {
							text : '日期',
							cls : 'x-grid-header-1',
							dataIndex: 'PR_DATE',
							width: 120,
							filter: {
			    				xtype : 'datefield'
			    			},
			    			renderer: function(val) {return Ext.Date.format(new Date(val), 'Y-m-d');}
						}, {
							text : '冲账币别',
							cls : 'x-grid-header-1',
							dataIndex: 'PR_CURRENCY',
							width: 70,
							filter: {
			    				xtype : 'textfield'
			    			}
						}, {
							text : '预收挂账金额',
							cls : 'x-grid-header-1',
							dataIndex: 'PR_JSAMOUNT',
							width: 100,
							filter: {
			    				xtype : 'textfield'
			    			}
						}, {
							text : '已结算金额',
							cls : 'x-grid-header-1',
							dataIndex: 'PR_HAVEBALANCE',
							width: 80,
							filter: {
			    				xtype : 'textfield'
			    			}
						},{
							text : '类型',
							cls : 'x-grid-header-1',
							dataIndex: 'PR_KIND',
							width: 120,
							filter: {
			    				xtype : 'textfield'
			    			}
						},{
							text : '本次结算金额',
							cls : 'x-grid-header-1',
							dataIndex: 'PR_THISAMOUNT',
							width: 100,
							filter: {
			    				xtype : 'textfield'
			    			},
			    			xtype:'numbercolumn',
			    			editable:true,
			    			editor:{
			    				  xtype:'numberfield',
			    				  format:'0',
			    				  hideTrigger: true
			    			}
						}, {
							text : '销售单号',
							cls : 'x-grid-header-1',
							dataIndex: 'PR_ORDERCODE',
							width: 200,
							filter: {
			    				xtype : 'textfield'
			    			}
						}],
						store : new Ext.data.Store({
							fields : [ 'PR_CODE', 'PR_DATE', 'PR_JSAMOUNT',
									'PR_HAVEBALANCE', 'PR_THISAMOUNT','PR_KIND',
									'PR_ID','PR_CUSTCODE','PR_CURRENCY', 'PR_ORDERCODE' ],
							proxy : {
								type : 'ajax',
								url : basePath + 'fa/ars/getPreRec.action',
								reader : {
									type : 'json',
									root : 'data'
								}
							},
							autoLoad : false
						})
				    },{
				    	xtype : 'grid',
						anchor: '100% 45%',
						columnLines : true,
						id : 'grid2',
						plugins: [Ext.create('erp.view.core.grid.HeaderFilter', {
							remoteFilter: false
						}),Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1,
						})],
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
									var me = this;
					            	me.countAmount();
								},
							},
							countAmount: function(record){
					        	var me = this;
				            	var grid = Ext.getCmp('grid2');
				            	var items = grid.selModel.selected.items;
				            	var taxsum = 0;
				            	Ext.each(items,function(item,index){
			                		taxsum = taxsum + Number(item.data['AB_THISAMOUNT']);
			                	});
				               	Ext.getCmp('abamount').setValue(taxsum.toFixed(2));
					        }
						}),
						columns : [ {
							text : '发票编号',
							cls : 'x-grid-header-1',
							dataIndex: 'AB_CODE',
							width: 100,
							filter: {
			    				xtype : 'textfield'
			    			}
						}, {
							text : '日期',
							xtype:'datecolumn',
							cls : 'x-grid-header-1',
							dataIndex: 'AB_DATE',
							width: 120,
							filter: {
			    				xtype : 'datefield'
			    			},
			    			renderer: function(val) {return Ext.Date.format(new Date(val), 'Y-m-d');}
						}, {
							text : '发票金额',
							cls : 'x-grid-header-1',
							dataIndex: 'AB_ARAMOUNT',
							width: 80,
							filter: {
			    				xtype : 'textfield'
			    			}
						}, {
							text : '已收金额',
							cls : 'x-grid-header-1',
							dataIndex: 'AB_PAYAMOUNT',
							width: 80,
							filter: {
			    				xtype : 'textfield'
			    			}
						}, {
							text : '类型',
							cls : 'x-grid-header-1',
							dataIndex: 'AB_CLASS',
							width: 100,
							filter: {
			    				xtype : 'textfield'
			    			}
						},{
							text : '本次结算金额',
							cls : 'x-grid-header-1',
							dataIndex: 'AB_THISAMOUNT',
							width: 100,
							disable:true,
							filter: {
			    				xtype : 'textfield'
			    			},
			    			xtype:'numbercolumn',
			    			editable:true,
			    			editor:{
			    				  xtype:'numberfield',
			    				  format:'0',
			    				  hideTrigger: true
			    			}
						}, {
							text : '开票金额',
							cls : 'x-grid-header-1',
							dataIndex: 'AB_INVOAMOUNT',
							width: 80,
							filter: {
			    				xtype : 'textfield'
			    			},
			    			listeners:{
								afterrender:function(btn){
									if(!editable) {
										btn.hide();
									}
								}
							}
						}, {
							text : '开票状态',
							cls : 'x-grid-header-1',
							dataIndex: 'AB_INVOSTATUS',
							width: 80,
							filter: {
			    				xtype : 'textfield'
			    			},
			    			listeners:{
								afterrender:function(btn){
									if(!editable) {
										btn.hide();
									}
								}
							}
						}, {
							text : '发货类型',
							cls : 'x-grid-header-1',
							dataIndex: 'AB_SENDTYPE',
							width: 100,
							disable:true,
							filter: {
			    				xtype : 'textfield'
			    			}
						}, {
							text : '销售单号',
							cls : 'x-grid-header-1',
							dataIndex: 'AB_ORDERCODE',
							width: 370,
							filter: {
			    				xtype : 'textfield'
			    			}
						}],
						store : new Ext.data.Store({
							fields : [ 'AB_CODE', 'AB_DATE', 'AB_ARAMOUNT',
									'AB_PAYAMOUNT', 'AB_ORDERCODE','AB_CLASS',
									'AB_ID','AB_VENDCODE','AB_CURRENCY', 'AB_THISAMOUNT',
									'AB_SENDTYPE', 'AB_INVOAMOUNT', 'AB_INVOSTATUS'],
							proxy : {
								type : 'ajax',
								url : basePath + 'fa/ars/getARBill.action',
								reader : {
									type : 'json',
									root : 'data'
								}
							},
							autoLoad : false
						})
				    }],
				    buttons : [{
				    	name: 'confirm',
				    	text : $I18N.common.button.erpConfirmButton,
				    	iconCls: 'x-button-icon-confirm',
				    	cls: 'x-btn-gray',
				    	listeners: {
   				    		buffer: 500,
   				    		click: function(btn) {
   				    			var grid1 = Ext.getCmp('grid1'), grid2 = Ext.getCmp('grid2');
   		        				var items1 = grid1.selModel.getSelection(),
   		        					items2 = grid2.selModel.getSelection();
   		        				var data1 = Ext.Array.map(items1, function(item){
   		        					return {pr_id: item.get('PR_ID'), pr_thisamount: item.get('PR_THISAMOUNT')}; 
   		        				}), data2 = Ext.Array.map(items2, function(item){
   		        					return {ab_id: item.get('AB_ID'), ab_thisamount: item.get('AB_THISAMOUNT')}; 
   		        				});
   		        				
   		        				if(data1.length > 0 || data2.length > 0) {
   		        					Ext.Ajax.request({
   		        				   		url : basePath + 'fa/confirmPreRecARBill.action',
   		        				   		params: {
   		        				   			cmid: Ext.getCmp("cm_id").value,
   		        				   			thisamount: Ext.getCmp("cm_prepaybalance").value,
   		        				   			data1: Ext.JSON.encode(data1).toString(),
   		        				   			data2: Ext.JSON.encode(data2).toString()
   		        				   		},
   		        				   		method : 'post',
   		        				   		timeout: 6000000,
   		        				   		callback : function(options,success,response){
   		        				   			var localJson = new Ext.decode(response.responseText);
   		        				   			if(localJson.exceptionInfo){
   		        				   				var str = localJson.exceptionInfo;
   		        				   				showError(str);return;
   		        				   			}
   		        			    			if(localJson.success){
   		        			    				if(localJson.log){
   		        			    					showMessage("提示", localJson.log);
   		        			    				}
   		        			    				record.set('cm_prepaybalance', Ext.getCmp('cm_prepaybalance').value);
   		        			    				Ext.getCmp('win').close();
   		        				   			}
   		        				   		}
   		        					});
   		        				} else {
   		        					showError("请勾选需要的明细!");
   		        				}
   				    		}
				    	}
				    }, {
				    	text : $I18N.common.button.erpCloseButton,
				    	iconCls: 'x-button-icon-close',
				    	cls: 'x-btn-gray',
				    	handler : function(){
				    		Ext.getCmp('win').close();
				    	}
				    }]
				});
				win.show();
				win.down('#grid1').getStore().load({
					params: {
						custcode: record.get('cm_custcode'),
						currency: record.get('cm_currency')
					}
				});
				win.down('#grid2').getStore().load({
					params: {
						custcode: record.get('cm_custcode'),
						currency: record.get('cm_currency')
					}
				});
			});
		}
	});