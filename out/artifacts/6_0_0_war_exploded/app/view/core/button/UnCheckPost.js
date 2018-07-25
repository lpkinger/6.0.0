/**
 * 反结账按钮
 */
Ext.define('erp.view.core.button.UnCheckPost', {
	extend : 'Ext.Button',
	alias : 'widget.erpUnCheckPostButton',
	iconCls : 'x-button-icon-stop',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpUnCheckPostButton,
	style : {
		marginLeft : '10px'
	},
	width : 80,
	initComponent : function() {
		this.callParent(arguments);
	}
});