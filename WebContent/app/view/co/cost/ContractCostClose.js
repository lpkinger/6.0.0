Ext.define('erp.view.co.cost.ContractCostClose',{ 
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
					saveUrl: 'co/cost/saveContractCostClose.action',
					deleteUrl: 'co/cost/deleteContractCostClose.action',
					updateUrl: 'co/cost/updateContractCostClose.action',
					auditUrl: 'co/cost/auditContractCostClose.action',
					resAuditUrl: 'co/cost/resAuditContractCostClose.action',
					submitUrl: 'co/cost/submitContractCostClose.action',
					resSubmitUrl: 'co/cost/resSubmitContractCostClose.action',
					catchABUrl:'co/cost/catchProjectCost.action',
					cleanABUrl:'co/cost/cleanProjectCost.action',
					getIdUrl: 'common/getId.action?seq=ContractCostClose_SEQ',
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
					tbar:['<span  class="x-panel-header-text-default-framed">合同详情</span>','->',{
						text:'获取合同',
						iconCls: 'x-button-icon-check',
						cls: 'x-btn-gray',
						name:'catchab'
					},'-',{
						text:'清除合同',
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