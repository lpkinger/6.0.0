Ext.define('erp.view.fa.ars.FARepSet', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 35%',
				saveUrl : 'fa/ars/saveFARepSet.action',
				deleteUrl : 'fa/ars/deleteFARepSet.action',
				updateUrl : 'fa/ars/updateFARepSet.action',
				auditUrl : 'fa/ars/auditFARepSet.action',
				resAuditUrl : 'fa/ars/resAuditFARepSet.action',
				submitUrl : 'fa/ars/submitFARepSet.action',
				resSubmitUrl : 'fa/ars/resSubmitFARepSet.action',
				getIdUrl : 'common/getId.action?seq=FAREPSET_SEQ',
				keyField : 'fs_id',
				codeField : 'fs_code',
				dumpable : true
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 65%',
				detno : 'fsd_detno',
				necessaryField : 'fsd_name',
				keyField : 'fsd_id',
				mainField : 'fsd_fsid'
			} ]
		});
		me.callParent(arguments);
	}
});