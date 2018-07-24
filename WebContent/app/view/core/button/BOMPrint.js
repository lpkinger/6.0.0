/**
 * BOM打印
 */
Ext.define('erp.view.core.button.BOMPrint', {
	extend : 'Ext.Button',
	alias : 'widget.erpBOMPrintButton',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpBOMPrintButton,
	width : 90,
	id : 'BOMPrint',
	initComponent : function() {
		this.callParent(arguments);
	}
});