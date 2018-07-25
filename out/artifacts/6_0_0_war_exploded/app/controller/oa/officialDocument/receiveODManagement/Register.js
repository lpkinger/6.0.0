Ext.QuickTips.init();
Ext.define('erp.controller.oa.officialDocument.receiveODManagement.Register', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.officialDocument.receiveODManagement.Register','core.form.Panel','core.form.FileField',
    		'core.button.Save','core.button.Close','core.button.Over','core.button.Submit',
    		'core.button.Update','core.button.Delete','core.button.Distribute2','core.button.Transmit','core.button.File',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.beforeSave(this);    			
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.onClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpSubmitButton': {
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('rod_id').value);
    				alert('提交成功');
    				var main = parent.Ext.getCmp("content-panel"); 
    	    		main.getActiveTab().close();
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('rod_id').value);
    			}
    		},
    		'erpOverButton': {
    			click: function(btn){
    				Ext.getCmp('rod_status').setValue('已结束');
    				Ext.getCmp('rod_statuscode').setValue('OVERED');
    				this.FormUtil.onUpdate(this);
    				alert('提交成功');
    				var main = parent.Ext.getCmp("content-panel"); 
    	    		main.getActiveTab().close();
    			}
    		},
    		'htmleditor[id=rod_context]': {
    			afterrender: function(f){
    				f.setHeight(400);
    			},
    			render: function(f){
    				var id = getUrlParam('id');
    				if(id != null){
    					me.getSODDetail(id);
    					f.setReadOnly(true);
    				}
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getSODDetail: function(id){
		var me = this;
//		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "oa/officialDocument/getSODDetail2.action",
        	params: {
        		id: id
        	},
        	method : 'post',
        	async: false,
        	callback : function(options, success, response){
        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
        		console.log(response);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(!res.sod){
        			return;
        		} else {        			
        			Ext.getCmp('rod_context').setValue(res.sod.sod_context);
//        			me.attach = res.sod.sod_attach;
        			Ext.getCmp('rod_attach').setValue(res.sod.sod_attach);        			
        		}
        	}
        });
	}
});