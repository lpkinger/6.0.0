Ext.QuickTips.init();
Ext.define('erp.controller.common.mailTemp', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
    views:[
    		'core.form.Panel','core.form.FtField','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger',
    		'core.grid.TfColumn','core.grid.YnColumn','core.trigger.DbfindTrigger','core.form.FtDateField','core.form.FtFindField',
    		'core.form.FtNumberField','core.form.MonthDateField','core.button.AddDetail','core.button.Save','core.button.Add',
    		'core.button.ResAudit', 'core.button.Audit', 'core.button.Close', 'core.button.Delete','core.button.Update','core.button.Delete',
    		'core.button.ResSubmit','core.button.Submit','common.mailTemp.Viewport'
    	],
    	 init:function(){
    		var me = this;
	        this.BaseUtil = Ext.create('erp.util.BaseUtil');
	        this.FormUtil = Ext.create('erp.util.FormUtil');
	    	this.control({
	    		'htmleditor[name=mt_content]': {
	    			initialize:function(f){
	    			   var iframe=document.getElementById('mt_content').getElementsByTagName("iframe")[0];
	    			   if(iframe.contentWindow.document.body.childNodes.length>0){
	    				   console.log(iframe.scrolling);
	    				   iframe.scrolling="yes";
	           			   var body=iframe.contentWindow.document.body;
	       				   var child=body.childNodes;
	       				   var h=0;
	       				   for(var i=0;i<child.length;i++){
	       					   if(child[i].offsetHeight){
	       						h+=child[i].offsetHeight;
	       						if(child[i].nodeName!='tr'){
	       							h+=14;
	       						}
	       					   }else if(child[i].nodeName!='br'){
	       						   h+=20;
	       					   }
	       				   }  
	       				   if(h<350){
	       					 f.setHeight(350);
	       				   }else{
	       					f.setHeight(h);
	       				   }
	    			   }else{
	    				   f.setHeight(350);
	    			   }
	    			   var form=Ext.getCmp('form');
	    				form.doLayout();
	    			}    			
	    		},
	    		'multidbfindtrigger':{
	            	 aftertrigger: function(){
	            		var newValue = Ext.getCmp('mt_address').value;
//	            		newValue = newValue.replace(/#/g,";");
	            		var reciveName = Ext.getCmp('mt_reciveman').value;
	            		var arr1 = newValue.split("#"), arr2 = reciveName.split("#");
	            		if(arr1.length != arr2.length){
	            			Ext.Msg.alert("提示", "选择的记录中有重复邮箱");
	            			return;
	            		}
	            		var array = [];
	            		for(var i = 0; i < arr1.length; i++){
	            			array.push(arr2[i] + '<' + arr1[i] + '>');
	            		}
	            		var value = array.join(';');
	            		Ext.getCmp('mt_address').setValue(value);
	            	 } 
	             },
	    		'erpSaveButton': {
	                click: function(btn) {
	                	var form = me.getForm(btn), codeField = Ext.getCmp(form.codeField);
						if(codeField.value == null || codeField.value == ''){
							me.BaseUtil.getRandomNumber(caller);//自动添加编号
						}
	                    //保存
						this.FormUtil.beforeSave(this);
	                }
	            },
	            'erpDeleteButton': {
	                click: function(btn) {
	                    me.FormUtil.onDelete(Ext.getCmp('mt_id').value);
	                }
	            },
	            'erpUpdateButton': {
	                afterrender: function(btn) {
	                    var status = Ext.getCmp('mt_statuscode');
	                    if (status && status.value != 'ENTERING') {
	                        btn.hide();
	                    }
	                },
	                click: function(btn) {
	                	this.FormUtil.onUpdate(this);
	                }
	            },
	            'erpAddButton': {
	                click: function() {
	                    me.FormUtil.onAdd('addmailTemp', '邮件模板设置', 'jsps/common/mailTemp.jsp');
	                }
	            },
	            'erpCloseButton': {
	                click: function(btn) {
	                    me.FormUtil.beforeClose(me);
	                }
	            },
	            'erpSubmitButton': {
	                afterrender: function(btn) {
	                    var status = Ext.getCmp('mt_statuscode');
	                    if (status && status.value != 'ENTERING') {
	                        btn.hide();
	                    }
	                },
	                click: {
	                	lock: 2000,
		                fn:function(btn) {
		                	this.FormUtil.onSubmit(Ext.getCmp('mt_id').value);
		                }
	                }
	            },
	            'erpResSubmitButton': {
	                afterrender: function(btn) {
	                    var status = Ext.getCmp('mt_statuscode');
	                    if (status && status.value != 'COMMITED') {
	                        btn.hide();
	                    }
	                },
	                click: {
	                    lock: 2000,
		                fn:function(btn) {
	                        me.FormUtil.onResSubmit(Ext.getCmp('mt_id').value);
		                }
	                }
	            },
	            'erpAuditButton': {
	                afterrender: function(btn) {
	                    var status = Ext.getCmp('mt_statuscode');
	                    if (status && status.value != 'COMMITED') {
	                        btn.hide();
	                    }
	                },
	                click:{ 
	    				lock: 2000,
		                fn: function(btn) {
	                    	me.FormUtil.onAudit(Ext.getCmp('mt_id').value);
		                }
	                }
	            },
	            'erpResAuditButton': {
	                afterrender: function(btn) {
	                    var status = Ext.getCmp('mt_statuscode');
	                    if (status && status.value != 'AUDITED') {
	                        btn.hide();
	                    }
	                },
	                click:{ 
	    				lock: 2000,
		                fn: function(btn) {
	                  	  	me.FormUtil.onResAudit(Ext.getCmp('mt_id').value);
		                }
	                }
	            }
	    		
	    	})
    	 },
	 getForm: function(btn) {
		    return btn.ownerCt.ownerCt;
		}
	
});
