/**
 * 按条件打印按钮
 */	
Ext.define('erp.view.core.button.PrintByCondition',{ 
		id:'printbycondition',
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintByConditionButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	printType:'',
    	text: $I18N.common.button.erpPrintByConditionButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
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
        handler: function(){
    		var me = this;
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
    						modal: true,
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
    									me.jasperReportPrint(caller,selData.REPORTNAME);
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
    				me.jasperReportPrint(caller,data.datas[0].REPORTNAME);
    			}    			
    		});
            }else{
            	me.beforePrint(caller,function(data){
    			if(data.datas.length>1){   				
    					Ext.create('Ext.window.Window', {
    						modal: true,
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
    									me.Print(caller,selData.FILE_NAME,selData.CONDITION);
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
    				me.Print(caller,data.datas[0].FILE_NAME,data.datas[0].CONDITION);
    			}    			
    		});
            }
    	},
    	Print:function(caller,reportName,condition){
    		var me = this, form = me.ownerCt.ownerCt;
    		var id = Ext.getCmp(form.fo_keyField).value;  
    		if(condition==null) condition='';
    		condition = condition.replace(/\[(.+?)\]/g,function(r){
  				var field=r.substring(1,r.length-1);
  				var da = Ext.getCmp(field);
  				if(da){
  					if(da.xtype=='textfield'){
  						return "'"+da.value+"'";
  					}else{
  						return da.value;
  					}
  				}else{
	    		   return '';
  				}
	    	});
    		if (form.printUrl) {
    			this.execPrintLogic(form.printUrl, id, caller, reportName, condition, this.getPrintConfig);
    		} else {
    			this.getPrintConfig(id, caller, reportName, condition);
    		}
    	},
    	execPrintLogic: function(logicUrl, id, caller, reportName, condition, callback) {
    		var me = this, params={
				id:id,
				reportName:reportName,
				condition:condition
			};
			if(logicUrl.indexOf('caller=') =='-1'){
				params.caller = caller;
			} 
    		Ext.Ajax.request({
    			url : basePath + logicUrl,
    			params: params,
    			timeout: 120000,
    			method:'post',
    			callback : function(options, success, response){
					var res = new Ext.decode(response.responseText);
					if(res.success){
						callback.call(me, id, caller, reportName, condition);
			        } else{
			        	showError(res.exceptionInfo);
			        }
    			}
    		});
    	},
    	getPrintConfig: function(id, caller, reportName, condition) {
    		Ext.Ajax.request({
    			url : basePath + 'common/report/print.action',
    			params:{
    				id:id,
    				caller:caller,
    				reportName:reportName,
    				condition:condition
    			},
    			timeout: 120000,
    			method:'post',
    			callback : function(options, success, response){
					var res = new Ext.decode(response.responseText);
					if(res.success){
						if(res.info.printtype=='jasper'){
							var url= res.info.printUrl+'?userName='+res.info.whichsystem+'&reportName='+res.info.reportname+'&whereCondition='+encodeURIComponent(res.info.condition)+'&printType='+res.info.jasperprinttype;
							window.open(url,'_blank');
						}else if(res.info.isbz=='pdf'){
							window.location.href=res.info.printUrl+'/print?reportname='+res.info.reportname+'&condition='+res.info.condition+'&whichsystem='+res.info.whichsystem+"&"+'defaultCondition='+res.info.defaultCondition;
						}else{
							var url = res.info.printUrl + '?reportfile=' + res.info.reportname + '&&rcondition='+res.info.condition+'&&company=&&sysdate=373FAE331D06E956870163DCB2A96EC7&&key=3D7595A98BFF809D5EEEA9668B47F4A5&&whichsystem='+res.info.whichsystem+'';		
							window.open(url,'_blank');
						}
					}else{
			        	showError(res.exceptionInfo);
			        }
    			}
    		});
    	},
    	jasperReportPrint : function(caller, reportname) {
		var form = this.ownerCt.ownerCt;
		var keyField = form.keyField;
		var id = Ext.getCmp(keyField).value;
		var isProdIO = false;
		if (Ext.getCmp('pi_class')) {
			isProdIO = true;// 出入库单据
		}
		form.setLoading(true);

		// 调用后台存储过程，报表默认为原报表名
		Ext.Ajax.request({
			url : basePath
					+ 'common/JasperReportPrint/JasperGetReportnameByProcedure.action',
			params : {
				ids : id,
				caller : caller,
				reportname : reportname
			},
			method : 'post',
			timeout : 360000,
			callback : function(options, success, response) {
				var res = new Ext.decode(response.responseText);
				if (res.success) {
					//当后台返回拼接的报表字符串时
					var str = res.reportname.split("#");
					if (str != null) {
						for (i = 0; i <= str.length; i++) {
							if (str[i]!=""&&str[i] != null && str[i].length > 0) {
								var reportnames=str[i];
								Ext.Ajax.request({
									url : basePath
											+ 'common/JasperReportPrint/printDefault.action',
									params : {
										id : id,
										caller : caller,
										reportname : reportnames,
										isProdIO : isProdIO
									},
									method : 'post',
									timeout : 360000,
									callback : function(options, success,
											response) {
										form.setLoading(false);
										var res = new Ext.decode(response.responseText);
										if (res.success) {
											var url = res.info.printurl
													+ '?userName='
													+ res.info.userName
													+ '&reportName='
													+ reportnames
													+ '&whereCondition='
													+ encodeURIComponent(res.info.whereCondition)
													+ '&printType='
													+ res.info.printtype
													+ '&title='
													+ res.info.title;
											window.open(url, '_blank');
											window.location.href = window.location.href;
										} else if (res.exceptionInfo) {
											var str = res.exceptionInfo;
											showError(str);
											return;
										}
									}
								});
							}
						}
					}

				} else {
					Ext.Ajax.request({
						url : basePath
								+ 'common/JasperReportPrint/printDefault.action',
						params : {
							id : id,
							caller : caller,
							reportname : reportname,
							isProdIO : isProdIO
						},
						method : 'post',
						timeout : 360000,
						callback : function(options, success, response) {
							form.setLoading(false);
							var res = new Ext.decode(response.responseText);
							if (res.success) {
								var url = res.info.printurl
										+ '?userName='
										+ res.info.userName
										+ '&reportName='
										+ res.info.reportName
										+ '&whereCondition='
										+ encodeURIComponent(res.info.whereCondition)
										+ '&printType=' + res.info.printtype
										+ '&title=' + res.info.title;
								window.open(url, '_blank');
								window.location.href = window.location.href;
							} else if (res.exceptionInfo) {
								var str = res.exceptionInfo;
								showError(str);
								return;
							}
						}
					});
				}
			}
		});

	},
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});