Ext.define('erp.view.ma.DetailGrid',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'mydetail',
					whoami:getUrlParam('gridCondition')?getUrlParam('gridCondition').split('dg_callerIS')[1]:null,
					anchor: '100% 95%',
					id:'mydetail',
					detno: 'dg_sequence',
					necessaryField: 'dg_field',
					keyField: 'dg_id'
				},{
					xtype:'toolbar',
					anchor:'100% 5%',
					items:['->',
						   {iconCls: 'tree-save',
							name: 'save',
							cls: 'x-btn-gray',
							text: $I18N.common.button.erpSaveButton
							},'-',{iconCls: 'tree-close',
							name: 'close',
							cls: 'x-btn-gray',
							text: $I18N.common.button.erpCloseButton},'->']
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});