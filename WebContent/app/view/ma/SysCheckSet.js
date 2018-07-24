Ext.define('erp.view.ma.SysCheckSet', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [{
			  anchor:'100% 100%',
			  xtype:'erpGridPanel4'
			}]
		});
		me.callParent(arguments);
	}
});