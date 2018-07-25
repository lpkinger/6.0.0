/**
 * 转领料单
 */
Ext.define('erp.view.core.button.TurnProdIOGet', {
	extend : 'Ext.Button',
	alias : 'widget.erpTurnProdIOGetButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpTurnProdIOGetButton,
	style : {
		marginLeft : '10px'
	},
	width : 90,
	initComponent : function() {
		this.callParent(arguments);
	}
});