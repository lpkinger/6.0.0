Ext.define('erp.view.crm.customercare.Giacceptance',{ 
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
					saveUrl: 'crm/customercare/saveGiacceptance.action',
					deleteUrl: 'crm/customercare/deleteGiacceptance.action',
					updateUrl: 'crm/customercare/updateGiacceptance.action',		
					getIdUrl: 'common/getId.action?seq=Giacceptance_SEQ',
					auditUrl: 'crm/customercare/auditGiacceptance.action',
					resAuditUrl: 'crm/customercare/resAuditGiacceptance.action',
					submitUrl: 'crm/customercare/submitGiacceptance.action',
					resSubmitUrl: 'crm/customercare/resSubmitGiacceptance.action',
					keyField: 'ga_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					necessaryField: 'gad_gicode',
					keyField: 'gad_id',
					detno: '',
					mainField: 'gad_gaid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});