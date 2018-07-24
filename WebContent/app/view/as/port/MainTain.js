Ext.define('erp.view.as.port.MainTain', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 50%',
				saveUrl : 'as/port/saveMainTain.action',
				updateUrl : 'as/port/updateMainTain.action',
				getIdUrl : 'common/getId.action?seq=mainTain_user_SEQ',
				deleteUrl : 'as/port/deleteMainTain.action',
				submitUrl : 'as/port/submitMainTain.action',
				resSubmitUrl : 'as/port/resSubmitMainTain.action',
				auditUrl : 'as/port/auditMainTain.action',
				resAuditUrl : 'as/port/resAuditMainTain.action',
				marketUrl:'as/port/marketMainTain.action',
				keyField : 'mt_id',
				statusField: 'mt_status',
				codeField : 'mt_code'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'mtd_deptno',
				keyField: 'mtd_id',
				mainField: 'mtd_mtid'
			}]
		});
		me.callParent(arguments);
	}
});