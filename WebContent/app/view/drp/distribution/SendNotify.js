Ext.define('erp.view.drp.distribution.SendNotify',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'saleViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'drp/distribution/saveSendNotify.action',
					deleteUrl: 'drp/distribution/deleteSendNotify.action',
					updateUrl: 'drp/distribution/updateSendNotify.action',	
					auditUrl: 'drp/distribution/auditSendNotify.action',
					resAuditUrl: 'drp/distribution/resAuditSendNotify.action',
					printUrl:'drp/distribution/printSendNotify.action',
					submitUrl: 'drp/distribution/submitSendNotify.action',
					resSubmitUrl: 'drp/distribution/resSubmitSendNotify.action',
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
					mainField: 'snd_snid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});