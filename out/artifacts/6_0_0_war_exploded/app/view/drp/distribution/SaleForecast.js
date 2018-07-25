Ext.define('erp.view.drp.distribution.SaleForecast',{ 
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
					saveUrl: 'drp/distribution/saveSaleForecast.action',
					deleteUrl: 'drp/distribution/deleteSaleForecast.action',
					updateUrl: 'drp/distribution/updateSaleForecast.action',
					auditUrl: 'drp/distribution/auditSaleForecast.action',
					resAuditUrl: 'drp/distribution/resAuditSaleForecast.action',
					submitUrl: 'drp/distribution/submitSaleForecast.action',
					resSubmitUrl: 'drp/distribution/resSubmitSaleForecast.action',
                    endUrl:'drp/distribution/EndSaleForecast.action',
                    resEndUrl:'drp/distribution/resEndSaleForecast.action',
					getIdUrl: 'common/getId.action?seq=SALEFORECAST_SEQ',
					printUrl:'drp/distribution/printSaleForecast.action',
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