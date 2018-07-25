Ext.define('erp.view.pm.mould.PurcMould',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'PurcMouldViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'pm/mould/savePurcMould.action',
					deleteUrl: 'pm/mould/deletePurcMould.action',
					updateUrl: 'pm/mould/updatePurcMould.action',
					auditUrl: 'pm/mould/auditPurcMould.action',
					printUrl: 'pm/mould/printModAlter.action',
					resAuditUrl: 'pm/mould/resAuditPurcMould.action',
					submitUrl: 'pm/mould/submitPurcMould.action',
					resSubmitUrl: 'pm/mould/resSubmitPurcMould.action',
					getIdUrl: 'common/getId.action?seq=PurcMould_SEQ',
					keyField: 'pm_id',
					codeField: 'pm_code',
					statusField: 'pm_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'pmd_detno',
					necessaryField: 'pmd_pscode',
					keyField: 'pmd_id',
					mainField: 'pmd_pmid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});