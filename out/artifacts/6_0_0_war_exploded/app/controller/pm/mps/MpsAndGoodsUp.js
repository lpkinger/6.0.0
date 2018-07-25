Ext.QuickTips.init();
Ext.define('erp.controller.pm.mps.MpsAndGoodsUp', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','pm.mps.MpsAndGoodsUp','core.toolbar.Toolbar','core.button.ExecuteOperation',
    		'core.button.Close','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
    		'core.button.DullStockUp'
    	],
    init:function(){ 
    	var me=this;      	
    	this.control({ 
    		'dbfindtrigger[name=mm_code]':{
    		  afterrender:function(f){				 
				   f.dbBaseCondition="mm_runkind ='B2C'";    		          	          
				   Ext.Ajax.request({ //获取最近一次时间的计划编号
			        	url : basePath + 'common/getFieldData.action',
			        	params: {
			        	  field : "mm_code", 
			        	  caller : "(select * from mpsmain where mm_runenddate is not null order by mm_runenddate desc )",
			        	  condition : "rownum=1"
			        	},
			        	method : 'post',
			        	callback : function(options,success,response){
			        	var res = new Ext.decode(response.responseText); 
			        	   if(res.exceptionInfo){ 
			        			return;
			        		}else if(res.success && res.data != null){ 
			        			 f.setValue(res.data);
		      	                 f.autoDbfind('form', caller, f.name, f.name + ' like\'%' + f.value + '%\'');
			        		}
			        	}
			        });
			     }    
    		},
    		'erpExecuteOperationButton':{
				  click:function(btn){
				     me.ExecuteOperation();
				  }
    		},  		
    		'button[id=close]':{
    		  click:function(btn){
    			  var main = parent.Ext.getCmp("content-panel");
    			  main.getActiveTab().close();
    		  }   		
    		},
    		'erpDullStockUpButton':{//呆滞库存处理
	    		  click:function(btn){
		    		  var id=Ext.getCmp('mm_id').getValue();
		    		  var code=Ext.getCmp('mm_code').getValue();
		    		  if(!code){
		    		  	 showError("请先进行库存运算，再上架！");
		    		  	 return;
		    		  }
		    		  var s1 = Ext.getCmp("mm_runenddate").getValue();
		    		  //判断最近一次的计划编号时间是否超过一天
		    		  if(s1 != null){
		    		  	   s1 = new Date(s1.replace(/-/g, "/"));
						   var s2 = new Date();
						   var time = (s2.getTime() - s1.getTime())/(1000 * 60 * 60 * 24);
						   console.log(time);
						   if(time >1){
						   	  //提示用户
							   	warnMsg('距离上次库存运算时间已经超过一天，是否继续上架', function(btn){
									if(btn == 'yes'){
										   var condition="mdd_mpsid='"+id+"' AND nvl(pr_supplytype,' ') <>'VIRTUAL' and mdd_action='UP'";
				    		               me.FormUtil.onAdd('MRPOnhandThrow_'+id,'呆滞库存上架','jsps/pm/mps/MRPOnHandThrow.jsp?whoami=MRPOnhandThrow&_noc=1&urlcondition='+condition+'&mdd_mpscode='+code);
									}
								});
						   }else{
						   	   var condition="mdd_mpsid='"+id+"' AND nvl(pr_supplytype,' ') <>'VIRTUAL' and mdd_action='UP'";
			    		       me.FormUtil.onAdd('MRPOnhandThrow_'+id,'呆滞库存上架','jsps/pm/mps/MRPOnHandThrow.jsp?whoami=MRPOnhandThrow&_noc=1&urlcondition='+condition+'&mdd_mpscode='+code);
						   }
		    		  }else{
		    		  	   showError("计划编号还未进行库存运算，请先进行库存运算");
		    		  	   return ;
		    		  }
    		  }   		
    		}
    	});  
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt; 
	},
	ExecuteOperation:function(){
	  var code=Ext.getCmp('mm_code').getValue(); 
	/*  var main = parent.Ext.getCmp('content-panel');*/
	  var mb = new Ext.window.MessageBox();
	    mb.wait('正在运算中','请稍后...',{
		   interval: 10000, 
		   duration: 1000000,
		   increment: 20,
		 /*  text: 'Runing...',*/
		   scope: this
		});
	  Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'pm/mps/RunMrpAndGoods.action',
        	params: {
        	  code:code, 
        	  caller:caller
        	},
        	method : 'post',
        	timeout: 600000,
        	callback : function(options,success,response){
        	mb.close();
        	var res = new Ext.decode(response.responseText); 
        	   if(res.exceptionInfo){ 
        			showError(res.exceptionInfo);
        			return;
        		}else if(res.success){ 
        			Ext.Msg.alert('提示',"运算成功",function(){        			  
        			    window.location.href=basePath+"jsps/pm/mps/MpsAndGoodsUp.jsp?formCondition=mm_code='"+code+"'&mm_kind='B2C'";
        			});
        		}
        	}
        });
	}   
}
);