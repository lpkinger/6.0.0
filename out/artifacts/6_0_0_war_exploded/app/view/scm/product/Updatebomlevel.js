Ext.define('erp.view.scm.product.Updatebomlevel',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',
				saveUrl: 'scm/product/saveUpdatebomlevel.action',
				deleteUrl: 'scm/product/deleteUpdatebomlevel.action',
				updateUrl: 'scm/product/updateUpdatebomlevel.action',		
				getIdUrl: 'common/getId.action?seq=Updatebomlevel_SEQ',
				auditUrl: 'scm/product/auditUpdatebomlevel.action',
				resAuditUrl: 'scm/product/resAuditUpdatebomlevel.action',
				submitUrl: 'scm/product/submitUpdatebomlevel.action',
				resSubmitUrl: 'scm/product/resSubmitUpdatebomlevel.action',
				keyField: 'ub_id'
			},{
				xtype: 'erpGridPanel2',
				id:'grid',
				anchor: '100% 60%', 
				necessaryField: 'ud_prodcode',
				keyField: 'ud_id',
				detno: 'ud_detno',
				mainField: 'ud_cpid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});