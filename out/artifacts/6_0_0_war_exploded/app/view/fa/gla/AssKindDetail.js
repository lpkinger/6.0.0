Ext.define('erp.view.fa.gla.AssKindDetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'assKindDetailViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'fa/ars/saveAssKindDetail.action',
					deleteUrl: 'fa/ars/deleteAssKindDetail.action',
					updateUrl: 'fa/ars/updateAssKindDetail.action',
					auditUrl: 'fa/ars/auditAssKindDetail.action',
					resAuditUrl: 'fa/ars/resAuditAssKindDetail.action',
					submitUrl: 'fa/ars/submitAssKindDetail.action',
					resSubmitUrl: 'fa/ars/resSubmitAssKindDetail.action',
					getIdUrl: 'common/getId.action?seq=AssKindDetail_SEQ',
					keyField: 'cd_id',
				/*	codeField: 'abb_code',*/
					/*statusField: ''*/
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'akd_detno',  //  整个要改啊 啊啊 啊………………
					/*necessaryField: 'pd_prodcode',*/
					keyField: 'akd_id',
					mainField: 'akd_akid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
