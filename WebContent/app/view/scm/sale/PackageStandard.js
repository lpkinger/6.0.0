Ext.define('erp.view.scm.sale.PackageStandard',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/sale/savePackageStandard.action',
				deleteUrl: 'scm/sale/deletePackageStandard.action',
				updateUrl: 'scm/sale/updatePackageStandard.action',
				auditUrl: 'common/auditCommon.action?caller=' +caller,
				resAuditUrl: 'common/resAuditCommon.action?caller=' +caller,
				submitUrl: 'common/submitCommon.action?caller=' +caller,
				resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller,
				bannedUrl: 'common/bannedCommon.action?caller='+caller,
				resBannedUrl: 'common/resBannedCommon.action?caller='+caller,
				getIdUrl: 'common/getId.action?seq=PACKAGESTANDARD_SEQ',
				keyField: 'ps_id',
				codeField: 'ps_code'
			}]
		}); 
		me.callParent(arguments); 
	} 
});