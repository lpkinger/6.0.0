Ext.define('erp.view.drp.aftersale.repairaccount',{
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
					auditUrl: 'common/auditCommon.action?caller=' +caller,
					resAuditUrl: 'common/resAuditCommon.action?caller=' +caller,
					submitUrl: 'common/submitCommon.action?caller=' +caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=REPAIRACCOUNT_SEQ',
					keyField: 'ra_id',
					codeField: 'ra_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%',
                    necessaryField: '',
					keyField: 'rad_id',
					detno: 'rad_detno',
					mainField: 'rad_raid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});