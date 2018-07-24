Ext.define('erp.view.hr.emplmana.Recruitment',{ 
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
					anchor: '100% 30%',
					saveUrl: 'hr/emplmana/saveRecruitment.action',
					deleteUrl: 'hr/emplmana/deleteRecruitment.action',
					updateUrl: 'hr/emplmana/updateRecruitment.action',		
					getIdUrl: 'common/getId.action?seq=Recruitment_SEQ',
					auditUrl: 'hr/emplmana/auditRecruitment.action',
					resAuditUrl: 'hr/emplmana/resAuditRecruitment.action',
					submitUrl: 'hr/emplmana/submitRecruitment.action',
					resSubmitUrl: 'hr/emplmana/resSubmitRecruitment.action',
					keyField: 're_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					necessaryField: 'rd_hrorg',
					keyField: 'rd_id',
					detno: 'rd_detno',
					mainField: 'rd_reid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});