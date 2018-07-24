Ext.define('erp.view.scm.reserve.FreezeCheck',{ 
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
					xtype: 'monthdatefield',
			    	fieldLabel: '日期',
			    	allowBlank: false,
			    	labelWidth: 50,
			    	width: 150,
			    	id: 'yearmonth'
				},{
					xtype: 'tbtext',
					text:'当前冻结期间:',
					margin: '0 0 0 15'
				}, {
					xtype: 'displayfield',
					height: 23,
					id: 'date',
					name:'date',
					margin: '0 0 0 10'
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
						action: 'scm/reserve/chk_k.action',
						detno: 1,
						type: 'scm_chk_k',
						value: '其它出入库单据的单据类型+小类+部门是否设置了对方科目'
					},{
						action: 'scm/reserve/chk_l.action',
						detno: 2,
						type: 'scm_chk_l',
						value: '其它出入库基础科目设置是否有重复的'
					},{
						action: 'scm/reserve/chk_m.action',
						detno: 3,
						type: 'scm_chk_m',
						value: '是否有出入库单据出、入数量都不为0'
					},{
						action: 'scm/reserve/chk_n.action',
						detno: 4,
						type: 'scm_chk_n',
						value: '是否有出入库单据料号不存在'
					},{
						action: 'scm/reserve/chk_o.action',
						detno: 5,
						type: 'scm_chk_o',
						value: '是否有当月发生出入库单据明细物料/仓库存货科目未设置'
					},{
						action: 'scm/reserve/chk_p.action',
						detno: 6,
						type: 'scm_chk_p',
						value: '当期是否有未过账的出入库单据'
					},{
						action: 'scm/reserve/chk_y.action',
						detno: 7,
						type: 'scm_chk_y',
						value: '当月所有生产报废单是否审核'
					},{
						action: 'scm/reserve/chk_z.action',
						detno: 8,
						type: 'scm_chk_z',
						value: '是否有工单已完工未结案的'
					}]
				}),
				bbar: [{
					xtype: 'checkbox',
					boxLabel: '知道错误了，我要继续冻结',
					id : 'allow',
					hidden : true,
					margin: '0 5 0 20'
				},'->',{
					cls: 'x-btn-blue',
					id: 'check',
					text: '冻结检查',
					width: 80,
					margin: '0 0 0 50'
				},{
					cls: 'x-btn-blue',
					id: 'accoutover',
					text: '冻  结',
					width: 80,
					disabled : true,
					margin: '0 0 0 5'
				},{
					cls: 'x-btn-blue',
					id: 'resaccoutover',
					text: '反冻结',
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