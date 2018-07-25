Ext.define('erp.view.common.EmpDbfind.Viewport', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		Ext.apply(this, {
			items : [ {
				anchor : '100% 100%',
				xtype : 'emptreegrid'
			} ]
		});
		this.callParent(arguments);
	}
});