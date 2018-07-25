Ext.define('erp.view.crm.chance.BusinessChance',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				caller:'BusinessChance',
				anchor: '100% 60%',
				saveUrl: 'crm/chance/saveBusinessChance.action',
				deleteUrl: 'crm/chance/deleteBusinessChance.action',
				updateUrl: 'crm/chance/updateBusinessChance.action',
				getIdUrl: 'common/getId.action?seq=BusinessChance_SEQ',
				auditUrl: 'crm/chance/auditBusinessChance.action',
				resAuditUrl: 'crm/chance/resAuditBusinessChance.action',
				submitUrl: 'crm/chance/submitBusinessChance.action',
				resSubmitUrl: 'crm/chance/resSubmitBusinessChance.action',
				endUrl: 'crm/chance/endBusinessChance.action',
				resEndUrl: 'crm/chance/resEndBusinessChance.action',
				keyField: 'bc_id',				
				codeField: 'bc_code'
			},
    		{
				title : '商机动态',
				xtype: 'erpGridPanel2',
				anchor: '100% 40%', 
				//necessaryField: 'ppd_costname',
				keyField: 'bcd_id',
				mainField: 'bcd_bcid',	
			}
		]
		}); 
		me.callParent(arguments); 
	} 
});