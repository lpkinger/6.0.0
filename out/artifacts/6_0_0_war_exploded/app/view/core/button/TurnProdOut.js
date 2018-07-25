/**
 * 转出货单
 */
Ext.define('erp.view.core.button.TurnProdOut', {
	extend : 'Ext.Button',
	alias : 'widget.erpTurnProdOutButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpTurnProdOutButton,
	style : {
		marginLeft : '10px'
	},
	width : 110,
	initComponent : function() {
		this.callParent(arguments);
	}
});