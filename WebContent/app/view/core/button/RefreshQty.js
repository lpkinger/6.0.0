/**
 * 刷新已转数
 */
Ext.define('erp.view.core.button.RefreshQty', {
	extend : 'Ext.Button',
	alias : 'widget.erpRefreshQtyButton',
	param : [],
	id : 'erpRefreshQtyButton',
	text : $I18N.common.button.erpRefreshQtyButton,
	iconCls : 'x-button-icon-reset',
	cls : 'x-btn-gray',
	width : 100,
	style : {
		marginLeft : '10px'
	},
	initComponent : function() {
		this.callParent(arguments);
	}
});