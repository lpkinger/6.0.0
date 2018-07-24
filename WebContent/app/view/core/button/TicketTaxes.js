/**
 * 更新票税信息
 */
Ext.define('erp.view.core.button.TicketTaxes', {
	extend : 'Ext.Button',
	alias : 'widget.erpTicketTaxesButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpTicketTaxesButton,
	style : {
		marginLeft : '10px'
	},
	width : 120,
	initComponent : function() {
		this.callParent(arguments);
	}
});