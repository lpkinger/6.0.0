Ext.define('erp.view.core.button.Refresh', {
	extend : 'Ext.Button',
	alias : 'widget.erpRefreshButton',
	iconCls : 'x-button-icon-reset',
	cls : 'x-btn-gray',
	id : 'refresh',
	text : $I18N.common.button.erpRefreshButton,
	style : {
		marginLeft : '10px'
	},
	width : 60,
	initComponent : function() {
		this.callParent(arguments);
	}
});