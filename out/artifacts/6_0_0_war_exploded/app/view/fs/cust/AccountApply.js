Ext.define('erp.view.fs.cust.AccountApply',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var tabItems = [],title='保理额度出账申请';
		if(caller=='AccountApply!HX'){
			title = '核心企业出账申请';
			tabItems.push({title:'票据详情',
				xtype : 'erpGridPanel2',
				id: 'accountapplybill',
				caller:'AccountApplyBill',
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToEdit: 1
				}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				condition:condition!=null?condition.replace(/IS/g, "=").replace('aas_aaid','ab_aaid') :'',
				onExport: function(caller, type, condition){
					this.BaseUtil.createExcel(caller, type, condition,title+'-'+this.title + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
				},
				bbar: {xtype: 'erpToolbar',id:'toolbar2'},
				keyField : 'ab_id',
				mainField : 'ab_aaid'
			});
		}
		tabItems.push({
			title:'基础合同信息',
			xtype : 'erpGridPanel2',
			id: 'accountapplysa',
			caller:'AccountApply',
			plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
				clicksToEdit: 1
			}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
			onExport: function(caller, type, condition){
				this.BaseUtil.createExcel(caller, type, condition,title+'-'+this.title + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
			},
			keyField : 'aas_id',
			mainField : 'aas_aaid'
		},{
			title:'基础发票信息',
			xtype : 'erpGridPanel2',
			id: 'accountapplyinv',
			caller:'AccountApplyInv',
			plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
				clicksToEdit: 1
			}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
			condition:condition!=null?condition.replace(/IS/g, "=").replace('aas_aaid','aai_aaid') :'',
			bbar: {xtype: 'erpToolbar',id:'toolbar1'},
			onExport: function(caller, type, condition){
				this.BaseUtil.createExcel(caller, type, condition,title+'-'+this.title + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
			},
			keyField : 'aai_id',
			mainField : 'aai_aaid'
		});
		if (caller=='AccountApply!ZL'){
			title = '融资租赁出账申请';
			tabItems.push({title:'还款计划',
				xtype : 'erpGridPanel2',
				id: 'reimbursementplan',
				caller:'ReimbursementPlan',
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToEdit: 1
				}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				condition:condition!=null?condition.replace(/IS/g, "=").replace('aas_aaid','rp_aaid') :'',
				onExport: function(caller, type, condition){
					this.BaseUtil.createExcel(caller, type, condition,title+'-'+this.title + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
				},
				bbar: {xtype: 'erpToolbar',id:'toolbar2'},
				keyField : 'rp_id',
				mainField : 'rp_aaid'
			});
		}
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 60%',
				saveUrl: 'fs/cust/saveAccountApply.action',
				updateUrl: 'fs/cust/updateAccountApply.action',
				deleteUrl: 'fs/cust/deleteAccountApply.action',
				submitUrl: 'fs/cust/submitAccountApply.action',
				resSubmitUrl: 'fs/cust/resSubmitAccountApply.action',
				auditUrl: 'fs/cust/auditAccountApply.action',
				resAuditUrl: 'fs/cust/resAuditAccountApply.action',
				getIdUrl: 'common/getId.action?seq=AccountApply_SEQ',
				keyField: 'aa_id',
				codeField: 'aa_code',
				statusField: 'aa_status',
				statuscodeField: 'aa_statuscode'
			},{
				xtype: 'tabpanel',
				anchor: '100% 40%',
				items: tabItems
			}]
		}); 
		this.callParent(arguments); 
	}
});