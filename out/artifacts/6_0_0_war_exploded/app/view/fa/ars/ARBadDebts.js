Ext.define('erp.view.fa.ars.ARBadDebts',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'ARBadDebtsViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 40%',
					saveUrl: 'fa/ars/saveARBadDebts.action',
					deleteUrl: 'fa/ars/deleteARBadDebts.action',
					updateUrl: 'fa/ars/updateARBadDebts.action',
					auditUrl: 'fa/ars/auditARBadDebts.action',
					resAuditUrl: 'fa/ars/resAuditARBadDebts.action',
					submitUrl: 'fa/ars/submitARBadDebts.action',
					printUrl: 'fa/ars/printARBadDebts.action',
					resSubmitUrl: 'fa/ars/resSubmitARBadDebts.action',
					postUrl: 'fa/ars/postARBadDebts.action',
					resPostUrl: 'fa/ars/resPostARBadDebts.action',
					getIdUrl: 'common/getId.action?seq=ARBADDEBTS_SEQ',
					keyField: 'bd_id',
					codeField: 'bd_code',
					statusField: 'bd_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					detno: 'bdd_detno',
					keyField: 'bdd_id',
//					allowExtraButtons:true,
					mainField: 'bdd_bdid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});