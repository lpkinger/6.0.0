Ext.define('erp.view.scm.sale.CustTurn', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 30%',
				saveUrl : 'scm/sale/saveCustTurn.action',
				updateUrl : 'scm/sale/updateCustTurn.action',
				getIdUrl : 'common/getId.action?seq=custTurn_SEQ',
				deleteUrl : 'scm/sale/deleteCustTurn.action',
				submitUrl : 'scm/sale/submitCustTurn.action',
				resSubmitUrl : 'scm/sale/resSubmitCustTurn.action',
				auditUrl : 'scm/sale/auditCustTurn.action',
				resAuditUrl : 'scm/sale/resAuditCustTurn.action',
				keyField : 'ct_id',
				statusField: 'ct_statuscode',
				codeField : 'ct_code'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'cd_detno',
				keyField: 'cd_id',
				mainField: 'cd_ctid'
			}]
		});
		me.callParent(arguments);
	}
});