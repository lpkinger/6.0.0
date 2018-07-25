Ext.define('erp.view.core.button.TurnProductApproval', {
	extend : 'Ext.Button',
	alias : 'widget.erpTurnProductApprovalButton',
	iconCls : 'x-button-icon-delete',
	cls : 'x-btn-gray',
	tooltip : '转认定单',
	text : $I18N.common.button.erpTurnProductApprovalButton,
	initComponent : function() {
		this.callParent(arguments);
	},
	width : 120,
	handler : function() {
	}
});