Ext.define('erp.view.oa.appliance.Oaapplication',{ 
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
					saveUrl: 'oa/appliance/saveOaapplication.action',
					deleteUrl: 'oa/appliance/deleteOaapplication.action',
					updateUrl: 'oa/appliance/updateOaapplication.action',		
					getIdUrl: 'common/getId.action?seq=Oaapplication_SEQ',
					auditUrl: 'oa/appliance/auditOaapplication.action',
					printUrl: 'oa/appliance/printOaapplication.action',
					resAuditUrl: 'oa/appliance/resAuditOaapplication.action',
					submitUrl: 'oa/appliance/submitOaapplication.action',
					resSubmitUrl: 'oa/appliance/resSubmitOaapplication.action',
					codeField:'oa_code',
					keyField: 'oa_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					necessaryField: 'od_procode',
					keyField: 'od_id',
					detno: 'od_detno',
					mainField: 'od_oaid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});