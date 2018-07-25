Ext.define('erp.view.ma.Optimize',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'grid',
				id: 'check-grid',
				anchor: '100% 100%',
				tbar: ['->',{
					cls: 'x-btn-blue',
					id: 'check',
					text: '一键优化',
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
					text: '可优化项',
					dataIndex: 'value',
					flex: 10
				},{
					text: '',
					dataIndex: 'check',
					flex: 1,
					renderer: function(val, meta, record) {
						meta.tdCls = val;
						return '';
					}
				},{
					text: '',
					dataIndex: 'link',
					flex: 1,
					renderer: function(val, meta, record, x, y, store) {
						var idx = store.indexOf(record);
						meta.style = 'color:blue;text-decoration: underline;cursor: pointer;';
						return '<a href="javascript:Ext.getCmp(\'check-grid\').check(' + idx + ')">优化</a>';
					}
				}],
				columnLines: true,
				store: Ext.create('Ext.data.Store',{
					fields: [{name: 'action', type: 'string'}, {name: 'value', type: 'string'}, {name: 'detail', type: 'string'}],
					data: [/*{
						action: 'ma/kill_dblock.action',
						value: '清除数据库锁定进程'
					},*/{
						action: 'ma/kill_cache.action',
						value: '清除系统缓存'
					},{
						action: 'ma/update_seq.action',
						value: '更新数据库序列LAST_NUMBER'
					},{
						type: 'turn_icq',
						action: 'oa/info/turnHistory.action',
						value: '已查阅寻呼转入寻呼历史表，加速检索'
					}]
				}),
				plugins: [{
 		            ptype: 'rowexpander',
 		            rowBodyTpl : [
 		                '<ul>',          
 		                '<li style="margin-left:30px;color:gray;">{detail}</li>',
 		                '</ul>'
 		            ]
 		        }],
 		        selModel: new Ext.selection.CellModel(),
 		        toggleRow: function(record) {
 		        	var rp = this.plugins[0];
 		        	if(rp)
 		        		rp.toggleRow(this.store.indexOf(record));
 		        }
			}] 
		}); 
		me.callParent(arguments); 
	} 
});