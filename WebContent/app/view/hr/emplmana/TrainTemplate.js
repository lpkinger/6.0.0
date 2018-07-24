Ext.define('erp.view.hr.emplmana.TrainTemplate',{ 
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
					anchor: '100% 35%',
					saveUrl: 'common/saveCommon.action?caller=' +caller,
					deleteUrl: 'common/deleteCommon.action?caller=' +caller,
					updateUrl: 'common/updateCommon.action?caller=' +caller,
					submitUrl: 'common/submitCommon.action?caller=' +caller,	
					resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller,
					auditUrl: 'common/auditCommon.action?caller=' +caller,
					resAuditUrl: 'common/resAuditCommon.action?caller=' +caller,
					printUrl: 'common/printCommon.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=TrainTemplate_SEQ',
					keyField: 'tt_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%', 
					keyField: 'ttd_id',
					detno: 'ttd_detno',
					mainField: 'ttd_ttid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});