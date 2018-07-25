Ext.define('erp.view.pm.mes.SerialTrace',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%'
			},{
				xtype:'tabpanel',
				anchor: '100% 80%', 
				items:[{
					xtype: 'erpQueryGridPanel',
					title:'上料信息',
					caller: 'SerialTrace',
					id : 'grid1',
					viewConfig: {
						listeners: {
							render: function(view) {
								if(!view.tip) {
									view.tip = Ext.create('Ext.tip.ToolTip', {
								        target: view.el,
								        delegate: view.itemSelector,
								        trackMouse: true,
								        width: 500,
								        renderTo: Ext.getBody(),
								        listeners: {
								            beforeshow: function(tip) {
								            	var record = view.getRecord(tip.triggerElement),
								            		grid = view.ownerCt;
								            	if(record && grid.barData) {
													var c = record.get('cm_barcode'), tipData = {};
													for(var i = 0;i < grid.barData.length;i++) {
														if(grid.barData[i].BAR_CODE == c) {
															tipData = grid.barData[i];
															break;
														}
													}
													tip.down('form').setTitle(c);
													tip.down('form').getForm().setValues(tipData);
												}
								            }
								        },
								        items: [{
								        	xtype: 'form',
								        	bodyStyle: 'background: #f1f1f1;',
								        	title: '原材料信息追溯条',
								        	defaults: {
								        		xtype: 'textfield',
								        		margin: '5',
								        		flex: 1
								        	},
								        	items: [{
								        		fieldLabel: '采购单号',
								        		name: 'BAR_PUCODE'
								        	},{
								        		fieldLabel: '供应商号',
								        		name: 'BAR_VENDCODE'
								        	},{
								        		fieldLabel: '来料日期',
								        		name: 'BAR_INDATE',
								        		xtype: 'datefield'
								        	},{
								        		fieldLabel: '有效日期',
								        		name: 'BAR_VALIDDATE',
								        		xtype: 'datefield'
								        	},{
								        		fieldLabel: '生产日期',
								        		name: 'BAR_MADEDATE',
								        		xtype: 'datefield'
								        	}]
								        }]
								    });
								}
							}
						}
					}
				},{
					title:'不良记录',
					items: [],
					id : 'grid2-tab',
					layout: 'anchor',
				}]
			}]
		}); 
		me.callParent(arguments); 
	}
});