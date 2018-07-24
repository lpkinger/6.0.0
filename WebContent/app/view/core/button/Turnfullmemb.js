/**
 * 批量删除按钮
 */
Ext.define('erp.view.core.button.Turnfullmemb', {
	extend : 'Ext.Button',
	alias : 'widget.erpTurnfullmembButton',
	iconCls : 'x-button-icon-Turnfullmemb',
	cls : 'x-btn-gray',
	id : 'Turnfullmemb',
	tooltip : '人员转正申请单',
	id : 'erpTurnfullmembButton',
	text : $I18N.common.button.erpTurnfullmembButton,
	initComponent : function() {
		this.callParent(arguments);
	},
	handler : function() {
	}
});