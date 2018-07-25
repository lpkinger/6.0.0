Ext.define('erp.view.hr.emplmana.ExamQuest',{ 
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
					saveUrl: 'hr/emplmana/saveExamQuest.action',
					deleteUrl: 'hr/emplmana/deleteExamQuest.action',
					updateUrl: 'hr/emplmana/updateExamQuest.action',
					getIdUrl: 'common/getId.action?seq=ExamQuest_SEQ',
					auditUrl: 'hr/emplmana/auditExamQuest.action',
					resAuditUrl: 'hr/emplmana/resAuditExamQuest.action',
					submitUrl: 'hr/emplmana/submitExamQuest.action',
					resSubmitUrl: 'hr/emplmana/resSubmitExamQuest.action',
					keyField: 'eq_id',
					codeField: 'eq_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});