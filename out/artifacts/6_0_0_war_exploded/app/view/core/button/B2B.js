/**
 * 同步至B2B
 */
Ext.define('erp.view.core.button.B2B', {
	extend : 'Ext.Button',
	alias : 'widget.erpB2BButton',
	iconCls : 'x-button-icon-print',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpB2BButton,
	style : {
		marginLeft : '10px'
	},
	width : 90,
	initComponent : function() {
		this.callParent(arguments);
	}
});