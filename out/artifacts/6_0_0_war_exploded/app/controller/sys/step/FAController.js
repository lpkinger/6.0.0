Ext.define('erp.controller.sys.step.FAController', {
	extend: 'Ext.app.Controller',
	id:'FAController',
	views:['sys.fa.FAPortal'],
	init:function(){
		var me=this;
		this.control({
			
		});
		var app=erp.getApplication();
		var faportal = activeItem.child('faportal');
		if(!faportal){
			var faportal =  Ext.widget('faportal',{desc:'财务会计'});
			activeItem.add(faportal);
			Ext.getCmp('syspanel').setTitle(faportal.desc);
		}
	}
});