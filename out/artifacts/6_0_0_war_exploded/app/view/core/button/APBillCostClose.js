Ext.define('erp.view.core.button.APBillCostClose', {
	extend : 'Ext.Button',
	alias : 'widget.erpAPBillCostCloseButton',
	param : [],
	text : $I18N.common.button.erpAPBillCostCloseButton,
	iconCls : 'x-button-icon-save',
	id : 'APBillCostCloseButton',
	cls : 'x-btn-gray',
	formBind : true,
	width : 150,
	style : {
		marginLeft : '10px'
	},
	initComponent : function() {
		this.callParent(arguments);
	}
});