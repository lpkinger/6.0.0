Ext.define('erp.view.fa.ars.RepAccount',{ 
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
				id: 'form',
				cls: 'singleWindowForm',
				bodyCls: 'singleWindowForm',
				title: '报表计算',
				layout: {
					type: 'vbox',
					align: 'center'
				},
				items: [{		
					margin : '40 0 0 0',
			    	xtype: 'monthdatefield',
			    	fieldLabel: '期间',
			    	readOnly: false,
			    	labelWidth: 60,
			    	id: 'date',
			    	name: 'date'
				},{		
					margin : '40 0 0 0',
			    	xtype: 'textfield',
			    	fieldLabel: '期间',
			    	readOnly: false,
			    	hidden: true,
			    	labelWidth: 60,
			    	id: 'addate',
			    	name: 'addate'
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