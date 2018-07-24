Ext.define('erp.view.scm.sale.MakeForecast', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 25%',
				saveUrl : 'scm/sale/saveMakeForecast.action',
				deleteUrl : 'scm/sale/deleteMakeForecast.action',
				updateUrl : 'scm/sale/updateMakeForecast.action',
				auditUrl : 'scm/sale/auditMakeForecast.action',
				resAuditUrl : 'scm/sale/resAuditMakeForecast.action',
				submitUrl : 'scm/sale/submitMakeForecast.action',
				resSubmitUrl : 'scm/sale/resSubmitMakeForecast.action',
				getIdUrl : 'common/getId.action?seq=MakeForecast_SEQ',
				keyField : 'mf_id',
				statusField : 'mf_statuscode'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 75%',
				detno : 'mfd_detno',
				necessaryField : 'mfd_prodid',
				keyField : 'mfd_id',
				mainField : 'mfd_mfid'
			} ]
		});
		me.callParent(arguments);
	}
});