/**
 * 无PO打印按钮
 */
Ext.define('erp.view.core.button.Printnosale', {
	extend : 'Ext.Button',
	alias : 'widget.erpPrintnosaleButton',
	iconCls : 'x-button-icon-print',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpPrintnosaleButton,
	style : {
		marginLeft : '10px'
	},
	width : 90,
	initComponent : function() {
		this.callParent(arguments);
	}
});