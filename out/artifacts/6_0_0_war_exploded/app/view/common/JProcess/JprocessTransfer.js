Ext.define('erp.view.common.JProcess.JprocessTransfer',{ 
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
					region: 'north',   
					anchor:'100% 30%',
					saveUrl: 'common/saveProcessTransfer.action',
					deleteUrl: 'common/deleteProcessTransfer.action',
					updateUrl: 'common/updateProcessTransfer.action',
					bannedUrl: 'common/abledProcessTransfer.action',
					resBannedUrl:'common/disabledProcessTransfer.action',
					getIdUrl: 'common/getId.action?seq=JPROCESSSTRANSFER_SEQ',
					keyField: 'jt_id',
					codeField:'jt_code'
				},{
					xtype: 'erpGridPanel2',
					region:'center',
					/*layout : 'auto',*/
					anchor:'100% 70%',
					bodyStyle: 'background-color:#f1f1f1;',				   
					selModel: Ext.create('Ext.selection.CheckboxModel',{
				    	headerWidth: 0
					}),
					plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters')],
					condition:"Jt_id is not null",
					
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});