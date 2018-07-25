Ext.define('erp.view.crm.customercare.Gipurchase',{ 
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
					saveUrl: 'crm/customercare/saveGipurchase.action',
					deleteUrl: 'crm/customercare/deleteGipurchase.action',
					updateUrl: 'crm/customercare/updateGipurchase.action',		
					getIdUrl: 'common/getId.action?seq=Gipurchase_SEQ',
					auditUrl: 'crm/customercare/auditGipurchase.action',
					resAuditUrl: 'crm/customercare/resAuditGipurchase.action',
					submitUrl: 'crm/customercare/submitGipurchase.action',
					resSubmitUrl: 'crm/customercare/resSubmitGipurchase.action',
					keyField: 'gp_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					necessaryField: 'gpd_gicode',
					keyField: 'gpd_id',
					detno: 'gpd_detno',
					mainField: 'gpd_gpid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});