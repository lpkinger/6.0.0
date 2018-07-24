Ext.define('erp.view.drp.distribution.CargoApplicationCu',{ 
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
					saveUrl: 'drp/distribution/saveCargoApplicationCu.action',
					deleteUrl: 'drp/distribution/deleteCargoApplicationCu.action',
					updateUrl: 'drp/distribution/updateCargoApplicationCu.action',
					auditUrl: 'drp/distribution/auditCargoApplicationCu.action',
					resAuditUrl: 'drp/distribution/resAuditCargoApplicationCu.action',
					submitUrl: 'drp/distribution/submitCargoApplicationCu.action',
					resSubmitUrl: 'drp/distribution/resSubmitCargoApplicationCu.action',
					bannedUrl: 'drp/distribution/bannedCargoApplicationCu.action',
					resBannedUrl: 'drp/distribution/resBannedCargoApplicationCu.action',
					getIdUrl: 'common/getId.action?seq=CARGOAPPLICATIONCU_SEQ',
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