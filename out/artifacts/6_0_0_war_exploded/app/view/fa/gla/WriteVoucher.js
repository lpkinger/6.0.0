Ext.define('erp.view.fa.gla.WriteVoucher', { 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {
			items: [{
				region: 'north',
				height: 35,
				xtype: 'toolbar',
				items: [{
					xtype: 'erpAccountedButton'
				},{
					margin:'0 0 0 10',
			    	text: $I18N.common.button.erpCloseButton,
			    	iconCls: 'x-button-icon-close',
			        cls: 'x-btn-gray',
			        handler: function(){
			        	var main = parent.Ext.getCmp("content-panel"); 
			        	main.getActiveTab().close();
			        }
				},'->','期间:', {
					xtype: 'tbtext',
					id: 'yearmonth'
				},'-','总凭证数:', {
					xtype: 'tbtext',
					id: 'total'
				},'-','在录入凭证数:', {
					xtype: 'tbtext',
					id: 'enter'
				},'-','已提交凭证数 :', {
					xtype: 'tbtext',
					id: 'commit'
				},'-','已审核凭证数:', {
					xtype: 'tbtext',
					id: 'audit'
				}]
			},{
				region: 'center',
				xtype: 'erpGridPanel2'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});