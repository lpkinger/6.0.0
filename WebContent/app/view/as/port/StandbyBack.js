Ext.define('erp.view.as.port.StandbyBack', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 41%',
				saveUrl : 'as/port/saveStandbyBack.action',
				updateUrl : 'as/port/updateStandbyBack.action',
				getIdUrl : 'common/getId.action?seq=as_standbyBack_SEQ',
				deleteUrl : 'as/port/deleteStandbyBack.action',
				submitUrl : 'as/port/submitStandbyBack.action',
				resSubmitUrl : 'as/port/resSubmitStandbyBack.action',
				auditUrl : 'as/port/auditStandbyBack.action',
				resAuditUrl : 'as/port/resAuditStandbyBack.action',
				keyField : 'sb_id',
				statusField: 'sb_status',
				codeField : 'sb_code'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 59%', 
				detno: 'sbd_deptno',
				keyField: 'sbd_id',
				mainField: 'sbd_sbid'
			}]
		});
		me.callParent(arguments);
	}
});