Ext.define('erp.view.pm.mes.Craft',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				saveUrl: 'pm/mes/saveCraft.action',
				deleteUrl: 'pm/mes/deleteCraft.action',
				updateUrl: 'pm/mes/updateCraft.action',
				getIdUrl: 'common/getId.action?seq=Craft_SEQ',
				submitUrl: 'pm/mes/submitCraft.action',
				auditUrl: 'pm/mes/auditCraft.action',
				resAuditUrl: 'pm/mes/resAuditCraft.action',			
				resSubmitUrl: 'pm/mes/resSubmitCraft.action',
				keyField: 'cr_id',
				codeField: 'cr_code', 
				statusField: 'cr_status',
				statuscodeField: 'cr_statuscode'
			},{
				xtype:'tabpanel',
				anchor: '100% 65%', 
				items:[{
					xtype: 'erpGridPanel2',
					detno: 'cd_detno',
					keyField: 'cd_id',
					title:'工艺路线明细',
					mainField: 'cd_crid'
				},{
					xtype:'erpDisplayGridPanel',
					title:'工艺路线展开',
					caller:'CraftStruct',
					querycondition : 'cs_topbomid=(select cr_boid from craft where @formCondition )',
					
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});