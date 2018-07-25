/**
 * 导出按钮
 */
Ext.define('erp.view.core.button.Export', {
	extend : 'Ext.Button',
	require : ['erp.util.BaseUtil'],
	alias : 'widget.erpExportButton',
	iconCls : 'x-button-icon-excel',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpExportButton,
	style : {
		marginLeft : '10px'
	},
	width : 60,
	initComponent : function() {
		this.callParent(arguments);
	},
	handler : function() {
		var grid = Ext.getCmp('grid'),
			util = grid.BaseUtil || Ext.create('erp.util.BaseUtil');
		util.exportGrid(grid);
	}
});