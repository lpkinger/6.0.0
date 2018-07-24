Ext.define('erp.view.scm.sale.PreForecastClash',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				saveUrl: 'scm/sale/savePreForecastClash.action',
				deleteUrl: 'scm/sale/deletePreForecastClash.action',
				updateUrl: 'scm/sale/updatePreForecastClash.action',
				submitUrl: 'scm/sale/submitPreForecastClash.action',
				auditUrl: 'scm/sale/auditPreForecastClash.action',
				resSubmitUrl: 'scm/sale/resSubmitPreForecastClash.action',
				getIdUrl: 'common/getId.action?seq=PreForecastClash_SEQ',
				keyField: 'pfc_id',
				statusField: 'pfc_status',
				codeField: 'pfc_code'
			},{	
				anchor: '100% 65%',
			    xtype:'erpGridPanel2',
			    detno : 'pfd_detno',
				keyField : 'pfd_id',
				mainField : 'pfd_pfcid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});