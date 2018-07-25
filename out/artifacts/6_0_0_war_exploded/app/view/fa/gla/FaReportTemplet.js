Ext.define('erp.view.fa.gla.FaReportTemplet',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				region: 'north',
				saveUrl : 'common/saveCommon.action?caller=' + caller,
				deleteUrl : 'common/deleteCommon.action?caller=' + caller,
				updateUrl : 'common/updateCommon.action?caller=' + caller,
				keyField : 'ft_id',
				codeField : 'ft_code'
			},{
				xtype: 'erpGridPanel2',
				region: 'center',
				detno: 'fd_detno',  
				keyField: 'fd_id',
				mainField: 'fd_ftid'
			}]
		}); 
		this.callParent(arguments); 
	}
});