Ext.define('erp.view.hr.emplmana.Archive',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {
							layout: 'anchor',
							items: [{
								xtype: 'erpFormPanel',
								title: '<font color=#00868B>人员档案主表</font>',
								saveUrl: 'hr/emplmana/saveArchive.action',
								deleteUrl: 'hr/emplmana/deleteArchive.action',
								updateUrl: 'hr/emplmana/updateArchive.action',
								auditUrl: 'hr/employee/auditEmployee.action',
								submitUrl: 'hr/employee/submitEmployee.action',
								resSubmitUrl: 'hr/employee/resSubmitEmployee.action',
								resAuditUrl: 'hr/employee/resAuditEmployee.action',
								getIdUrl: 'common/getId.action?seq=employee_SEQ',
								keyField: 'em_id',
								statusField: 'em_statuscode',
								anchor: '100% 70%'
							},{
								xtype:'tabpanel',
								anchor: '100% 30%',
								items:[{
									xtype: 'erpGridPanel2',
									title:'家庭情况',
									detno: 'af_detno',
	    							keyField: 'af_id',
	    							mainField: 'af_arid'
								},{
									id: 'educationgrid',
									title:'教育经历',
									xtype: 'educationgrid',
									keyField: 'ad_id',
									mainField: 'ad_arid',
									necessaryField: 'ad_school',
									detno: 'ad_detno'
								},{
									id: 'workgrid',
									xtype: 'workgrid',
									title:'工作履历',
									mainField: 'aw_arid',
									detno: 'aw_detno',
									necessaryField: 'aw_comname',
									keyField: 'aw_id'
								},{
									id: 'reandpunishgrid',
									xtype: 'reandpunishgrid', 
									title:'奖惩记录',
									necessaryField: 'ar_title',
									keyField: 'ar_id',
									detno: 'ar_detno',
									mainField: 'ar_arid'
								},{
									id: 'positiongrid',
									xtype: 'positiongrid',
									title:'异动情况',
									necessaryField: 'ap_newpos',
									keyField: 'ap_id',
									detno: 'ap_detno',
									mainField: 'ap_arid'
								},{
									id:'relationgrid',
									xtype: 'relationgrid',
									title:'公司亲友',
									necessaryField: 're_code',
									keyField: 're_id',
									detno: 're_detno',
									mainField: 're_emid'
								},{
 									id:'traininggrid',
 								 	xtype: 'traininggrid',
 									title:'内部培训'
 								},{
 								 	xtype: 'empsjobsgrid',
 									title:'兼职岗位',
 									id: 'empsjobsgrid',
									caller: 'Empsjobs', 
 									keyField: 'job_id',
	    							mainField: 'emp_id'
 								}]
						}]
		}); 
		me.callParent(arguments); 
	} 
});