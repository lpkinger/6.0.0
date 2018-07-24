Ext.define('erp.view.hr.emplmana.Recruitplan',{ 
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
					saveUrl: 'hr/emplmana/saveRecruitplan.action',
					deleteUrl: 'hr/emplmana/deleteRecruitplan.action',
					updateUrl: 'hr/emplmana/updateRecruitplan.action',	
					auditUrl: 'hr/emplmana/auditRecruitplan.action',
					resAuditUrl: 'hr/emplmana/resAuditRecruitplan.action',
					submitUrl: 'hr/emplmana/submitRecruitplan.action',
					resSubmitUrl: 'hr/emplmana/resSubmitRecruitplan.action',
					getIdUrl: 'common/getId.action?seq=Recruitplan_SEQ',
					keyField: 'rp_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					necessaryField: 'rd_depart',
					keyField: 'rd_id',
					detno: 'rd_detno',
					mainField: 'rd_rpid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});