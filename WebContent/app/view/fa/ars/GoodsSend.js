Ext.define('erp.view.fa.ars.GoodsSend',{ 
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
					saveUrl: 'fa/ars/GoodsSendController/saveGoodsSend.action',
					deleteUrl: 'fa/ars/GoodsSendController/deleteGoodsSend.action',
					updateUrl: 'fa/ars/GoodsSendController/updateGoodsSend.action',
					auditUrl: 'fa/ars/GoodsSendController/auditGoodsSend.action',
					resAuditUrl: 'fa/ars/GoodsSendController/resAuditGoodsSend.action',
					submitUrl: 'fa/ars/GoodsSendController/submitGoodsSend.action',
					resSubmitUrl: 'fa/ars/GoodsSendController/resSubmitGoodsSend.action',
					postUrl: 'fa/ars/GoodsSendController/postGoodsSend.action',
					resPostUrl: 'fa/ars/GoodsSendController/resPostGoodsSend.action',
					printUrl:'fa/ars/GoodsSendController/printGoodsSend.action',
					getIdUrl: 'common/getId.action?seq=GoodsSend_SEQ',
					keyField: 'gs_id',
					codeField: 'gs_code',
					auditStatusCode:'gs_auditstatuscode',
					statusCode:'gs_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'gsd_detno',  //  整个要改啊 啊啊 啊………………
					necessaryField: 'gsd_prodcode',
					keyField: 'gsd_id',
					mainField: 'gsd_gsid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});