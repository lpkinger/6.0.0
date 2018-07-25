Ext.define('erp.view.fa.gla.AssKind',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'assKindViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'fa/ars/saveAssKind.action',
					deleteUrl: 'fa/ars/deleteAssKind.action',
					updateUrl: 'fa/ars/updateAssKind.action',
					auditUrl: 'fa/ars/auditAssKind.action',
					resAuditUrl: 'fa/ars/resAuditAssKind.action',
					submitUrl: 'fa/ars/submitAssKind.action',
					resSubmitUrl: 'fa/ars/resSubmitAssKind.action',
					getIdUrl: 'common/getId.action?seq=AssKind_SEQ',
					keyField: 'ak_id',
				/*	codeField: 'abb_code',*/
					/*statusField: ''*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});