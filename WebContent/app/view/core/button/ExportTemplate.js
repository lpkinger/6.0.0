Ext.define('erp.view.core.button.ExportTemplate', {
	extend : 'Ext.Button',
	alias : 'widget.erpExportTemplateButton',
	iconCls : 'x-button-icon-template',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpExportTemplateButton,
	style : {
		marginLeft : '10px'
	},
	width : 90,
	enableToggle : true,
	pressed : true,
	initComponent : function() {
		this.callParent(arguments);
	}
});