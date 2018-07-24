Ext.define('erp.view.fa.gla.ReportYearBegin',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				region: 'north',
				saveUrl : 'fa/gla/saveReportYearBegin.action?caller=' +caller,
				updateUrl : 'fa/gla/updateReportYearBegin.action?caller=' +caller,
				getIdUrl: 'common/getId.action?seq=REPORTYEARBEGIN_SEQ',
				keyField : 'yb_id'
			},{
				xtype: 'erpGridPanel2',
				region: 'center',
				detno: 'ybd_detno',  
				keyField: 'ybd_id',
				mainField: 'ybd_ybid'
			}]
		}); 
		this.callParent(arguments); 
	}
});