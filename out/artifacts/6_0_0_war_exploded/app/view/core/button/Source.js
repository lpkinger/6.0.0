/**
 * 查看单据来源按钮
 */
Ext.define('erp.view.core.button.Source', {
	extend : 'Ext.Button',
	alias : 'widget.erpSourceButton',
	iconCls : 'x-button-icon-source',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpSourceButton,
	style : {
		marginLeft : '10px'
	},
	width : 90,
	initComponent : function() {
		this.callParent(arguments);
	}
});