Ext.define('erp.view.ma.MobileCondition',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'grid',
				id: 'mobileCondition-grid',
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
					id: 'mobile_count'
				}],
				plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
				columns: [{
					text: '登录账号',
					dataIndex: 'EM_CODE',
					cls: 'x-grid-header-1',
					flex: 0.8,
					filter: {xtype:"textfield", filterName:"EM_CODE"}
				},{
					text: '姓名',
					dataIndex: 'EM_NAME',
					cls: 'x-grid-header-1',
					flex: 0.8,
					filter: {xtype:"textfield", filterName:"EM_NAME"}
				},{
					text: '账套',
					dataIndex: 'MA_USER',
					cls: 'x-grid-header-1',
					flex: 0.5,
					filter: {xtype:"textfield", filterName:"MA_USER"}
				},{
					text: '账套名称',
					dataIndex: 'MA_FUNCTION',
					cls: 'x-grid-header-1',
					flex: 0.5,
					filter: {xtype:"textfield", filterName:"MA_FUNCTION"}
				},{
					text: '最近登陆时间',
					dataIndex: 'EM_MOLOGINTIME',
					cls: 'x-grid-header-1',
					flex: 1.5,
					filter: {xtype:"datefield", filterName:"EM_MOLOGINTIME"},
					renderer: function(val) {
						if(val != null) {
							return Ext.Date.format(new Date(val), 'Y-m-d H:i:s');
						}
						return null;
					}
				}],
				columnLines: true,
				enableColumnResize: true,
				store: Ext.create('Ext.data.Store',{
					fields: ['EM_CODE', 'EM_NAME', 'MA_USER', 'MA_FUNCTION', 'EM_MOLOGINTIME'],
					proxy: {
				        type: 'ajax',
				        url : basePath + 'mobile/ma/user/getLoginedEmployees.action',
				        reader: {
				            type: 'json',
				            root: 'data'
				        }
				    },
				    autoLoad: true,
				    listeners: {
				    	load: function(store, items) {
				    		Ext.getCmp('mobile_count').setText('共' + items.length + '人使用过移动客户端');
				    	}
				    }
				}),
 		        selModel: new Ext.selection.CellModel()
			}] 
		}); 
		me.callParent(arguments); 
	} 
});