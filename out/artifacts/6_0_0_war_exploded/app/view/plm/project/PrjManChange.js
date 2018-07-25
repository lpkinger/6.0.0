Ext.define('erp.view.plm.project.PrjManChange',{ 
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
					anchor: '100% 40%',
					saveUrl: 'plm/project/savePrjManChange.action',
					deleteUrl: 'plm/project/deletePrjManChange.action',
					updateUrl: 'plm/project/updatePrjManChange.action',
					auditUrl: 'plm/project/auditPrjManChange.action',
					resAuditUrl: 'plm/project/resAuditPrjManChange.action',
					submitUrl: 'plm/project/submitPrjManChange.action',
					resSubmitUrl: 'plm/project/resSubmitPrjManChange.action',
					getIdUrl: 'common/getId.action?seq=PrjManChange_SEQ',
					keyField: 'mc_id',
					codeField: 'mc_code',
					statusField: 'mc_status'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					detno: 'mcd_detno',
					//necessaryField: '',
					keyField: 'mcd_id',
					mainField: 'mcd_mcid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});