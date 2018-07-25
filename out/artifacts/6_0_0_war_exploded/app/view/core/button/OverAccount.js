Ext.define('erp.view.core.button.OverAccount', {
	extend : 'Ext.Button',
	alias : 'widget.erpOverAccountButton',
	param : [],
	id : 'overaccountbutton',
	text : $I18N.common.button.erpOverAccountButton,
	iconCls : 'x-button-icon-stop',
	cls : 'x-btn-gray',
	width : 80,
	style : {
		marginLeft : '10px'
	},
	initComponent : function() {
		this.callParent(arguments);
	}
});