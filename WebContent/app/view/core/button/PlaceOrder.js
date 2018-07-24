/**
 * 下单按钮
 */
Ext.define('erp.view.core.button.PlaceOrder', {
	extend : 'Ext.Button',
	alias : 'widget.erpPlaceOrderButton',
	iconCls : 'x-button-icon-add',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpPlaceOrderButton,
	style : {
		marginLeft : '10px'
	},
	width : 60,
	initComponent : function() {
		this.callParent(arguments);
	}
});