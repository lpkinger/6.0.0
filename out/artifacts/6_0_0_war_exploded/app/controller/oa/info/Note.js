Ext.QuickTips.init();
Ext.define('erp.controller.oa.info.Note', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.info.Note','core.form.Panel','core.form.FileField','core.form.YnField','core.trigger.MultiDbfindTrigger',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    			'core.trigger.DbfindTrigger','core.button.Upload','core.button.DownLoad','core.button.Scan','core.form.YnField'
    			,'core.trigger.HrOrgTreeDbfindTrigger','core.form.HrOrgSelectField'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					if(! this.FormUtil.checkForm()){
						return;
					}
					if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
						this.FormUtil.getSeqId(form);
					}
					var r=form.getValues();	
					var ispublic=r.no_ispublic;
					var recipientid=r.no_recipientid;					
					if(ispublic=="0"&&recipientid==""){
						showError("请选择要接收的人员或者组织");
					}else{
						
						Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
						if(contains(k, 'ext-', true)){
							delete r[k];
						}
						});
					form.setLoading(true);//loading...
					Ext.Ajax.request({
				   		url : basePath + form.saveUrl,
				   		params : {
				   			/*
				   			 * 反馈编号：2017070930 ， 3、不需要把 '\' 替换成'%' ， 把后台解析Json格式的JSONUtil.toMap()改成BaseUtil.parseFormStoreToMap()解决html格式问题
				   			 */
				   			formStore: unescape(escape(Ext.JSON.encode(r)))
				   		},
				   		method : 'post',
				   		async: false,
				   		callback : function(options,success,response){
				   			form.setLoading(false);
				   			var localJson = new Ext.decode(response.responseText);
			    			if(localJson.success){
			    				saveSuccess(function(){
			    					//add成功后刷新页面进入只读的页面 
					   				window.location.href = basePath + "jsps/oa/info/NoteR.jsp?formCondition=no_idIS" + Ext.getCmp(form.keyField).value;
			    				});
				   			} else if(localJson.exceptionInfo){
				   				var str = localJson.exceptionInfo;
				   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
				   					str = str.replace('AFTERSUCCESS', '');
				   					saveSuccess(function(){
				    					//add成功后刷新页面进入只读的页面 
				   						window.location.href = basePath + "jsps/oa/info/NoteR.jsp?formCondition=no_idIS" + Ext.getCmp(form.keyField).value;
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
					
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				me.FormUtil.onAdd('addNote', '新增通知', 'jsps/oa/info/Note.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete({id: Number(Ext.getCmp('no_id').value)});
    			}
    		},
    		'textarea[name=no_content]': {
    			afterrender: function(f){
    				f.setHeight(300);
    				f.el.dom.className = "x-field x-form-item x-column x-field-default";
    			}
    		},
    		'htmleditor[name=no_content]': {
    			afterrender: function(f){
    				f.setHeight(500);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});