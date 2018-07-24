Ext.define('erp.view.ma.ShiftCheck',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'grid',
				id: 'shift-check',
				anchor: '100% 100%',
				tbar: ['->',{
					cls: 'x-btn-blue',
					id: 'check',
					text: '检查',
					width: 80
				},{
					cls: 'x-btn-blue',
					id: 'close',
					text: $I18N.common.button.erpCloseButton,
					width: 80,
					margin: '0 0 0 5'
				},'->'],
				columns: [{
					text: '',
					dataIndex: 'check',
					flex: 1,
					renderer: function(val, meta, record) {
						meta.tdCls = val;
						return '';
					}
				},{
					text: '检测项',
					dataIndex: 'value',
					flex: 10,
					renderer: function(val, meta, record) {
						if(record.get('check') == 'error') {
							meta.style = 'color: gray';
						}
						return val;
					}
				},{
					text: '',
					dataIndex: 'link',
					flex: 1,
					renderer: function(val, meta, record) {
						if(record.get('check') == 'error') {
							meta.tdCls = 'detail';
							return '详细情况';
						}
						return '';
					}
				}],
				columnLines: true,
				features : [Ext.create('Ext.grid.feature.Grouping',{
			   		startCollapsed: false,
			        groupHeaderTpl: '{name} (Count:{rows.length})'
			    })],
				store: Ext.create('Ext.data.Store',{
					fields: ['action', 'type', 'group', 'value'],
					groupField: 'group',
					data: [{
						action: 'ma/shiftcheck/chk_a.action',
						type: 'ma_chk_a',
						group: '采购模块',
						value: '采购单数量与来源请购单已转数量一致'
					},{
						action: 'ma/shiftcheck/chk_b.action',
						type: 'ma_chk_b',
						group: '采购模块',
						value: '收料单数量与来源采购单已转收料数一致'
					},{
						action: 'ma/shiftcheck/chk_c.action',
						type: 'ma_chk_c',
						group: '采购模块',
						value: '检验单检验数量、合格数、不合格数与收料单的一致'
					},{
						action: 'ma/shiftcheck/chk_d.action',
						type: 'ma_chk_d',
						group: '采购模块',
						value: '采购验收单数量与检验单入良品仓数一致'
					},{
						action: 'ma/shiftcheck/chk_e.action',
						type: 'ma_chk_e',
						group: '采购模块',
						value: '采购验收单(已过账)数量与采购单已验收数量一致'
					},{
						action: 'ma/shiftcheck/chk_f.action',
						type: 'ma_chk_f',
						group: '采购模块',
						value: '不良品入库单数量与检验单入不良品仓数一致'
					},{
						action: 'ma/shiftcheck/chk_g.action',
						type: 'ma_chk_g',
						group: '采购模块',
						value: '不良品入库单(已过账)数量与采购单不良入库数一致'
					},{
						action: 'ma/shiftcheck/chk_h.action',
						type: 'ma_chk_h',
						group: '销售模块',
						value: '通知单数量与订单已转数量一致'
					},{
						action: 'ma/shiftcheck/chk_i.action',
						type: 'ma_chk_i',
						group: '销售模块',
						value: '出货单(包含未过账)数量与来源通知单已转数量一致'
					},{
						action: 'ma/shiftcheck/chk_j.action',
						type: 'ma_chk_j',
						group: '销售模块',
						value: '出货单(已过账)数量与来源订单已发货数量一致'
					},{
						action: 'ma/shiftcheck/chk_k.action',
						type: 'ma_chk_k',
						group: '生产模块',
						value: '领料单数量(未过账)与工单已转领料数一致'
					},{
						action: 'ma/shiftcheck/chk_l.action',
						type: 'ma_chk_l',
						group: '生产模块',
						value: '领料单数量(已过账)与工单已领料数一致'
					},{
						action: 'ma/shiftcheck/chk_m.action',
						type: 'ma_chk_m',
						group: '生产模块',
						value: '补料单数量(未过账)与工单已转补料数一致'
					},{
						action: 'ma/shiftcheck/chk_n.action',
						type: 'ma_chk_n',
						group: '生产模块',
						value: '补料单数量(已过账)与工单已补料数一致'
					}]
				})
			}] 
		}); 
		me.callParent(arguments); 
	} 
});