Ext.define('erp.view.pm.mps.SaleForecast',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [
				{
					xtype: 'erpFormPanel',
					anchor: '100% 55%',
					saveUrl: 'pm/mps/saveSaleForecast.action',
					deleteUrl: 'pm/mps/deleteSaleForecast.action',
					updateUrl: 'pm/mps/updateSaleForecast.action',
					getIdUrl: 'common/getId.action?seq=SALEFORECAST_SEQ',
					keyField: 'sf_id',
					codeField:'sf_code'
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 45%',
					detno: 'sd_detno',
					necessaryField: 'sd_code',
					keyField: 'sd_id',
					mainField: 'sd_sfid'
				  }]
			}]
		}); 
		me.callParent(arguments); 
	} 
});