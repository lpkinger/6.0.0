Ext.define('erp.view.scm.sale.BigLineApply',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'scm/sale/saveBigLineApply.action',
					deleteUrl: 'scm/sale/deleteBigLineApply.action',
					updateUrl: 'scm/sale/updateBigLineApply.action',
					getIdUrl: 'common/getId.action?seq=BigLineApply_SEQ',
					auditUrl: 'scm/sale/auditBigLineApply.action',
					resAuditUrl: 'scm/sale/resAuditBigLineApply.action',
					submitUrl: 'scm/sale/submitBigLineApply.action',
					resSubmitUrl: 'scm/sale/resSubmitBigLineApply.action',
					keyField: 'ba_id',
					codeField: 'ba_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});