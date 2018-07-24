Ext.define('erp.view.co.inventory.CheckIVAccount',{ 
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
					text: '当前期间：',
					margin: '0 0 0 20'
				},{
					xtype: 'tbtext',
					id: 'yearmonth',
					text: '201305',
					margin: '0 0 0 2'
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
						action: 'co/inventory/chk_a.action',
						detno: 2,
						type: 'co_chk_a',
						value: '库存是否已经冻结'
					},{
						action: 'co/inventory/chk_b.action',
						detno: 3,
						type: 'co_chk_b',
						value: '当期是否有未过账的出入库单据'
					},{
						action: 'co/inventory/chk_c.action',
						detno: 4,
						type: 'co_chk_c',
						value: '当月出入库单据料号是否存在'
					},{
						action: 'co/inventory/chk_d.action',
						detno: 5,
						type: 'co_chk_d',
						value: '生产领料单、生产补料单、生产退料单、完工入库单、拆件入库单存货金额是否与总账科目一致'
					},{
						action: 'co/inventory/chk_e.action',
						detno: 6,
						type: 'co_chk_e',
						value: '委外领料单、委外补料单、委外退料单存货金额是否与总账科目一致'
					},{
						action: 'co/inventory/chk_f.action',
						detno: 7,
						type: 'co_chk_f',
						value: '其它出库单、其它入库单存货金额是否与总账科目一致'
					},{
						action: 'co/inventory/chk_g.action',
						detno: 8,
						type: 'co_chk_g',
						value: '盘盈调整单、盘亏调整单、报废单与相应凭证存货科目是否与总账科目一致'
					},{
						action: 'co/inventory/chk_h.action',
						detno: 9,
						type: 'co_chk_h',
						value: '拨入拨出单、销售拨入拨出单存货金额是否与总账科目一致'
					},
					/*{
						action: 'co/inventory/chk_i.action',
						type: 'co_chk_i',
						value: '采购验收单、采购验退单存货金额是否与总账科目一致'
					},*/
					{
						action: 'co/inventory/chk_j.action',
						detno: 10,
						type: 'co_chk_j',
						value: '出货单、销售退货单存货金额是否与总账科目一致'
					},{
						action: 'co/inventory/chk_k.action',
						detno: 11,
						type: 'co_chk_k',
						value: '仓库物料别库存总账【期末结存金额】与总账对应存货科目余额是否一致'
					},{
						action: 'co/inventory/chk_l.action',
						detno: 12,
						type: 'co_chk_l',
						value: '仓库物料别库存总账：期初数量是否与上月期末数量一致'
					},{
						action: 'co/inventory/chk_m.action',
						detno: 13,
						type: 'co_chk_m',
						value: '存货月结表：期初金额是否与上月期末金额一致'
					},{
						action: 'co/inventory/chk_n.action',
						detno: 14,
						type: 'co_chk_n',
						value: '存货月结表：物料期末数量是否有数量无金额的情况'
					},{
						action: 'co/inventory/chk_o.action',
						detno: 15,
						type: 'co_chk_o',
						value: '存货月结表：物料期末金额是否有金额无数量的情况'
					},{
						action: 'co/inventory/chk_p.action',
						detno: 16,
						type: 'co_chk_p',
						value: '存货月结表：物料是否负数金额、负数数量的情况'
					},{
						action: 'co/inventory/chk_q.action',
						detno: 17,
						type: 'co_chk_q',
						value: '存货核算：所有的出入库单批次单价是否核算进去'
					},{
						action: 'co/inventory/chk_r.action',
						detno: 18,
						type: 'co_chk_r',
						value: '所有的出入库单据是否做了凭证'
					},{
						action: 'co/inventory/chk_s.action',
						detno: 19,
						type: 'co_chk_s',
						value: '是否存在无来源单据总账制作存货科目凭证及来源单据非出入库单、期初调整单、应付发票、应付暂估、发出商品、主营业务成本存货科目凭证'
					},{
						action: 'co/inventory/chk_t.action',
						detno: 20,
						type: 'co_chk_t',
						value: '是否存在非无值仓单价为负数的物料'
					},{
						action: 'co/inventory/chk_t1.action',
						detno: 21,
						type: 'co_chk_t1',
						value: '是否存在非无值仓单价为0的物料'
					},{
						action: 'co/inventory/chk_u.action',
						detno: 22,
						type: 'co_chk_u',
						value: '无值仓是否存在单价'
					},{
						action: 'co/inventory/chk_v.action',
						detno: 23,
						type: 'co_chk_v',
						value: '是否存在上月出库当月入库的情况'
					},{
						action: 'co/inventory/chk_w.action',
						detno: 24,
						type: 'co_chk_w',
						value: '所有出入库单据会计期间是否和凭证一致'
					}]
				}),
				bbar: [{
					xtype: 'checkbox',
					boxLabel: '知道错误了，我要继续结账',
					id : 'allow',
					hidden : true,
					margin: '0 5 0 20'
				},'->',{
					cls: 'x-btn-blue',
					id: 'check',
					text: '结账检查',
					width: 80,
					margin: '0 0 0 50'
				},{
					cls: 'x-btn-blue',
					id: 'accoutover',
					text: '结  账',
					width: 80,
					disabled : true,
					margin: '0 0 0 5'
				},{
					cls: 'x-btn-blue',
					id: 'resaccoutover',
					text: '反结账',
					width: 80,
					margin: '0 0 0 5'
				},{
					cls: 'x-btn-blue',
					id: 'close',
					text: $I18N.common.button.erpCloseButton,
					width: 80,
					margin: '0 175 0 5'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});