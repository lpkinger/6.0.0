Ext.define('erp.view.plm.request.ProductType',{ 
	extend: 'Ext.Viewport', 
	layout: 'border',
	width:'100%',
	height:'100%',
	border:false,
	requires:['erp.view.plm.base.ProductTypeTree'],
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				region: 'north',
				xtype:'container',
				items: [{
					xtype: 'form',
					layout : 'fit',
					bodyStyle: {background: '#f0f0f1'},
					items: [{
						xtype: 'button', 
						id: 'confirm',
						name: 'confirm',
						text : $I18N.common.button.erpConfirmButton,
					    iconCls: 'x-button-icon-submit',
					    cls: 'x-btn-gray-1',
					    width:85
					},{
						xtype: 'button',
						name: 'close',
						text : $I18N.common.button.erpCloseButton,
					    iconCls: 'x-button-icon-close',
					    cls: 'x-btn-gray-1',
					    width:85
					}]
				}]
			}, {
				xtype:'erpProductTypeTreePanel',
				region: 'center',
				layout:'fit',
				width:'100%'
			}]
		}); 
		me.callParent(arguments); 
	} 
});