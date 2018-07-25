/**
 * 查看特征定义按钮
 */
Ext.define('erp.view.core.button.FeatureQuery', {
	extend : 'Ext.Button',
	alias : 'widget.erpFeatureQueryButton',
	cls : 'x-btn-gray',
	id : 'FeatureQuery',
	disabled : true,
	text : $I18N.common.button.erpFeatureQueryButton,
	style : {
		marginLeft : '10px'
	},
	width : 100,
	initComponent : function() {
		this.callParent(arguments);
	}
});