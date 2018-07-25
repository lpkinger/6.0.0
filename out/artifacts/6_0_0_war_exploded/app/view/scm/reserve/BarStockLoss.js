Ext.define('erp.view.scm.reserve.BarStockLoss',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',
				saveUrl: 'scm/barStockLoss/saveLoss.action?caller=' +caller,
				deleteUrl: 'scm/barStockLoss/deleteLoss.action?caller=' +caller,
				updateUrl: 'scm/barStockLoss/updateLoss.action?caller=' +caller,
				auditUrl: 'scm/barStockLoss/auditLoss.action?caller=' +caller,
				resAuditUrl: 'scm/barStockLoss/resAduitLoss.action?caller=' +caller,
				submitUrl: 'scm/barStockLoss/submitLoss.action?caller=' +caller,
				resSubmitUrl: 'scm/barStockLoss/resSubmitLoss.action?caller=' +caller,
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