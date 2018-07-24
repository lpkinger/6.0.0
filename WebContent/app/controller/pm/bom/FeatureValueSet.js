Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.FeatureValueSet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.bom.FeatureProduct','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Upload','core.button.LoadFeature',
      		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
  			'core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.form.MultiField',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.DoubleField'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
//				afterrender: function(grid){
//					alert(grid.getStore().getCount());
//				},
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			}
    		},
    		'dbfindtrigger[name=sfcode]': {
    			afterrender: function(){
					var condition = getUrlParam('condition');
					if(condition != null && condition != ''){
						var data = condition.split(' AND ');
						Ext.getCmp('pr_code').setValue(data[1].split('IS')[1]);
						Ext.getCmp('pr_name').setValue(data[2].split('IS')[1]);
						Ext.getCmp('id').setValue(data[0].split('IS')[1]);
					}
				},
    		},
			'dbfindtrigger[name=fd_value]': {
				focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var pr = record.data['fe_code'];
    				if(pr == null || pr == ''){
    					showError("请先选择特征ID!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else { 
    					t.dbBaseCondition = "fd_code='" + pr + "'";
    					if(getUrlParam('fromwhere') == 'SaleDetail') {
    						t.dbBaseCondition=t.dbBaseCondition+" and nvl(fd_style,' ')<>'研发'";
    					}
    				}
    			}
    		},
			'field[name=pr_code]': {
				change: function(f){
					if(f.value != null && f.value != ''){
//						var grid = Ext.getCmp('grid');
						me.loadFeature(f.value);
					} else {
						Ext.getCmp('grid').removeAll();
					}
				}
				
			},
			'button[id=save]': {
				afterrender: function(btn){
					if(getUrlParam('fromwhere') != null && getUrlParam('fromwhere') != '' && 
							getUrlParam('fromwhere') == 'SaleDetail' || getUrlParam("fromwhere") == 'SaleForecastDetail'){
						btn.setDisabled(false);
					} else {
						btn.setDisabled(true);
					}
				},
				click: function(btn){
					var grid = Ext.getCmp('grid');
					var des = '';
					var items = grid.getStore().data.items;
					Ext.each(items, function(item, index){
						if(item.data.fd_value != null && item.data.fd_value != ''){
							if(des == ''){
								des += item.data.fe_code + ":" + item.data.fd_valuecode;								
							} else {
								des += '|' + item.data.fe_code + ":" + item.data.fd_valuecode;	
							}
						}
					});
					var need = '';
					Ext.each(items, function(s, index){
						if(s.data.sd_custneed != null && s.data.sd_custneed != ''){
							if(need == ''){
								need += s.data.fe_code + ":" + s.data.sd_custneed;								
							} else {
								need += '|' + s.data.fe_code + ":" + s.data.sd_custneed;	
							}
						}
					});
					var field = ['sd_specdescription','sd_custneed'];
					var fieldvalue = [des,need];
					me.saveDesAndNeed(getUrlParam('fromwhere'),field,fieldvalue,'sd_id=' + Ext.getCmp('id').value);
				}
			},
			'textfield[name=RealCode]':{
				change:function(field){
					if(field){
						Ext.getCmp('find').setDisabled(false);
					}
				}
			},
			'button[id=getrealcode]':{
				click: function(btn){
					var grid = Ext.getCmp('grid');
					var flag = true;
					var des = '';
					var items = grid.getStore().data.items;
					console.log(items);
					Ext.each(items, function(item, index){		
						console.log(item);
						if (item.data.fe_code!=null && item.data.fe_code != ''){
							if(item.data.fd_value != null && item.data.fd_value != ''){
								if(des == ''){
									des += item.data.fe_code + ":" + item.data.fd_valuecode;								
								} else {
									des += '|' + item.data.fe_code + ":" + item.data.fd_valuecode;	
								}
							} else {
								flag = false;	
								return;
							}
						}
						
					});
					if(!flag){ showError('必须填写所有的特征项的特征值才能生成料号,特征名称的特征值为空');
					return;
					}
					if(flag && des != ''){
						Ext.Ajax.request({//拿到grid的columns
				        	url : basePath + "pm/bom/getRealCode.action",
				        	params: {
				        		prodcode: Ext.getCmp('pr_code').value,
				        		fromwhere: getUrlParam('fromwhere'),
				        		specdescription: des
				        	},
				        	method : 'post',
				        	callback : function(options,success,response){
				        		var res = new Ext.decode(response.responseText);
				        		if(res.exceptionInfo){
				        			showError(res.exceptionInfo);return;
				        		}
				        		if(res.success && res.realCode != null && res.realCode != ''){
				        			Ext.getCmp('RealCode').setValue(res.realCode);
				        			Ext.getCmp('find').setDisabled(false);
				        		}
				        	}
						});
					}
				}
			},
			'button[id=ok]':{
				afterrender:function(btn){
				  var grid = parent.Ext.ComponentQuery.query('grid');
				  if(!grid[0]){
					  btn.setDisabled(true);
				  }
				},
				click: function(btn){
					var me = this;
					var realcode = Ext.getCmp('RealCode').value;
					var grid = parent.Ext.ComponentQuery.query('grid');
					var items = grid[0].getStore().data.items;
//					alert(parent.windefwweow.location.href);
					if(realcode != null && realcode != ''){
//					    grid[0].selModel.lastSelected.set('sd_prodcode', realcode);
//					    grid[0].selModel.lastSelected.set('sd_prodid', me.getDescription("Product",'pr_id',"pr_code='" + realcode + "'"));
//					    grid[0].beforeUpdate();
					    me.saveDesAndNeed(getUrlParam('fromwhere'),['sd_prodcode','sd_prodid'],
					    		[realcode,me.getDescription("Product",'pr_id',"pr_code='" + realcode + "'")],
					    		'sd_id='+grid[0].selModel.lastSelected.data.sd_id);
					    parent.window.location.reload();
					} else {
						showError('必须先生成料号');return;
					}				
				}
			},
			'button[id=cancel]':{			
				click:function(btn){
					var win=parent.Ext.getCmp('win');
			     if(win) win.close();			  
			     else {
			    	 var main = parent.Ext.getCmp("content-panel");
			    	 main.getActiveTab().close();
			     }
				}
			},
			'button[name=refer]':{
				click: function(btn){
					var description='';
					if(btn.id == 'refer1'){
						description = Ext.getCmp('pr_specdescription').value;
						var des = description == null || description =='' ? null :me.toArrays(null,null,null,description);
						me.loadData(des);
					} else if(btn.id=='refer2'){
						description = Ext.getCmp('sd_specdescription').value;
						var des = description == null || description =='' ? null :me.toArrays(null,null,null,description);
						me.loadData(des);
					} else if(btn.id=='refer3'){
						description = Ext.getCmp('sfd_specdescription').value;
						var des = description == null || description =='' ? null :me.toArrays(null,null,null,description);
						me.loadData(des);
					}
				}
			},
			'button[id=find]': {
				click: function(btn){
					me.FormUtil.onAdd('BOM' + id, 'BOM多级展开', 'jsps/common/batchDeal.jsp?whoami=BOMStruct!Struct!Query');
				}
			}
