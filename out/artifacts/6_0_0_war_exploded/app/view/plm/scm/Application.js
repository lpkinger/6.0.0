Ext.define('erp.view.plm.scm.Application',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'applicationViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 40%',
					saveUrl: 'plm/application/saveApplication.action',
					deleteUrl: 'plm/application/deleteApplication.action',
					updateUrl: 'plm/application/updateApplication.action',
					auditUrl: 'plm/application/auditApplication.action',
					resAuditUrl: 'plm/application/resAuditApplication.action',
					submitUrl: 'plm/application/submitApplication.action',
					resSubmitUrl: 'plm/application/resSubmitApplication.action',
					getIdUrl: 'common/getId.action?seq=APPLICATION_SEQ',
					keyField: 'ap_id',
					codeField: 'ap_code',
					statusField: 'ap_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					detno: 'ad_detno',
					necessaryField: 'ad_prodcode',
					keyField: 'ad_id',
					mainField: 'ad_apid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});