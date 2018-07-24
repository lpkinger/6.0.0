Ext.define('erp.view.ma.MultiGrid',{ 
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
						title: '从表',
						id: 'detailtab',
						iconCls: 'formset-grid',
						layout: 'anchor',
						items: [{
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
						text: '关联查询设置'
					},'-',{
						text: '业务逻辑设置'
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