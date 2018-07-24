Ext.define('erp.view.fa.fix.assetsDepreciation',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'fa/fix/saveAssetsDepreciation.action?caller=' +caller,
				deleteUrl: 'fa/fix/deleteAssetsDepreciation.action?caller=' +caller,
				updateUrl: 'fa/fix/updateAssetsDepreciation.action?caller=' +caller,
				auditUrl: 'fa/fix/auditAssetsDepreciation.action?caller=' +caller,
				resAuditUrl: 'fa/fix/resAuditAssetsDepreciation.action?caller=' +caller,
				submitUrl: 'fa/fix/submitAssetsDepreciation.action?caller=' +caller,
				resSubmitUrl: 'fa/fix/resSubmitAssetsDepreciation.action?caller=' +caller,
				postUrl: 'fa/fix/postAssetsDepreciation.action?caller=' +caller,
				resPostUrl: 'fa/fix/resPostAssetsDepreciation.action?caller=' +caller,
				printUrl: 'fa/fix/printAssetsDepreciation.action?caller=' +caller,
				getIdUrl: 'common/getId.action?seq=AssetsDepreciation_SEQ',
				keyField: 'de_id',
				codeField: 'de_code',
				statusField: 'de_status',
				statuscodeField: 'de_statuscode',
				voucherConfig: {
					voucherField: 'de_vouchercode',
					vs_code: 'Depreciation',
					yearmonth: 'de_date',
					datas: 'de_code',
					status: 'de_statuscode',
					mode: 'single',
					kind: function(form){
						var f = form.down('#de_class');
						return f ? f.getValue() : null;
					},
					vomode: 'AS'
				}
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'dd_detno',
				necessaryField: 'dd_ascode',
				keyField: 'dd_id',
				mainField: 'dd_deid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});