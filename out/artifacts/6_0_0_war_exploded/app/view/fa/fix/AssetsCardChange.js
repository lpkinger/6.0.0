Ext.define('erp.view.fa.fix.AssetsCardChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'assetsCardChangeViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fa/fix/saveAssetsCardChange.action',
					deleteUrl: 'fa/fix/deleteAssetsCardChange.action',
					updateUrl: 'fa/fix/updateAssetsCardChange.action',
					auditUrl: 'fa/fix/auditAssetsCardChange.action',
					resAuditUrl: 'fa/fix/resAuditAssetsCardChange.action',
					submitUrl: 'fa/fix/submitAssetsCardChange.action',
					resSubmitUrl: 'fa/fix/resSubmitAssetsCardChange.action',
					getIdUrl: 'common/getId.action?seq=ASSETSCARDCHANGE_SEQ',
					keyField: 'acc_id',
					codeField: 'acc_code',
					statusField: 'acc_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});