Ext.define('erp.view.scm.sale.CustomerKind', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor:'100% 100%',
				saveUrl : 'scm/sale/saveCustomerKind.action',
				deleteUrl : 'scm/sale/deleteCustomerKind.action',
				updateUrl : 'scm/sale/updateCustomerKind.action',
				bannedUrl :    'scm/sale/bannedCustomerKind.action',
				resBannedUrl : 'scm/sale/resBannedCustomerKind.action',
				getIdUrl : 'common/getId.action?seq=CUSTOMERKIND_SEQ',
				keyField : 'ck_id',
				codeField : 'ck_code',
			}/*, {
				region : 'east',
				width : '38%',
				xtype : 'custkindtree',
				tbar : [ {
					iconCls : 'tree-add',
					name : 'add',
					text : $I18N.common.button.erpAddButton
				}, {
					iconCls : 'tree-delete',
					name : 'delete',
					text : $I18N.common.button.erpDeleteButton
				} ]
			} */]
		});
		me.callParent(arguments);
	}
});