Ext.QuickTips.init();
Ext.define('erp.controller.common.datalistPrint', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
    views:[
	       'common.datalist.Viewport','common.datalist.GridPanel','common.datalist.Toolbar','core.button.VastAudit','core.button.VastDelete',
	       'core.button.VastPrint','core.button.VastReply','core.button.VastSubmit','core.button.ResAudit','core.form.FtField',
	       'core.grid.TfColumn','core.grid.YnColumn','core.trigger.DbfindTrigger','core.form.FtDateField','core.form.FtFindField',
	       'core.form.FtNumberField', 'core.form.MonthDateField','core.plugin.NewRowNumberer','core.form.BtnDateField'],
    init:function(){
    	this.control({ 
    		 'erpDatalistGridPanel': { 
	    			   itemclick: this.onGridItemClickPrint
	    			   
	    		   }
    	});
    },
    onGridItemClickPrint:function(selModel, record){	
    		var me = this;
    		if(keyField==null||keyField==''){
    			showError('列表没有配置主键！');
    			return;
    		}
    		 Ext.Ajax.request({
	    	  url : basePath + 'common/JasperReportPrint/getPrintType.action',
	    		   method : 'get',
	    		   async : false,
	    		   callback : function(opt, s, res){
	    			   var r = new Ext.decode(res.responseText);
					   if(r.success && r.printtype){
					   		me.printType=r.printtype;
	    			   }
	    		   }
	    	   });
    		if(me.printType=='jasper'){    			
         	   me.beforePrint(caller,function(data){
    				if(data.datas.length>1){   				
    					Ext.create('Ext.window.Window', {
    						autoShow: true,
    						title: '选择打印类型',
    						width: 400,
    						height: 300,
    						layout: 'anchor',
    						items: [{ 							    					
  							  anchor:'100% 100%',
  							  xtype:'form',
  							  id :'printbycondition',
  							  buttonAlign : 'center',
  							  items:[{
  							        xtype: 'combo',
  									id: 'template',
  									fieldLabel: '选择打印类型', 									
  									store: Ext.create('Ext.data.Store', {
  										autoLoad: true,
  									    fields: ['TITLE','ID','REPORTNAME'],
  									    data:data.datas 									 
  									}),
  									queryMode: 'local',
  								    displayField: 'TITLE',
  								    valueField: 'ID',
  									width:300,
  								    allowBlank:false,
  								    selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录  
  									style:'margin-left:15px;margin-top:15px;',
  									listeners : {
									      afterRender : function(combo) {
									         combo.setValue(data.datas[0].ID);
									      }
									   }
  								}]	 							    	     				    							           	
  						 }], 
    						buttonAlign: 'center',
    						buttons: [{
    							text: '确定',
    							handler: function(b) {
    								var temp = Ext.getCmp('template');
    								if(temp &&  temp.value!= null){
    									var selData = temp.valueModels[0].data;
    									me.jasperReportPrint(record,caller,selData.REPORTNAME);
    								}else{
    									alert("请选择打印模板");
    								}   								
    							}
    						}, {
    							text: '取消',
    							handler: function(b) {
    								b.ownerCt.ownerCt.close();
    							}
    						}]
    					});   					
    			}else{
    				me.jasperReportPrint(record,caller,data.datas[0].REPORTNAME);
    			}    			
    		});
            }else{
            	me.beforePrint(caller,function(data){
    			if(data.datas.length>1){   				
    					Ext.create('Ext.window.Window', {
    						autoShow: true,
    						title: '选择打印类型',
    						width: 400,
    						height: 300,
    						layout: 'anchor',
    						items: [{ 							    					
  							  anchor:'100% 100%',
  							  xtype:'form',
  							  id :'printbycondition',
  							  buttonAlign : 'center',
  							  items:[{
  							        xtype: 'combo',
  									id: 'template',
  									fieldLabel: '选择打印类型', 									
  									store: Ext.create('Ext.data.Store', {
  										autoLoad: true,
  									    fields: ['TITLE','ID','CONDITION','FILE_NAME'],
  									    data:data.datas 									 
  									}),
  									queryMode: 'local',
  								    displayField: 'TITLE',
  								    valueField: 'ID',
  									width:300,
  								    allowBlank:false,
  								    selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录  
  									style:'margin-left:15px;margin-top:15px;',
  									listeners : {
									      afterRender : function(combo) {
									         combo.setValue(data.datas[0].ID);
									      }
									   }
  								}]	 							    	     				    							           	
  						 }], 
    						buttonAlign: 'center',
    						buttons: [{
    							text: '确定',
    							handler: function(b) {
    								var temp = Ext.getCmp('template');
    								if(temp &&  temp.value!= null){
    									var selData = temp.valueModels[0].data;
    									me.Print(record,caller,selData.FILE_NAME,selData.CONDITION);
    								}else{
    									alert("请选择打印模板");
    								}   								
    							}
    						}, {
    							text: '取消',
    							handler: function(b) {
    								b.ownerCt.ownerCt.close();
    							}
    						}]
    					});   					
    			}else{
    				me.jasperReportPrint(record,caller,data.datas[0].REPORTNAME);
    			}    			
    		});
            }   	
    },
    beforePrint: function(f,callback) {
    		var me = this;
    		if(me.printType!='jasper'){
	    		Ext.Ajax.request({
	    			url: basePath + 'common/report/getFields.action',
	    			method: 'post',
	    			params:{
	    				caller:f
	    			},
	    			callback: function(opt, s, r) {
	    				var rs = Ext.decode(r.responseText);    				
	    				callback.call(null,rs);
	    			}
	    		});
    		}else{
    			Ext.Ajax.request({
	    			url: basePath + 'common/JasperReportPrint/getFields.action',
	    			method: 'post',
	    			params:{
	    				caller:f
	    			},
	    			callback: function(opt, s, r) {
	    				var rs = Ext.decode(r.responseText);    				
	    				callback.call(null,rs);
	    			}
    			});
    		}
    	},
    	jasperReportPrint:function(record,caller,reportname){    	
			//多加了个参数record，通过点击grid明细获取到配置的主键和主键值	
	    	var id=record.data[keyField];
	    	var isProdIO=false;
	    	if(Ext.getCmp('pi_class')){
	    		isProdIO=true;//出入库单据
	    	}
	    	//form.setLoading(true);
	    	Ext.Ajax.request({
	    		url : basePath +'common/JasperReportPrint/printDefault.action',
				params: {
					id: id,
					caller:caller,
					reportname:reportname,
					isProdIO:isProdIO
				},
				method : 'post',
				timeout: 360000,
				callback : function(options,success,response){
					//form.setLoading(false);
					var res = new Ext.decode(response.responseText);
					if(res.success){
						var url = res.info.printurl+'?userName='+res.info.userName+'&reportName='+res.info.reportName+'&whereCondition='
						+'where '+keyField+'='+id+'&printType='+res.info.printtype+'&title='+res.info.title;
						window.open(url,'_blank');
						window.location.href = window.location.href;
					}else if(res.exceptionInfo){
						var str = res.exceptionInfo;
						showError(str);return;
					}
				}
	    	});
		
    	}
});