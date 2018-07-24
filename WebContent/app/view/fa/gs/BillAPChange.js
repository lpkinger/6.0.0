Ext.define('erp.view.fa.gs.BillAPChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				saveUrl: 'fa/gs/saveBillAPChange.action',
				deleteUrl: 'fa/gs/deleteBillAPChange.action',
				updateUrl: 'fa/gs/updateBillAPChange.action',
				auditUrl: 'fa/gs/auditBillAPChange.action',
				resAuditUrl: 'fa/gs/resAuditBillAPChange.action',
				submitUrl: 'fa/gs/submitBillAPChange.action',
				resSubmitUrl: 'fa/gs/resSubmitBillAPChange.action',
				accountedUrl: 'fa/gs/accountBillAPChange.action',
				resAccountedUrl: 'fa/gs/resAccountBillAPChange.action',	
				getIdUrl: 'common/getId.action?seq=BILLAPCHANGE_SEQ',
				codeField: 'bpc_code',
				keyField: 'bpc_id',
				statusField: 'bpc_statuscode',
				assCaller:'BillAPChangeAss',
				voucherConfig: {
					voucherField: 'bpc_vouchercode',
					vs_code: 'BillAPChange',
					yearmonth: 'bpc_date',
					datas: 'bpc_code',
					status: 'bpc_statuscode',
					mode: 'single',
					kind: function(form){
						var f = form.down('#bpc_kind'), v = f ? f.getValue() : null;
						if(v && ['退票','兑现'].indexOf(v) > -1) 
							v = 'unneed';
						return v;
					},
					vomode: 'CB'
				}
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 65%', 
				detno: 'bpd_detno',
				necessaryField: 'bpd_bapcode',
				keyField: 'bpd_id',
				mainField: 'bpd_bpcid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});