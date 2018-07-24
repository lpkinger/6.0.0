Ext.define('erp.view.scm.product.Sampleapply',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',
				saveUrl: 'scm/product/saveSampleapply.action',
				deleteUrl: 'scm/product/deleteSampleapply.action',
				updateUrl: 'scm/product/updateSampleapply.action',		
				getIdUrl: 'common/getId.action?seq=Sampleapply_SEQ',
				auditUrl: 'scm/product/auditSampleapply.action',
				resAuditUrl: 'scm/product/resAuditSampleapply.action',
				submitUrl: 'scm/product/submitSampleapply.action',
				resSubmitUrl: 'scm/product/resSubmitSampleapply.action',
				keyField: 'sa_id'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 60%', 
				necessaryField: 'sd_prodcode',
				keyField: 'sd_id',
				detno: 'sd_detno',
				mainField: 'sd_said'
			}]
		}); 
		me.callParent(arguments); 
	} 
});