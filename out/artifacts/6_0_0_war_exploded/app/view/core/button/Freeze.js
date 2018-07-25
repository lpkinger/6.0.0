/**
 * 冻结按钮
 */
Ext.define('erp.view.core.button.Freeze', {
	extend : 'Ext.Button',
	alias : 'widget.erpFreezeButton',
	iconCls : 'x-button-icon-check',
	cls : 'x-btn-gray',
	id : 'Freeze',
	text : $I18N.common.button.erpFreezeButton,
	style : {
		marginLeft : '10px'
	},
	initComponent : function() {
		this.callParent(arguments);
	}
});