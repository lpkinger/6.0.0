Ext.define('erp.view.core.button.StartAccount', {
	extend : 'Ext.Button',
	alias : 'widget.erpStartAccountButton',
	param : [],
	id : 'startaccountbutton',
	text : $I18N.common.button.erpStartAccountButton,
	iconCls : 'x-button-icon-start',
	cls : 'x-btn-gray',
	width: 65,
	style : {
		marginLeft : '10px'
	},
	initComponent : function() {
		this.callParent(arguments);
	}
});