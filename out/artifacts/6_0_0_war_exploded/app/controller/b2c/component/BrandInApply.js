Ext.QuickTips.init();
Ext.define('erp.controller.b2c.component.BrandInApply', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'b2c.component.BrandInApply','core.form.FileField','core.button.Add','core.button.Save',
    		'core.button.Close','core.button.Submit','core.button.ResSubmit','core.button.Audit',
    		'core.button.ResAudit','core.button.Update','core.button.Delete','core.form.YnField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	_nobutton=getUrlParam('_nobutton');
    	me.checkB2BEnable();
    	this.control({ 
    		'#form':{
    			beforeshow:function(f){
    				me.getData(f);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					if(form.keyField){
						if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
							me.FormUtil.getSeqId(form);
						}
					}
					me.save(btn);
    			}
    		},
    		'#brandGrid':{
    			itemclick:this.onGridItemClick,
    			headerfilterchange:function(e){
               		Ext.getCmp('paging').doRefresh();
           		}
    		},
    		'#br_statuscode':{
    		    change:function(field,newvalue){
    		      var form=field.ownerCt,toolbar=form.down('toolbar');
    		      if(newvalue && toolbar){
    		        switch(newvalue){
    		          case 'COMMITED': 
    		             toolbar.down('erpSaveButton').hide();
    		             toolbar.down('erpUpdateButton').hide();
    		             toolbar.down('erpSubmitButton').hide();
    		             toolbar.down('erpDeleteButton').hide();
    		             toolbar.down('#change').hide();
    		            break;
    		           case 'AUDITED':
    		            toolbar.down('erpSaveButton').hide();
    		            toolbar.down('erpUpdateButton').hide();
    		        	toolbar.down('erpSubmitButton').hide();
    		            toolbar.down('erpResSubmitButton').hide();
    		            toolbar.down('erpDeleteButton').hide();
    		            toolbar.down('erpAuditButton').hide();
    		            Ext.getCmp('basicinf').show();
    		            break;
    		           default:
    		            toolbar.down('erpSaveButton').hide();
    		            toolbar.down('erpResSubmitButton').hide();
    		            toolbar.down('erpAuditButton').hide();
    		            toolbar.down('#change').hide();
    		        }
    		      }
    		    }
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.update(btn);
    			},
    			afterrender:function (btn){
    				var status = Ext.getCmp('br_statuscode'),
    				br_id = Ext.getCmp("br_id");
    				if(br_id && (br_id.value != '' && br_id.value != null) && status && status.value!='ENTERING'){
    					btn.hide();
    				}
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('br_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addBrandInApply', '新增品牌入库申请', 'jsps/b2c/component/brandInApply.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			click: function(btn){
    				me.FormUtil.submit(Ext.getCmp('br_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('br_id').value);
    			}
    		},
    		'erpAuditButton': {
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('br_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('br_id').value);
    			}
    		},
    		'#change':{
    			click:function(){
    				br_name=Ext.getCmp("br_name").value;
    				br_engname=Ext.getCmp("br_engname").value;
    				window.location.href = basePath+'jsps/b2c/component/brandInApply.jsp?br_name='+br_name+'&br_engname='+br_engname;
    			}
    		}
    	});
    },
    onGridItemClick:function(selModel, record){
    	getbyUUid=true;
        var win =Ext.getCmp('brandWindow');
        var brandcnname=Ext.getCmp('br_name');
        var brandengname=Ext.getCmp('br_engname');
        var branduuid=Ext.getCmp('br_uuid');
        branduuid.setValue(record.data.uuid);
        brandcnname.setValue(record.data.nameCn);
        brandengname.setValue(record.data.nameEn);
        win.close();
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save:function(btn){
		var nameCn=Ext.getCmp('br_name').value;
		var nameEn=Ext.getCmp('br_engname').value;
		var me = this;
		var data = me.getData();
		Ext.Ajax.request({
	   		url : basePath + 'b2c/product/checkBrandName.action',
	   		params: {
	   			nameCn:nameCn,
	   			nameEn:nameEn,
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var res = new Ext.decode(response.responseText);
	   			if(res.exceptionInfo){
                    showError(res.exceptionInfo);
                }
	   			if(res.success){
	   				if(res.data.nameCn!=''&&res.data.nameCn!=null){
	   					Ext.MessageBox.confirm('提示','该品牌已存在，是否覆盖',function(btn){
	   						if(btn=='yes'){
	   						me.saveAjax('DeviceInApply', data);
	   						}
	   					});
	   				}else{
	   						me.saveAjax('DeviceInApply', data);
	   				}
	   			}
	   		}
		});
	},
	update :function(btn){
		var me = this;
		var data = me.getData();
		Ext.Ajax.request({
	   		url : basePath + 'b2c/product/updateBrandInApply.action',
	   		params: {
	   			caller: caller,
	   			formStore:data
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   			}
    			if(localJson.success){
    				window.location.href = window.location.href ;
	   			}
	   		}
		});
	},
	getData: function(){
		
		var me = this;
		var form = Ext.getCmp('form');
		//判断必填项是否都已经填写
		me.FormUtil.checkForm();		
		var r = form.getValues();
		//判断领域区域是否填写
		var application = Ext.getCmp('br_application').getValue()['br_application'],
		 area = Ext.getCmp('br_area').getValue()['br_area'],
		 otherap = r['otherApplication'],
		 otherarea = r['otherArea'];
	   if(Ext.isEmpty(application) && ( Ext.isEmpty(otherap) ||  me.checkEmptyArray(otherap) ) ){
		   showError("请选择应用领域或者填写其他领域!");
		   return;
	   }
	   if(Ext.isEmpty(area) && ( Ext.isEmpty(otherarea) || me.checkEmptyArray(otherarea))){
		   showError("请选择区域或者填写其他区域!");
		   return;
	   } 
		//去除ignore字段
		var keys = Ext.Object.getKeys(r), f;
		var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
		Ext.each(keys, function(k){
			if(k == form.codeField && !Ext.isEmpty(r[k])) {
				r[k] = r[k].trim().toUpperCase().replace(reg, '');
			}
			if(k == 'br_application' ){
				if(Ext.isEmpty(application)){
					r[k] = r['otherApplication'].toString();
				}else if(!Ext.isEmpty(r['otherApplication']) && !me.checkEmptyArray(r['otherApplication'])){
					r[k] = application.toString() +','+ r['otherApplication'].toString();
				}else{
					r[k] = application.toString();
				}
			}
			if(k=='br_area'){
				if(Ext.isEmpty(area)){
					r[k] = r['otherArea'].toString();
				}else if(!Ext.isEmpty(r['otherArea']) && !me.checkEmptyArray(r['otherArea'])){
					r[k] = area.toString() +','+ r['otherArea'].toString();
				}else{
					r[k] = area.toString();
				}
			}
		});	
		//移除
		delete r.otherApplication;
		delete r.otherArea;
		return unescape(escape(Ext.JSON.encode(r)));
	},
	checkEmptyArray :function(ar){
		 Ext.Array.each(ar, function(value) {
	        if(Ext.isEmpty(value) || value =="" || value == null){
	        	delete ar.value;
	        }
		  });
		 if(ar.length == 0){
			 return true;
		 }else{
			 return false;
		 }	    
	},
	saveAjax:function(caller,data){
		Ext.Ajax.request({
		   		url : basePath + 'b2c/product/saveBrandInApply.action',
		   		params: {
		   			caller: caller,
		   			formStore:data
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   			}
	    			if(localJson.success){
	    				formCondition = "br_id="+Ext.getCmp("br_id").value;
	    				window.location.href = basePath+"jsps/b2c/component/brandInApply.jsp?formCondition="+formCondition;
		   			}
		   	}
		});
	},
	checkB2BEnable:function(){
		Ext.Ajax.request({
	   		url : basePath + 'b2c/product/checkB2BEnable.action',
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.error){	
	   				showError("该账套尚未开通B2B平台，请在<a href='http://www.ubtob.com/' target='_blank'>www.ubtob.com</a>注册");
	   			}

	   		}
    	});
	}
});