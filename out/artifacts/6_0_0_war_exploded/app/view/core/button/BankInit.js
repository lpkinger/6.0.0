Ext.define('erp.view.core.button.BankInit', {
	extend : 'Ext.Button',
	alias : 'widget.erpBankInitButton',
	param : [],
	id : 'confirmbutton',
	text : $I18N.common.button.erpBankInitButton,
	iconCls : 'x-button-icon-save',
	cls : 'x-btn-gray',
	formBind : true,// form.isValid() == false时,按钮disabled
	width : 90,
	style : {
		marginLeft : '10px'
	},
	initComponent : function() {
		this.callParent(arguments);
	}
});