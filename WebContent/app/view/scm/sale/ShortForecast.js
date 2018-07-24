Ext.define('erp.view.scm.sale.ShortForecast',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				region:'north',
				saveUrl: 'scm/sale/saveSaleForecast.action',
				deleteUrl: 'scm/sale/deleteShortForecast.action',
				updateUrl: 'scm/sale/updateShortForecast.action',
				submitUrl: 'scm/sale/submitShortForecast.action',
				auditUrl: 'scm/sale/auditShortForecast.action',
				resAuditUrl: 'scm/sale/resAuditShortForecast.action',					
				resSubmitUrl: 'pm/make/resSubmitShortForecast.action',
				getIdUrl: 'common/getId.action?seq=SaleForeCast_SEQ',
				keyField: 'sf_id',
				statusField: 'sf_status',
				codeField: 'sf_statuscode'
			},{	
				anchor: '100% 80%',
				//xtype:'erpWCPlanGrid',
			     xtype:'erpShortForecastGrid',
			   headerCt: Ext.create("Ext.grid.header.Container"),
			  /*  invalidateScrollerOnRefresh: false,
			    viewConfig: {
			        trackOver: false
			    },
			    buffered: true,
			    sync: true,
				//sync:true,
*/					region:'center'
			}]
		}); 
		me.callParent(arguments); 
	} 
});