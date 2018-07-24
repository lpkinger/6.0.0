Ext.define('erp.view.fa.arp.Estimate',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
//				id:'arbillViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'fa/arp/EstimateController/saveEstimate.action',
					deleteUrl: 'fa/arp/EstimateController/deleteEstimate.action',
					updateUrl: 'fa/arp/EstimateController/updateEstimate.action',
					auditUrl: 'fa/arp/EstimateController/auditEstimate.action',
					resAuditUrl: 'fa/arp/EstimateController/resAuditEstimate.action',
					submitUrl: 'fa/arp/EstimateController/submitEstimate.action',
					resSubmitUrl: 'fa/arp/EstimateController/resSubmitEstimate.action',
					postUrl: 'fa/arp/EstimateController/postEstimate.action',
					resPostUrl: 'fa/arp/EstimateController/resPostEstimate.action',
					printUrl:'fa/arp/EstimateController/printEstimate.action',
					getIdUrl: 'common/getId.action?seq=Estimate_SEQ',
					keyField: 'es_id',
					codeField: 'es_code',
					auditStatusCode:'es_auditstatuscode',
					statusCode:'es_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%'
//					detno: 'gsd_detno',  //  整个要改啊 啊啊 啊………………
//					necessaryField: 'gsd_prodcode',
//					keyField: 'esd_id',
//					mainField: 'gsd_gsid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});