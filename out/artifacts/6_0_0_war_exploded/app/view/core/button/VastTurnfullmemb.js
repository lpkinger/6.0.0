/**
 * 批量删除按钮
 */
Ext.define('erp.view.core.button.VastTurnfullmemb', {
	extend : 'Ext.Button',
	alias : 'widget.erpVastTurnfullmembButton',
	iconCls : 'x-button-icon-delete',
	cls : 'x-btn-gray',
	id : 'VastTurnfullmemb',
	tooltip : '人员批量转正',
	id : 'erpVastTurnfullmembButton',
	text : $I18N.common.button.erpVastTurnfullmembButton,
	initComponent : function() {
		this.callParent(arguments);
	},
	handler : function() {
	}
});