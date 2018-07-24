Ext.define('erp.view.scm.purchase.PreVendor',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/purchase/savePreVendor.action',
				deleteUrl: 'scm/purchase/deletePreVendor.action',
				updateUrl: 'scm/purchase/updatePreVendor.action',
				auditUrl: 'scm/purchase/auditPreVendor.action',
				resAuditUrl: 'scm/purchase/resAuditPreVendor.action',
				submitUrl: 'scm/purchase/submitPreVendor.action',
				resSubmitUrl: 'scm/purchase/resSubmitPreVendor.action',					
				getIdUrl: 'common/getId.action?seq=PREVENDOR_SEQ',
				keyField: 've_id',
				codeField: 've_code',
				statusField: 've_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});