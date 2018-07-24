Ext.define('erp.view.pm.bom.PreProductBOM',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'pm/bom/savePreProductBOM.action',
				deleteUrl: 'pm/bom/deletePreProductBOM.action',
				updateUrl: 'pm/bom/updatePreProductBOM.action',
				auditUrl: 'pm/bom/auditPreProductBOM.action',
				resAuditUrl: 'pm/bom/resAuditPreProductBOM.action',
				submitUrl: 'pm/bom/submitPreProductBOM.action',
				resSubmitUrl: 'pm/bom/resSubmitPreProductBOM.action',
				getIdUrl: 'common/getId.action?seq=PREPRODUCT_SEQ',
				keyField: 'pre_id',
				statusField: 'pre_status',
				codeField: 'pre_code'
			}]
		}); 
		me.callParent(arguments); 
	} 
});