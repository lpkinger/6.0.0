Ext.define('erp.view.scm.product.Updateprodlevel', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 40%',
				saveUrl : 'scm/product/saveUpdateprodlevel.action',
				deleteUrl : 'scm/product/deleteUpdateprodlevel.action',
				updateUrl : 'scm/product/updateUpdateprodlevel.action',
				getIdUrl : 'common/getId.action?seq=Updateprodlevel_SEQ',
				auditUrl : 'scm/product/auditUpdateprodlevel.action',
				resAuditUrl : 'scm/product/resAuditUpdateprodlevel.action',
				submitUrl : 'scm/product/submitUpdateprodlevel.action',
				resSubmitUrl : 'scm/product/resSubmitUpdateprodlevel.action',
				keyField : 'cp_id'
			}, {
				xtype : 'erpGridPanel2',
				id : 'grid',
				anchor : '100% 60%',
				necessaryField : 'cd_prodcode',
				keyField : 'cd_id',
				detno : 'cd_detno',
				mainField : 'cd_cpid'
			} ]
		});
		me.callParent(arguments);
	}
});