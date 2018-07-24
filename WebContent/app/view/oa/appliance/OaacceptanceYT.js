Ext.define('erp.view.oa.appliance.OaacceptanceYT',{ 
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
					anchor: '100% 35%',
					saveUrl: 'oa/appliance/saveOaacceptance.action?caller='+caller,
					updateUrl: 'oa/appliance/updateOaacceptance.action?caller='+caller,	
					deleteUrl: 'oa/appliance/deleteOaacceptance.action?caller='+caller,
					printUrl: 'oa/appliance/printOaacceptance.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=Oaacceptance_SEQ',
					auditUrl: 'oa/appliance/auditOaacceptance.action?caller='+caller,
					resAuditUrl: 'oa/appliance/resAuditOaacceptance.action?caller='+caller,
					submitUrl: 'oa/appliance/submitOaacceptance.action?caller='+caller,
					resSubmitUrl: 'oa/appliance/resSubmitOaacceptance.action?caller='+caller,
					postUrl: 'oa/appliance/postOaacceptance.action?caller='+caller,
					resPostUrl: 'oa/appliance/resPostOaacceptance.action?caller='+caller,
					keyField: 'op_id',
					codeField: 'op_code',
					statusField: 'op_status',
					statuscodeField: 'op_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%', 
					necessaryField: 'od_procode',
					keyField: 'od_id',
					detno: 'od_detno',
					mainField: 'od_opid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});