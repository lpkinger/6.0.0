/**
 * 无价打印按钮
 */
Ext.define('erp.view.core.button.PrintNoPrice', {
	extend : 'Ext.Button',
	alias : 'widget.erpPrintNoPriceButton',
	iconCls : 'x-button-icon-print',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpPrintNoPriceButton,
	style : {
		marginLeft : '10px'
	},
	width : 90,
	initComponent : function() {
		this.callParent(arguments);
	}
});