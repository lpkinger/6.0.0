Ext.define('erp.view.crm.marketmgr.marketresearch.MultiForm',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'tabpanel', 
					anchor: '100% 95%',
					id: 'mytab',
					items: [{
						title: '主表',
						id: 'maintab',
						iconCls: 'formset-form',
						layout: 'anchor',
						items: [{
							id:'form',
							xtype: 'myform',
							saveUrl:'crm/saveMultiForm.action',
							deleteUrl: 'crm/deleteMultiForm.action',
							updateUrl: 'crm/updateMultiForm.action',
							anchor: '100% 45%'
						},{
							xtype: 'mygrid',
							id:'grid',
							anchor: '100% 55%'
						}]
					},{
						title: '从表',
						id: 'detailtab',
						iconCls: 'formset-grid',
						layout: 'anchor',
						items: [{
							id: 'detail', 
							xtype: 'mydetail',
							anchor: '100% 100%',
							detno: 'dg_sequence',
							necessaryField: 'dg_field',
							keyField: 'dg_id'
						}]
					}]
				},{
					xtype: 'toolbar', 
					anchor: '100% 5%',
					items: ['->',{
						xtype: 'erpUUListenerButton'
					},'-',{
						iconCls: 'x-button-icon-preview',
						name: 'preview',
						cls: 'x-btn-gray',
						text: $I18N.common.button.erpPreviewButton
					},'-',{
						iconCls: 'tree-save',
						name: 'save',
						cls: 'x-btn-gray',
						text: $I18N.common.button.erpSaveButton
					},'-',{
						iconCls: 'tree-delete',
						name: 'delete',
						cls: 'x-btn-gray',
						text: $I18N.common.button.erpDeleteButton
					},'-',{
						iconCls: 'tree-close',
						name: 'close',
						cls: 'x-btn-gray',
						text: $I18N.common.button.erpCloseButton
					},'->']
				}]
			}] 
		}); 
		me.callParent(arguments);
	} 
});