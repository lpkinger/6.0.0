/**
 * 选择账套
 */
Ext.define('erp.view.core.button.GetB2CProductKind', {
	extend : 'Ext.Button',
	alias : 'widget.erpGetB2CProductKindButton',
	iconCls : 'x-button-icon-query',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpGetB2CProductKindButton,
	style : {
		marginLeft : '10px'
	},
	width : 90,
	id:'getb2cproductkind',
	initComponent : function() {
		this.callParent(arguments);
	}
});