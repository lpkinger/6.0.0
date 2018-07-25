/**
 * 
 * 批量获取供应商、客户UU号
 */
Ext.define('erp.view.b2b.ma.BatchDealUU',{ 
	extend: 'Ext.Viewport', 
	layout: {
		type: 'hbox',
		align: 'middle',
		pack: 'center'
	}, 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				xtype: 'gridpanel',
				id: 'grid',
				bodyStyle: 'background: #f1f1f1;',
				title: '批量获取客户供应商UU',
				columns: [{
					text: '',
					dataIndex: 'item'
				},{
					text: '已维护',
					xtype: 'numbercolumn',
					dataIndex: 'checkedcount',
					format:0
				},{
					text: '未维护',
					xtype: 'numbercolumn',
					dataIndex: 'nocheckedcount',
					format:0
				},{
					text: '获取成功',
					xtype: 'numbercolumn',
					dataIndex: 'success',
					format:0
				},{
					text: '获取失败',
					xtype: 'numbercolumn',
					dataIndex: 'failure',
					format:0
				},{
					xtype: 'actioncolumn',
					align: 'center',
					items: [{
						icon: basePath + 'resource/images/icon/turn.png',
						handler: function(grid, rowIndex, colIndex) {
		                    var rec = grid.getStore().getAt(rowIndex),
		                    	caller = rec.get('type') == 'vendor'?'Vendor!CheckUU':'Customer!CheckUU';
		                    var url;
		                    if(rec.get('type')=='vendor'){
		                    	caller='Vendor!CheckUU';
		                    	url="jsps/common/batchDeal.jsp?whoami=" + caller+"&urlcondition=ve_emailkf='已获取'";
		                    }else{
		                    	caller='Customer!CheckUU';
		                    	url="jsps/common/batchDeal.jsp?whoami=" + caller+"&urlcondition=cu_checkuustatus='已获取'";
		                    }
		                    openTable('批处理界面',url);
		                }
					}]
				}],
				store: new Ext.data.Store({
					fields: ['item', 'type', 'checkedcount','nocheckedcount', 'success', 'failure'],
					data: [{
						item: '客户',
						type: 'customer'
					},{
						item: '供应商',
						type: 'vendor'
					}]
				}),
				buttonAlign: 'center',
				bbar: {cls: 'singleWindowBar',items:['->',{
					text: '一键获取',
					id: 'deal',
					cls: 'x-btn-gray',
					width: 100,
					iconCls: 'x-button-icon-submit'
				}, {
					xtype: 'erpCloseButton'
				},'->']}
			}] 
		}); 
		this.callParent(arguments); 
	} 
});