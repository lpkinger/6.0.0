Ext.QuickTips.init();
Ext.define('erp.controller.oa.news.News', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.news.News','core.form.Panel',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    			'core.trigger.DbfindTrigger','core.button.Upload','core.button.DownLoad','core.button.Scan',
    			'core.form.FileField','core.form.YnField','core.form.FTPFileField'
    			
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
					var form = me.getForm(btn);						
					var content=Ext.getCmp('ne_content').getValue().replace(/\\u/g,"%u");
					if(content==""||content==undefined||content==null){
						showError("新闻内容必须填写！");
					}else{
						if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					if(! this.FormUtil.checkForm()){
						return;
					}
					if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
						this.FormUtil.getSeqId(form);
					}
					var f = Ext.getCmp('ne_istop'), top = 0;
					if(f)
						top = f.value;
					form.setLoading(true);//loading...
					Ext.Ajax.request({
				   		url : basePath + form.saveUrl,
				   		params :{
				   			ne_id: Ext.getCmp('ne_id').value,
							ne_type: Ext.getCmp('ne_type').value,
							ne_code: Ext.getCmp('ne_code').value,
							ne_releaser: Ext.getCmp('ne_releaser').value,
							ne_theme: Ext.getCmp('ne_theme').value,
							ne_istop: top,
							ne_content: unescape(Ext.getCmp('ne_content').getValue().replace(/\\u/g,"%u")),
							ne_attachs:Ext.getCmp('form').getValues().ne_attachs
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){
				   			form.setLoading(false);
				   			var localJson = new Ext.decode(response.responseText);
			    			if(localJson.success){
			    				saveSuccess(function(){
			    					//add成功后刷新页面进入只读的页面 
					   				window.location.href = basePath + "jsps/oa/news/NewsR.jsp?formCondition=ne_idIS" + Ext.getCmp(form.keyField).value;
			    				});
				   			} else if(localJson.exceptionInfo){
				   				var str = localJson.exceptionInfo;
				   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
				   					str = str.replace('AFTERSUCCESS', '');
				   					saveSuccess(function(){
				    					//add成功后刷新页面进入只读的页面 
				   						window.location.href = basePath + "jsps/oa/news/NewsR.jsp?formCondition=ne_idIS" + Ext.getCmp(form.keyField).value;
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
    				me.FormUtil.onAdd('addNews', '新增新闻', 'jsps/oa/news/News.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				//this.FormUtil.onDelete([]);
    				me.FormUtil.onDelete({id: Number(Ext.getCmp('ne_id').value)});
    			}
    		},
    		'htmleditor': {
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