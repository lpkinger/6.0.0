Ext.define('erp.view.oa.publicAdmin.book.basicData.BookStorage',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'bookDataViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 70%',
					saveUrl: 'oa/publicAdmin/book/basicData/saveBookStorage.action',
					updateUrl: 'oa/publicAdmin/book/basicData/updateBookStorage.action',
					deleteUrl: 'oa/publicAdmin/book/basicData/deleteBookStorage.action',
					auditUrl: 'oa/publicAdmin/book/basicData/auditBookStorage.action',
					resAuditUrl: 'oa/publicAdmin/book/basicData/resAuditBookStorage.action',
					submitUrl: 'oa/publicAdmin/book/basicData/submitBookStorage.action',
					resSubmitUrl: 'oa/publicAdmin/book/basicData/resSubmitBookStorage.action',
					getIdUrl: 'common/getId.action?seq=BOOKSTORAGE_SEQ',
					keyField: 'bs_id',
					codeField: 'bs_code',
					statusField: 'bs_statuscode'
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 30%', 
					detno: 'bsd_detno',
					necessaryField: 'bsd_code',
					keyField: 'bsd_id',
					mainField: 'bsd_bsid',
					
				}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});