Ext.QuickTips.init();
Ext.define('erp.controller.drp.distribution.SaleForecastAsk', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','drp.distribution.SaleForecastAsk','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ChangeDate',
      		'core.button.ResAudit','core.button.Scan','core.button.DeleteDetail','core.button.ResSubmit','core.button.FeatureDefinition',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.End','core.button.ResEnd','core.button.Print',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn','core.button.FeatureView','core.button.TurnSale'
  	],
	init:function(){
		var me = this;
		this.control({
		   'erpGridPanel2': { 
				itemclick: function(selModel, record){
					Ext.getCmp('featuredefinition').setDisabled(false);
					Ext.getCmp('featureview').setDisabled(false);
					this.onGridItemClick
				}
			},
		   'erpChangeDateButton':{
		     afterrender: function(btn){
					var status = Ext.getCmp('sf_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
			  click:function(btn){
			  var keyvalue=Ext.getCmp('sf_id').value;
			  var condition='sd_sfid='+keyvalue;
			  	var win = new Ext.window.Window({
			    	id : 'win',
   				    height: "100%",
   				    width: "80%",
   				    maximizable : true,
   					buttonAlign : 'center',
   					layout : 'anchor',
   				    items: [{
   				    	  tag : 'iframe',
   				    	  frame : true,
   				    	  anchor : '100% 100%',
   				    	  layout : 'fit',
   				    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/editorColumn.jsp?caller=SaleForecast!Change' 
   				    	  +"&condition=" + condition +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
   				    }],
   				    buttons : [{
   				    	text : $I18N.common.button.erpConfirmButton,
   				    	iconCls: 'x-button-icon-confirm',
   				    	cls: 'x-btn-gray',
   				    	handler : function(){
   				    		var grid = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
   				    		var data = grid.getEffectData();                      
		                if(data != null){
			               grid.setLoading(true);
			               Ext.Ajax.request({
		   		           url : basePath + 'scm/sale/SaleForecastChangedate.action',
		   		           params: {
		   			            caller: caller,
		   			            data: Ext.encode(data)
		   		            },
		   		           method : 'post',
		   		          callback : function(options,success,response){
		   			           grid.setLoading(false);
		   			           var localJson = new Ext.decode(response.responseText);
		   			           if(localJson.exceptionInfo){
		   				       showError(localJson.exceptionInfo);
		   				      return "";
		   			        }
	    			        if(localJson.success){
	    				         if(localJson.log){
	    					    showMessage("提示", localJson.log);
	    				     }
		   				     Ext.Msg.alert("提示", "处理成功!", function(){
		   					     win.close();
		   					   var detailgrid= Ext.getCmp('grid');		   					   
		   					    gridParam = {caller: 'SaleForecast', condition: condition};
		   					   me.GridUtil.getGridColumnsAndStore(detailgrid, 'common/singleGridPanel.action', gridParam, "")
		   				});
		   			}
		   		}
			});
		   }
   				    	}
   				    }, {
   				    	text : $I18N.common.button.erpCloseButton,
   				    	iconCls: 'x-button-icon-close',
   				    	cls: 'x-btn-gray',
   				    	handler : function(){
   				    		Ext.getCmp('win').close();
   				    	}
   				    }]
   				});
   				win.show();
			  
			  }			
			},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					this.beforeSaveSaleForecast(this);
				}
			},
			'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				btn.ownerCt.add({
    					xtype: 'erpFeatureDefinitionButton'
    				});
    				btn.ownerCt.add({
    					xtype: 'erpFeatureViewButton'
    				});
    			}
    		},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('sf_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.beforeUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addSaleForecastAsk', '新增待确认销售预测单', 'jsps/drp/distribution/saleForecastAsk.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sf_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('sf_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sf_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('sf_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sf_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('sf_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sf_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('sf_id').value);
				}
			},
			'erpPrintButton': {
				click:function(btn){
				var reportName="SaleForecastAudit1";
				var condition='{SaleForeCast.sf_id}='+Ext.getCmp('sf_id').value+'';
				var id=Ext.getCmp('sf_id').value;
				me.FormUtil.onwindowsPrint(id,reportName,condition);
			}
			},
			'erpEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sf_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onEnd(Ext.getCmp('sf_id').value);
    			}
    		},
    		'erpResEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sf_statuscode');
    				if(status && status.value != 'FINISH'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResEnd(Ext.getCmp('sf_id').value);
    			}
    		},
    		'erpFeatureDefinitionButton':{
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var record = grid.selModel.lastSelected;
    				console.log(record);
    				if(record.data.sd_prodcode != null){
    					Ext.Ajax.request({//拿到grid的columns
    						url : basePath + "pm/bom/getDescription.action",
    						params: {
    							tablename: 'Product',
    							field: 'pr_specvalue',
    							condition: "pr_code='" + record.data.sd_prodcode + "'"
    						},
    						method : 'post',
    						async: false,
    						callback : function(options,success,response){
    							var res = new Ext.decode(response.responseText);
    							if(res.exceptionInfo){
    								showError(res.exceptionInfo);return;
    							}
    							if(res.success){
    								if(res.description != '' && res.description != null && res.description == 'SPECIFIC'){
    									var win = new Ext.window.Window({
    			    						id : 'win' + record.data.sd_id,
    			    						title: '生成特征料号',
    			    						height: "90%",
    			    						width: "70%",
    			    						maximizable : true,
    			    						buttonAlign : 'center',
    			    						layout : 'anchor',
    			    						items: [{
    			    							tag : 'iframe',
    			    							frame : true,
    			    							anchor : '100% 100%',
    			    							layout : 'fit',
    			    							html : '<iframe id="iframe_' + record.data.sd_id + '" src="' + basePath + 
    			    							"jsps/pm/bom/FeatureValueSet.jsp?fromwhere=SaleForecastDetail&condition=formidIS" + record.data.sd_id + ' AND pr_codeIS' + record.data.sd_prodcode + ' AND pr_nameIS' + record.data.pr_detail +'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
    			    						}]
    			    					});
    			    					win.show();   									
    								} else {
    									showError('物料特征必须为 虚拟特征件');return;
    								}
    							}
    						}
    					});
    				}
    			}
    		},
    		'erpFeatureViewButton':{
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var record = grid.selModel.lastSelected;
    				console.log(record);
    				if(record.data.sd_prodcode != null){
    					Ext.Ajax.request({//拿到grid的columns
    						url : basePath + "pm/bom/getDescription.action",
    						params: {
    							tablename: 'Product',
    							field: 'pr_specvalue',
    							condition: "pr_code='" + record.data.sd_prodcode + "'"
    						},
    						method : 'post',
    						async: false,
    						callback : function(options,success,response){
    							var res = new Ext.decode(response.responseText);
    							if(res.exceptionInfo){
    								showError(res.exceptionInfo);return;
    							}
    							if(res.success){
    								if(res.description != '' && res.description != null && res.description == 'NOTSPECIFIC'){
    									var win = new Ext.window.Window({
    			    						id : 'win' + record.data.sd_id,
    			    						title: '特征查看',
    			    						height: "90%",
    			    						width: "70%",
    			    						maximizable : true,
    			    						buttonAlign : 'center',
    			    						layout : 'anchor',
    			    						items: [{
    			    							tag : 'iframe',
    			    							frame : true,
    			    							anchor : '100% 100%',
    			    							layout : 'fit',
    			    							html : '<iframe id="iframe_' + record.data.sd_id + '" src="' + basePath + 
    			    							"jsps/pm/bom/FeatureValueView.jsp?fromwhere=SaleForecastDetail&condition=formidIS" + record.data.sd_id + ' AND pr_codeIS' + record.data.sd_prodcode + ' AND pr_nameIS' + record.data.pr_detail +'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
    			    						}]
    			    					});
    			    					win.show();  									
    								} else {
    									showError('物料特征必须为 虚拟特征件');return;
    								}
    							}
    						}
    					});    					
    				}
    			}
    		},
			'textfield[name=sf_fromdate]': {
				change: function(field){
					if(field.value != null && field.value != ''){
						var grid = Ext.getCmp('grid');
						var date = field.value;
						Ext.Array.each(grid.getStore().data.items,function(item){
							item.set('sd_startdate',date);
						});
					}
				}
    		},
			'textfield[name=sf_todate]': {
				change: function(field){
					if(field.value != null && field.value != ''){
						var grid = Ext.getCmp('grid');
						var date = field.value;
						Ext.Array.each(grid.getStore().data.items,function(item){
							item.set('sd_enddate',date);
						});
					}
				}
    		},
			'textfield[name=sf_custcode]': {
				change: function(field){
					if(field.value != null && field.value != ''){
						var grid = Ext.getCmp('grid');
						var date = field.value;
						Ext.Array.each(grid.getStore().data.items,function(item){
							item.set('sd_custcode',date);
						});
					}
				}
    		},
    		'erpTurnSaleButton': {
    			afterrender: function(btn){
				var status = Ext.getCmp('sf_statuscode');
				if(status && status.value != 'AUDITED'){
					btn.hide();
				}
			},
			click: function(btn){
				var grid = Ext.getCmp('grid');
				var form = Ext.getCmp('form');
				if(form.getForm().isValid()){
					//form里面数据
					Ext.each(form.items.items, function(item){
						if(item.xtype == 'numberfield'){
							//number类型赋默认值，不然sql无法执行
							if(item.value == null || item.value == ''){
								item.setValue(0);
							}
						}
					});
					var r = form.getValues();
					//去除ignore字段
					var keys = Ext.Object.getKeys(r), f;
					var reg = /[!@#$%^&*()'":,\/?]/;
					Ext.each(keys, function(k){
						f = form.down('#' + k);
						if(f && f.logic == 'ignore') {
							delete r[k];
						}
						//codeField值强制大写,自动过滤特殊字符
						if(k == form.codeField && !Ext.isEmpty(r[k])) {
							r[k] = r[k].trim().toUpperCase().replace(reg, '');
						}
						//获取新id
						if(k==form.keyField){
							Ext.Ajax.request({
						   		url : basePath + 'common/getId.action?seq=SALEFORECAST_SEQ',
						   		method : 'get',
						   		async: false,
						   		callback : function(options,success,response){
						   			var rs = new Ext.decode(response.responseText);
						   			if(rs.exceptionInfo){
					        			showError(rs.exceptionInfo);return;
					        		}
					    			if(rs.success){
						   				r[k]=rs.id
						   			}
						   		}
							});
						}
					});
				}
				var param=new Array();
				var s = grid.getStore().data.items;//获取store里面的数据
				var dd;
				for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
					var data = s[i].data;
					dd = new Object();
						Ext.each(grid.columns, function(c){
							if((!c.isCheckerHd)&&(c.logic != 'ignore')){//只需显示，无需后台操作的字段，自动略去
								if(c.xtype == 'datecolumn'){
									c.format = c.format || 'Y-m-d';
									if(Ext.isDate(data[c.dataIndex])){
										dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
									} else {
										if(c.editor){
											dd[c.dataIndex] = Ext.Date.format(new Date(), c.format);//如果用户没输入日期，或输入有误，就给个默认日期，
										}
									}
								} else if(c.xtype == 'datetimecolumn'){
									if(Ext.isDate(data[c.dataIndex])){
										dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
									} else {
										if(c.editor){
											dd[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d H:i:s');//默认日期，
										}
									}
								} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
									if(data[c.dataIndex] == null || data[c.dataIndex] == ''){
										dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
									} else {
										dd[c.dataIndex] = s[i].data[c.dataIndex];
									}
								} else {
									dd[c.dataIndex] = s[i].data[c.dataIndex];
								}
							}
						});
						if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
							dd[grid.mainField] = r[form.keyField];
						}
						param.push(Ext.JSON.encode(dd));
					}
			    var params = new Object();
				Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
					if(contains(k, 'ext-', true)){
						delete r[k];
					}
				});
				params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
				params.param = unescape(param.toString().replace(/\\/g,"%"));
				for(var i=2; i<arguments.length; i++) {  //兼容多参数
					params['param' + i] = unescape(arguments[i].toString().replace(/\\/g,"%"));
				}  
				var me = this;
				var form = Ext.getCmp('form');
