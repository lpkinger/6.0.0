Ext.define('erp.view.fs.cust.AssetsLiabilities',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
			Ext.apply(this, { 
				items:[{
					xtype: 'erpFormPanel',
					anchor: '100% 55%',
					saveUrl: 'fs/cust/saveAssetsLiabilities.action',
					readOnly:readOnly==1,
					keyField: 'al_id'
				},{
					xtype: 'tabpanel',
					anchor: '100% 45%',
					items: [{
						title:'货币资金',
						xtype : 'erpGridPanel2',
						id: 'al_monetaryfund',
						caller:'AssetsLiabilities',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=") + " and ai_kind='货币资金'":'',
						bbar: null,
						keyField : 'ai_id',
						mainField : 'ai_alid'
					},{
						title:'应收账款',
						xtype : 'erpGridPanel2',
						id: 'al_accountinforar',
						caller:'AL_AccountInforAR',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=") +" and ai_kind='应收账款'":'',
						bbar: null,
						keyField : 'ai_id',
						mainField : 'ai_alid'
					},{
						title:'其他应收账款',
						xtype : 'erpGridPanel2',
						id: 'al_accountinforothar',
						caller:'AL_AccountInforOthAR',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=") +" and ai_kind='其他应收账款'":'',
						bbar: null,
						keyField : 'ai_id',
						mainField : 'ai_alid'
					},{
						title:'预付账款',
						id: 'al_accountinforpp',
						xtype : 'erpGridPanel2',
						caller:'AL_AccountInforPP',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=") +" and ai_kind='预付账款'":'',
						bbar: null,
						keyField : 'ai_id',
						mainField : 'ai_alid'
					},{
						title:'存货',
						id: 'al_accountinforinv',
						xtype : 'erpGridPanel2',
						caller:'AL_AccountInforInv',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=") +" and ai_kind='存货'":'',
						bbar: null,
						keyField : 'ai_id',
						mainField : 'ai_alid'
					},{
						title:'固定资产',
						id: 'al_accountinforfix',
						xtype : 'erpGridPanel2',
						caller:'AL_AccountInforFix',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=") + " and ai_kind='固定资产'":'',
						bbar: null,
						keyField : 'ai_id',
						mainField : 'ai_alid'
					},{
						xtype: 'container',
						title: '短期借款',
						layout: 'anchor', 
						items: [{
							title:'授信银行',
							anchor: '100% 50%',
							xtype : 'erpGridPanel2',
							id: 'al_accountinforcb',
							caller:'AL_AccountInforCB',
							plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
								clicksToEdit: 1
							}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
							condition:condition!=null?condition.replace(/IS/g, "=") + " and ai_kind='短期借款-授信银行'":'',
							bbar: null,
							keyField : 'ai_id',
							mainField : 'ai_alid'
						},{
							title:'贷款银行',
							anchor: '100% 50%',
							xtype : 'erpGridPanel2',
							id: 'al_accountinforlb',
							caller:'AL_AccountInforLB',
							plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
								clicksToEdit: 1
							}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
							condition:condition!=null?condition.replace(/IS/g, "=") + " and AI_KIND='短期借款-贷款银行'":'',
							bbar: null,
							keyField : 'aid_id',
							mainField : 'aid_alid'
						}]
					},{
						title:'应付账款',
						xtype : 'erpGridPanel2',
						id: 'al_accountinforap',
						caller:'AL_AccountInforAP',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=") + " and ai_kind='应付账款'":'',
						bbar: null,
						keyField : 'ai_id',
						mainField : 'ai_alid'
					},{
						title:'其他应付账款',
						xtype : 'erpGridPanel2',
						id: 'al_accountinforothap',
						caller:'AL_AccountInforOthAP',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=") + " and ai_kind='其他应付账款'":'',
						bbar: null,
						keyField : 'ai_id',
						mainField : 'ai_alid'
					},{
						title:'长期借款',
						id: 'al_accountinforlong',
						xtype : 'erpGridPanel2',
						caller:'AL_AccountInforLong',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=") + " and ai_kind='长期借款'":'',
						bbar: null,
						keyField : 'ai_id',
						mainField : 'ai_alid'
					}]
				}]
		}); 
		this.callParent(arguments); 
	}
});