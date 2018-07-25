Ext.define('erp.view.common.subs.SubsNum',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 55%',
				bodyStyle: 'background:#f1f1f1;',
				saveUrl: 'common/charts/save.action?caller=' +caller,
				updateUrl: 'common/charts/update.action?caller=' +caller,
				deleteUrl: 'common/charts/delete.action?caller=' +caller,
				auditUrl: 'common/charts/audit.action?caller=' +caller,			
				resAuditUrl: 'common/charts/resAudit.action?caller=' +caller,
				submitUrl: 'common/charts/submit.action?caller=' +caller,
				resSubmitUrl: 'common/charts/resSubmit.action?caller=' +caller,
				bannedUrl: 'common/charts/bannedCharts.action',
				resBannedUrl: 'common/charts/resBannedCharts.action',
				getIdUrl: 'common/getCommonId.action?caller=' +caller,
				dumpable: true
	    	},{
	    		xtype:'tabpanel',
				anchor: '100% 45%',
				items:[{
					xtype: 'erpGridPanel2',
					title:'订阅项',
				},{
					title:'参数设置',
					xtype : 'SubsConditions',
					caller:'SubsConditions',
					id: 'subsCondition',
					detno : 'detno_',
					keyField : 'id_',
					mainField : 'num_id',
					condition:getUrlParam('gridCondition')?getUrlParam('gridCondition').replace('IS','='):null
				},{
					title: '关联关系设置',
					xtype: 'subsRelationConfig',
					caller: 'subsRelationConfig',
					id: 'subsRelationConfig',
					detno: 'detno',
					keyField: 'sr_id',
					mainField: 'num_id',
					condition:getUrlParam('gridCondition')?getUrlParam('gridCondition').replace('IS','='):null
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});