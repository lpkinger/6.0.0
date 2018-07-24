Ext.define('erp.view.fa.gs.BillARChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				saveUrl: 'fa/gs/saveBillARChange.action',
				deleteUrl: 'fa/gs/deleteBillARChange.action',
				updateUrl: 'fa/gs/updateBillARChange.action',
				auditUrl: 'fa/gs/auditBillARChange.action',
				resAuditUrl: 'fa/gs/resAuditBillARChange.action',
				submitUrl: 'fa/gs/submitBillARChange.action',
				resSubmitUrl: 'fa/gs/resSubmitBillARChange.action',
				accountedUrl: 'fa/gs/accountBillARChange.action',
				resAccountedUrl: 'fa/gs/resAccountBillARChange.action',	
				getIdUrl: 'common/getId.action?seq=BILLARCHANGE_SEQ',
				codeField: 'brc_code',
				keyField: 'brc_id',
				statusField: 'brc_statuscode',
				assCaller:'BillARChangeAss',
				voucherConfig: {
					voucherField: 'brc_vouchercode',
					vs_code: 'BillARChange',
					yearmonth: 'brc_date',
					datas: 'brc_code',
					status: 'brc_statuscode',
					mode: 'single',
					kind: function(form){
						var f = form.down('#brc_kind'), v = f ? f.getValue() : null;
						if(v && ['贴现','退票','背书转让','收款'].indexOf(v) > -1) 
							v = 'unneed';
						return v;
					},
					vomode: 'CB'
				}
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 65%', 
				detno: 'brd_detno',
				necessaryField: 'brd_bapcode',
				keyField: 'brd_id',
				mainField: 'brd_bpcid'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});