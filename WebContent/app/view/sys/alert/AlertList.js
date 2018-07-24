Ext.define('erp.view.sys.alert.AlertList', {
	extend : 'Ext.Viewport',
	layout : 'fit',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				layout : 'anchor',
				items :  {
					xtype : 'erpFormPanel',
					anchor : '100% 100%',
					getIdUrl : 'common/getId.action?seq=Alert_Data_SEQ',
					keyField : 'ad_id',
					statusField : 'ad_status',
					codeField : 'ad_statuscode'
				}
			} ]
		});
		me.callParent(arguments);
	}
});