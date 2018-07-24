Ext.define('erp.view.core.window.MasterChange', {
	extend: 'Ext.window.Window',
	alias: 'widget.majoritemwarn',
	id:'majoritemwarn',
	width: 595,
	height:400,
	frame: true,
	resizable:false,
	modal: true,
	bodyStyle: 'background: #F1F1F1;',
	layout: 'column',
	closable : false,
	store:'',
	initComponent: function() {
		var store=this.store;
		
		var me=this;
		Ext.apply(me, { 
			items: [{
					xtype:'label',
					text:'禁用/启用账套',
					height:20,
					baseCls:'fontset',
					columnWidth:1.0
				},{
					xtype: 'checkboxgroup',				
					columnWidth:0.8,
					id:'cbgroup',
					columns:2,
                    items: []
				}]
		});
			
		
		
		this.callParent(arguments);
		this.show();
	}
});