/*					me.getActiveTab().setLoading(true);//loading...
*/					Ext.Ajax.request({
			   		url : basePath + 'drp/distribution/saveSaleForecast.action?caller=SaleForecast',
			   		params : params,
			   		method : 'post',
			   		callback : function(options,success,response){
			   			var localJson = new Ext.decode(response.responseText);
		    			if(localJson.success){
		    				saveSuccess(function(){
		    				});
			   			} else if(localJson.exceptionInfo){
			   				var str = localJson.exceptionInfo;
			   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
			   					str = str.replace('AFTERSUCCESS', '');
			   					saveSuccess(function(){
			    					//add成功后刷新页面进入可编辑的页面 
					   				var value = r[form.keyField];
					   		    	var formCondition = form.keyField + "IS" + value ;
					   		    	var gridCondition = '';
					   		    	var grid = Ext.getCmp('grid');
					   		    	if(grid && grid.mainField){
					   		    		gridCondition = grid.mainField + "IS" + value;
					   		    	}
					   		    	if(me.contains(window.location.href, '?', true)){
						   		    	window.location.href = window.location.href + '&formCondition=' + 
						   					formCondition + '&gridCondition=' + gridCondition;
						   		    } else {
						   		    	window.location.href = window.location.href + '?formCondition=' + 
						   					formCondition + '&gridCondition=' + gridCondition;
						   		    }
			    				});
			   					showError(str);
			   				} else {
			   					showError(str);
				   				return;
			   				}
			   			} else{
			   				saveFailure();//@i18n/i18n.js
			   			}
			   		}
			   		
				});
			}
		},
    		
		});
	}, 
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSaveSaleForecast: function(){
		var grid = Ext.getCmp('grid'), items = grid.store.data.items, c = Ext.getCmp('sf_code').value;
	    Ext.Array.each(items, function(item){
	    	item.set('sd_code', c);
		});
		//保存
	    this.FormUtil.beforeSave(this);
	},
	beforeUpdate: function(){
		var grid = Ext.getCmp('grid'), items = grid.store.data.items, c = Ext.getCmp('sf_code').value;
	    Ext.Array.each(items, function(item){
	    	item.set('sd_code', c);
		});
		//更新
	    this.FormUtil.onUpdate(this);	
	}
});