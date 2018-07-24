Ext.define('erp.view.core.button.VoucherFlow', {
	extend : 'Ext.Button',
	alias : 'widget.erpVoucherFlowButton',
	iconCls : 'x-button-icon-source',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpVoucherFlowButton,
	style : {
		marginLeft : '10px'
	},
	width : 70,
	initComponent : function() {
		this.callParent(arguments);
	}
});