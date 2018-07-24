Ext.define('erp.controller.sysmng.step.MessageController', {
	extend: 'Ext.app.Controller',
	views:['sysmng.message.MessageSetBar','sysmng.message.MessageSetPanel',
			'sysmng.message.MessagenavPanel','sysmng.message.MessageGridPanel',
			'sysmng.message.MessageAddPanel'
	],
	init:function(){
		var me=this;
		
		this.control({
			
		});		
		var panel = activeItem.child('erpMessageSetPanel');
		if(!panel){
			var panel =  Ext.widget('erpMessageSetPanel');
			activeItem.add(panel);
		}
		
	}
});