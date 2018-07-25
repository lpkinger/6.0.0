Ext.define('erp.view.pm.bom.PreProdFeature',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 25%',
				saveUrl: 'pm/bom/savePreProdFeature.action',
				deleteUrl: 'pm/bom/deletePreProdFeature.action',
				updateUrl: 'pm/bom/updatePreProdFeature.action',
				getIdUrl: 'common/getId.action?seq=PREPRODFEATURE_SEQ',
				keyField: 'pre_id',
				codeField: 'pre_code',
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 75%', 
				detno: 'ppf_detno',
				keyField: 'ppf_id',
				mainField: 'ppf_prid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});