Ext.define('erp.view.hr.emplmana.RecruitApply',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'common/saveCommon.action?caller='+caller,
					deleteUrl: 'common/deleteCommon.action?caller='+caller,
					updateUrl: 'common/updateCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=RecruitApply_SEQ',
					auditUrl: 'hr/emplmana/auditRecruitApply.action',
					resAuditUrl: 'hr/emplmana/resAuditRecruitApply.action',
				    endUrl:'hr/emplmana/endRecruitApply.action',
					submitUrl: 'common/submitCommon.action?caller='+caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					keyField: 'ra_id',
					codeField: 'ra_code',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});