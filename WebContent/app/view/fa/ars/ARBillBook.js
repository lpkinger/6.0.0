Ext.define('erp.view.fa.ars.ARBillBook',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'arbillBookViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'fa/ars/saveARBillBook.action',
					deleteUrl: 'fa/ars/deleteARBillBook.action',
					updateUrl: 'fa/ars/updateARBillBook.action',
					auditUrl: 'fa/ars/auditARBillBook.action',
					resAuditUrl: 'fa/ars/resAuditARBillBook.action',
					submitUrl: 'fa/ars/submitARBillBook.action',
					resSubmitUrl: 'fa/ars/resSubmitARBillBook.action',
					getIdUrl: 'common/getId.action?seq=ARBillBook_SEQ',
					keyField: 'abb_id',
					codeField: 'abb_code',
					/*statusField: ''*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});