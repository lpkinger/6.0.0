Ext.define('erp.view.core.button.MakeFeeClose', {
	extend : 'Ext.Button',
	alias : 'widget.erpMakeFeeCloseButton',
	param : [],
	text : $I18N.common.button.erpMakeFeeCloseButton,
	iconCls : 'x-button-icon-check',
	id : 'makeFeeCloseButton',
	cls : 'x-btn-gray',
	width : 120,
	style : {
		marginLeft : '10px'
	},
	initComponent : function() {
		this.callParent(arguments);
	}
});