Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.FeatureProduct', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.bom.FeatureProduct','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Upload','core.button.LoadFeature',
      		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
  			'core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			}
    		},
    		'erpLoadFeatureButton': {
    			click: function(btn){
    				if(getUrlParam('formCondition') != null && Ext.getCmp('grid').getStore().getCount() != 0){
    					showError('载入前请删除明细，以免数据重复');return;
    				}
    				var refno = Ext.getCmp('fp_ftcode').value;
    				if(refno == null || refno == ''){
    					showError('没有模板编号');return;
    				} else {
    					me.loadFeature(refno);
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = btn.ownerCt.ownerCt;
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber('FeatureProduct',1,'fp_code');//自动添加编号
    				}
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var des = '';
    				Ext.each(items, function(item, index){
    					if(item.dirty){
    						if(des == ''){
    							des += item.data.fpd_fecode + ":" + item.data.fpd_fevaluecode;    							
    						} else {
    							des += '|' + item.data.fpd_fecode + ":" + item.data.fpd_fevaluecode; 
    						}
    					}
    				});
    				Ext.getCmp('fp_description').setValue(des);
    				Ext.getCmp('fp_detcount').setValue(Ext.getCmp('grid').getStore().getCount());
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				if(Ext.getCmp('fp_id').value == null || Ext.getCmp('fp_id').value == ''){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var des = '';
    				var items = Ext.getCmp('grid').store.data.items;
    				Ext.each(items, function(item, index){
    					if(item.data.fpd_fecode != '' && item.data.fpd_fecode != 0 && item.data.fpd_fecode != null){
    						if(des == ''){
    							des += item.data.fpd_fecode + ":" + item.data.fpd_fevaluecode;    							
    						} else {
    							des += '|' + item.data.fpd_fecode + ":" + item.data.fpd_fevaluecode; 
    						}
    					}
    				});
    				Ext.getCmp('fp_description').setValue(des);
    				Ext.getCmp('fp_detcount').setValue(Ext.getCmp('grid').getStore().getCount());
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			afterrender: function(btn){
    				if(Ext.getCmp('fp_id').value == null || Ext.getCmp('fp_id').value == ''){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('fp_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				//可能重复点多个
    				me.FormUtil.onAdd('addFeatureProduct'+new Date().getTime(), '新增物料特征值', 'jsps/pm/bom/FeatureProduct.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('fp_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('fp_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('fp_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}else {
    					Ext.getCmp('deleteDetail').setDisabled(true);
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('fp_id').value);
    			}
    		},
			'dbfindtrigger[name=fpd_fevalue]': {
				focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var pr = record.data['fpd_fecode'];
    				if(pr == null || pr == ''){
    					showError("请先选择特征ID!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "fd_code='" + pr + "'";
    				}
    			}
    		},
			'field[name=ecrd_id]': {
				change: function(f){
					if(f.value != null && f.value != ''){
						me.GridUtil.loadNewStore(Ext.getCmp('grid'), {
							caller: caller,
							condition: 'edl_bdid=' + f.value
						});
						Ext.getCmp('deletebutton').show();
						Ext.getCmp('updatebutton').show();
						//Ext.getCmp('save').hide();
					} else {
						Ext.getCmp('deletebutton').hide();
						Ext.getCmp('updatebutton').hide();
						//Ext.getCmp('save').show();
					}
					}
				
			},
			'button[id=deleteDetail]':{
				afterrender: function(btn){
					if(getUrlParam('formCondition') == null){
						btn.hide();
					}						
				},
				click: function(btn){
					var grid = Ext.getCmp('grid');
					if(grid.getStore().getCount() != 0){
						Ext.Ajax.request({//拿到grid的columns
				        	url : basePath + "pm/bom/deleteAllFeatureProductDetail.action",
				        	params: {
				        		id: Ext.getCmp('fp_id').value
				        	},
				        	method : 'post',
				        	callback : function(options,success,response){
				        		var res = new Ext.decode(response.responseText);
				        		if(res.exceptionInfo){
				        			showError(res.exceptionInfo);return;
				        		}
				        		if(res.success){
				        			window.location.reload();
				        		}
				        	}
						});
					} else {
						alert('明细行没有数据可删，可直接载入特征');
					}
				}
			}
		});
	}, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	loadFeature: function(num){
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/loadNewGridStore.action",
        	params: {
        		caller: 'FeatureTemplet',
    			condition: "fd_code='" + num + "'"
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
        					fpd_detno : d.fd_detno,
            				fpd_fecode : d.fd_fecode,
            				fpd_fename : d.fd_fename
        				};
        				fpd[index] = da;
        			});
        			Ext.getCmp('grid').store.loadData(fpd);
        		} else {
        			showError('没有可载入的特征');return;
        		}
        	}
		});
	},
	check: function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		Ext.each(items, function(item, i){
			if(item.dirty){
				if(item.data.fpd_fecode == '' || item.data.fpd_fecode == null){
					showError("特征项ID不能为空");return;
				}
				Ext.each(items, function(t, j){
					if(i != j && item.data.fpd_fecode == t.data.fpd_fecode){
						showError("特征项ID不能重复");return;
					}
				});
			}
		});
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "pm/bom/getList.action",
        	params: {
        		tablename: 'ProdFeature',
        		field: ["pf_fecode"],
    			condition: "pf_prodcode='" + Ext.getCmp("fp_refno").value + "'"
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.success){
        			if(res.list.length > 0){
        				Ext.each(items, function(item, i){
        					if(item.dirty){
        						Ext.each(res.list, function(s, index){
        							item.data.fpd_fecode == s[0];
        							showError("序号[" + item.data.fpd_detno + "]特征不属于所选的虚拟特征件的特征范围");return;
        						});        						
        					}
        				});
        			}
        		}
        	}
		});
		
	},
});