Ext.define('erp.view.oa.fee.OrderFood',{ 
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
					saveUrl: 'common/saveCommon.action?caller='+caller,
					deleteUrl: 'common/deleteCommon.action?caller='+caller,
					updateUrl: 'common/updateCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=OrderFood_SEQ',
					auditUrl: 'common/auditCommon.action?caller='+caller,
					resAuditUrl: 'common/resAuditCommon.action?caller='+caller,
					submitUrl: 'common/submitCommon.action?caller='+caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					keyField: 'of_id',
					codeField: 'of_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'ofd_detno',
//					necessaryField: 'fcd_code',
					keyField: 'ofd_id',
					mainField: 'ofd_ofid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});