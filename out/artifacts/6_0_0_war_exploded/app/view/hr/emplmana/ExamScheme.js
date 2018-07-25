Ext.define('erp.view.hr.emplmana.ExamScheme',{ 
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
					anchor: '100% 30%',
					saveUrl: 'hr/emplmana/saveExamScheme.action',
					deleteUrl: 'hr/emplmana/deleteExamScheme.action',
					updateUrl: 'hr/emplmana/updateExamScheme.action',
					getIdUrl: 'common/getId.action?seq=ExamScheme_SEQ',
					auditUrl: 'hr/emplmana/auditExamScheme.action',
					resAuditUrl: 'hr/emplmana/resAuditExamScheme.action',
					submitUrl: 'hr/emplmana/submitExamScheme.action',
					resSubmitUrl: 'hr/emplmana/resSubmitExamScheme.action',
					keyField: 'es_id',
					codeField: 'es_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					keyField: 'esd_id',
					detno: 'esd_detno',
					mainField: 'esd_esid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});