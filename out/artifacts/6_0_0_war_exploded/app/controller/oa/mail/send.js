Ext.QuickTips.init();
Ext.define('erp.controller.oa.mail.send', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:[
    		'oa.mail.send','oa.mail.MailForm','oa.mail.TreePanel'
    	],
    init:function(){
    	var me = this;
    	me.attachcount = 0;
    	me.files = new Array();
    	this.control({ 
    		'erpMailTreePanel': {
    			itemmousedown: function(selModel, record){
    				if(record.get('leaf')){//不是根节点
						var value = Ext.getCmp('receAddr').value;
						if(value == null || value == ''){
							Ext.getCmp('receAddr').setValue(record.get('qtip'));
						}
						else if(me.BaseUtil.contains(Ext.getCmp('receAddr').value, record.get('qtip'), true)){
							return;
						}
						else{
							Ext.getCmp('receAddr').setValue(value + ';' + record.get('qtip'));
						}
					}
					selModel.deselect(record);
    			}
    		},
    		'displayfield[id=attachs]': {
    			afterrender: function(field){
    				field.hide();
    			}
    		},
    		'filefield[id=attach]': {
    			change: function(field){
    				if(field.value != null){
    					var container = Ext.create('Ext.form.FieldContainer', {
    						layout: 'hbox',
    						fieldLabel: "附件" + (me.attachcount + 1),
    				        items: [{
    				            xtype: 'textfield',
    				            id: 'attach' + me.attachcount,
    				            flex: 1
    				        },{
    				            xtype: 'button',
    				            text: '上传',
    				            id: 'upload' + me.attachcount,
    				            handler: function(btn){
    				            	var form = btn.ownerCt.ownerCt;
    				            	var f = Ext.getCmp(btn.id.replace('upload', 'attach'));
    				            	if(f.value != null && f.value != ''){
    				            		//field.value = f.value;
    				            		form.getForm().submit({
        				            		url: basePath + 'common/upload.action?em_code=' + em_code,
        				            		waitMsg: "正在上传:" + f.value,
        				            		success: function(fp, o){
        				            			if(o.result.error){
        				            				showError(o.result.error);
        				            			} else {
        				            				Ext.Msg.alert("恭喜", f.value + " 上传成功!");
            				            			btn.setText("上传成功(" + Ext.util.Format.fileSize(o.result.size) + ")");
            				            			btn.disable(true);
            				            			//field.button.disable(false);
            				            			me.files[Number(btn.id.replace('upload', ''))] = o.result.filepath;
        				            			}
        				            		}
        				            	});
    				            	}
    				            },
    				            flex: 1
    				        }, {
    				            xtype: 'button',
    				            text: '删除',
    				            id: 'delete' + me.attachcount,
    				            handler: function(btn){
    				            	var f = Ext.getCmp(btn.id.replace('delete', 'attach'));
    				            	if(f.value != null && f.value != ''){
    				            		me.files[Number(btn.id.replace('delete', ''))] = '';
    				            	}
    				            	btn.ownerCt.destroy(true);
    				            	me.attachcount--;
    				            },
    				            flex: 1
    				        }]
    					});
    					if(me.FormUtil.contains(field.value, "\\", true)){
    						Ext.getCmp('attach' + me.attachcount).setValue(field.value.substring(field.value.lastIndexOf('\\') + 1));
    					} else {
    						Ext.getCmp('attach' + me.attachcount).setValue(field.value.substring(field.value.lastIndexOf('/') + 1));
    					}
    					Ext.getCmp('form').insert(3, container);
    					me.attachcount++;
    					//field.reset();
    					//field.button.disable(true);
    					field.button.setText("继续...");
    				}
    			}
    		},
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
							files: me.files,
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
			    				Ext.Msg.alert("提示","邮件发送成功！", function(){
			    					window.location.href = window.location.href;
			    				});
				   			}
				   		}
					});
    			}
    		},
    		'button[id=save]': {
    			click: function(){
					me.FormUtil.getActiveTab().setLoading(true);
					Ext.Ajax.request({
				   		url : basePath + 'oa/mail/save.action',
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
			    				Ext.Msg.alert("提示","邮件保存成功！");
				   			}
				   		}
					});
    			}
    		}
    	});
    }
});