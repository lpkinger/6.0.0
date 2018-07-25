Ext.define('erp.controller.sys.step.MOController', {
	extend: 'Ext.app.Controller',
	id:'MOController',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	FormUtil: Ext.create('erp.util.FormUtil'),
	views:['sys.mo.MoPortal'],
	init:function(){
		var me=this;
		this.control({
			
		});
		var app=erp.getApplication();
		var portal = activeItem.child('moportal');
		if(!portal){
			portal =  Ext.widget('moportal',{desc:'生产委外管理'});
			activeItem.add(portal);
			Ext.getCmp('syspanel').setTitle(portal.desc);
		}
	},
	onSaveConfigs:function(field,value){
		var obj=new Object(),value=value || field.value;
		obj.data= typeof value === 'boolean' ? (value ? 1 : 0) : (field.xtype == 'radiogroup' ? Ext.Object.getValues(value)[0] : value);
		obj.id=field.id.split("-")[1];
		this.saveParamSet(field, unescape(escape(Ext.JSON.encode(obj))),showResult);
	},
	saveParamSet:function(field,update,fn){
		var params=new Object();
		if(field)params.argType=field.id.split("-")[0]; 
	    params.update=update;
		Ext.Ajax.request({
			url: basePath + 'ma/sysinit/saveParamSet.action',
		    params:params,
			method: 'POST',
			callback: function(opt, s, r) {
				if(r && r.status == 200) {
					var res = Ext.JSON.decode(r.responseText);
					fn.call(null,'提示','修改成功!');
				}
			}
		});
	}
});