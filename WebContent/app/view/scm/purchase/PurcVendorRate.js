Ext.define('erp.view.scm.purchase.PurcVendorRate',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',
				saveUrl:'scm/purchase/savePurcVendorRate.action',
				deleteUrl: 'scm/purchase/deletePurcVendorRate.action',
				updateUrl: 'scm/purchase/updatePurcVendorRate.action',
				auditUrl: 'scm/purchase/auditPurcVendorRate.action',
				resAuditUrl: 'scm/purchase/resAuditPurcVendorRate.action',
				submitUrl: 'scm/purchase/submitPurcVendorRate.action',
				resSubmitUrl: 'scm/purchase/resSubmitPurcVendorRate.action',
				getIdUrl: 'common/getId.action?seq=PurcVendorRate_SEQ',
				keyField: 'pvr_id',
				statusField: 'pvr_status',
				statuscodeField: 'pvr_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 60%', 
				detno: 'pvd_detno',
				keyField: 'pvd_id',
				mainField: 'pvd_pviid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});