Ext.define('erp.view.common.VisitERP.ActorMaintain', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 30%',
				updateUrl : 'common/VisitERP/updateAM.action',
				getIdUrl : 'common/getId.action?seq=ROLEPERMISSION_SEQ',
				keyField : 'rp_id',
				codeField : 'rp_code'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 70%',
				keyField : 'rp_id',
				mainField : 'rp_code'
			} ]
		});
		me.callParent(arguments);
	}
});