Ext.define('erp.view.ma.data.ExcelFx',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'ma/saveExcelFx.action',
				deleteUrl: 'ma/deleteExcelFx.action',
				updateUrl: 'ma/updateExcelFx.action',
				getIdUrl: 'common/getId.action?seq=EXCELFX_SEQ',
				keyField: 'ef_id'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
