Ext.define('erp.view.oa.appliance.Oapurchase',{ 
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
					saveUrl: 'oa/appliance/saveOapurchase.action',
					deleteUrl: 'oa/appliance/deleteOapurchase.action',
					updateUrl: 'oa/appliance/updateOapurchase.action',
					printUrl: 'oa/appliance/printOapurchase.action',	
					getIdUrl: 'common/getId.action?seq=Oapurchase_SEQ',
					auditUrl: 'oa/appliance/auditOapurchase.action',
					resAuditUrl: 'oa/appliance/resAuditOapurchase.action',
					submitUrl: 'oa/appliance/submitOapurchase.action',
					resSubmitUrl: 'oa/appliance/resSubmitOapurchase.action',
					keyField: 'op_id',
					codeField: 'op_code',
					statusField: 'op_status',
					statuscodeField: 'op_statuscode'
						
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