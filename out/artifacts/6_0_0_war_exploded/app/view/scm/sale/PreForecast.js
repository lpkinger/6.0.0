Ext.define('erp.view.scm.sale.PreForecast',{ 
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
				saveUrl: 'scm/sale/savePreSaleForecast.action',
				deleteUrl: 'scm/sale/deletePreSaleForecast.action',
				updateUrl: 'scm/sale/updatePreForecast.action',
				submitUrl: 'scm/sale/submitPreSaleForecast.action',
				auditUrl: 'scm/sale/auditPreSaleForecast.action',
				resAuditUrl: 'scm/sale/resAuditPreSaleForecast.action',					
				resSubmitUrl: 'scm/sale/resSubmitPreSaleForecast.action',
				getIdUrl: 'common/getId.action?seq=PreSaleForeCast_SEQ',
				keyField: 'sf_id',
				statusField: 'sf_status',
				codeField: 'sf_statuscode'
			},{	
				anchor: '100% 80%',
				//xtype:'erpWCPlanGrid',
			     xtype:'erpPreForecastGrid',
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