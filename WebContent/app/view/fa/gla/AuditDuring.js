Ext.define('erp.view.fa.gla.AuditDuring',{ 
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
				title: '审计期间设置',
				layout: {
					type: 'vbox',
					align: 'center'
				},
				items: [{
					margin : '30 0 0 0',
					xtype: 'yeardatefield',
					id: 'year',
					name: 'year',
					fieldLabel: '年份',
					checked: true
				},{
					margin : '10 0 0 0',
					xtype: 'checkbox',
					id: 'myear',
					name: 'myear',
					//checked: true,
					boxLabel: '启用年中审计期间'
					
				},{
					margin : '10 0 0 0',
					xtype: 'checkbox',
					id: 'eyear',
					name: 'eyear',
					//checked: true,
					boxLabel: '启用年末审计期间'
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