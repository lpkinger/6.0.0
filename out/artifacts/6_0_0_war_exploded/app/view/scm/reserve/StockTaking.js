Ext.define('erp.view.scm.reserve.StockTaking',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				deleteUrl: 'scm/reserve/deleteStockTaking.action',
				updateUrl: 'scm/reserve/updateStockTaking.action',
				auditUrl: 'scm/reserve/auditStockTaking.action',
				resAuditUrl: 'scm/reserve/resAuditStockTaking.action',
				postUrl: 'scm/reserve/postStockTaking.action',
				resPostUrl: 'scm/reserve/resPostStockTaking.action',
				getIdUrl: 'common/getId.action?seq=STOCKTAKING_SEQ',
				printUrl: 'common/printCommon.action',
				keyField: 'st_id',
				codeField: 'st_code',
				statuscodeField: 'st_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'std_detno',
				necessaryField: 'std_prodcode',
				keyField: 'std_id',
				mainField: 'std_stid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});