Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.StepTest', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.mes.StepTest','core.trigger.DbfindTrigger',
    		'core.form.YnField','core.grid.YnColumn', 'core.grid.TfColumn',
    		'core.button.Query','core.button.Close', 'core.trigger.TextAreaTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({    		
    		'dbfindtrigger[name=mc_code]': {
     			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var st_code = Ext.getCmp('st_code').value;
    				if(st_code == null || st_code == ''){
    					showError("请先选择资源编号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    					return;
    				} else {
    					t.dbBaseCondition = "st_code='" + st_code + "'";
    				}
    			},
    			beforetrigger:function(t,e){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var st_code = Ext.getCmp('st_code').value;
    				if(st_code == null || st_code == ''){
    					showError("请先选择资源编号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    					return false;
    				} else {
    					t.dbBaseCondition = "st_code='" + st_code + "'";
    				}
    			}
     		},
    		'#deletebutton':{//删除不良原因明细
    			click:function(btn){
    				var grid = Ext.getCmp("querygrid");
    				var items = grid.selModel.getSelection();
    				var bool = false;
    				var data = new Array();
			        Ext.each(items, function(item, index){
			        	if(this.data['mb_id'] != null && this.data['mb_id'] != ''
			        		&& this.data['mb_id'] != '0' && this.data['mb_id'] != 0){
			        		bool = true; 
			        		var o = new Object();
			        		o['mb_id'] = this.data['mb_id'];
			        		o['mb_sncode'] = this.data['mb_sncode'];
			        		data.push(o);
			        	}
			        });
			        if(bool){
			        	warnMsg("确认删除?", function(btn){
	    					if(btn == 'yes'){			        	
				        	   me.deleteBadCode(data);
				        	}
			        	});
			        }else{
			        	showError("请勾选需要的明细!");
			        	return;
			        }
    				
    			}
    		},
    		'combo[id=bc_reason]':{
    			focus:function(){
    				if(Ext.getCmp("bc_groupcode").value ==''){
    					showError("请先选择不良组别");
    					return;
    				}
    			}
    		},
    		'combo[id=bc_groupcode]':{
                change: function(combo, nv, ov){
                    if(nv!=ov && !Ext.isEmpty(nv)){
                         var reasonCombo = Ext.getCmp("bc_reason");
                         reasonCombo.clearValue(); 
                         var reasonStore=reasonCombo.getStore();
                         reasonStore.proxy.extraParams.condition = nv;
                         reasonStore.load();
                    }
                 }
    		},   		
    		'#ms_code': {
    			specialkey: function(f, e){//按ENTER执行确认
    				if (e.getKey() == e.ENTER) {
    					if(f.value != null && f.value != '' ){    						
    						me.onCheck(f.value);
        				}
    				}
    			}
    		},
    		'button[id=confirmBtn]':{//确认合格通过
    			click : function(btn){
    				var ms_code = Ext.getCmp("ms_code").value;
    				if(ms_code && !Ext.isEmpty(ms_code)){
    					Ext.Ajax.request({
					    url : basePath + 'pm/mes/confirmQualified.action',
					    params: {
					      mcd_stepcode:Ext.getCmp('st_code').value,
					      mc_code:Ext.getCmp('mc_code').value,
					      sc_code:Ext.getCmp('sc_code').value,
					      ms_code:ms_code,
					      makecode:Ext.getCmp("mc_makecode").value
					    },
					    method : 'post',
					    callback : function(options,success,response){
						   var r = new Ext.decode(response.responseText);
						   if(r.exceptionInfo){
						   		showError('确认合格通过失败：'+r.exceptionInfo);
							}else{
							   showMessage('提示','确认合格通过成功,序列号：'+ms_code); 							   		
						   	   Ext.getCmp("ms_code").setValue('');
						   	   //更新已采集数
						   	   Ext.getCmp('mcd_inqty').setValue(r.data.mcd_inqty);
						   	}
					   	}
		             });
    				}else{
    					showError("请先填写序列号！");
    				}
    			}
    		},
    		'#mcd_inqty':{
    			change:function(f,n,o){
					var qty = Ext.getCmp("mc_qty").value;
					Ext.getCmp("mc_restqty").setValue(qty-n);//设置剩余数的值
				}
    		},
    		'button[id=saveBad]':{//保存不良原因
    			click:function(btn){
    				var ms_code = Ext.getCmp("ms_code").value;
    				var bc_reason = Ext.getCmp("bc_reason").value
    				if(!Ext.isEmpty(ms_code)){
	    				if(!Ext.isEmpty(bc_reason)){
	    					Ext.Ajax.request({
						    url : basePath + 'pm/mes/saveBadReason.action',
						    params: {
						      mcd_stepcode:Ext.getCmp('st_code').value,
						      mc_code:Ext.getCmp('mc_code').value,
						      sc_code:Ext.getCmp('sc_code').value,
						      ms_code:ms_code,
						      bc_reason:bc_reason,
						      bc_remark:Ext.getCmp('bc_remark').value
						    },
						    method : 'post',
						    callback : function(options,success,response){
							   var r = new Ext.decode(response.responseText);
							   if(r.exceptionInfo){
							   		showError('保存不良原因失败：'+r.exceptionInfo);
							   	}else{
							   		showMessage('提示','保存不良原因成功,序列号：'+ms_code + '，不良原因：'+bc_reason);							   									   		
							   		me.onCheck(ms_code);
							   	}
						   	}
			             });
	    				}else{
    						showError("请选择不良原因！");
    					}
    				}else{
    					showError("请先填写序列号！");
    				}
    			}
    		},
    		'button[id=confirmRepairStep]':{//确定返修途程
    			click:function(btn){
    				var ms_code = Ext.getCmp("ms_code").value;
    				var st_rcode = Ext.getCmp("st_rcode").value
    				if(!Ext.isEmpty(ms_code)){
	    				if(!Ext.isEmpty(st_rcode)){
	    					Ext.Ajax.request({
						    url : basePath + 'pm/mes/confirmRepairStep.action',
						    params: {
						      mcd_stepcode:Ext.getCmp('st_code').value,
						      mc_code:Ext.getCmp('mc_code').value,
						      sc_code:Ext.getCmp('sc_code').value,
						      ms_code:ms_code,
						      st_rcode:st_rcode
						    },
						    method : 'post',
						    callback : function(options,success,response){
							   var r = new Ext.decode(response.responseText);
							   if(r.exceptionInfo){
							   		showError('确认返修途程失败：'+r.exceptionInfo);
							   	}else{
							   		showMessage('提示','确认返修途程成功,序列号：'+ms_code + '，返修工序：'+st_rcode); 	
							   	    //更新已采集数
							   		Ext.getCmp('testform').getForm().reset();
							   		Ext.getCmp('querygrid').store.removeAll();
							   	    //更新已采集数
						   	        Ext.getCmp('mcd_inqty').setValue(r.data.mcd_inqty);					   	    
							   	}
						   	}
			             });
	    			  }else{
    						showError("请填写返修工序！");
    				  }
    				}else{
    					showError("请先填写序列号！");
    				}
    			}
    		}
    	});
    },
    getSourceM:function(data){/*
    	Ext.Ajax.request({
			   url : basePath + 'pm/mes/getSourceM.action',
			   params: {condition:data},
			   method : 'post',
			   callback : function(options,success,response){
				   var r = new Ext.decode(response.responseText);
				   if(r.exceptionInfo){
				   		showError(r.exceptionInfo);
				   		Ext.getCmp('sc_code').setValue();
				   	}else if(r.data){
					   Ext.getCmp("form").getForm().setValues(r.data);
					   if(r.data['st_rstepcode']){//根据资源带出返修工序
					   	Ext.getCmp("st_rcode").setValue(r.data['st_rstepcode']);
					   }
				   	}
			   	}
		 });
    */},
    getFormStore : function(data){
    	var stepcode = Ext.getCmp("st_code").value,sccode =  Ext.getCmp("sc_code").value;
    	if(Ext.isEmpty(sccode)){
    		showError("请先录入资源编号!");
    		return ;
    	}   	
    	Ext.Ajax.request({
			   url : basePath + 'pm/mes/getFormStore.action',
			   params: {condition:unescape(escape(Ext.JSON.encode(data)))},
			   method : 'post',
			   callback : function(options,success,response){
				   var r = new Ext.decode(response.responseText);
				   if(r.exceptionInfo){
				   		showError(r.exceptionInfo);
				   		Ext.getCmp('mc_code').setValue();
				   	}else if(r.data){
					   Ext.getCmp("form").getForm().setValues(r.data);
				   	}
			   	}
		 });
    },
    onCheck:function(data){   	
    	var makecode = Ext.getCmp("mc_makecode").value,stepcode = Ext.getCmp("st_code").value,
    	mccode = Ext.getCmp("mc_code").value; 
    	if(Ext.isEmpty(stepcode)){
    		showError("请先录入资源编号!");return ;
    	}
    	if(Ext.isEmpty(mccode)){
    		showError("请先录入作业单号!");return;
    	}
    	Ext.getCmp("querygrid").setLoading(true);
    	Ext.Ajax.request({
			   url : basePath + 'pm/mes/checkStep.action',
			   params: {
			   	   makecode:makecode,
			   	   stepcode:stepcode,
			   	   mscode:data,
			   	   mccode:mccode
			   },
			   method : 'post',
			   callback : function(options,success,response){
			       Ext.getCmp("querygrid").setLoading(false);
				   var r = new Ext.decode(response.responseText);
				   if(r.exceptionInfo){
				   		showError(r.exceptionInfo);
				   		Ext.getCmp('ms_code').setValue();
				   		return ;
				   }else if(r.data && r.data.length > 0){
				   	   Ext.getCmp("querygrid").store.loadData(r.data);
				   }else{
				   	   Ext.getCmp("querygrid").store.loadData([{},{},{},{},{},{},{},{},{},{},{}]);
				   }
			   	}
		 });
    },
    deleteBadCode : function(data){    	
    	var me = this;
    	Ext.Ajax.request({
			   url : basePath + 'pm/mes/deleteTestBadCode.action',
			   params: {
			   	  condition:unescape(Ext.JSON.encode(data))
			   },
			   method : 'post',
			   callback : function(options,success,response){
				   var r = new Ext.decode(response.responseText);
				   if(r.exceptionInfo){
				   		showError(r.exceptionInfo);
				   }else if(r.success){				   
					    me.onCheck(Ext.getCmp('ms_code').value);
				   	    showMessage('系统提示', '删除成功!');				   	  					   	   
				   }
			   	}
		 });
    }
});