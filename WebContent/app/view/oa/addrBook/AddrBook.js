Ext.define('erp.view.oa.addrBook.AddrBook',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'border', 
				items: [{
					xtype: 'erpFormPanel',
					region: 'center',
					width: '80%',
					saveUrl: 'oa/addrBook/saveAddrBook.action',
					deleteUrl: 'oa/addrBook/deleteAddrBook.action',
					updateUrl: 'oa/addrBook/updateAddrBook.action',
					getIdUrl: 'common/getId.action?seq=EMPLOYEEMAIL_SEQ',
					keyField: 'emm_id'
				},{
					region: 'west',
					width: '20%',
					xtype: 'addrbooktree',
					tbar: [{
						iconCls: 'tree-add',
						name: 'add',
						text: $I18N.common.button.erpAddButton
					},{
						iconCls: 'tree-delete',
						name: 'delete',
						text: $I18N.common.button.erpDeleteButton
					}]
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});