Ext.define('erp.view.drp.distribution.CargoApplication',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'drp/distribution/saveCargoApplication.action',
					deleteUrl: 'drp/distribution/deleteCargoApplication.action',
					updateUrl: 'drp/distribution/updateCargoApplication.action',
					auditUrl: 'drp/distribution/auditCargoApplication.action',
					resAuditUrl: 'drp/distribution/resAuditCargoApplication.action',
					submitUrl: 'drp/distribution/submitCargoApplication.action',
					resSubmitUrl: 'drp/distribution/resSubmitCargoApplication.action',
					bannedUrl: 'drp/distribution/bannedCargoApplication.action',
					resBannedUrl: 'drp/distribution/resBannedCargoApplication.action',
					getIdUrl: 'common/getId.action?seq=CARGOAPPLICATION_SEQ',
					keyField: 'ca_id',
					codeField: 'ca_code',
					statusField:'ca_status'
				},{
					xtype:'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'cd_detno',
					keyField: 'cd_id',
					mainField: 'cd_caid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});