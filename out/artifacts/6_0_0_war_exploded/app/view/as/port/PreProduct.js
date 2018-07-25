Ext.define('erp.view.as.port.PreProduct', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [{
						xtype : 'grid',
						anchor: '100% 50%',
						columnLines : true,
						id : 'grid1',
						plugins: [Ext.create('erp.view.core.grid.HeaderFilter', {
							remoteFilter: true
						})],
						tbar : [ {
							xtype : 'erpVastDealButton',
							margin: '0 0 0 5'
						}, {
							xtype : 'erpDeleteButton',
							margin: '0 0 0 5'
						}, '->', {
							xtype : 'erpCloseButton',
							margin : '0 15 0 0'
						} ],
						selModel: Ext.create('Ext.selection.CheckboxModel',{
							ignoreRightMouseSelection : false,
							checkOnly: true,
							listeners:{
								selectionchange:function(selModel, selected, options){
									var record = selModel.lastSelected;
									if(record && selModel.isSelected(record)) {
										var code = selModel.lastSelected.get('APPLY_NO');
										if(!Ext.isEmpty(code)) {
											Ext.getCmp('grid2').getStore().load({
												params: {
													code: code
												}
											});
										}
									} else {
										Ext.getCmp('grid2').getStore().removeAll();
									}
								}
							},
							getEditor: function(){
								return null;
							}
						}),
						columns : [ {
							text : '申请单号',
							cls : 'x-grid-header-1',
							dataIndex: 'APPLY_NO',
							flex : 1.5,
							filter: {
			    				xtype : 'textfield'
			    			}
						}, {
							text : '服务网点',
							cls : 'x-grid-header-1',
							dataIndex: 'STORE_NAME',
							flex : 1,
							filter: {
			    				xtype : 'textfield'
			    			}
						}, {
							text : '申请时间',
							cls : 'x-grid-header-1',
							dataIndex: 'APPLY_DAY',
							flex : 1,
							filter: {
			    				xtype : 'datefield'
			    			}
						}, {
							text : '申请人',
							cls : 'x-grid-header-1',
							dataIndex: 'SHENQING_OP',
							flex : 1,
							filter: {
			    				xtype : 'textfield'
			    			}
						}, {
							text : '目标公司',
							cls : 'x-grid-header-1',
							dataIndex: 'OBJECTIVE_STORE',
							flex : 1,
							filter: {
			    				xtype : 'textfield'
			    			}
						}, {
							text : '明细摘要',
							cls : 'x-grid-header-1',
							dataIndex: 'BILL_DETAIL',
							flex : 1,
							filter: {
			    				xtype : 'textfield'
			    			}
						}, {
							text : '申请备注',
							cls : 'x-grid-header-1',
							dataIndex: 'BEIZHU',
							flex : 1,
							filter: {
			    				xtype : 'textfield'
			    			}
						} ],
						store : new Ext.data.Store({
							fields : [ 'APPLY_NO', 'STORE_NAME', 'APPLY_DAY',
									'SHENQING_OP', 'OBJECTIVE_STORE',
									'BILL_DETAIL', 'BEIZHU' ],
							proxy : {
								type : 'ajax',
								url : basePath + 'as/port/getPreProduct.action',
								reader : {
									type : 'json',
									root : 'data'
								}
							},
							autoLoad : true
						})
					},{
						xtype : 'grid',
						anchor: '100% 50%',
						columnLines : true,
						id : 'grid2',
						columns : [ {
							text : 'ID',
							cls : 'x-grid-header-1',
							dataIndex: 'ID',
							flex : 1
						}, {
							text : '配件编码',
							cls : 'x-grid-header-1',
							dataIndex: 'FITTING_CODE',
							flex : 1
						}, {
							text : '配件名称',
							cls : 'x-grid-header-1',
							dataIndex: 'FITTING_NAME',
							flex : 1
						}, {
							text : '数量',
							cls : 'x-grid-header-1',
							dataIndex: 'QUANTITY',
							flex : 1
						}, {
							text : '单位',
							cls : 'x-grid-header-1',
							dataIndex: 'UNIT',
							flex : 1
						}, {
							text : '是否可核销',
							cls : 'x-grid-header-1',
							dataIndex: 'KEHEXIAO',
							flex : 1
						}, {
							text : '备注',
							cls : 'x-grid-header-1',
							dataIndex: 'BEIZHU',
							flex : 1
						} ],
						store : new Ext.data.Store({
							fields : [ 'ID', 'FITTING_CODE', 'FITTING_NAME',
									'QUANTITY', 'UNIT', 'KEHEXIAO',
									'BEIZHU' ],
							proxy : {
								type : 'ajax',
								url : basePath + 'as/port/getPreProductDetail.action',
								reader : {
									type : 'json',
									root : 'data'
								}
							},
							autoLoad : false
						})
					}]
					
		});
		me.callParent(arguments);
	}
});