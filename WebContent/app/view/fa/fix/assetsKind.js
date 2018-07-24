Ext.define('erp.view.fa.fix.assetsKind',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'assetsKindViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fa/fix/saveAssetsKind.action',
					deleteUrl: 'fa/fix/deleteAssetsKind.action',
					updateUrl: 'fa/fix/updateAssetsKind.action',
					auditUrl: 'fa/fix/auditAssetsKind.action',
					resAuditUrl: 'fa/fix/resAuditAssetsKind.action',
					submitUrl: 'fa/fix/submitAssetsKind.action',
					resSubmitUrl: 'fa/fix/resSubmitAssetsKind.action',
					getIdUrl: 'common/getId.action?seq=ASSETSKIND_SEQ',
					keyField: 'ak_id',
					codeField: 'ak_name',
					statusField: 'ak_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});