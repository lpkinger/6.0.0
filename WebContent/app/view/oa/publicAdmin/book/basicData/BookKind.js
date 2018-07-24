Ext.define('erp.view.oa.publicAdmin.book.basicData.BookKind',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				//id:'bookDataViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					/*saveUrl: 'oa/publicAdmin/book/basicData/saveBookKind.action',
					updateUrl: 'oa/publicAdmin/book/basicData/updateBookKind.action',
					deleteUrl: 'oa/publicAdmin/book/basicData/deleteBookKind.action',
					getIdUrl: 'common/getId.action?seq=BOOKKIND_SEQ',*/
					saveUrl: 'common/saveCommon.action?caller=' +caller,
					deleteUrl: 'common/deleteCommon.action?caller=' +caller,
					updateUrl: 'common/updateCommon.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=BOOKKIND_SEQ',
					keyField: 'bk_id',
					codeField: 'bk_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});