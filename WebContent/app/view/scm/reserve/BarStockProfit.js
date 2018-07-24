Ext.define('erp.view.scm.reserve.BarStockProfit',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',
				saveUrl: 'scm/barStockProfit/saveProfit.action?caller=' +caller,
				deleteUrl: 'scm/barStockProfit/deleteProfit.action?caller=' +caller,
				updateUrl: 'scm/barStockProfit/updateProfit.action?caller=' +caller,
				auditUrl: 'scm/barStockProfit/auditProfit.action?caller=' +caller,
				resAuditUrl: 'scm/barStockProfit/resAduitProfit.action?caller=' +caller,
				submitUrl: 'scm/barStockProfit/submitProfit.action?caller=' +caller,
				resSubmitUrl: 'scm/barStockProfit/resSubmitProfit.action?caller=' +caller,
				getIdUrl: 'common/getId.action?seq=BARSTOCKTAKING_SEQ',
				keyField: 'bs_id',
				codeField: 'bs_code',
				statusField: 'bs_status',
				statuscodeField: 'bs_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 60%', 
				allowExtraButtons: true,
				detno: 'bsd_detno',
				keyField: 'bsd_id',
				mainField: 'bsd_bsid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});