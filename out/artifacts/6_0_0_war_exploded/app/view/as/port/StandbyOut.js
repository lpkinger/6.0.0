Ext.define('erp.view.as.port.StandbyOut', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 41%',
				saveUrl : 'as/port/saveStandbyOut.action',
				updateUrl : 'as/port/updateStandbyOut.action',
				getIdUrl : 'common/getId.action?seq=as_standbyOut_SEQ',
				deleteUrl : 'as/port/deleteStandbyOut.action',
				submitUrl : 'as/port/submitStandbyOut.action',
				resSubmitUrl : 'as/port/resSubmitStandbyOut.action',
				auditUrl : 'as/port/auditStandbyOut.action',
				resAuditUrl : 'as/port/resAuditStandbyOut.action',
				keyField : 'so_id',
				statusField: 'so_status',
				codeField : 'so_code'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 59%', 
				detno: 'sod_deptno',
				keyField: 'sod_id',
				mainField: 'sod_soid'
			}]
		});
		me.callParent(arguments);
	}
});