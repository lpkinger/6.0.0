Ext.define('erp.view.fa.gla.UnWriteVoucher', { 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {
			items: [{
				anchor: '100% 6%',
				xtype: 'toolbar',
				items: [{
					style:null,
					xtype: 'erpResAccountedButton'
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
				}]
			},{
				anchor: '100% 94%',
				xtype: 'erpGridPanel2'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});