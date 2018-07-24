Ext.QuickTips.init();
Ext.define('erp.controller.oa.officialDocument.sendODManagement.Draft', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.officialDocument.sendODManagement.Draft','core.form.Panel','core.form.WordSizeField','core.form.FileField',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.File','core.button.Transmit',
    		'core.button.Update','core.button.Delete','core.button.Submit','core.button.Over',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger'
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
    				this.FormUtil.onSubmit(Ext.getCmp('sod_id').value);
    				alert('提交成功');
    				var main = parent.Ext.getCmp("content-panel"); 
    	    		main.getActiveTab().close();
    			}
    		},
    		'erpOverButton': {
    			click: function(btn){
    				Ext.getCmp('sod_status').setValue('已结束');
    				Ext.getCmp('sod_statuscode').setValue('OVERED');
    				this.FormUtil.onUpdate(this);
    				alert('提交成功');
    				var main = parent.Ext.getCmp("content-panel"); 
    	    		main.getActiveTab().close();
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('sod_id').value);
    			}
    		},
    		'textfield[id=sod_cs_organ]': {
    			render: function(field){
    				Ext.Ajax.request({//拿到grid的columns
    		        	url : basePath + "hr/employee/getHrOrg.action",
    		        	params: {
    		        		em_id: em_uu
    		        	},
    		        	method : 'post',
    		        	async: false,
    		        	callback : function(options, success, response){
    		        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo){
    		        			showError(res.exceptionInfo);return;
    		        		}
    		        		if(res.hrOrg){
    		        			field.setValue(res.hrOrg.or_name);
    		        			field.setReadOnly(true);
    		        		}
    		        	}
    				});
    			}
    		},
    		'htmleditor[id=sod_context]': {
    			afterrender: function(f){
    				f.setHeight(400);
    			},
    			render: function(f){
    				var id = getUrlParam('id');
    				if(id != null){
    					me.getRODDetail(id);
    					f.setReadOnly(true);
    				}
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getRODDetail: function(id){
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "oa/officialDocument/getRODDetail2.action",
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
        		if(!res.rod){
        			return;
        		} else {
        			Ext.getCmp('sod_context').setValue(res.rod.rod_context);
        			Ext.getCmp('sod_attach').setValue(res.rod.rod_attach);
        		}
        	}
        });
	}
});