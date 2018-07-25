Ext.define('erp.view.fa.fix.assetsCard',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'fa/fix/saveAssetsCard.action',
				deleteUrl: 'fa/fix/deleteAssetsCard.action',
				updateUrl: 'fa/fix/updateAssetsCard.action',
				auditUrl: 'fa/fix/auditAssetsCard.action',
				resAuditUrl: 'fa/fix/resAuditAssetsCard.action',
				submitUrl: 'fa/fix/submitAssetsCard.action',
				resSubmitUrl: 'fa/fix/resSubmitAssetsCard.action',
				getIdUrl: 'common/getId.action?seq=ASSETSCARD_SEQ',
				keyField: 'ac_id',
				codeField: 'ac_code',
				statusField: 'ac_status',
				statuscodeField: 'ac_statuscode',
				voucherConfig: {
					voucherField: 'ac_vouchercode',
					vs_code: 'AssetsCard',
					yearmonth: 'ac_date',
					datas: 'ac_code',
					status: 'ac_statuscode',
					statusValue: 'AUDITED',
					mode: 'single',
					kind: '卡片',
					vomode: 'AS'
				}
			}]
		}); 
		me.callParent(arguments); 
	} 
});