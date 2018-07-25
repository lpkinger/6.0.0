/**
 * 转未认定
 */
Ext.define('erp.view.core.button.ResAppstatus', {
	extend : 'Ext.Button',
	alias : 'widget.erpResAppstatusButton',
	iconCls : 'x-button-icon-check',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpResAppstatusButton,
	id : 'erpResAppstatusButton',
	style : {
		marginLeft : '10px'
	},
	initComponent : function() {
		this.callParent(arguments);
	}
});