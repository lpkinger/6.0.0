Ext.define('erp.view.scm.reserve.WarehouseMan', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 20%',
				updateUrl : 'scm/reserve/updateWarehouseMan.action',
				getIdUrl : 'common/getId.action?seq=WAREHOUSE_SEQ',
				keyField : 'wh_id',
				codeField : 'wh_code'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 80%',
				keyField : 'wm_id',
				mainField : 'wm_whid',
				detno : 'wm_detno',
				necessaryField : 'wm_cgycode'
			} ]
		});
		me.callParent(arguments);
	}
});