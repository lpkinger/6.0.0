Ext.define('erp.view.co.inventory.BeforeAccount',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'grid',
				id: 'account-check',
				anchor: '100% 100%',
				tbar: [{
					xtype: 'tbtext',
					text: '会计期间：',
					margin: '0 0 0 20'
				},{
					xtype: 'tbtext',
					id: 'yearmonth',
					text: '201305',
					margin: '0 0 0 2'
				},{
					cls: 'x-btn-blue',
					id: 'check',
					text: '检查',
					width: 80,
					margin: '0 0 0 50'
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
					text: '序号',
					dataIndex: 'detno',
					flex: 0.7,
					renderer: function(val, meta, record) {
						return val;
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
				store: Ext.create('Ext.data.Store',{
					fields: [{name: 'action', type: 'string'}, {name: 'detno', type : 'number'}, {name: 'type', type: 'string'}, {name: 'value', type: 'string'}],
					data: [{
						action: 'co/inventory/chk_x.action',
						detno: 1,
						type: 'co_chk_x',
						value: '成本期间  与 库存期间一致'
					},{
						action: 'co/inventory/chk_before_a.action',
						detno: 2,
						type: 'co_chk_before_a',
						value: '同成本期间的库存期间是否已冻结'
					},{
						action: 'co/inventory/chk_before_b.action',
						detno: 3,
						type: 'co_chk_before_b',
						value: '当期是否有未过账的出入库单'
					},{
						action: 'co/inventory/chk_before_c.action',
						detno: 4,
						type: 'co_chk_before_c',
						value: '当期是否有未审核的生产报废单'
					},
					/*{
						action: 'co/inventory/chk_before_d.action',
						type: 'co_chk_before_d',
						value: '是否有非无值仓原材料单价为0'
					},*/
					{
						action: 'co/inventory/chk_before_e.action',
						detno: 5,
						type: 'co_chk_before_e',
						value: '出入库单单据中文状态是否有异常的'
					},{
						action: 'co/inventory/chk_before_f.action',
						detno: 6,
						type: 'co_chk_before_f',
						value: '是否有工单的成品物料编号不存在'
					},{
						action: 'co/inventory/chk_before_g.action',
						detno: 7,
						type: 'co_chk_before_g',
						value: '是否有工单用料表的物料号不存在'
					},{
						action: 'co/inventory/chk_before_h.action',
						detno: 8,
						type: 'co_chk_before_h',
						value: '当月是否有出入库单料号不存在'
					},{
						action: 'co/inventory/chk_before_i.action',
						detno: 9,
						type: 'co_chk_before_i',
						value: '当月出入库单制作了凭证的'
					},{
						action: 'co/inventory/chk_before_j.action',
						detno: 10,
						type: 'co_chk_before_j',
						value: '当月有出入库凭证编号但是凭证在当月不存在'
					},{
						action: 'co/inventory/chk_before_k.action',
						detno: 11,
						type: 'co_chk_before_k',
						value: '当月采购验收单、采购验退单、委外验收单、委外验退单生成应付暂估/应付发票并制作了凭证的'
					},{
						action: 'co/inventory/chk_before_n.action',
						detno: 12,
						type: 'co_chk_before_n',
						value: '当月采购验收单、委外验收单汇率与当月月度汇率是否一致'
					},{
						action: 'co/inventory/chk_before_l.action',
						detno: 13,
						type: 'co_chk_before_l',
						value: '当月出货单、销售退货单生成应收发票并制作了结转主营业务成本凭证的'
					},{
						action: 'co/inventory/chk_before_m.action',
						detno: 14,
						type: 'co_chk_before_m',
						value: '当月发出商品制作了凭证的'
					}]
				})
			}] 
		}); 
		me.callParent(arguments); 
	} 
});