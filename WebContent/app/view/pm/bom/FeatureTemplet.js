Ext.define('erp.view.pm.bom.FeatureTemplet',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				saveUrl: 'pm/bom/saveFeatureTemplet.action',
				deleteUrl: 'pm/bom/deleteFeatureTemplet.action',
				updateUrl: 'pm/bom/updateFeatureTemplet.action',
				auditUrl: 'pm/bom/auditFeatureTemplet.action',
				resAuditUrl: 'pm/bom/resAuditFeatureTemplet.action',
				submitUrl: 'pm/bom/submitFeatureTemplet.action',
				resSubmitUrl: 'pm/bom/resSubmitFeatureTemplet.action',
				getIdUrl: 'common/getId.action?seq=FEATUREPRODUCT_SEQ',
				keyField: 'ft_id',
				codeField: 'ft_code',
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 65%', 
				detno: 'fd_detno',
				keyField: 'fd_id',
				mainField: 'fd_ftid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});