Ext.define('erp.view.fa.fix.CheckFIXAccount',{ 
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
						action: 'fa/fix/chk_a.action',
						detno: 1,
						type: 'fix_chk_a',
						value: '固定资产期间与总账期间是否一致'
					},{
						action: 'fa/fix/chk_b.action',
						detno: 2,
						type: 'fix_chk_b',
						value: '当月是否计提折旧'
					},{
						action: 'fa/fix/chk_c.action',
						detno: 3,
						type: 'fix_chk_c',
						value: '当月所有卡片是否已审核'
					},{
						action: 'fa/fix/chk_d.action',
						detno: 4,
						type: 'fix_chk_d',
						value: '卡片变更单是否全部已审核'
					},{
						action: 'fa/fix/chk_e.action',
						detno: 5,
						type: 'fix_chk_e',
						value: '当月的资产增加单、资产减少单、折旧单是否过账'
					},{
						action: 'fa/fix/chk_f.action',
						detno: 6,
						type: 'fix_chk_f',
						value: '当月新增的已审核卡片是否已经生成凭证'
					},{
						action: 'fa/fix/chk_g.action',
						detno: 7,
						type: 'fix_chk_g',
						value: '当月的折旧单、资产增加单、资产减少单是否已经生成凭证'
					},{
						action: 'fa/fix/chk_k.action',
						detno: 8,
						type: 'fix_chk_k',
						value: '是否有手工录入固定资产科目的凭证'
					},{
						action: 'fa/fix/chk_l.action',
						detno: 9,
						type: 'fix_chk_l',
						value: '是否有手工录入累计折旧科目的凭证'
					},{
						action: 'fa/fix/chk_j.action',
						detno: 10,
						type: 'fix_chk_j',
						value: '当月满足折旧条件的卡片是否都计提折旧'
					},{
						action: 'fa/fix/chk_h.action',
						detno: 11,
						type: 'fix_chk_h',
						value: '当前期间固定资产所有卡片的原值金额合计与总账固定资产科目的本期余额是否一致'
					},{
						action: 'fa/fix/chk_i.action',
						detno: 12,
						type: 'fix_chk_i',
						value: '当前期间固定资产所有卡片的累计折旧金额合计与总账累计折旧科目的本期余额是否一致'
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