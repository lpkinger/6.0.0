Ext.define('erp.view.plm.project.ProjectTG',{ 
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
					saveUrl: 'plm/project/saveProjectTG.action?caller='+caller,
//					deleteUrl: 'common/deleteCommon.action?caller='+caller,
					deleteUrl: 'plm/project/deleteProjectTG.action?caller='+caller,
					updateUrl: 'plm/project/updateProjectTG.action?caller='+caller,
					auditUrl: 'plm/project/auditProjectTG.action?caller='+caller,
					resAuditUrl: 'plm/project/resAuditProjectTG.action?caller='+caller,
					submitUrl: 'common/submitCommon.action?caller='+caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=Project_SEQ',
					keyField: 'prj_id',
					codeField: 'prj_code',
					statusField: 'prj_status'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'prd_detno',
					//necessaryField: '',
					keyField: 'prd_id',
					mainField: 'prd_prjid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});