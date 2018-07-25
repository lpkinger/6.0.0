/**
 * 查看特征定义按钮
 */
Ext.define('erp.view.core.button.FeatureDefinition', {
	extend : 'Ext.Button',
	alias : 'widget.erpFeatureDefinitionButton',
	cls : 'x-btn-gray',
	id : 'featuredefinition',
	text : $I18N.common.button.erpFeatureDefinitionButton,
	style : {
		marginLeft : '10px'
	},
	initComponent : function() {
		this.callParent(arguments);
	}
});