Ext.define('erp.view.pm.plm.Make',{ 
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
					saveUrl: 'pm/plm/saveMake.action?caller=' +caller,
					deleteUrl: 'pm/plm/deleteMake.action?caller=' +caller,
					updateUrl: 'pm/plm/updateMake.action?caller=' +caller,
					submitUrl: 'pm/plm/submitMake.action?caller=' +caller,
					auditUrl: 'pm/plm/auditMake.action?caller=' +caller,
					resAuditUrl: 'pm/plm/resAuditMake.action?caller=' +caller,			
					resSubmitUrl: 'pm/plm/resSubmitMake.action?caller=' +caller,
					checkUrl: 'pm/plm/checkMake.action?caller=' +caller,
					resCheckUrl: 'pm/plm/resCheckMake.action?caller=' +caller,
					endUrl: 'pm/plm/endMake.action?caller=' +caller,
					resEndUrl: 'pm/plm/resEndMake.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=MAKE_SEQ',
					keyField: 'ma_id',
					statusField: 'ma_status',
					codeField: 'ma_statuscode'
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'mm_detno',
					necessaryField: 'mm_prodcode',
					keyField: 'mm_id',
					mainField: 'mm_maid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});