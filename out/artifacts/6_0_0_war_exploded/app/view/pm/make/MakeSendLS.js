Ext.define('erp.view.pm.make.MakeSendLS', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				region : 'north',
				xtype : 'erpBatchDealFormPanel',
				anchor : '100% 30%',
				tbar : [ {
					name : 'query',
					id : 'query',
					text : $I18N.common.button.erpQueryButton,
					iconCls : 'x-button-icon-query',
					cls : 'x-btn-gray'
				}, '-', {
					text : '发料',
					id : 'add',
					iconCls : 'x-button-icon-add',
					style : {
						marginLeft : '10px'
					},
					cls : 'x-btn-gray'
				}, '-', {
					text : $I18N.common.button.erpCloseButton,
					iconCls : 'x-button-icon-close',
					style : {
						marginLeft : '10px'
					},
					cls : 'x-btn-gray',
					handler : function() {
						var main = parent.Ext.getCmp("content-panel");
						main.getActiveTab().close();
					}
				} ],
			}, {
				region : 'south',
				xtype : 'erpBatchDealGridPanel',
				id : 'batchDealGridPanel',
				anchor : '100% 70%',
//				selModel : Ext.create('Ext.selection.CheckboxModel', {}),
				viewConfig: {
					listeners: {
						render: function(view) {
							if(!view.tip) {
								view.tip = Ext.create('Ext.tip.ToolTip', {
							        target: view.el,
							        delegate: view.itemSelector,
							        trackMouse: true,
							        renderTo: Ext.getBody(),
							        listeners: {
							            beforeshow: function updateTipBody(tip) {
							            	var record = view.getRecord(tip.triggerElement),
							            		grid = view.ownerCt;
							            	if(record && grid.productwh) {
												var c = record.get('mm_prodcode'), pws = new Array();
												Ext.each(grid.productwh, function(d){
													if(d.PW_PRODCODE == c) {
														pws.push(d);
													}
												});
												tip.down('grid').setTitle(c);
												tip.down('grid').store.loadData(pws);
											}
							            }
							        },
							        items: [{
							        	xtype: 'grid',
							        	width: 300,
							        	columns: [{
							        		text: '仓库编号',
							        		cls: 'x-grid-header-1',
							        		dataIndex: 'PW_WHCODE',
							        		width: 80
							        	},{
							        		text: '仓库名称',
							        		cls: 'x-grid-header-1',
							        		dataIndex: 'WH_DESCRIPTION',
							        		width: 120
							        	},{
							        		text: '库存',
							        		cls: 'x-grid-header-1',
							        		xtype: 'numbercolumn',
							        		align: 'right',
							        		dataIndex: 'PW_ONHAND',
							        		width: 90
							        	}],
							        	columnLines: true,
							        	title: '物料分仓库存',
							        	store: new Ext.data.Store({
							        		fields: ['PW_WHCODE', 'WH_DESCRIPTION', 'PW_ONHAND'],
							        		data: [{}]
							        	})
							        }]
							    });
							}
						},
						cellcontextmenu:function (){
							//alert('sdsd');
						}
					}
				}
			} ]
		});
		me.callParent(arguments);
	}
});