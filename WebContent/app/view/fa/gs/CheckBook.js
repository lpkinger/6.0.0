Ext.define('erp.view.fa.gs.CheckBook',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'checkBookViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'fa/gs/saveCheckBook.action',
					deleteUrl: 'fa/gs/deleteCheckBook.action',
					updateUrl: 'fa/gs/updateCheckBook.action',
					getIdUrl: 'common/getId.action?seq=CHECKBOOK_SEQ',
					keyField: 'cb_id',
					codeField: 'cb_checkcode',
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'cbd_detno',
					keyField: 'cbd_id',
					mainField: 'cbd_cbid',
					necessaryField: 'cbd_checkcode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});