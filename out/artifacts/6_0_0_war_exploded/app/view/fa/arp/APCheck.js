Ext.define('erp.view.fa.arp.APCheck',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'APCheckViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl: 'fa/arp/saveAPCheck.action',
					deleteUrl: 'fa/arp/deleteAPCheck.action',
					updateUrl: 'fa/arp/updateAPCheck.action',
					auditUrl: 'fa/arp/auditAPCheck.action',
					resAuditUrl: 'fa/arp/resAuditAPCheck.action',
					printUrl: 'fa/arp/printAPCheck.action',
					submitUrl: 'fa/arp/submitAPCheck.action',
					resSubmitUrl: 'fa/arp/resSubmitAPCheck.action',
					postUrl: 'fa/arp/postAPCheck.action',
					resPostUrl: 'fa/arp/resPostAPCheck.action',	
					checkUrl : 'fa/arp/checkAPCheck.action',
					resCheckUrl : 'fa/arp/resCheckAPCheck.action',
					getIdUrl: 'common/getId.action?seq=APCHECK_SEQ',
					codeField: 'ac_code',
					keyField: 'ac_id',
					statusField: 'ac_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%', 
					detno: 'ad_detno',
					keyField: 'ad_id',
					mainField: 'ad_acid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});