Ext.define('erp.view.scm.purchase.inquiryVastDatalist.Viewport', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				anchor : '100% 100%',
				xtype : 'erpInquiryVastDatalistGridPanel'
			} ]
		});
		me.callParent(arguments);
	}
});