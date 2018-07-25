Ext.define('erp.view.pm.bom.FeatureProduct',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				saveUrl: 'pm/bom/saveFeatureProduct.action',
				deleteUrl: 'pm/bom/deleteFeatureProduct.action',
				updateUrl: 'pm/bom/updateFeatureProduct.action',
				auditUrl: 'pm/bom/auditFeatureProduct.action',
				resAuditUrl: 'pm/bom/resAuditFeatureProduct.action',
				submitUrl: 'pm/bom/submitFeatureProduct.action',
				resSubmitUrl: 'pm/bom/resSubmitFeatureProduct.action',
				getIdUrl: 'common/getId.action?seq=FEATUREPRODUCT_SEQ',
				keyField: 'fp_id',
				codeField: 'fp_code',
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 65%', 
//				title:'特征值明细',
				detno: 'fpd_detno',
				keyField: 'fpd_id',
				mainField: 'fpd_fpid',
				necessaryField: 'fpd_fevaluecode',
				tbar:[{
					xtype:'tbtext',
					text:'特征值明细'
				},'->',{
					text:'删除明细',
					xtype:'button',
					id:'deleteDetail',
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});