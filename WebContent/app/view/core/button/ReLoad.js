/**
 * 重新载入
 */
Ext.define('erp.view.core.button.ReLoad', {
	extend : 'Ext.Button',
	alias : 'widget.erpReLoadButton',
	param : [],
	id : 'reload',
	text : $I18N.common.button.erpReLoadButton,
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	style : {
		marginLeft : '10px'
	},
	initComponent : function() {
		this.callParent(arguments);
	}
});