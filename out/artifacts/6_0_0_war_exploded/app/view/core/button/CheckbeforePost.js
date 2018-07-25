/**
 * 过账按钮
 */
Ext.define('erp.view.core.button.CheckbeforePost', {
	extend : 'Ext.Button',
	alias : 'widget.erpCheckbeforePostButton',
	iconCls : 'x-button-icon-check',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpCheckbeforePostButton,
	style : {
		marginLeft : '10px'
	},
	width : 110,
	initComponent : function() {
		this.callParent(arguments);
	}
});