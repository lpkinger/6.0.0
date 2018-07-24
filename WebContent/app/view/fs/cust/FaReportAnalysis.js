Ext.define('erp.view.fs.cust.FaReportAnalysis',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	autoScroll: true,
	initComponent : function(){ 
			Ext.apply(this, { 
				items:[{
					xtype: 'erpFormPanel',
					saveUrl: 'fs/cust/saveFaReportAnalysis.action',
					readOnly:readOnly==1,
					keyField: 'ra_id'
				},{
					title:'财务报表',
					xtype : 'erpGridPanel2',
					id: 'ra_faitems',
					caller:'RA_Faitems',
					height: 180,
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					bbar: null
				},{
					title:'项目指标',
					xtype : 'erpGridPanel2',
					id: 'ra_credittargetsitems',
					caller:'RA_CreditTargetsItems',
					height: 180,
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					bbar: null
				}]
			}); 
		this.callParent(arguments); 
	}
});