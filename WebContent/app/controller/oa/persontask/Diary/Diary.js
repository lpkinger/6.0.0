Ext.QuickTips.init();
Ext.define('erp.controller.oa.persontask.Diary.Diary', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.persontask.Diary.Diary','core.form.Panel',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    			'core.trigger.DbfindTrigger','core.button.Upload','core.button.DownLoad','core.button.Scan','core.form.YnField'
    			
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						Ext.getCmp(form.codeField).setValue(me.BaseUtil.getRandomNumber());//自动添加编号
					}
					if(! this.FormUtil.checkForm()){
						return;
					}
					if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
						this.FormUtil.getSeqId(form);
					}
					form.setLoading(true);//loading...
					Ext.Ajax.request({
				   		url : basePath + form.saveUrl,
				   		params : {
				   			formStore: unescape(Ext.JSON.encode(form.getValues()).replace(/\\/g,"%"))
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){
				   			form.setLoading(false);
				   			var localJson = new Ext.decode(response.responseText);
			    			if(localJson.success){
			    				saveSuccess(function(){
			    					//add成功后刷新页面进入只读的页面 
					   				window.location.href = basePath + "jsps/oa/persontask/Diary/DiaryR.jsp?formCondition=di_idIS" + Ext.getCmp(form.keyField).value;
			    				});
				   			} else if(localJson.exceptionInfo){
				   				var str = localJson.exceptionInfo;
				   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
				   					str = str.replace('AFTERSUCCESS', '');
				   					saveSuccess(function(){
				    					//add成功后刷新页面进入只读的页面 
				   						window.location.href = basePath + "jsps/oa/persontask/Diary/DiaryR.jsp?formCondition=di_idIS" + Ext.getCmp(form.keyField).value;
				    				});
				   					showError(str);
				   				} else {
				   					showError(str);
					   				return;
				   				}
				   			} else{
				   				saveFailure();
				   			}
				   		}
				   		
					});
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				me.FormUtil.onAdd('addNote', '新增日记', 'jsps/oa/persontask/Diary/Diary.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete({id: Number(Ext.getCmp('di_id').value)});
    			}
    		},
    		'textarea[name=di_thoughts]': {
    			afterrender: function(f){
    				f.setHeight(300);
    				f.setWidth(800);
    				f.el.dom.className = "x-field x-form-item x-column x-field-default";
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});