//			'gridcolumn[dataIndex=fd_valuecode]':{
//				change: function(field){
//					console.log(field);
//				}
//			}
		});
	}, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	loadData: function(des, needs){
		var me = this;
		var items = Ext.getCmp('grid').getStore().data.items;
		var fpd = [];
		Ext.each(items, function(d, index){
			var da = {
					fe_code : d.data.fe_code,
					fe_name : d.data.fe_name,	
			};
			if(des != null){
				Ext.each(des[0], function(de, i){
					if(de==d.data.fe_code){			
						da.fd_valuecode = des[1][i];
						var os = me.getFdValues(de, des[1][i]);
						da.fd_value=os[0];
						da.fd_spec=os[1];
						da.fd_remark=os[2];
					}
				});
			} else {
//				da.fd_valuecode = d.data.fd_valuecode;
//				da.fd_value=d.data.fd_value;
				showError('无参考数据'); return;
			}
//			if(needs != null){
//				Ext.each(needs[0], function(de, i){
//					if(de==d.data.fe_fecode){
//						da.sd_custneed = needs[1][i];
//					} else {
//						da.sd_custneed = d.data.sd_custneed;
//					}
//				});
//			} else {
//				da.sd_custneed = d.data.sd_custneed;
//			}     				
			fpd[index] = da;
		});
		Ext.getCmp('grid').store.loadData(fpd);
	},
	loadFeature: function(num){
		var me = this;
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/loadNewGridStore.action",
        	params: {
        		caller: 'ProdFeature',
    			condition: "pf_prodcode='" + num + "'"
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = res.data;
        		var fpd = [];
        		if(data != null && data.length > 0){
        			Ext.each(data, function(d, index){
        				var da = {
        						fe_code : d.pf_fecode,
        						fe_name : d.fe_name,	
        				};
        				if(getUrlParam('fromwhere') != 'Product' && getUrlParam('fromwhere') != null && getUrlParam('fromwhere') != ''){
        					var des = me.toArrays(getUrlParam('fromwhere'),"sd_specdescription","sd_id=" + Ext.getCmp('id').value);
        					if(des != '' && des != null){//获取之前保存记录
            					Ext.each(des[0], function(de, i){
            						if(de==d.pf_fecode){
            							da.fd_valuecode = des[1][i];
            							var os = me.getFdValues(de, des[1][i]);
            							da.fd_value=os[0];
            							da.fd_spec=os[1];
            							da.fd_remark=os[2];
            						}
            					});
            				}
            				var needs = me.toArrays(getUrlParam('fromwhere'),"sd_custneed","sd_id=" + Ext.getCmp('id').value);
            				if(needs != '' && needs != null){//获取之前保存记录
            					Ext.each(needs[0], function(de, i){
            						if(de==d.pf_fecode){
            							da.sd_custneed = needs[1][i];
            						}
            					});
            				}
            				if(des=='' && needs==''){//取默认值
            					var fd = me.getFields('FeatureDetail',['fd_value','fd_valuecode'],
            							"fd_code='" + d.pf_fecode + "' and fd_ifdefault=-1");
            					if(fd != ''){
            						da.fd_valuecode = fd[1];
        							da.fd_value = fd[0];
            					}
            				}
        				}        				
        				fpd[index] = da;
        			});
        			Ext.getCmp('grid').store.loadData(fpd);
        		} else {
        			showError('没有可载入的特征');return;
        		}
        	}
		});
	},
	toArrays:function(tn, field, con, description){
		var code = [];
		var valuecode = [];
		var result = [];
		var data = description==null ? this.getDescription(tn, field, con) : description;
		if(data != null && data != ''){
			var da = data.split('|');
			Ext.each(da, function(d, index){
				code[index] = d.split(':')[0];
				valuecode[index] = d.split(':')[1];
			});
			result = [code,valuecode];
		}
		return result;
	},
	getDescription: function(tn, field, con){
		var des = '';
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "pm/bom/getDescription.action",
        	params: {
        		tablename: tn,
        		field: field,
    			condition: con
        	},
        	method : 'post',
        	async: false,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.success && res.description != null){
        			des = res.description;
        		}
        	}
		});
		return des;
	},
	saveDesAndNeed: function(tn, field, fieldvalue, con){
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "pm/bom/updateDescription.action",
        	params: {
        		tablename: tn,
        		field: field,
        		fieldvalue: fieldvalue,
    			condition: con
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.success){
        			Ext.Msg.alert('提示','保存成功!');
        			/*var win = parent.Ext.ComponentQuery.query('window');
					if(win){
						Ext.each(win, function(){
							this.close();
						});
					} else {
						window.close();
					}*/
        		}
        	}
		});
	},
	getFdValue: function(code, valuecode){//根据特征项code和特征值码获取特征值
		var result = '';
		Ext.Ajax.request({
        	url : basePath + "pm/bom/getDescription.action",
        	params: {
        		tablename: 'FeatureDetail',
        		field: 'fd_value',
    			condition: "fd_code='" + code + "' and fd_valuecode='" + valuecode + "'"
        	},
        	method : 'post',
        	async: false,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.success){
        			result = res.description;
        		}
        	}
		});
		return result;
	},	
	getFdValues: function(code, valuecode){//根据特征项code和特征值码获取特征值
		var result = '';
		Ext.Ajax.request({
        	url : basePath + "pm/bom/getFields.action",
        	params: {
        		tablename: 'FeatureDetail',
        		field: ['fd_value','fd_spec','fd_remark'],//'fd_value',
    			condition: "fd_code='" + code + "' and fd_valuecode='" + valuecode + "'"
        	},
        	method : 'post',
        	async: false,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.success){
        			result = res.data;
        		}
        	}
		});
		console.log(result);
		return result;
	},
	getFields: function(tn, fields, con){
		var des = '';
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "pm/bom/getFields.action",
        	params: {
        		tablename: tn,
        		field: fields,
    			condition: con
        	},
        	method : 'post',
        	async: false,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.success && res.data != null){
        			des = res.data;
        		}
        	}
		});
		return des;
	}
});