Ext.define('erp.view.fa.arp.PayBalanceAR',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 44%',
				saveUrl: 'fa/PayBalanceController/savePayBalanceAR.action',
				deleteUrl: 'fa/arp/deletePayBalance.action',
				updateUrl: 'fa/PayBalanceController/updatePayBalanceARById.action',
				auditUrl: 'fa/arp/auditPayBalance.action',
				postUrl: 'fa/arp/postPayBalance.action',
				resPostUrl: 'fa/arp/resPostPayBalance.action',
				resAuditUrl: 'fa/arp/resAuditPayBalance.action',
				submitUrl: 'fa/arp/submitRecBalance.action',
				resSubmitUrl: 'fa/arp/resSubmitRecBalance.action',
				catchAPUrl:'fa/PayBalanceController/catchAP.action',
				cleanAPUrl:'fa/PayBalanceController/cleanAP.action',
				catchARUrl:'fa/PayBalanceController/catchAR.action',
				cleanARUrl:'fa/PayBalanceController/cleanAR.action',
				getIdUrl: 'common/getId.action?seq=PayBalance_SEQ',
				keyField: 'pb_id',
				codeField: 'pb_code',
				auditStatusCode:'pb_auditstatuscode',
				statusCode:'pb_statuscode',
				printStatusCode:'pb_printstatuscode',
				mStatusCode:'pb_strikestatuscode'
			},{
				xtype: 'paybalancear',
				anchor: '100% 28%',
				caller:'PBARGird',
				keyField: 'pbar_id',
				mainField: 'pbar_pbid',
				tbar:['应收发票详情','->',{
					xtype:'button',
					text:'获取发票',
					name:'catchar'
				},'-',{
					xtype:'button',
					text:'清除发票',
					name:'cleanar'
				}]
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 28%',
				keyField: 'pbd_id',
				mainField: 'pbd_pbid',
				detailAssCaller:'PayBalanceDetailAss',
				tbar:['应付发票详情','->',{
					xtype:'button',
					text:'获取发票',
					name:'catchap'
				},'-',{
					xtype:'button',
					text:'清除发票',
					name:'cleanap'
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});