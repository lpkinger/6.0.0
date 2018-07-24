/**
 * 特征查看按钮
 */
Ext.define('erp.view.core.button.FeatureView', {
	extend : 'Ext.Button',
	alias : 'widget.erpFeatureViewButton',
	cls : 'x-btn-gray',
	id : 'featureview',
	text : $I18N.common.button.erpFeatureViewButton,
	style : {
		marginLeft : '10px'
	},
	initComponent : function() {
		this.callParent(arguments);
	}
});