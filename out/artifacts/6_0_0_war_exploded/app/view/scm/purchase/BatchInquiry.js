Ext.define('erp.view.scm.purchase.BatchInquiry',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 45%',
				saveUrl: 'scm/purchase/saveBatchInquiry.action',
				updateUrl: 'scm/purchase/updateBatchInquiry.action',
				deleteUrl: 'scm/purchase/deleteBatchInquiry.action',
				auditUrl: 'scm/purchase/auditBatchInquiry.action',
				resAuditUrl: 'scm/purchase/resAuditBatchInquiry.action',
				submitUrl: 'scm/purchase/submitBatchInquiry.action',
				resSubmitUrl: 'scm/purchase/resSubmitBatchInquiry.action',
				getIdUrl: 'common/getId.action?seq=BATCHINQUIRY_SEQ',
				keyField: 'bi_id',
				id:'form',
				codeField: 'bi_code',
				statusField: 'bi_statuscode'
			},{
				xtype: 'tabpanel',
				anchor: '100% 55%',
				id:'tab',
				items:[{
					xtype: 'erpGridPanel2',
					id : 'prodtab',
					title : '物料明细',
					detno: 'bip_detno',
					necessaryField: 'bip_prodcode',
					keyField: 'bip_id',
					mainField: 'bip_biid',
					allowExtraButtons: true,
					bbar: {xtype: 'erpToolbar',id:'toolbar1'},
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
				        clicksToEdit: 1
				    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')]
				},{
					xtype: 'erpGridPanel2',
					id: 'vendtab' ,
					detno: 'biv_detno',
					title : '供&nbsp应&nbsp商',
					necessaryField: 'biv_vendcode',
					caller : 'BatchInVend',
					condition:condition!=null?condition.replace(/IS/g, "=").replace('bip_biid','biv_biid'):'',
					keyField: 'biv_id',
					mainField: 'biv_biid',
					allowExtraButtons: true,
					bbar: {xtype: 'erpToolbar',id:'toolbar2'},
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
				        clicksToEdit: 1
				    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')]
				}],
			}]
		}); 
		me.callParent(arguments); 
	} 
});