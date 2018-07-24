Ext.define('erp.view.salary.SalaryRequest', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'salaryRequest/saveRequire.action',
				updateUrl: 'salaryRequest/updateRequire.action',
				deleteUrl: 'salaryRequest/deleteRequire.action',
				auditUrl:'salaryRequest/auditRequire.action',
				resAuditUrl: 'salaryRequest/resAuditRequire.action',
				submitUrl: 'salaryRequest/submitRequire.action',
				resSubmitUrl: 'salaryRequest/resSubmitRequire.action',
				getIdUrl: 'common/getId.action?seq=salarypassword_SEQ',
				keyField: 'sp_id',
				statusField: 'sp_statuscode'
			}]
		});
		me.callParent(arguments);
	}
});