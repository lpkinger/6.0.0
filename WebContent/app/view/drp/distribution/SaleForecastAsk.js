Ext.define('erp.view.drp.distribution.SaleForecastAsk',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'saleViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 40%',
					saveUrl: 'drp/distribution/saveSaleForecastAsk.action',
					deleteUrl: 'drp/distribution/deleteSaleForecastAsk.action',
					updateUrl: 'drp/distribution/updateSaleForecastAsk.action',
					auditUrl: 'drp/distribution/auditSaleForecastAsk.action',
					resAuditUrl: 'drp/distribution/resAuditSaleForecastAsk.action',
					submitUrl: 'drp/distribution/submitSaleForecastAsk.action',
					resSubmitUrl: 'drp/distribution/resSubmitSaleForecastAsk.action',
                    endUrl:'drp/distribution/EndSaleForecastAsk.action',
                    resEndUrl:'drp/distribution/resEndSaleForecastAsk.action',
					getIdUrl: 'common/getId.action?seq=SALEFORECASTASK_SEQ',
					printUrl:'drp/distribution/printSaleForecastAsk.action',
					keyField: 'sf_id',
					codeField: 'sf_code',
					statusField: 'sf_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					detno: 'sd_detno',
					necessaryField: 'sd_prodcode',
					keyField: 'sd_id',
					mainField: 'sd_sfid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});