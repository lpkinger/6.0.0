Ext.define('erp.view.oa.publicAdmin.book.bookManage.Book',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				//id:'bookManageViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
/*					saveUrl: 'common/saveCommon.action?caller=' +caller,
					deleteUrl: 'common/deleteCommon.action?caller=' +caller,
					updateUrl: 'common/updateCommon.action?caller=' +caller,*/
					saveUrl: 'oa/publicAdmin/book/bookManage/saveBook.action',
					updateUrl: 'oa/publicAdmin/book/bookManage/updateBook.action',
					deleteUrl: 'oa/publicAdmin/book/bookManage/deleteBook.action',
					auditUrl: 'oa/publicAdmin/book/bookManage/auditBook.action',
					resAuditUrl: 'oa/publicAdmin/book/bookManage/resAuditBook.action',
					submitUrl: 'oa/publicAdmin/book/bookManage/submitBook.action',
					resSubmitUrl: 'oa/publicAdmin/book/bookManage/resSubmitBook.action',
					getIdUrl: 'common/getId.action?seq=BOOK_SEQ',
					keyField: 'bo_id',
					codeField: 'bo_code',
					statusField: 'bo_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});