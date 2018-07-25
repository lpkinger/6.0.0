Ext.define('erp.view.plm.project.Transaction',{ 
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
					anchor: '100% 100%',
					saveUrl: 'plm/project/saveTransaction.action',
					deleteUrl: 'plm/project/deleteTransaction.action',
					updateUrl: 'plm/project/updateTransaction.action',
					getIdUrl: 'common/getId.action?seq=TeammemberTran_SEQ',
					keyField: 'tt_id',
                    codeField:'tt_code'
				}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});