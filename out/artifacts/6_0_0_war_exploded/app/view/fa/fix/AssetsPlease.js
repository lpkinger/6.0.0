Ext.define('erp.view.fa.fix.AssetsPlease',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'fa/fix/saveAssetsPlease.action?caller=' +caller,
					deleteUrl: 'fa/fix/deleteAssetsPlease.action?caller=' +caller,
					updateUrl: 'fa/fix/updateAssetsPlease.action?caller=' +caller,
					auditUrl: 'fa/fix/auditAssetsPlease.action?caller=' +caller,
					resAuditUrl: 'fa/fix/resAuditAssetsPlease.action?caller=' +caller,
					submitUrl: 'fa/fix/submitAssetsPlease.action?caller=' +caller,
					resSubmitUrl: 'fa/fix/resSubmitAssetsPlease.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=AssetsPlease_SEQ',
					keyField: 'ap_id',
					codeField: 'ap_code',
					statusField: 'ap_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'apd_detno',
					necessaryField: 'apd_cardid',
					keyField: 'apd_id',
					mainField: 'apd_apid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});