Ext.define('erp.view.ma.group.BaseData',{ 
	extend: 'Ext.Viewport', 
	layout: {
		type: 'vbox',
		align: 'center'
	},
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
	    		cls: 'singleWindowForm',
				bodyCls: 'singleWindowForm',
				xtype: 'form',
				title: '勾选不允许在当前账套新增和修改的资料',
				bodyStyle: 'background: #f1f1f1;',
				width: 500,
				height: 500,
				layout: 'column',
				defaults: {
					xtype: 'checkbox',
					margin: '2 10 2 10',
					columnWidth: .33
				},
				items: [{
					boxLabel: '全选',
					id: 'selectall',
					columnWidth: 1
				}],
				buttonAlign: 'center',
				buttons: [{
					text: $I18N.common.button.erpConfirmButton,
					cls: 'x-btn-blue',
					id: 'confirm'
				}, {
					text: $I18N.common.button.erpCloseButton,
					cls: 'x-btn-blue',
					id: 'close'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});