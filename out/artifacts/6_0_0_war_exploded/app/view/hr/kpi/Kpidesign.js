Ext.define('erp.view.hr.kpi.Kpidesign',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 60%',
				saveUrl: 'hr/Kpi/saveKpidesign.action',
				deleteUrl: 'hr/Kpi/deleteKpidesign.action',
				updateUrl: 'hr/Kpi/updateKpidesign.action',
				submitUrl: 'hr/kpi/submitKpidesign.action',
				resSubmitUrl: 'hr/kpi/resSubmitKpidesign.action',
				auditUrl: 'hr/kpi/auditKpidesign.action',
				resAuditUrl: 'hr/kpi/resAuditKpidesign.action',
				getIdUrl: 'common/getId.action?seq=Kpidesign_SEQ',
				keyField: 'kd_id',
				codeField: 'kd_code',
				statusField: 'kd_status',
				statuscodeField: 'kd_statuscode'
			},{
				xtype:'tabpanel',
				id:'kpitab',
				anchor: '100% 40%', 
				tabPosition: 'bottom',
				items:[{
					xtype: 'erpGridPanel2',					
					keyField: 'ki_id',
					mainField: 'ki_kdid',
					detno: 'ki_detno',
					_noc:1,
					tbar: {xtype: 'Kpitoolbar',id:'Kpitoolbar1'},
					bbar:[],
					allowExtraButtons: true,
					title:'考核项目'
				},{
					id: 'Kpidesignpoint_F',
					title:'评分设计',
					_noc:1,
					xtype: 'GradeDesignGrid',
					caller:'Kpidesignpoint',
				    keyField: 'kp_id',
				    mainField: 'kp_kdid',
					detno: 'kp_detno'
				},{
					id: 'KpidesigngradeLevel_F',
					title:'评分等级',
					_noc:1,
					xtype: 'KpidesigngradeLevelGrid',
					caller:'KpidesigngradeLevel',
				    keyField: 'kl_id',
				    mainField: 'kl_kdid',
					detno: 'kl_detno'
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});