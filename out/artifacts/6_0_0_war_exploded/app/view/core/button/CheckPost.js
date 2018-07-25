/**
 * 结账按钮
 */
Ext.define('erp.view.core.button.CheckPost', {
	extend : 'Ext.Button',
	alias : 'widget.erpCheckPostButton',
	iconCls : 'x-button-icon-start',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpCheckPostButton,
	style : {
		marginLeft : '10px'
	},
	width : 65,
	initComponent : function() {
		this.callParent(arguments);
	}
});