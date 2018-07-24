Ext.define('erp.view.fa.wg.WageAccount',{ 
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
				bodyStyle: 'background: #f1f1f1;',
				xtype: 'form',
				title: '佣金计算',
				cls: 'singleWindowForm',
				bodyCls: 'singleWindowForm',
				layout: {
					type: 'vbox',
					align: 'center'
				},
				items: [{		
					margin : '40 0 0 0',
			    	xtype: 'monthdatefield',
			    	fieldLabel: '期间',
			    	allowBlank: false,
			    	readOnly: false,
			    	labelWidth: 60,
			    	id: 'date',
			    	name: 'date'
				}],
				buttonAlign: 'center',
	    		buttons: [{
	    			xtype: 'erpConfirmButton',
	    			height: 26
	    		},{
	    			xtype:'erpCloseButton',
	    			height: 26
	    		}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
