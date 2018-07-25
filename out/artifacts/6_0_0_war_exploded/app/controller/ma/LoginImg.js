Ext.QuickTips.init();
Ext.define('erp.controller.ma.LoginImg', {
	extend: 'Ext.app.Controller',
	views: ['ma.LoginImg'],
	FormUtil:Ext.create('erp.util.FormUtil'),        
	init: function(){ 
		var me = this;
		me.FormUtil = Ext.create('erp.util.FormUtil');
		me.Toast = Ext.create('erp.view.core.window.Toast');
		this.control({ 
			'#logo':{
				afterrender:function(img){
					Ext.Ajax.request({
						url: basePath + '/ma/loginImg/hasLoginImg.action',
						success:function(response){
							var res = new Ext.decode(response.responseText);
							if(!res.success){
								img.hide();
							}else{
								img.setSrc(basePath+'/loginImg/getLoginImg.action?_time='+Date.parse(new Date()));
							}
						}						
					});
					
				}		
			}
		});
	}
});