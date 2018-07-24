Ext.define('erp.view.fa.gla.ExchangeGl',{ 
	extend: 'Ext.Viewport', 
	layout: {
		type: 'vbox',
		align: 'center'
	}, 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				cls: 'singleWindowForm',
				bodyCls: 'singleWindowForm',
				xtype: 'form',
				flex: 3,
				width: '70%',
				bodyStyle: 'background: #f1f1f1;',
				title: '汇兑损益',
				layout: 'vbox',
				fieldDefaults: {
					margin: '2 3 2 60'
				},
				items: [{
					xtype: 'displayfield',
					fieldLabel: '总账期间',
					name: 'yearmonth',
					id: 'yearmonth'
				},{
					xtype: 'cateTreeDbfindTrigger',
					fieldLabel: '汇兑损益科目',
					name: 'ca_code',
					id: 'ca_code'
				},{
					xtype: 'checkbox',
					boxLabel: '将期末调汇产生的凭证立即登账',
					name: 'account',
					id: 'account'
				}],
				buttonAlign: 'center',
				buttons: [{
					text: '执行',
					id: 'deal',
					cls: 'x-btn-gray',
					iconCls: 'x-button-icon-submit'
				}, {
					xtype: 'erpCloseButton'
				}]
			}, {
				flex: 7,
				width: '70%',
				xtype: 'gridpanel',
				columnLines: true,
				columns: [{
					text: '编号',
					flex: 0.4,
					dataIndex: 'cm_code',
					cls: 'x-grid-header-1',
					align: 'center',
					tdCls: 'x-grid-cell-special'
				},{
					text: '币别',
					flex: 0.5,
					dataIndex: 'cm_crname',
					align: 'center',
					cls: 'x-grid-header-1'
				},{
					text: '汇率',
					flex: 1.5,
					dataIndex: 'cm_endrate',
					cls: 'x-grid-header-1',
					xtype: 'numbercolumn',
					align: 'center',
					format: '0,000.00000000',
					editor: {
						xtype: 'numberfield',
						hideTrigger: true
					}
				}],
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToEdit: 1
				})],
				store: new Ext.data.Store({
					fields: ['cm_code', 'cm_crname', 'cm_endrate'],
					data: [{}, {}, {}, {}, {}, {}, {}, {}, {}, {}]
				})
			}] 
		}); 
		this.callParent(arguments); 
	} 
});