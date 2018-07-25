Ext.define('erp.view.common.subs.subscribedgrid', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.erpsubscribedGridPanel',
	enableTools : true,
	id : 'subscribedgrid',
	columnLines : true,
	iconCls : 'main-subs',
	requires : [ 'erp.view.core.grid.HeaderFilter' ],
	plugins : [ Ext.create('erp.view.core.grid.HeaderFilter'),
			Ext.create('erp.view.core.plugin.CopyPasteMenu') ],
	viewConfig : {
		loadMask : false
	},
	columns : [
			{
				text : '订阅号',
				dataIndex : 'NUM_ID',
				cls : 'x-grid-header-simple',
				width : 100,
				filter : {
					xtype : 'textfield',
					filterName : 'NUM_ID'
				},
				renderer : function(val, meta, record) {
					var insId = record.get('NUM_ID');
					return Ext.String.format(
							'<a href="javascript:showWindow(\'' + insId
									+ '\');">{0}</a>', val);
				},
				fixed : true,
			}, {
				text : '模块类型',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 100,
				dataIndex : 'TYPE_',
				filter : {
					xtype : 'textfield',
					filterName : 'TYPE_'
				},
				fixed : true,

			}, {
				text : '标题',
				cls : 'x-grid-header-simple',
				width : 200,
				dataIndex : 'TITLE_',
				filter : {
					xtype : 'textfield',
					filterName : 'TITLE_'
				},
				fixed : true,
			}, {
				text : '备注',
				draggable : false,
				cls : 'x-grid-header-simple',
				flex : 1,
				dataIndex : 'REMARK_',
				filter : {
					xtype : 'textfield',
					filterName : 'REMARK_'
				},
				fixed : true,

			}, {
				text : '来源',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 150,
				dataIndex : 'ISAPPLIED_',
				filter : {
					xtype : 'combo',
					filterName : 'ISAPPLIED_',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "分配",
							Value : 0
						}, {
							Name : "申请",
							Value : -1
						}, {
							Name : "所有",
							Value : null
						} ]
					}),
					listeners : {
						afterrender : function(comb) { // 设置下拉框默认值
							comb.setValue(null);
						}
					},
					displayField : 'Name',
					valueField : 'Value'
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 0)
						return '<span style="color:green">分配</span>';
					else
						return '<span style="color:green">申请</span>';
				},
				fixed : true,

			}, {
				text : '取消',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : '',
				filter : {
					xtype : 'textfield',
					filterName : ''
				},
				fixed : true,
				xtype : "actioncolumn",
				header : "取消",
				items : [ {
					iconCls : 'subsDelete',
					tooltip : "取消",
					getClass : function(v, metaData, record) {
						if (record.get('ISAPPLIED_') === 0) {
							return ' subsnoDelete';
						} else {
							return 'subsDelete';
						}
					},
					handler : function(grid, rindex, cindex) {
						var record = grid.getStore().getAt(rindex);
						var ISAPPLIED_ = record.get("ISAPPLIED_");
						if (ISAPPLIED_ == 0) { // 将ISAPPLIED_==0的数据设置成只读
							return;
						}
						this.subscribe(record);
					}
				} ],

				subscribe : function(record) {
					Ext.Ajax.request({
						url : basePath + 'common/charts/removeSubsMans.action',
						params : {
							numIds : record.get('NUM_ID'),
							emcode : em_code
						},
						success : function(response) {
							var text = response.responseText;
							Ext.getCmp('subscribedgrid').store.load();

						}

					});
				},
			}

	],
	store : Ext.create('Ext.data.Store', {
		fields : [ 'NUM_ID', 'TYPE_', 'TITLE_', 'ISAPPLIED_', 'REMARK_' ],
		proxy : {
			type : 'ajax',
			url : basePath + 'common/charts/getPersonalSubs.action?em_code='
					+ em_code,
			method : 'GET',
			reader : {
				type : 'json',
				root : 'datas'
			}
		},
		autoLoad : false,
	}),

	initComponent : function() {
		var me = this;
		this.callParent(arguments);
	}

});