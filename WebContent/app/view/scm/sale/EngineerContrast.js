Ext.define('erp.view.scm.sale.EngineerContrast',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 60%',
					saveUrl: 'scm/sale/saveEngineerContrast.action',
					deleteUrl: 'scm/sale/deleteEngineerContrast.action',
					updateUrl: 'scm/sale/updateEngineerContrast.action',
					getIdUrl: 'common/getId.action?seq=EngineerContrast_SEQ',
					auditUrl: 'scm/sale/auditEngineerContrast.action',
					resAuditUrl: 'scm/sale/resAuditEngineerContrast.action',
					submitUrl: 'scm/sale/submitEngineerContrast.action',
					resSubmitUrl: 'scm/sale/resSubmitEngineerContrast.action',
					keyField: 'ec_id',
					codeField: 'ec_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 40%', 
					keyField: 'ecd_id',
					detno: 'ecd_detno',
					mainField: 'ecd_ecid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});