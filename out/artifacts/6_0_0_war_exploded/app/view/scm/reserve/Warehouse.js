Ext.define('erp.view.scm.reserve.Warehouse', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 100%',
				saveUrl: 'common/saveCommon.action?caller=' +caller,
				updateUrl: 'scm/updateWarehouse.action',
				deleteUrl: 'scm/deleteWarehouse.action',
				submitUrl: 'scm/submitWarehouse.action',
				resSubmitUrl: 'scm/resSubmitWarehouse.action',
				auditUrl: 'scm/auditWarehouse.action',
				resAuditUrl: 'scm/resAuditWarehouse.action',
				bannedUrl: 'common/bannedCommon.action?caller='+caller,
				resBannedUrl: 'common/resBannedCommon.action?caller='+caller,
				getIdUrl : 'common/getId.action?seq=WAREHOUSE_SEQ',
				keyField : 'wh_id',
				codeField : 'wh_code',
				statusField: 'wh_status',
				statuscodeField: 'wh_statuscode'
			}]
		});
		me.callParent(arguments);
	}
});