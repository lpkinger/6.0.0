Ext.define('erp.view.scm.purchase.VendorKind',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				region: 'center',
			    anchor:'100% 100%',
				saveUrl: 'scm/purchase/saveVendorKind.action',
				deleteUrl: 'scm/purchase/deleteVendorKind.action',
				updateUrl: 'scm/purchase/updateVendorKind.action',
				bannedUrl: 'scm/purchase/bannedVendorKind.action',
				resBannedUrl: 'scm/purchase/resBannedVendorKind.action',
				getIdUrl: 'common/getId.action?seq=VENDORKIND_SEQ',
				keyField: 'vk_id',
			    codeField: 'vk_code',
			}/*,{
				region: 'east',
				width: '38%',
				xtype: 'vendkindtree',
				tbar: [{
					iconCls: 'tree-add',
					name: 'add',
					text: $I18N.common.button.erpAddButton
				},{
					iconCls: 'tree-delete',
					name: 'delete',
					text: $I18N.common.button.erpDeleteButton
				}]
			}*/]
		}); 
		me.callParent(arguments); 
	} 
});