Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.MakeBad', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.mes.MakeBad','core.trigger.DbfindTrigger','core.button.Save',
    		'core.form.YnField','core.grid.YnColumn', 'core.grid.TfColumn','core.button.Delete',
    		'core.button.Add','core.button.Close', 'core.trigger.TextAreaTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({    		    		
    		'#querygrid':{
    			itemclick: function(selModel, record){//grid 单击
    				Ext.getCmp("fixForm").getForm().setValues(record.data);
    			}
    		},
    		'combo[id=bc_groupcode]':{
    		  change: function(combo, nv, ov){
                    if(nv!=ov && !Ext.isEmpty(nv)){
                         var reasonCombo = Ext.getCmp("mb_badcode");
                         reasonCombo.clearValue(); 
                         var reasonStore = reasonCombo.getStore();
                         reasonStore.proxy.extraParams.condition = nv;
                         reasonStore.load();
                    }
                 }
    		},
    		'combo[id=mb_badcode]':{
    			focus:function(){
    				if(Ext.getCmp("bc_groupcode").value ==''){
    					showError("请先选择不良组别");
    					return;
    				}
    			}
    		},
    		'#ms_sncode': {//序列号
    			specialkey: function(f, e){//按ENTER执行确认
    				if (e.getKey() == e.ENTER) {
    					if(f.value != null && f.value != '' ){
    						me.onCheck(f.value);
        				}
    				}
    			}
    		},
    		'dbfindtrigger[name=cd_stepcode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var cr_code = Ext.getCmp('cr_code').value;
    				if(cr_code == null || cr_code == ''){
    					showError("请先选择回流工艺!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    					return;
    				} else {
    					t.dbBaseCondition = "cr_code='" + cr_code + "'";
    				}
    			}
    		},
    		'erpSaveButton':{//如果ID为空，则为新增的记录，插入记录到makebad
    		  click:function(){
    		  	var mb_id = Ext.getCmp("mb_id").value,mb_badcode = Ext.getCmp("mb_badcode").value,
    		  	mb_badremark = Ext.getCmp("mb_badremark").value,mb_status = Ext.getCmp("mb_status").value,
    		  	ms_sncode = Ext.getCmp("ms_sncode").value, sc_code = Ext.getCmp("scCode").value,
    		  	st_code = Ext.getCmp("stCode").value;
    		  	if(Ext.isEmpty(sc_code)){
    		  		showError("请先选择资源编号!");
    		  		return ;
    		  	}
    		  	if(Ext.isEmpty(ms_sncode)){
    		  		showError("请先录入序列号!");
    		  		return ;
    		  	}   
    		  	//新增或修改
    		  	me.addOrUpdateMakeBad(mb_id,mb_badcode,mb_badremark,mb_status,ms_sncode,sc_code,st_code);    		  	
    		  }
    		},
    		
    		'erpAddButton':{//清空维修处理信息所有字段显示的内容
    		   click:function(btn){
    		   	 var form = btn.ownerCt.ownerCt;
    		   	 form.getForm().reset();
    		   }
    		},
    		'erpDeleteButton':{//弹出确认（“是否要删除此不良原因？”），确认后删除当前记录
    		   click:function(btn){
    		   	var mb_id = Ext.getCmp("mb_id").value;
    		   	if(Ext.isEmpty(mb_id)){
    		   		showError("该记录不存在不需要删除！");
    		   		return ;
    		   	}
				// confirm box modify
				// zhuth 2018-2-1
				Ext.Msg.confirm('提示', '是否要删除此不良原因?', function(btn) {
					if(btn == 'yes') {
						me.deleteB();   
					}
				});
    		   }
    		},
    		'button[id=finishFix]':{//完成维修
    			click:function(btn){
                    var cr_code = Ext.getCmp("cr_code").value ,cd_stepcode = Ext.getCmp("cd_stepcode").value;
                    if(Ext.isEmpty(cr_code)){
                    	showError('请先指定回流工艺!');
                    	return;
                    }
                    if(Ext.isEmpty(cd_stepcode)){
                    	showError('请先指定回流工序!');
                    	return;
                    }
                    var data =  Ext.getCmp("form").getForm().getValues();
    				me.finishFix(data);
    			}
    		},
    		'button[id=scrap]':{//报废
    			 click:function(btn){
    			 	me.makeBadScrap( Ext.getCmp("form").getForm().getValues());   			 
    			 }
    		}
    	});
    },
    onCheck:function(data){   //检测序列号，查询已采集不良grid    	
    	if(Ext.isEmpty(Ext.getCmp("stCode").value)){
    		showError('请先选择资源编号!');
    		return ;
    	}
    	Ext.getCmp("querygrid").setLoading(true);
    	Ext.Ajax.request({
			   url : basePath + 'pm/mes/checkSNcode.action',
			   params: {
			   	   ms_sncode:data,
			   	   st_code:Ext.getCmp("stCode").value
			   },
			   method : 'post',
			   callback : function(options,success,response){
			       Ext.getCmp("querygrid").setLoading(false);
				   var r = new Ext.decode(response.responseText);
				   if(r.exceptionInfo){
				   		showError(r.exceptionInfo);
				   	}else if(r.data){
				   		if(r.data['bddatas'])
				   		   Ext.getCmp('querygrid').store.loadData(r.data['bddatas']);
				   		Ext.getCmp('mc_makecode').setValue(r.data['ms_makecode']);
				   		Ext.getCmp('mc_code').setValue(r.data['ms_mccode']);
				   	}
			   	}
		 });    
    },
    deleteB:function(){//删除不良记录
    	var me = this;
    	Ext.Ajax.request({
			   url : basePath + 'pm/mes/deleteMakeBad.action',
			   params: {
			   	   mb_id:Ext.getCmp("mb_id").value
			   },
			   method : 'post',
			   callback : function(options,success,response){
				   var r = new Ext.decode(response.responseText);
				   if(r.exceptionInfo){
				   		showError(r.exceptionInfo);
				   		return ;
				   	}else{
				   		showMessage('系统提示', '删除成功!');
				   	}
				   	me.onCheck(Ext.getCmp("ms_sncode").value);
			   	}
		 });    
    },
    addOrUpdateMakeBad:function(a,b,c,d,e,f,g){//新增或修改
    	var me =  this;
    	var condition = {mb_id:a,mb_badcode:b,mb_badremark:c,mb_status:d,ms_sncode:e,sc_code:f,st_code:g}
    	Ext.Ajax.request({
			   url : basePath + 'pm/mes/addOrUpdateMakeBad.action',
			   params:{data : unescape(escape(Ext.JSON.encode(condition)))},
			   method : 'post',
			   callback : function(options,success,response){
				   var r = new Ext.decode(response.responseText);
				   if(r.exceptionInfo){
				   		showError(r.exceptionInfo);
				   		return ;
				   	}else{
				   		showMessage('系统提示', '保存成功!');
				   		if(r.data != null){
				   		   Ext.getCmp("mb_id").setValue(r.data);
				   		}
				   	}
				   	me.onCheck(Ext.getCmp("ms_sncode").value);
			   	}
		 });    
    },
    finishFix:function(data){//完成维修
    	var me =  this;
    	Ext.Ajax.request({
			   url : basePath + 'pm/mes/finishFix.action',
			   params:{data : unescape(escape(Ext.JSON.encode(data)))},
			   method : 'post',
			   callback : function(options,success,response){
				   var r = new Ext.decode(response.responseText);
				   if(r.exceptionInfo){
				   		showError(r.exceptionInfo);
				   		return ;
				   	}else{
				   		showMessage('系统提示', '完成维修!');	
				   		Ext.getCmp("ms_sncode").setValue();
				   		Ext.getCmp("fixForm").getForm().reset();
				   	}				   
			   	}
		 });    
    },
    makeBadScrap : function(data){    	
    	var me =  this;
    	Ext.Ajax.request({
			   url : basePath + 'pm/mes/makeBadScrap.action',
			   params:{data : unescape(escape(Ext.JSON.encode(data)))},
			   method : 'post',
			   callback : function(options,success,response){
				   var r = new Ext.decode(response.responseText);
				   if(r.exceptionInfo){
				   		showError(r.exceptionInfo);
				   		return ;
				   	}else{
				   		showMessage('系统提示', '已报废!');				   		
				   	}				   
			   	}
		 });   
    }
});