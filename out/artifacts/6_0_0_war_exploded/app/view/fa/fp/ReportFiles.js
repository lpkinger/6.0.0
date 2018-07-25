Ext.define('erp.view.fa.fp.ReportFiles',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				anchor: '100% 100%',
				xtype: 'erpFormPanel',
				saveUrl: 'fa/fp/saveReportFiles.action',
				deleteUrl: 'fa/fp/deleteReportFiles.action',
				updateUrl: 'fa/fp/updateReportFiles.action',
				getIdUrl: 'common/getId.action?seq=ReportFiles_SEQ',
				keyField: 'id'
			}]
		}); 
		me.callParent(arguments); 
	} 
});
