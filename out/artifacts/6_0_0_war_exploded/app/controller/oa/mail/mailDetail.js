Ext.QuickTips.init();
Ext.define('erp.controller.oa.mail.mailDetail', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:[
    		'oa.mail.mailDetail','oa.mail.MailDetailForm'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'button[id=close]': {
    			click: function(){
    				me.FormUtil.onClose();
    			}
    		},
    		'button[id=post]': {
    			click: function(){
					var form = Ext.getCmp('form').getForm();
					me.FormUtil.getActiveTab().setLoading(true);
					Ext.Ajax.request({
				   		url : basePath + 'oa/mail/send.action',
				   		params: {
				   			receAddr: Ext.getCmp('receAddr').value,
							subject: Ext.getCmp('subject').value,
							context: unescape(Ext.getCmp('context').getValue().replace(/\\u/g,"%u"))
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){
				   			me.FormUtil.getActiveTab().setLoading(false);
				   			var localJson = new Ext.decode(response.responseText);
				   			if(localJson.exceptionInfo){
				   				showError(localJson.exceptionInfo);
				   			}
			    			if(localJson.success){
			    				Ext.Msg.alert("提示","邮件发送成功！");
								form.reset();
				   			}
				   		}
					});
    			}
    		}
    	});
    }
});