Ext.define('erp.view.ma.data.Export',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 70%',
				region:'center',
				saveUrl: '/ma/saveExportDataSet.action',
				deleteUrl: '/ma/deleteExportDataSet.action',
				updateUrl: '/ma/updateExportDataSet.action',
				loadUrl:'ma/getExportDetails.action',
				testUrl: '/ma/testExportDataSet.action',
				getIdUrl: 'common/getId.action?seq=EXCELFX_SEQ',
				keyField: 'ed_id'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
