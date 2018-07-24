Ext.define('erp.view.scm.sale.LendApply', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 50%',
				saveUrl : 'scm/sale/saveLendApply.action',
				deleteUrl : 'scm/sale/deleteLendApply.action',
				updateUrl : 'scm/sale/updateLendApply.action',
				auditUrl : 'scm/sale/auditLendApply.action',
				resAuditUrl : 'scm/sale/resAuditLendApply.action',
				submitUrl : 'scm/sale/submitLendApply.action',
				resSubmitUrl : 'scm/sale/resSubmitLendApply.action',
				getIdUrl : 'common/getId.action?seq=LENDAPPLY_SEQ',
				keyField : 'ld_id',
				codeField : 'ld_code'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 50%',
				keyField : 'ldd_id',
				mainField : 'ldd_ldid'
			} ]
		});
		me.callParent(arguments);
	}
});