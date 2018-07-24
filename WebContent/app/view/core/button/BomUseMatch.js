Ext.define('erp.view.core.button.BomUseMatch', {
	extend : 'Ext.Button',
	alias : 'widget.erpBomUseMatchButton',
	cls : 'x-btn-gray',
	id : 'BomUseMatch',
	text : $I18N.common.button.erpBomUseMatchButton,
	width : 100,
	initComponent : function() {
		this.callParent(arguments);
	}
});