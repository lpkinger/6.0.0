Ext.define('erp.view.fs.cust.CustomerInfor',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
			Ext.apply(this, { 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'fs/cust/saveCustomerInfor.action',
					updateUrl: 'fs/cust/updateCustomerInfor.action',
					deleteUrl: 'fs/cust/deleteCustomerInfor.action',
					submitUrl: 'fs/cust/submitCustomerInfor.action',
					resSubmitUrl: 'fs/cust/resSubmitCustomerInfor.action',
					auditUrl: 'fs/cust/auditCustomerInfor.action',
					resAuditUrl: 'fs/cust/resAuditCustomerInfor.action',
					bannedUrl : 'fs/cust/bannedCustomerInfor.action',
					resBannedUrl : 'fs/cust/resBannedCustomerInfor.action',
					getIdUrl: 'common/getId.action?seq=CUSTOMERINFOR_SEQ',
					keyField: 'cu_id',
					codeField: 'cu_code',
					statusField: 'cu_status',
					statuscodeField: 'cu_statuscode'
				},{
					xtype:'tabpanel',
				 	anchor : '100% 50%',
					items:[{
						title:'高管信息',
						id: 'excutive',
						xtype : 'erpGridPanel2',
						caller: 'CustomerExcutive',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						keyField : 'ce_id',
						mainField : 'ce_cuid'
					},{
						title:'股东情况',
						xtype : 'erpGridPanel2',
						caller:'CustomerShareHolder',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=").replace('ce_cuid','cs_cuid'):'',
						bbar: {xtype: 'erpToolbar',id:'toolbar1'},
						id: 'shareholder',
						keyField : 'cs_id',
						mainField : 'cs_cuid'
					},
//					{
//						title:'对外股权投资情况',
//						xtype : 'erpGridPanel2',
//						caller:'CustomerInverstment',
//						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
//							clicksToEdit: 1
//						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
//						condition:condition!=null?condition.replace(/IS/g, "=").replace('ce_cuid','ci_cuid'):'',
//						bbar: {xtype: 'erpToolbar',id:'toolbar2'},
//						id: 'inverstment',
//						keyField : 'ci_id',
//						mainField : 'ci_cuid'
//					},
					{
						title:'主要关联企业',
						xtype : 'erpGridPanel2',
						caller:'CustomerUDStream',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=").replace('ce_cuid','cud_cuid'):'',
						bbar: {xtype: 'erpToolbar',id:'toolbar3'},
						id: 'udstream',
						keyField : 'cud_id',
						mainField : 'cud_cuid'
					},{
						title:'变更说明',
						xtype : 'erpGridPanel2',
						caller:'FsChangesInstruction',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=").replace('ce_cuid','cs_cuid'):'',
						bbar: {
							xtype: 'erpToolbar',
							id:'toolbar4',
							enableAdd : false,
							enableDelete : false,
							enableCopy : false,
							enablePaste : false,
							enableUp : false,
							enableDown : false
						},
						id: 'changes',
						keyField : 'cs_id',
						mainField : 'cs_cuid'
						
					}]
				}]
			}); 
		this.callParent(arguments); 
	}
});