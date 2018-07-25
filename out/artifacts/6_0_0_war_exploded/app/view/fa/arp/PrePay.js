Ext.define('erp.view.fa.arp.PrePay',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'fa/PrePayController/savePrePay.action',
				deleteUrl: 'fa/PrePayController/deletePrePay.action',
				updateUrl: 'fa/PrePayController/updatePrePay.action',
				auditUrl: 'fa/PrePayController/auditPrePay.action',
				printUrl: 'fa/PrePayController/printPrePay.action',
				resAuditUrl: 'fa/PrePayController/resAuditPrePay.action',
				submitUrl: 'fa/PrePayController/submitPrePay.action',
				resSubmitUrl: 'fa/PrePayController/resSubmitPrePay.action',
				postUrl: 'fa/PrePayController/postPrePay.action',
				resPostUrl:'fa/PrePayController/resPostPrePay.action',
				getIdUrl: 'common/getId.action?seq=PrePay_SEQ',
				keyField: 'pp_id',
				codeField: 'pp_code',
				auditStatusCode:'pp_auditstatuscode',
				statusCode:'pp_statuscode',
				printStatusCode:'pp_printstatuscode',
				assCaller:'PrePayAss',
				voucherConfig: {
					voucherField: 'pp_vouchercode',
					vs_code: 'PrePay',
					yearmonth: 'pp_date',
					datas: 'pp_code',
					status: 'pp_statuscode',
					mode: 'single',
					kind: function(form){
						var f = form.down('#pp_type');
						return f ? f.getValue() : null;
					},
					vomode: 'AP'
				}
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'ppd_detno',  
				keyField: 'ppd_id',
				mainField: 'ppd_ppid',
				detailAssCaller:'PrePayDetailAss'
			}]
		}); 
		me.callParent(arguments); 
	} 
});