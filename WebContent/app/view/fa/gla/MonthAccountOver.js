Ext.define('erp.view.fa.gla.MonthAccountOver',{ 
	extend: 'Ext.Viewport', 
	layout: {
		type: 'vbox',
		align: 'center',
		pack: 'center'
	},
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				width: 450,
				height: 300,
				bodyStyle: 'background: #f1f1f1;border:1px solid #cfcfcf',
				xtype: 'form',
				title: '总账期末结转',
				layout: {
					type: 'vbox',
					align: 'center'
				},
				items: [{		
					margin : '40 0 0 0',
			    	xtype: 'monthdatefield',
			    	fieldLabel: '期间',
			    	allowBlank: false,
			    	readOnly: true,
			    	labelWidth: 60,
			    	id: 'date',
			    	name: 'date'
				}],
				buttonAlign: 'center',
	    		bbar: {
					style:'border:1px solid #cfcfcf',
					items:['->',{
						xtype: 'erpStartAccountButton',
						height: 26
					},{
						xtype:'erpOverAccountButton',
						height: 26
					},{
						xtype:'erpCloseButton',
	    				height: 26	
					},'->']
				}
			}] 
		}); 
		me.callParent(arguments); 
	} 
});