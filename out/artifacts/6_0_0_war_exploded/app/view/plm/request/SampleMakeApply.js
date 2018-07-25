Ext.define('erp.view.plm.request.SampleMakeApply',{
	extend: 'Ext.Viewport', 
	layout:'anchor',
	hideBorders: true, 
	initComponent:function(){
		var me =this;
		Ext.apply(me,{ 
			items: [{
				xtype: 'erpFormPanel',
				id:'form',
				anchor: '100% 100%',
				saveUrl: 'plm/request/saveSampleMakeApply.action?caller=' +caller,
				updateUrl: 'plm/request/updateSampleMakeApply.action?caller=' +caller,
				deleteUrl: 'plm/request/deleteSampleMakeApply.action',
				auditUrl:'plm/request/auditSampleMakeApply.action',
//				turnApplication:'plm/request/turnApplication.action',
				resAuditUrl: 'plm/request/resAuditSampleMakeApply.action',
				submitUrl: 'plm/request/submitSampleMakeApply.action',
				resSubmitUrl: 'plm/request/resSubmitSampleMakeApply.action',
				getIdUrl: 'common/getId.action?seq=SampleMakeApply_SEQ',
				keyField: 'sm_id',
				statusField: 'sm_statuscode'
			}]
		});
		me.callParent(arguments); 
	}
})