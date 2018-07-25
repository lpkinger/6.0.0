/**
 * (有价)打印按钮
 * 和PrintwithPrice一样，只是某些客户需要默认有价
 */
Ext.define('erp.view.core.button.PrintPrice', {
	extend : 'Ext.Button',
	alias : 'widget.erpPrintPriceButton',
	iconCls : 'x-button-icon-print',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpPrintPriceButton,
	style : {
		marginLeft : '10px'
	},
	width : 60,
	initComponent : function() {
		this.callParent(arguments);
	}
});