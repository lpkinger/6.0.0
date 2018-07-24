Ext.define('erp.view.as.port.StandbyApplication', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 41%',
				saveUrl : 'as/port/saveStandbyApplication.action',
				updateUrl : 'as/port/updateStandbyApplication.action',
				getIdUrl : 'common/getId.action?seq=as_standbyApplication_SEQ',
				deleteUrl : 'as/port/deleteStandbyApplication.action',
				submitUrl : 'as/port/submitStandbyApplication.action',
				resSubmitUrl : 'as/port/resSubmitStandbyApplication.action',
				auditUrl : 'as/port/auditStandbyApplication.action',
				resAuditUrl : 'as/port/resAuditStandbyApplication.action',
				keyField : 'sa_id',
				statusField: 'sa_status',
				codeField : 'sa_code'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 59%', 
				detno: 'sad_deptno',
				keyField: 'sad_id',
				mainField: 'sad_said'
			}]
		});
		me.callParent(arguments);
	}
});