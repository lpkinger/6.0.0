Ext.define('erp.view.fa.ars.ARCheck',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'ARCheckViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl: 'fa/ars/saveARCheck.action',
					deleteUrl: 'fa/ars/deleteARCheck.action',
					updateUrl: 'fa/ars/updateARCheck.action',
					auditUrl: 'fa/ars/auditARCheck.action',
					resAuditUrl: 'fa/ars/resAuditARCheck.action',
					printUrl: 'fa/ars/printARCheck.action',
					submitUrl: 'fa/ars/submitARCheck.action',
					resSubmitUrl: 'fa/ars/resSubmitARCheck.action',
					postUrl: 'fa/ars/postARCheck.action',
					resPostUrl: 'fa/ars/resPostARCheck.action',	
					getIdUrl: 'common/getId.action?seq=ARCHECK_SEQ',
					codeField: 'ac_code',
					keyField: 'ac_id',
					statusField: 'ac_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%', 
					detno: 'ad_detno',
					keyField: 'ad_id',
					mainField: 'ad_acid',
					allowExtraButtons: true
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});