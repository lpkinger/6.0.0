Ext.define('erp.view.scm.product.ChartMang',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/product/saveChartMang.action',
				deleteUrl: 'scm/product/deleteChartMang.action',
				updateUrl: 'scm/product/updateChartMang.action',
				auditUrl: 'scm/product/auditChartMang.action',
				resAuditUrl: 'scm/product/resAuditChartMang.action',
				submitUrl: 'scm/product/submitChartMang.action',
				resSubmitUrl: 'scm/product/resSubmitChartMang.action',
				getIdUrl: 'common/getId.action?seq=ChartMang_SEQ',
				keyField: 'ct_id',
			}]
		}); 
		me.callParent(arguments); 
	} 
});