Ext.define('erp.view.fa.ars.AssKind',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',
				saveUrl: 'fa/ars/saveAssKind.action',
				deleteUrl: 'fa/ars/deleteAssKind.action',
				updateUrl: 'fa/ars/updateAssKind.action',
				auditUrl: 'fa/ars/auditAssKind.action',
				resAuditUrl: 'fa/ars/resAuditAssKind.action',
				submitUrl: 'fa/ars/submitAssKind.action',
				resSubmitUrl: 'fa/ars/resSubmitAssKind.action',
				getIdUrl: 'common/getId.action?seq=ASSKIND_SEQ',
				keyField: 'ak_id',	
				codefield:'ak_code'
			}]
		}); 
		me.callParent(arguments); 
	} 
});