Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.SetBarcodeRule', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
	views:[
	       'scm.reserve.SetBarcodeRuleForm','scm.reserve.SetBarcodeRule',
	       'core.button.Save','core.button.Close','core.button.Update'
	       ],
	       init:function(){
	    	   var me = this;
	    	   me.FormUtil = Ext.create('erp.util.FormUtil');
	    	   me.BaseUtil = Ext.create('erp.util.BaseUtil');
	    	   this.control({
	    		   'erpSaveButton': {
	    			   click: function(btn){
	    			      var re = me.checkForm();//判断必填项填写是否规范
	    			      if(re !=''){
	    			      	 showError(re);
	    			      	 return ;
	    			      }	    			      
	    			      me.FormUtil.beforeSave(me);
	    			   }
	    		   },
	    		   'erpUpdateButton': {
	    			   click: function(btn){
	    			   	var re = me.checkForm();//判断必填项填写是否规范
	    			      if(re !=''){
	    			      	 showError(re);
	    			      	 return ;
	    			      }	    
	    			     this.update(this);
	    			   }
	    		   },
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   me.FormUtil.beforeClose(me);
	    			   }
	    		   }
	    	   });
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       } ,
	       checkForm:function(){
	       	 var result ='';
	       	 var lenprid = Ext.getCmp("bs_lenprid").value;
	    	 if(lenprid < 0){
	    		result += "物料长度(P)必须大于0!";
	    	 }
	    	 var lenveid = Ext.getCmp("bs_lenveid").value;
	    	 if(lenveid&&lenveid < 0){
	    		result += "<br>供应商长度(V)必须大于0!";
	    	 }
	    	 var lennum = Ext.getCmp("bs_lennum").value;
	    	 if(lennum < 0){
	    	     result +="<br>流水长度(N)必须大于0!";
	    	 }
	    	 return result;
	       },
	       update: function(){
				var me = this, params = new Object();
				var form = Ext.getCmp("form");
				if(form && form.getForm().isValid()){
					//form里面数据
					var r = form.getValues();
				}else{
					showError("表单校验错误");
					return;
				}
				params.formStore = unescape(escape(Ext.JSON.encode(r)));
				var url = form.updateUrl;
				if(url.indexOf('caller=') == -1){
					url = url + "?caller=" + caller;
				}
				Ext.Ajax.request({
					url : basePath + url,
					params: params,
					method : 'post',
					callback : function(options,success,response){
						var localJson = new Ext.decode(response.responseText);
						if(localJson.success){
							showMessage('提示', '保存成功!', 1000);
							//update成功后刷新页面进入可编辑的页面
							window.location.reload();
						} else if(localJson.exceptionInfo){
							var str = localJson.exceptionInfo;
							if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
								str = str.replace('AFTERSUCCESS', '');
								//update成功后刷新页面进入可编辑的页面 
								window.location.reload();
							}
							showError(str);return;
						} else {
							showError("更新失败!");
						}
					}
				});
			}
});