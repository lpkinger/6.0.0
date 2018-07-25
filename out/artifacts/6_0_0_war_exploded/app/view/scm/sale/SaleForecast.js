Ext.define('erp.view.scm.sale.SaleForecast',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',
				saveUrl: 'scm/sale/saveSaleForecast.action',
				deleteUrl: 'scm/sale/deleteSaleForecast.action',
				updateUrl: 'scm/sale/updateSaleForecast.action',
				auditUrl: 'scm/sale/auditSaleForecast.action',
				resAuditUrl: 'scm/sale/resAuditSaleForecast.action',
				submitUrl: 'scm/sale/submitSaleForecast.action',
				resSubmitUrl: 'scm/sale/resSubmitSaleForecast.action',
                endUrl:'scm/sale/endSaleForecast.action',
                resEndUrl:'scm/sale/resEndSaleForecast.action',
				getIdUrl: 'common/getId.action?seq=SALEFORECAST_SEQ',
				printUrl:'scm/sale/printSaleForecast.action',
				keyField: 'sf_id',
				codeField: 'sf_code',
				statusField: 'sf_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 60%', 
				detno: 'sd_detno',
				necessaryField: 'sd_prodcode',
				keyField: 'sd_id',
				mainField: 'sd_sfid',
				binds: [{
					refFields:['sd_source'],
					fields:['sd_prodcode']
				}],
				allowExtraButtons: true
			}]
		}); 
		me.callParent(arguments); 
	} 
});