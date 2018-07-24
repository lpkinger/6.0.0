Ext.define('erp.view.fa.fix.AssetsIO',{ 
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
					saveUrl: 'fa/fix/saveAssetsIO.action?caller=' +caller,
					deleteUrl: 'fa/fix/deleteAssetsIO.action?caller=' +caller,
					updateUrl: 'fa/fix/updateAssetsIO.action?caller=' +caller,
					auditUrl: 'fa/fix/auditAssetsIO.action?caller=' +caller,
					resAuditUrl: 'fa/fix/resAuditAssetsIO.action?caller=' +caller,
					submitUrl: 'fa/fix/submitAssetsIO.action?caller=' +caller,
					resSubmitUrl: 'fa/fix/resSubmitAssetsIO.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=AssetsIO_SEQ',
					keyField: 'ai_id',
					codeField: 'ai_code',
					statusField: 'ai_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'aid_detno',
					necessaryField: 'aid_cardcode',
					keyField: 'aid_id',
					mainField: 'aid_aiid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});