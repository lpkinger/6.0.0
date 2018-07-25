Ext.QuickTips.init();
Ext.define('erp.controller.common.UU', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['common.init.UU'],
    init: function(){ 
    	var me = this;
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'button[id=prev]': {
    			click: function(btn){
    				var bt = parent.Ext.ComponentQuery.query('button[step=1]')[0];
    				bt.fireEvent('click', bt);
    			}
    		},
    		'button[id=next]': {
    			click: function(btn){
    				var bt = parent.Ext.ComponentQuery.query('button[step=3]')[0];
    				bt.fireEvent('click', bt);
    			}
    		},
    		'button[id=confirm]': {
    			click: function(btn){
    				var form = btn.ownerCt.ownerCt;
    				if(form.getForm().isValid()){
    					me.uulogin(form);
    				} else {
    					alert("请正确输入您的uu信息!");
    				}
    			}
    		}
    	});
    },
    uulogin: function(form){
		Ext.Ajax.request({
			url : basePath + 'system/uulogin.action',
			params : {
				em_uu : form.down('#em_uu').value.toString(),
				em_password : form.down('#em_password').value.toString(),
				en_uu : form.down('#en_uu').value.toString()
			},
			method : 'post',
			callback : function(options,success,response){
				var res = Ext.JSON.decode(response.responseText);
				if(res.success){
					var r = Ext.decode(res.data);
					if(r){
						if (r.success) {
							var bt = parent.Ext.ComponentQuery.query('button[step=3]')[0];
		    				bt.fireEvent('click', bt);
						} else {
							if(r.loginStatus == 'noexit'){
								alert("您输入的企业UU不存在，请仔细核对后再重新输入!");
								form.down('#en_uu').focus(false, 100);
							} else if(r.loginStatus =='noactive' ) {
								alert("您输入的企业UU尚未激活，请先激活后再登录本系统！");
							} else if(r.loginStatus == 'loginerror') {
								alert("您输入的用户名或密码错误！");
								form.down('#em_password').focus(false, 100);
							} else {
								alert("您输入的用户名或密码错误！");
								form.down('#em_password').focus(false, 100);
							}
							
						}
					} else {
						var bt = parent.Ext.ComponentQuery.query('button[step=3]')[0];
	    				bt.fireEvent('click', bt);
					}
				}
			}
		});
    }
});
