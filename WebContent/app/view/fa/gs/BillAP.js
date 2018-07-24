Ext.define('erp.view.fa.gs.BillAP',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fa/gs/saveBillAP.action',
					deleteUrl: 'fa/gs/deleteBillAP.action',
					updateUrl: 'fa/gs/updateBillAP.action',
					auditUrl: 'fa/gs/auditBillAP.action',
					resAuditUrl: 'fa/gs/resAuditBillAP.action',
					submitUrl: 'fa/gs/submitBillAP.action',
					resSubmitUrl: 'fa/gs/resSubmitBillAP.action',
					nullifyUrl: 'fa/gs/nullifyBillAP.action',
					getIdUrl: 'common/getId.action?seq=BILLAP_SEQ',
					keyField: 'bap_id',
					statusField: 'bap_status',
					statuscodeField: 'bap_statuscode',
					codeField: 'bap_code',
					assCaller:'BillAPAss',
					voucherConfig: {
						voucherField: 'bap_vouchercode',
						vs_code: 'BillAP',
						yearmonth: 'bap_date',
						datas: 'bap_code',
						status: 'bap_statuscode',
						statusValue: 'AUDITED',
						mode: 'single',
						kind: function(form){
							var f = form.down('#bap_billkind'), v = f ? f.getValue() : null;
							if(v && ['应付款','预付款'].indexOf(v) > -1) 
								v = 'unneed';
							return v;
						},
						vomode: 'CB'
					}
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});