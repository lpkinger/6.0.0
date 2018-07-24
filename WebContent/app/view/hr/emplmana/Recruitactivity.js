Ext.define('erp.view.hr.emplmana.Recruitactivity',{ 
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
					saveUrl: 'hr/emplmana/saveRecruitactivity.action',
					deleteUrl: 'hr/emplmana/deleteRecruitactivity.action',
					updateUrl: 'hr/emplmana/updateRecruitactivity.action',		
					getIdUrl: 'common/getId.action?seq=Recruitactivity_SEQ',
					auditUrl: 'hr/emplmana/auditRecruitactivity.action',
					resAuditUrl: 'hr/emplmana/resAuditRecruitactivity.action',
					submitUrl: 'hr/emplmana/submitRecruitactivity.action',
					resSubmitUrl: 'hr/emplmana/resSubmitRecruitactivity.action',
					keyField: 're_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					necessaryField: 'rd_teamname',
					keyField: 'rd_id',
					detno: 'rd_detno',
					mainField: 'rd_reid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});