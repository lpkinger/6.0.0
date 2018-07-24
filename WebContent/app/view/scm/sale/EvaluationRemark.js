Ext.define('erp.view.scm.sale.EvaluationRemark', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 20%',
				updateUrl : 'scm/sale/updateEvaluationRemark.action',
				getIdUrl : 'common/getId.action?seq=EVALUATION_SEQ',
				keyField : 'ev_id',
				codeField : 'ev_code',
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 80%',
				keyField : 'er_id',
				detno : 'er_detno',
				mainField : 'er_evid',
				necessaryField : 'er_remark'
			} ]
		});
		me.callParent(arguments);
	}
});