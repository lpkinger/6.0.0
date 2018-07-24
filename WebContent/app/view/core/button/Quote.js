/**
 * 报价按钮
 */
Ext.define('erp.view.core.button.Quote', {
	extend : 'Ext.Button',
	alias : 'widget.erpQuoteButton',
	iconCls : 'x-button-icon-add',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpQuoteButton,
	style : {
		marginLeft : '10px'
	},
	width : 60,
	initComponent : function() {
		this.callParent(arguments);
	}
});