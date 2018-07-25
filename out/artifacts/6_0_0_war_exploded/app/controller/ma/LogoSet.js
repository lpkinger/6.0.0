Ext.QuickTips.init();
Ext.define('erp.controller.ma.LogoSet', {
	extend: 'Ext.app.Controller',
	views: ['ma.LogoSet'],
	FormUtil:Ext.create('erp.util.FormUtil'),        
	init: function(){ 
		var me = this;
		me.FormUtil = Ext.create('erp.util.FormUtil');
		me.Toast = Ext.create('erp.view.core.window.Toast');
		this.control({ 
			'#logo':{
				afterrender:function(img){
					Ext.Ajax.request({
						url: basePath + 'ma/logo/hasLogo.action',
						success:function(fp, o,rep){
							if(fp.responseText=='false') img.hide();
							else img.setSrc(basePath+'ma/logo/get.action');
						}						
					});
					
				}		
			}
		});
	}
});