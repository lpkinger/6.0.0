/**
 * 修改用料
 */
Ext.define('erp.view.core.button.ModifyMaterial', {
	extend : 'Ext.Button',
	alias : 'widget.erpModifyMaterialButton',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpModifyMaterialButton,
	width : 90,
	id : 'ModifyMaterial',
	initComponent : function() {
		this.callParent(arguments);
	}
});