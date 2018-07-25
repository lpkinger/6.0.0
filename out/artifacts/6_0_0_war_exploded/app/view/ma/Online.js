Ext.define('erp.view.ma.Online',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'grid',
				id: 'online-grid',
				anchor: '100% 100%',
				tbar: ['->',{
					cls: 'x-btn-blue',
					id: 'refresh',
					text: $I18N.common.button.erpRefreshButton,
					width: 80,
					margin: '0 0 0 5'
				},{
					cls: 'x-btn-blue',
					id: 'close',
					text: $I18N.common.button.erpCloseButton,
					width: 80,
					margin: '0 0 0 5'
				},'->',{
					xtype: 'tbtext',
					id: 'online_count'
				}],
				plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
				columns: [{
					text: 'ID',
					dataIndex: 'em_id',
					hidden: true
				},{
					text: '登录账号',
					dataIndex: 'em_code',
					cls: 'x-grid-header-1',
					flex: 0.8,
					filter: {xtype:"textfield", filterName:"em_code"}
				},{
					text: '姓名',
					dataIndex: 'em_name',
					cls: 'x-grid-header-1',
					flex: 0.8,
					filter: {xtype:"textfield", filterName:"em_name"}
				},{
					text: '登录IP',
					dataIndex: 'ip',
					cls: 'x-grid-header-1',
					flex: 1.5,
					filter: {xtype:"textfield", filterName:"ip"}
				},{
					text: '账套',
					dataIndex: 'sob',
					cls: 'x-grid-header-1',
					flex: 0.5,
					filter: {xtype:"textfield", filterName:"sob"}
				},{
					text: '最近请求时间',
					dataIndex: 'date',
					cls: 'x-grid-header-1',
					flex: 1.5,
					filter: {xtype:"datefield", filterName:"date"},
					renderer: function(val) {
						if(val != null) {
							return Ext.Date.format(new Date(val), 'Y-m-d H:i:s');
						}
						return null;
					}
				},{
					text: 'SessionID',
					dataIndex: 'sid',
					cls: 'x-grid-header-1',
					filter: {xtype:"textfield", filterName:"sid"},
					flex: 2.5
				},{
					text: '',
					dataIndex: 'lock',
					cls: 'x-grid-header-1',
					flex: 0.5,
					renderer: function(val, meta, record, x, y, store) {
						if(val == 'true') {
							return "<font color=red>已锁定</font>";
						}
						return '<button class="custom-button" onclick="Ext.getCmp(\'online-grid\').fireEvent(\'lockitem\', ' + 
							x + ')">锁定</button>';
					}
				}],
				columnLines: true,
				enableColumnResize: true,
				store: Ext.create('Ext.data.Store',{
					fields: [{name: 'em_id', type: 'number'}, 'em_code', 'em_name', 'ip', 'sob', 'date', 'sid', 'lock'],
					proxy: {
				        type: 'ajax',
				        url : basePath + 'ma/user/online.action',
				        reader: {
				            type: 'json',
				            root: 'data'
				        }
				    },
				    autoLoad: true,
				    listeners: {
				    	load: function(store, items) {
				    		Ext.getCmp('online_count').setText('共' + items.length + '人在线');
				    	}
				    }
				}),
 		        selModel: new Ext.selection.CellModel()
			}] 
		}); 
		me.callParent(arguments); 
	} 
});