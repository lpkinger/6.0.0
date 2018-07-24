Ext.define('erp.view.fa.fp.ReportFilesG.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpReportFilesGGrid',
				allowExtraButtons: true,
				anchor: '100% 100%',
				getIdUrl: 'common/getId.action?seq=ReportFiles_SEQ',
				keyField: 'id'
			}]
		}); 
		me.callParent(arguments); 
	} 
});