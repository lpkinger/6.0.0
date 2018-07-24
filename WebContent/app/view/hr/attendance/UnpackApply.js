Ext.define('erp.view.hr.attendance.UnpackApply',{
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
					anchor: '100% 100%',
					saveUrl: 'common/saveCommon.action?caller='+caller,
					deleteUrl: 'common/deleteCommon.action?caller='+caller,
					updateUrl: 'common/updateCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=UnpackApply_SEQ',
					auditUrl: 'hr/emplmana/auditUnpackApply.action',
					resAuditUrl: 'hr/emplmana/resAuditUnpackApply.action',
					submitUrl: 'common/submitCommon.action?caller='+caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					confirmUrl:'hr/emplmana/confirmUnpackApply.action',
					printUrl:'hr/emplmana/printUnpackApply.action',
					keyField: 'ua_id',
					codeField: 'ua_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});