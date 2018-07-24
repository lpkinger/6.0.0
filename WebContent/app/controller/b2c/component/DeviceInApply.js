Ext.QuickTips.init();
Ext.define('erp.controller.b2c.component.DeviceInApply', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
            'b2c.component.DeviceInApply','core.form.FileField','core.button.Add','core.button.Save',
            'core.button.Close','core.button.Submit','core.button.ResSubmit','core.button.Audit',
            'core.button.ResAudit','core.button.Update','core.button.Delete','core.form.YnField',
            'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.grid.HeaderFilter'
        ],
    init:function(){
        var me = this;
        me.checkB2BEnable();
        _nobutton=getUrlParam('_nobutton');
        this.control({
            '#form':{
                beforeshow:function(f){
                    me.getData(f);
                }
            },
            '#pr_leadtime':{
                afterrender: function(f) {
                    f.isInteger = true;
                }
            },
            '#brandGrid':{
                itemclick:this.onGridItemClick,
                //监听插件的事件
               	headerfilterchange:function(e){
               		Ext.getCmp('paging').doRefresh();
           		}
            },
            'erpSaveButton': {
                click: function(btn){
                    var form = me.getForm(btn);
                    if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
                            me.BaseUtil.getRandomNumber();
                    }
                    if(form.keyField){
                        if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
                            me.FormUtil.getSeqId(form);
                        }
                    }
                    me.save(btn);
                }
            },
            '#de_statuscode':{
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
   		             toolbar.down('#copy').hide();
   		            break;
   		           case 'AUDITED':
   		            toolbar.down('erpSaveButton').hide();
   		            toolbar.down('erpUpdateButton').hide();
   		        	toolbar.down('erpSubmitButton').hide();
   		            toolbar.down('erpResSubmitButton').hide();
   		            toolbar.down('erpDeleteButton').hide();
   		            toolbar.down('#copy').hide();
   		            toolbar.down('erpAuditButton').hide();
   		            Ext.getCmp('de_code').show();
   		         	Ext.getCmp('de_b2cauditopinion').show();
   		         	Ext.getCmp('de_recorder').show();
   		      		Ext.getCmp('de_indate').show();
   		            Ext.getCmp('de_status').show();
   		            Ext.getCmp('de_b2cstatus').show();
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
            'erpCloseButton': {},
            'erpUpdateButton': {
                click: function(btn){
                  me.update(btn);
                },
                afterrender:function (btn){
                    var status = Ext.getCmp('de_statuscode'),
                    de_id = Ext.getCmp("de_id");
                    if(de_id && (de_id.value != '' && de_id.value != null) && status && status.value!='ENTERING'){
                        btn.hide();
                    }
                }
            },
            'erpDeleteButton': {
                click: function(btn){
                    me.FormUtil.onDelete(Ext.getCmp('de_id').value);
                }
            },
            'erpAddButton': {
                click: function(){
                    me.FormUtil.onAdd('addDeviceInApply', '新增器件入库申请', 'jsps/b2c/component/deviceInApply.jsp');
                }
            },
            'erpSubmitButton': {
                click: function(btn){
                	me.FormUtil.submit(Ext.getCmp('de_id').value);
                }
            },
            'erpResSubmitButton': {
                click: function(btn){
                    me.FormUtil.onResSubmit(Ext.getCmp('de_id').value);
                }
            },
            'erpAuditButton': {
                click: function(btn){
                    me.FormUtil.onAudit(Ext.getCmp('de_id').value);
                }
            },
            '#change':{
    			click:function(){
    				de_id=Ext.getCmp("de_id").value;
    				window.location.href = basePath+'jsps/b2c/component/deviceInApply.jsp?de_id='+de_id;
    			}
    		},
            '#copy':{
            	click:function(){
            		de_id=Ext.getCmp("de_id").value;
            		Ext.MessageBox.confirm('提示','是否需要复制图片和附件',function(btn){
            			if(btn=='yes'){
            				me.BaseUtil.onAdd('DeviceInApply','器件入库申请','jsps/b2c/component/deviceInApply.jsp?de_id='+de_id);
            			}
            			else{
            				me.BaseUtil.onAdd('DeviceInApply','器件入库申请','jsps/b2c/component/deviceInApply.jsp?de_id='+de_id+'&pic_att=notcopy');
            			}
            		});		
            	}
            }
        });  
    },
    getForm: function(btn){
        return btn.ownerCt.ownerCt;
    },
    onGridItemClick:function(selModel, record){
        var win =Ext.getCmp('brandWindow');
        var brandname=Ext.getCmp('de_brand');
        var brandid=Ext.getCmp('de_brandid');
        var branduuid=Ext.getCmp('de_branduuid');
        branduuid.setValue(record.data.uuid);
        brandname.setValue(record.data.nameCn);
        brandid.setValue(record.data.id);
        win.close();
    },
    save:function(btn){
        var me = this;
     	//判断品牌的名称是否存在
    	Ext.Ajax.request({
            url : basePath + 'b2c/product/checkBrandAndCode.action',
            params: {
                nameCn:Ext.getCmp('de_brand').getRawValue(),
                code:Ext.getCmp('de_oldspec').value
            },
            method : 'post',
            timeout:100000,
            callback : function(options,success,response){
                var res = new Ext.decode(response.responseText);
                if(res.exceptionInfo){
                    showError(res.exceptionInfo);
                }
                if(res.success){
                	if(res.data.nameCn==null||res.data.nameCn==''){
                		showError('品牌不存在，请先申请品牌');
                	}else{
                		if(res.data.nameCn!=null&&res.data.code!=null){
                    		Ext.MessageBox.confirm('提示','该器件已存在，是否更新',function(btn){
                    			if(btn=='yes'){
                    				if(me.checknull()!=''){
                    		        	Ext.MessageBox.confirm('提示',me.checknull()+'未输入，是否继续保存', function(btn){ 
                    		        		if(btn=='yes'){
                    		        			 Ext.getCmp("de_brandid").setValue(res.data.brandid);
                    		        			 me.doSave(caller)
                    		        		}
                    		        	}); 
                    		        }else{
                    		        	Ext.getCmp("de_brandid").setValue(res.data.brandid);
                    		        	me.doSave(caller)
                    		        }
                    			}
                    		})
                    	}else if(res.data.nameCn!=null){
                    				if(me.checknull()!=''){
                    		        	Ext.MessageBox.confirm('提示',me.checknull()+'未输入，是否继续保存', function(btn){ 
                    		        		if(btn=='yes'){
                    		        			Ext.getCmp("de_brandid").setValue(res.data.brandid);
                    		        			 me.doSave(caller)
                    		        		}
                    		        }); 
                    		  }else{
                    			  me.doSave(caller)
                    		  }
                    	}
                	}
                }
            }
        });
    },
    doSave:function(caller){
    	 var me=this;
    	 me.setSubmitValue();
         var form = Ext.getCmp('form');
         //判断必填项是否都已经填写
         me.FormUtil.checkForm();
         var r = form.getValues();
         //去除ignore字段
         var keys = Ext.Object.getKeys(r), f;
         var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
         Ext.each(keys, function(k){
             f = form.down('#' + k);
             if(f&&f.logic=='ignore'){
             	delete r[k];
             }
             if(k == form.codeField && !Ext.isEmpty(r[k])) {
                 r[k] = r[k].trim().toUpperCase().replace(reg, '');
             }
         });
    	Ext.Ajax.request({
            url : basePath + 'b2c/product/saveDeviceInApply.action',
            params: {
                caller: caller,
                formStore:unescape(escape(Ext.JSON.encode(r))),
                _noc:1
            },
            method : 'post',
            callback : function(options,success,response){
                var localJson = new Ext.decode(response.responseText);
                if(localJson.exceptionInfo){
                    showError(localJson.exceptionInfo);
                }
                if(localJson.success){
                   formCondition = "de_id="+Ext.getCmp("de_id").value;
                   window.location.href = basePath+"jsps/b2c/component/deviceInApply.jsp?formCondition="+formCondition;
                }
            }
        });
    },
    update :function(btn){
    	var me=this;
        Ext.Ajax.request({
            url : basePath + 'b2c/product/checkBrandAndCode.action',
            params: {
            	nameCn:Ext.getCmp('de_brand').getRawValue(),
                code:Ext.getCmp('de_oldspec').value
            },
            method : 'post',
            callback : function(options,success,response){
                var res = new Ext.decode(response.responseText);
                if(res.exceptionInfo){
                    showError(res.exceptionInfo);
                }
                if(res.success){
                	if(res.data.nameCn==null||res.data.nameCn==''){
                		showError('品牌不存在，请先申请品牌');
                	}
                	else{
                		 me.doUpdate(caller)
                	}
                }
            }
        });
    },
    doUpdate:function(caller){
    	 var me = this;
         me.setSubmitValue();
         var form = Ext.getCmp('form');
         //判断必填项是否都已经填写
         me.FormUtil.checkForm();        
         var r = form.getValues();
         //去除ignore字段，不提交
         var keys = Ext.Object.getKeys(r), f;
         var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
         Ext.each(keys, function(k){
             f = form.down('#' + k);
             if(f&&f.logic=='ignore'){
             	delete r[k];
             }
             if(k == form.codeField && !Ext.isEmpty(r[k])) {
                 r[k] = r[k].trim().toUpperCase().replace(reg, '');
             }
         }); 
    	 Ext.Ajax.request({
             url : basePath + 'b2c/product/updateDeviceInApply.action',
             params: {
                 caller: caller,
                 formStore:unescape(escape(Ext.JSON.encode(r))),
                 _noc:1
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
    //进行提交之前的值的拼接
    setSubmitValue:function(){
    	combinevalue='';
        for(var i=0;i<propertiesnum;i++){
        	//获取到对应参数的Panel
        	var myPanel=Ext.getCmp('items'+i);
        	//获取这个Panel控件的长度,由于之前填充了隐藏控件,所以从后往前计算
        	var myPanelLength=myPanel.items.length;
        	//如果是数字类型
        	if(myPanel.type=='N'){
        		//如果是数字类型需要用进制乘以输入的值
        		var num=myPanel.items.items[myPanelLength-2].getValue();
        		var radix=myPanel.items.items[myPanelLength-1].getValue();
        		//如果输入的值是空值则直接设置为空，停止本次循环
        		if(num==''||num==null){
        			Ext.getCmp('num'+i).setValue(null);
        			myPanel.items.items[0].setValue(null);
        			continue;
        		}else{
        			Ext.getCmp('num'+i).setValue(num*radix);
        		}
        	}
        	//如果是范围类型
        	if(myPanel.type=='F'){
        		//如果是范围类型就需要获取最大值和最小值，方法同上
        		var min=myPanel.items.items[myPanelLength-5].getValue();	
        		var minradix=myPanel.items.items[myPanelLength-4].getValue();	
        		var max=myPanel.items.items[myPanelLength-2].getValue();	
        		var maxradix=myPanel.items.items[myPanelLength-1].getValue();
        		//如果两个值均为空,max和min都设置为0,跳出本次循环
        		if((min==''||min==null)&&(max==''||max==null)){
        			Ext.getCmp('min'+i).setValue(0);
            		Ext.getCmp('max'+i).setValue(0);
            		myPanel.items.items[0].setValue(null);
            		continue;
        		}else{
        			Ext.getCmp('min'+i).setValue(min*minradix);
            		Ext.getCmp('max'+i).setValue(max*maxradix);
        		}
        		if((min==''||min==null)&&(max!=''||max!=null)){
        			Ext.getCmp('min'+i).setValue(max*maxradix);
            		Ext.getCmp('max'+i).setValue(max*maxradix);
        		}
        		//范围值得最大最小均不为空
        		if((min==''||min==null)&&(max==''||max==null)){
        			Ext.getCmp('min'+i).setValue(min*minradix);
            		Ext.getCmp('max'+i).setValue(min*minradix);
        		}
        	}
        	for(var j=7;j<myPanelLength;j++){
        		//拼接String参数
        		if(myPanel.items.items[j].xtype!='label'){
        			if(myPanel.items.items[j].xtype!='combo'){
        				//如果是不Combo就获取实际输入的值
        				combinevalue+=myPanel.items.items[j].getValue();
        			}else{
        				//是Combo就获取展示的值
        				combinevalue+=myPanel.items.items[j].getRawValue();
        			}
        		}else{
        			//如果是label就判断后面的输入框有没有值，没有的话就不拼接
        			if(myPanel.items.items[j+1].value==''||myPanel.items.items[j+1].value==null){
        				break;
        			}
        			if(myPanel.items.items[j-2].value==''||myPanel.items.items[j-2].value==null){
        				//如果判断是前面的输入框没有输入,将值值为空
        				combinevalue='';
        			}else{
        				//有值的画用~符号链接
        				combinevalue+='~';
        			}
        		}
        	}
        	//给第一个隐藏字段赋值
        	myPanel.items.items[0].setValue(combinevalue);
        	//重置拼接的参数
        	combinevalue='';
        }
    },
    checknull:function(){
    	var alertstr="";
    	for(var i=0;i<propertiesnum;i++){
    		var myPanel=Ext.getCmp('items'+i);
    		if(myPanel.type=='F'){
    			var F=myPanel.items.items[7].getValue();
    			if(F==''||F==null){
    				alertstr+=myPanel.items.items[6].text+','
    			}
    		}
    		if(myPanel.type=='N'){
    			var N=myPanel.items.items[7].getValue();
    			if(N==''||N==null){
    				alertstr+=myPanel.items.items[6].text+','
    			}
    		}
    		if(myPanel.type=='S'){
    			var S=myPanel.items.items[7].getValue();
    			if(S==''||S==null){
    				alertstr+=myPanel.items.items[6].text+','
    			}
    		}
    		if(myPanel.type=='A'){
    			var A= myPanel.items.items[7].getValue();
    			if(A==''||A==null){
    				alertstr+=myPanel.items.items[6].text+','
    			}
    		}
    	}
    	return alertstr;
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