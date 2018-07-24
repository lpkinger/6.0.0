Ext.define('erp.view.fa.gs.BillAR',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'fa/gs/saveBillAR.action',
				deleteUrl: 'fa/gs/deleteBillAR.action',
				updateUrl: 'fa/gs/updateBillAR.action',
				auditUrl: 'fa/gs/auditBillAR.action',
				resAuditUrl: 'fa/gs/resAuditBillAR.action',
				submitUrl: 'fa/gs/submitBillAR.action',
				resSubmitUrl: 'fa/gs/resSubmitBillAR.action',
				nullifyUrl: 'fa/gs/nullifyBillAR.action',
				getIdUrl: 'common/getId.action?seq=BillAR_SEQ',
				keyField: 'bar_id',
				statusField: 'bar_status',
				statuscodeField: 'bar_statuscode',
				codeField: 'bar_code',
				assCaller:'BillARAss',
				voucherConfig: {
					voucherField: 'bar_vouchercode',
					vs_code: 'BillAR',
					yearmonth: 'bar_date',
					datas: 'bar_code',
					status: 'bar_statuscode',
					statusValue: 'AUDITED',
					mode: 'single',
					kind: function(form){
						var f = form.down('#bar_billkind'), v = f ? f.getValue() : null;
						if(v && ['应收款','预收款'].indexOf(v) > -1) 
							v = 'unneed';
						return v;
					},
					vomode: 'CB'
				}
			}]
		}); 
		me.callParent(arguments); 
	} 
});