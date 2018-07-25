Ext.define('erp.view.crm.chance.BusinessChanceData',{ 
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
					anchor: '100% 100%',
					saveUrl: 'crm/chance/saveBusinessChanceData.action',
					deleteUrl: 'crm/chance/deleteBusinessChanceData.action',
					updateUrl: 'crm/chance/updateBusinessChanceData.action',
					getIdUrl: 'common/getId.action?seq=BusinessChanceData_SEQ',
					auditUrl: 'crm/chance/auditBusinessChanceData.action',
					resAuditUrl: 'crm/chance/resAuditBusinessChanceData.action',
					submitUrl: 'crm/chance/submitBusinessChanceData.action',
					resSubmitUrl: 'crm/chance/resSubmitBusinessChanceData.action',
					keyField: 'bcd_id',
					codeField: 'bcd_code',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});