Ext.define('erp.view.scm.product.Plmpreproduct',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 70%',
				saveUrl: 'scm/product/savePlmpreproduct.action',
				deleteUrl: 'scm/product/deletePlmpreproduct.action',
				updateUrl: 'scm/product/updatePlmpreproduct.action',
				auditUrl: 'scm/product/auditPlmpreproduct.action',
				resAuditUrl: 'scm/product/resAuditPlmpreproduct.action',
				submitUrl: 'scm/product/submitPlmpreproduct.action',
				resSubmitUrl: 'scm/product/resSubmitPlmpreproduct.action',
				getIdUrl: 'common/getId.action?seq=Plmpreproduct_SEQ',
				keyField: 'pp_id'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 30%', 
				keyField: 'ppd_id',
				detno: 'ppd_detno',
				mainField: 'ppd_ppid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});