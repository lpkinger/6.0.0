Ext.define('erp.view.fa.fp.RefreshAnticipateBack',{ 
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
				height: 200,
				bodyStyle: 'background: #f1f1f1;border-left:1px solid #bdbdbd;border-right:1px solid #bdbdbd;',
				xtype: 'form',
				cls: 'singleWindowForm',
				bodyCls: 'singleWindowForm',
				title: '逾期回款刷新',
				layout: {
					type: 'vbox',
					align: 'center'
				},
				items: [{		
					margin : '15 0 0 0',
			    	xtype: 'condatefield',
			    	fieldLabel: '日期',
			    	allowBlank: false,
			    	readOnly: true,
			    	labelWidth: 80,
			    	id: 'date',
			    	name: 'date',
			    	width: 400
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