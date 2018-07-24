Ext.define('erp.view.fa.gs.BillARCheque',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'fa/gs/saveBillARCheque.action',
				deleteUrl: 'fa/gs/deleteBillARCheque.action',
				updateUrl: 'fa/gs/updateBillARCheque.action',
				auditUrl: 'fa/gs/auditBillARCheque.action',
				resAuditUrl: 'fa/gs/resAuditBillARCheque.action',
				submitUrl: 'fa/gs/submitBillARCheque.action',
				resSubmitUrl: 'fa/gs/resSubmitBillARCheque.action',
				nullifyUrl: 'fa/gs/nullifyBillARCheque.action',
				getIdUrl: 'common/getId.action?seq=BillARCHEQUE_SEQ',
				keyField: 'bar_id',
				statusField: 'bar_status',
				statuscodeField: 'bar_statuscode',
				codeField: 'bar_code'
			}]
		}); 
		me.callParent(arguments); 
	} 
});