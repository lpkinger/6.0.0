Ext.define('erp.view.oa.publicAdmin.book.borrowManage.BorrowApp',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'borrowManageViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 70%',
					saveUrl: 'oa/publicAdmin/book/borrowManage/saveBorrowList.action',
					updateUrl: 'oa/publicAdmin/book/borrowManage/updateBorrowList.action',
					deleteUrl: 'oa/publicAdmin/book/borrowManage/deleteBorrowList.action',
					auditUrl: 'oa/publicAdmin/book/borrowManage/auditBorrowList.action',
					resAuditUrl: 'oa/publicAdmin/book/borrowManage/resAuditBorrowList.action',
					submitUrl: 'oa/publicAdmin/book/borrowManage/submitBorrowList.action',
					resSubmitUrl: 'oa/publicAdmin/book/borrowManage/resSubmitBorrowList.action',
					getIdUrl: 'common/getId.action?seq=BORROWLIST_SEQ',
					keyField: 'bl_id',
					codeField: 'bl_code',
					statusField: 'bl_statuscode'
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 30%', 
					detno: 'bld_detno',
					necessaryField: 'bld_bookcode',
					keyField: 'bld_id',
					mainField: 'bld_blid',
					
				}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});