Ext.define('erp.view.pm.mould.MJProjectChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'MJProjectChangeViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'common/saveCommon.action?caller=' +caller,
					deleteUrl: 'common/deleteCommon.action?caller=' +caller,
					updateUrl: 'common/updateCommon.action?caller=' +caller,
					auditUrl: 'pm/mould/auditMJProjectChange.action',
					submitUrl: 'common/submitCommon.action?caller=' +caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller,
					getIdUrl: 'common/getCommonId.action?caller=' +caller,
					keyField: 'wsc_id',
					codeField: 'wsc_code',
					statusField: 'wsc_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});