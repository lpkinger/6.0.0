Ext.define('erp.view.plm.cost.ProjectCostClose',{ 
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
					saveUrl: 'plm/cost/saveProjectCostClose.action',
					deleteUrl: 'plm/cost/deleteProjectCostClose.action',
					updateUrl: 'plm/cost/updateProjectCostClose.action',
					auditUrl: 'plm/cost/auditProjectCostClose.action',
					resAuditUrl: 'plm/cost/resAuditProjectCostClose.action',
					submitUrl: 'plm/cost/submitProjectCostClose.action',
					resSubmitUrl: 'plm/cost/resSubmitProjectCostClose.action',
					catchABUrl:'plm/cost/catchProjectCost.action',
					cleanABUrl:'plm/cost/cleanProjectCost.action',
					getIdUrl: 'common/getId.action?seq=PROJECTCOSTCLOSE_SEQ',
					keyField: 'pcc_id',
					statusField:'pcc_status',
					codeField:'pcc_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%',
					detno: 'pcd_detno',
					necessaryField: 'pcd_prjcode',
					keyField: 'pcd_id',
					mainField: 'pcd_pccid',
					tbar:['<span  class="x-panel-header-text-default-framed">项目详情</span>','->',{
						text:'获取项目',
						iconCls: 'x-button-icon-check',
						cls: 'x-btn-gray',
						name:'catchab'
					},'-',{
						text:'清除项目',
						iconCls: 'x-button-icon-delete',
						cls: 'x-btn-gray',
						name:'cleanab'
					}]
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});