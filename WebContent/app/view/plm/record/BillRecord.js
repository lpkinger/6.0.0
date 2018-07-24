Ext.define('erp.view.plm.record.BillRecord', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 100%',
				autoScroll : true,
				keyField : 'wr_id',
				dumpable:true,				
			} ]
		});
		me.callParent(arguments);
	}
});