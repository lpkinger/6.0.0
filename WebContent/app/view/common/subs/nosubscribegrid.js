Ext.define('erp.view.common.subs.nosubscribegrid', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.erpnosubscribeGridPanel',
	enableTools : true,
	columnLines : true,
	id : 'nosubscribegrid',
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
				dataIndex : 'ID_',
				cls : 'x-grid-header-simple',
				width : 100,
				filter : {
					xtype : 'textfield',
					filterName : 'ID_'
				},
				renderer : function(val, meta, record) {
					var insId = record.get('ID_');
					return Ext.String.format(
							'<a href="javascript:showWindow(\'' + insId
									+ '\');">{0}</a>', val);
				},
				fixed : true,
			},
			{
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

			},
			{
				text : '标题',
				cls : 'x-grid-header-simple',
				width : 200,
				dataIndex : 'TITLE_',
				filter : {
					xtype : 'textfield',
					filterName : 'TITLE_'
				},
				fixed : true,
			},
			{
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

			},
			{
				text : '状态',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 150,
				dataIndex : 'STATUS_',
				filter : {
					xtype : 'combo',
					filterName : 'STATUS_',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "已申请",
							Value : 2
						}, {
							Name : "未订阅",
							Value : 3
						} ]
					}),
					displayField : 'Name',
					valueField : 'Value'
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 2)
						return '<span style="color:green">已申请</span>';
					else
						return '<span style="color:red">未订阅</span>';
				},
				fixed : true,

			},
			{
				text : '申请',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				fixed : true,
				dataIndex : '',
				filter : {
					xtype : 'textfield',
					filterName : ''
				},
				xtype : "actioncolumn",
				header : " 申请",
				items : [ {
					iconCls : 'subsAdd',
					tooltip : "申请",
					handler : function(grid, rindex, cindex) {
						var record = grid.getStore().getAt(rindex);
						var STATUS_ = record.get("STATUS_");
						if (STATUS_ == 2) {
							return;
						}
						this.status(record);

					},
					getClass : function(v, metaData, record) {
						if (record.get('STATUS_') === 2) {
							return ' subsnoAdd';
						} else {
							return 'subsAdd';
						}
					},

				}

				],
				status : function(record) {
					Ext.Ajax.request({
						url : basePath
								+ 'common/charts/vastAddSubsApply.action',
						params : {
							ids : record.get('ID_'),
							caller : 'VastAddSubsApply'
						},
						success : function(response) {
							var text = response.responseText;
							Ext.getCmp('nosubscribegrid').store.load();

						}

					});
				},
			} ],
	store : Ext.create('Ext.data.Store', {
		fields : [ 'ID_', 'TITLE_', 'TYPE_', 'STATUS_', 'REMARK_' ],
		proxy : {
			type : 'ajax',
			url : basePath
					+ 'common/charts/getPersonalApplySubs.action?em_code='
					+ em_code,
			method : 'GET',

			reader : {
				type : 'json',
				root : 'datas'
			}
		},

		autoLoad : true,

	}),

	initComponent : function() {
		var me = this;
		this.callParent(arguments);
	}

});