Ext.define('erp.view.hr.emplmana.Exam1',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'border', 
				items: [{
					region:'center',
					xtype: 'panel',
					width: '80%',
					id:"StartExamOut",
					autoScroll : true,
					items: [{
						xtype:'label',
						labelAlign:'top',
						text:'考试题目',
						cls:'formTitle'
					},{
						region:'center',
						xtype: 'StartExamForm1',
						width: '100%',
						saveUrl: 'hr/emplmana/saveExamQuest.action',
						deleteUrl: 'hr/emplmana/deleteExamQuest.action',
						updateUrl: 'hr/emplmana/updateExamQuest.action',
						getIdUrl: 'common/getId.action?seq=ExamQuest_SEQ',
						auditUrl: 'hr/emplmana/auditExamQuest.action',
						resAuditUrl: 'hr/emplmana/resAuditExamQuest.action',
						submitUrl: 'hr/emplmana/submitExamQuest.action',
						resSubmitUrl: 'hr/emplmana/resSubmitExamQuest.action',
						keyField: 'eq_id',
						codeField: 'eq_code',
						_noc:1
					}
					]}
					,{
					xtype:'AnswerForm1',
					width: '20%',
					region:'west',
					_noc:1
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});