Ext.define('erp.view.core.button.CreateTemplate', {
	extend : 'Ext.Button',
	alias : 'widget.erpCreateTemplateButton',
	iconCls : 'x-button-icon-template',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpCreateTemplateButton,
	style : {
		marginLeft : '10px'
	},
	width : 100,
	initComponent : function() {
		this.callParent(arguments);
	}
});