Ext.define('erp.view.as.port.NewBackList', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		var store = new Ext.data.Store({
			fields : [ 'NEWFITTINGBACK_NO', 'BACK_DAY', 'STORE_ID',
						'STORE_NAME', 'SHENQING_OP', 'OBJECTIVE_STORE', 'APPLY_DATETIME',
						'APPLY_RESULT', 'APPLY_OP', 'INOUTNO', 'CLASS',
						'UPDATEMAN', 'UPDATEDATE' ],
				proxy : {
					type : 'ajax',
					url : basePath + 'as/port/getNewBackList.action',
					reader : {
						type : 'json',
						root : 'target',
						totalProperty: 'totalCount'
					}
				},
				autoLoad : true,
				pageSize: parseInt((Ext.isIE ? screen.height*0.73 : window.innerHeight)*0.7/23)
			});
		Ext.apply(me, {
			items : [ {
				xtype : 'grid',
				anchor : '100% 100%',
				columnLines : true,
				id : 'grid',
				plugins: [Ext.create('erp.view.core.grid.HeaderFilter', {
					remoteFilter: true
				})],
				columns : [ {
					text : '申请单号',
					cls : 'x-grid-header-1',
					dataIndex : 'NEWFITTINGBACK_NO',
					width : 180,
					filter: {
	    				xtype : 'textfield'
	    			}
				}, {
					text : '退回时间',
					cls : 'x-grid-header-1',
					dataIndex : 'BACK_DAY',
					width : 90,
					filter: {
	    				xtype : 'datefield'
	    			}
				}, {
					text : '网点编号',
					cls : 'x-grid-header-1',
					dataIndex : 'STORE_ID',
					width : 150,
					filter: {
	    				xtype : 'textfield'
	    			}
				}, {
					text : '网点名称',
					cls : 'x-grid-header-1',
					dataIndex : 'STORE_NAME',
					width : 80,
					filter: {
	    				xtype : 'textfield'
	    			}
				}, {
					text : '申请人',
					cls : 'x-grid-header-1',
					dataIndex: 'SHENQING_OP',
					width : 70,
					filter: {
	    				xtype : 'textfield'
	    			}
				}, {
					text : '目标公司',
					cls : 'x-grid-header-1',
					dataIndex : 'OBJECTIVE_STORE',
					width : 80,
					filter: {
	    				xtype : 'textfield'
	    			}
				}, {
					text : '审核时间',
					cls : 'x-grid-header-1',
					dataIndex : 'APPLY_DATETIME',
					width : 90,
					filter: {
	    				xtype : 'datefield'
	    			}
				}, {
					text : '审核意见',
					cls : 'x-grid-header-1',
					dataIndex : 'APPLY_RESULT',
					width : 90,
					filter: {
	    				xtype : 'textfield'
	    			}
				}, {
					text : '审核人',
					cls : 'x-grid-header-1',
					dataIndex : 'APPLY_OP',
					width : 70,
					filter: {
	    				xtype : 'textfield'
	    			}
				}, {
					text : '出入库单号',
					cls : 'x-grid-header-1',
					dataIndex : 'INOUTNO',
					width : 110,
					filter: {
	    				xtype : 'textfield'
	    			}
				}, {
					text : '单据类型',
					cls : 'x-grid-header-1',
					dataIndex : 'CLASS',
					width : 100,
					filter: {
	    				xtype : 'textfield'
	    			}
				}, {
					text : '处理人',
					cls : 'x-grid-header-1',
					dataIndex : 'UPDATEMAN',
					width : 70,
					filter: {
	    				xtype : 'textfield'
	    			}
				}, {
					text : '处理时间',
					cls : 'x-grid-header-1',
					dataIndex : 'UPDATEDATE',
					width : 90,
					filter: {
	    				xtype : 'datefield'
	    			}
				} ],
				store : store,
				dockedItems: [{
			        xtype: 'pagingtoolbar',
			        store: store,
			        dock: 'bottom',
			        displayInfo: true
			    }]
			} ]
		});
		me.callParent(arguments);
	}
});