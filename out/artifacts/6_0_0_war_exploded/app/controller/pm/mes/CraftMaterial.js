Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.CraftMaterial', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.mes.CraftMaterial','core.form.Panel',
    		'core.form.YnField','core.grid.YnColumn', 'core.grid.TfColumn',
    		'core.button.Query','core.button.Close', 'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({    		
    		'erpQueryButton' : {//筛选
    			click: function(btn) {
    				var mccode = Ext.getCmp('mc_code').value ,sccode = Ext.getCmp('sc_code').value;
    				if(Ext.isEmpty(mccode)){
						showError('请先指定作业单号！');
						return;
					}
					if(Ext.isEmpty(sccode)){
						showError('请先指定资源号！');
						return;
					}
    				me.query(btn);
    				Ext.getCmp('input').focus();
    			}
    		},
    		'grid[id=qgrid]':{
    			edit:function(editor, e) {
				    // 编辑完成后，提交更改
				    e.record.commit();
    			}
    		},
    		'#input': {
    			specialkey: function(f, e){//按ENTER执行确认  				
    				if ([e.ENTER, e.RETURN, e.SPACE].indexOf(e.getKey()) > -1) {
    					Ext.defer(function(){
    						var val = e.target.value;
    						if(val){
        						me.onConfirm(val);
            				}
    					}, 50);
    				}				
    			}
    		},
			'#confirm' : {
				click: function(btn) {
					me.onConfirm();
				},
				afterrender:function(btn){
					btn.disable(true);
				}
			},
			'numberfield[id=mcd_inqty]':{
				change:function(f,n,o){
					var qty = Ext.getCmp("mc_qty").value;
					Ext.getCmp("mc_restqty").setValue(qty-n);//设置剩余数的值
				}
			}
    	});
    },
	onConfirm : function(value){		
		var me = this, get = Ext.getCmp('get').value,back = Ext.getCmp('back').value;
		var result = Ext.getCmp('t_result'), grid = Ext.getCmp('qgrid'),
            mscode = Ext.getCmp('ms_code').value,stepcode = Ext.getCmp("sc_stepcode").value,
			mccode = Ext.getCmp('mc_code').value ,sccode = Ext.getCmp('sc_code').value,		
			linecode = Ext.getCmp('sc_linecode').value,input = value;	
		if(Ext.isEmpty(mccode)){
			showError('请先指定作业单号！');
			return;
		}
		if(Ext.isEmpty(sccode)){
			showError('请先指定资源号！');
			return;
		}
		if(Ext.isEmpty(mscode) && Ext.isElement(input)){
			result.append('请录入序列号！');
			return;
		}else if(Ext.isEmpty(mscode) && !Ext.isElement(input)){//enter如果“序列号”字段为空,录入框不为空,上料检测序列号
			var ifGet = false;
			if(get){
			   ifGet = true;	
			}
			Ext.Ajax.request({
		   		url : basePath + 'pm/mes/checkCraftMaterialGet.action',
		   		params: {	
		   			mscode  : input,					    //序列号
		   			mccode  : mccode,					    //作业单号
		   			licode  : linecode,					    //线别
		   			sccode  : sccode ,					    //资源号
		   			stepcode: stepcode,                     //工序编号，
		   			ifGet   : ifGet                         //上料或者下料
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			var r = new Ext.decode(response.responseText);
		   			if(r.exceptionInfo){
		   				result.append(r.exceptionInfo, 'error');
		   			}else{
		   				Ext.getCmp('ms_code').setValue(input);//检测通过则“序列号”字段显示该录入内容
		   				if(r.data && r.data.length > 0){
		   				   //grid.store.loadData(r.data);	
		   				   var items = grid.store.data.items;
						   //提示采集的物料的条码号	   				
					       Ext.each(items,function(item,idx){//获取物料ID，sp_type 条码类型，		
					       	   item.set('if_pick',r.data[idx].if_pick);
					       });
		   				}
		   				if(ifGet){
		   					index = -1;
			   				me.getSelectProdcode();
		   				}
		   			}
		   			Ext.getCmp('input').setValue('');
		   		}
	   		});
		}else  if(!Ext.isEmpty(mscode) && !Ext.isEmpty(input)){
		   if(get){
		   	    me.getCraftMaterial(result,mscode,mccode,linecode,sccode,stepcode,input);
		   	  } else if(back){
				/*warnMsg("确定取消下料?", function(btn){
					if(btn == 'yes'){*/
						me.FormUtil.getActiveTab().setLoading(true);//loading...
	    				Ext.Ajax.request({
	    			   		url : basePath + 'pm/mes/backCraftMaterial.action',
	    			   		params: {
	    			   			mscode: mscode,					    //序列号
					   			mccode : mccode,					//作业单号					   		
					   			sccode : sccode	,					//资源号
					   			barcode:input                       //条码编号
	    			   		},
	    			   		method : 'post',
	    			   		callback : function(options,success,response){
	    			   			me.FormUtil.getActiveTab().setLoading(false);//loading...
	    			   			var r = new Ext.decode(response.responseText);
	    			   			if(r.exceptionInfo){
	    			   				result.append(r.exceptionInfo, 'error');
	    			   			}
	    		    			if(r.success){	 	    		    				
	    		    				 //下的是哪个料提示采集哪个料的条码 	  
    		    					var items = grid.store.data.items;
    		    					 Ext.each(items,function(item,idx){							
										if(item.data['sp_id'] == r.result.replace('success', '')){								   	
											index = idx;
											sp_id = item.data['sp_id'];
											pr_code = item.data['sp_soncode'];
											this.set('if_pick','未采集');
											this.commit();
										}
								    });
								    result.append('序列号：'+mscode+',条码：'+input + '物料:'+pr_code+'，取消上料成功！'); 						  								   	
								    if(r.result.trim().substr(0,7) == 'success'){//特殊情况:操作成功，但是出现警告,允许刷新页面
     		    	   					r.result = r.result.replace('success', '');
	    		    					result.append('下料完成！');		
							    		Ext.getCmp('ms_code').setValue('');
							    		var mcd_inqty = Ext.getCmp("mcd_inqty").value;
							    		Ext.getCmp('mcd_inqty').setValue(mcd_inqty-1);								    		
								    }else{
								    	 result.append("请采集物料:"+pr_code+"的条码号");	
								    }
	    		    			}	    		    				
	    			   			Ext.getCmp("input").setValue('');//置空录入框
	    			   		}
	    				});
					/*}
				});*/
			}
		  }
	},
	query : function(btn){//筛选
		var me = this, grid = Ext.getCmp('qgrid'), form = btn.ownerCt.ownerCt;			
    	var stepcode = Ext.getCmp("sc_stepcode").value,mcprodcode = Ext.getCmp('mc_prodcode').value
		  mccode = Ext.getCmp('mc_code').value ,sccode = Ext.getCmp('sc_code').value;
		  var btn1 =  Ext.getCmp("confirm");
		 grid.setLoading(true);//loading... 
		Ext.Ajax.request({//筛选之前判断
    		url : basePath + 'pm/mes/checkCraftMaterialQuery.action',
    		params: { 
				mccode : mccode,						//作业单号
				sccode : sccode	,						//资源号
				stepcode:stepcode,                      //工序编号
				mcprodcode:mcprodcode                   //产品编号
    		},
    		method : 'post',
    		callback : function(options,success,r){   		
    			grid.setLoading(false);
    			var res = Ext.decode(r.responseText);   			
				if(res.exceptionInfo) {
					showError(res.exceptionInfo);
					btn1.setDisabled(true);
					return;
				}else{
					var data = res.data;
					Ext.getCmp("mcd_inqty").setValue(data.mcd_inqty);
	        		if(data.datas && data.datas.length > 0 ){
	        			grid.store.loadData(data.datas);	
	        			btn1.setDisabled(false);	        			
	        		} else {
						grid.store.removeAll();
						btn1.setDisabled(true);
		        		me.GridUtil.add10EmptyItems(grid);			
					}
    		    }
    		}
    	});		
	},
	getCraftMaterial : function(result,mscode,mccode,linecode,sccode ,stepcode,input){//上料采集
		var me = this;
		var grid = Ext.getCmp("qgrid");
		var result = Ext.getCmp('t_result');
		var prefix = grid.getStore().getAt(index).get('pr_prefix'),
		    length = grid.getStore().getAt(index).get('pr_length');
		if(prefix != null && prefix!=''){//增加前缀判断
			var temp = input.substr(0,prefix.length);
			if(temp != prefix){
				showError('物料条码前缀是：'+prefix);
				Ext.getCmp('input').setValue('');
				return ;
			}
		}
		if(length != null && length!=''){
			if(input.length != length){
				showError('物料条码位数必须等于限制位数：'+length);
				Ext.getCmp('input').setValue('');
				return ;
			}
		}
		me.FormUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + 'pm/mes/getCraftMaterial.action',
			params: {		   
				  mscode : mscode,					//序列号
				  mccode : mccode,					//作业单号
				  licode : linecode,				//线别
				  sccode : sccode	,				//资源号
				  stepcode:stepcode ,               //工序编号
				  barcode:input     ,               //条码编号
				  sp_id  :sp_id
			},
			method : 'post',
			callback : function(options,success,response){
				me.FormUtil.getActiveTab().setLoading(false);//loading...
			    var r = new Ext.decode(response.responseText);
			    if(r.exceptionInfo){
				   	result.append(r.exceptionInfo, 'error');
				   	Ext.getCmp('input').setValue('');
				 }
			    if(r.success){
			    	//修改grid行		
			    	grid.getStore().getAt(index).set('if_pick','已采集');
			    	grid.getStore().getAt(index).commit();
			    	result.append('上料成功:'+mscode+','+input+'！');
			    	Ext.getCmp('input').setValue('');			    	
			    	if(r.result == 'success'){
			    		result.append('采集完成！');			    		
			    		Ext.getCmp('ms_code').setValue('');
			    		//更新主表 剩余数，和计数字段
			    		var mcd_inqty = Ext.getCmp("mcd_inqty").value;
			    		Ext.getCmp('mcd_inqty').setValue(Number(mcd_inqty)+1);		    		
			    	}else{//提示下一行采集的物料编号		    		
			    		me.getSelectProdcode();
			    	}			    	 	
				 }
			}
		});
	},
	getSelectProdcode:function(){//获取当前需要采集物料的条码
		index = -1;
		var pr_code ;
		var result = Ext.getCmp('t_result');
		var items = Ext.getCmp('qgrid').store.data.items;
		//提示采集的物料的条码号	   				
	    Ext.each(items,function(item,idx){//获取物料ID，sp_type 条码类型，							
			if(index == -1 && item.data['if_pick'] != '已采集'){		
				index = idx;
				sp_id = item.data['sp_id'];
				pr_code = item.data['sp_soncode'];
			}
	    });
	    result.append("请采集物料:"+pr_code+"的条码号");	
	}
});