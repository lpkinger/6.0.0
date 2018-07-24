Ext.define('erp.view.scm.sale.LineApply',{ 
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
					anchor: '100% 60%',
					saveUrl: 'scm/sale/saveLineApply.action',
					deleteUrl: 'scm/sale/deleteLineApply.action',
					updateUrl: 'scm/sale/updateLineApply.action',
					getIdUrl: 'common/getId.action?seq=LineApply_SEQ',
					auditUrl: 'scm/sale/auditLineApply.action',
					resAuditUrl: 'scm/sale/resAuditLineApply.action',
					submitUrl: 'scm/sale/submitLineApply.action',
					resSubmitUrl: 'scm/sale/resSubmitLineApply.action',
					keyField: 'la_id',
					codeField: 'la_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 40%', 
					keyField: 'lad_id',
					detno: 'lad_detno',
					mainField: 'lad_laid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});