Ext.define('erp.view.common.vastDatalist.Viewport', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				anchor : '100% 100%',
				xtype : 'erpVastDatalistGridPanel'
			} ]
		});
		me.callParent(arguments);
	}
});