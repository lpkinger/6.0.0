Ext.define('erp.view.scm.sale.SendNotify',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'scm/sale/saveSendNotify.action',
				deleteUrl: 'scm/sale/deleteSendNotify.action',
				updateUrl: 'scm/sale/updateSendNotify.action',	
				auditUrl: 'scm/sale/auditSendNotify.action',
				resAuditUrl: 'scm/sale/resAuditSendNotify.action',
				printUrl:'scm/sale/printSendNotify.action',
				submitUrl: 'scm/sale/submitSendNotify.action',
				resSubmitUrl: 'scm/sale/resSubmitSendNotify.action',
				getIdUrl: 'common/getId.action?seq=SENDNOTIFY_SEQ',
				keyField: 'sn_id',
				codeField: 'sn_code',	
				statusField: 'sn_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'snd_pdno',
				necessaryField: 'snd_prodcode',
				keyField: 'snd_id',
				mainField: 'snd_snid',
				allowExtraButtons: true,
				binds:[{
					refFields:['snd_sdid'],
					fields:['snd_ordercode','snd_orderdetno','snd_prodcode']
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});