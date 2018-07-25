Ext.define('erp.view.fa.gs.GoodsSend',{ 
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
					saveUrl: 'fa/gs/GoodsSendController/saveGoodsSend.action',
					deleteUrl: 'fa/gs/GoodsSendController/deleteGoodsSend.action',
					updateUrl: 'fa/gs/GoodsSendController/updateGoodsSend.action',
					auditUrl: 'fa/gs/GoodsSendController/auditGoodsSend.action',
					resAuditUrl: 'fa/gs/GoodsSendController/resAuditGoodsSend.action',
					submitUrl: 'fa/gs/GoodsSendController/submitGoodsSend.action',
					resSubmitUrl: 'fa/gs/GoodsSendController/resSubmitGoodsSend.action',
					postUrl: 'fa/gs/GoodsSendController/postGoodsSend.action',
					resPostUrl: 'fa/gs/GoodsSendController/resPostGoodsSend.action',
					printUrl:'fa/gs/GoodsSendController/printGoodsSend.action',
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