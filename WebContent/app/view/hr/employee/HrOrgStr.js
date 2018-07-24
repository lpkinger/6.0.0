Ext.define('erp.view.hr.employee.HrOrgStr',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'border', 
				items: [{
					region: 'center',
					width: '70%',
					layout: 'anchor', 
					items:[{
						xtype: 'erpFormPanel',
						anchor: '100% 45%',
						title: '<font color=#00868B>人员档案主表</font>',
						getIdUrl: 'common/getId.action?seq=employee_SEQ',
						keyField: 'em_id'
					},{
						xtype:'tabpanel',
						padding: 0,
						anchor: '100% 55%',
						items:[{
							xtype: 'erpGridPanel2',
							title:'家庭情况',
							detno: 'af_detno',
							keyField: 'af_id',
							necessaryField: 'af_name',
							mainField: 'af_arid',
							padding:0
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
							title:'职位调动',
							necessaryField: 'ap_newpos',
							keyField: 'ap_id',
							detno: 'ap_detno',
							mainField: 'ap_arid'
						}]
					}]
				},{
					region: 'west',
					width: '25%',
					xtype: 'hrOrgStrTree'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});