Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.FeatureValueView', {
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
//				afterrender: function(grid){
//					alert(grid.getStore().getCount());
//				},
    			itemclick: function(selModel, record){
//    				console.log(Ext.getCmp('grid'));
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			}    			
    		},
    		'erpCloseButton':{
    		 afterrender:function(btn){
    			 var value=Ext.getCmp('pr_code').value;
    			 if(value) me.loadFeature(value);
    		 }	
    			
    		},
    		'field[name=pr_code]': {
    			afterrender: function(f){
					var condition = getUrlParam('condition'); 
					if(condition != null && condition != ''){
						var data = condition.split(' AND ');
						Ext.getCmp('id').setValue(data[0].split('IS')[1]);
						Ext.getCmp('pr_code').setValue(data[1].split('IS')[1]); 
					}  
				},
				change: function(f){ 
					if(f!=null){ 
						me.loadFeature(f.value);
					} else {
						Ext.getCmp('grid').removeAll();
					}
				}
			},
			'button[id=expand]': {
				click: function(btn){
					var pr_code=Ext.getCmp('pr_code').value;
					var url="jsps/pm/bom/BOMStructQuery.jsp?whoami=BOMStruct!Struct!Query";
					var condition="";
					if(pr_code){
					   condition+="pr_codeIS'"+pr_code+"'";
					}
					me.FormUtil.onAdd('BOMStruct'+ pr_code, 'BOM多级展开', url+"&condition="+condition);
					 
				}
			},
			'button[id=find]':{
				click: function(btn){
					var bo_id=Ext.getCmp('pr_bomid');
					//me.FormUtil.onAdd('locationBOM' + id, 'BOM多级展开', 'jsps/common/BOMTree.jsp.jsp?whoami=BOMStruct!Struct!Query&condition=bo_idIS'+bo_id+' AND bo_mothercodeIS'+Ext.getCmp('pr_code').value);
					me.FormUtil.onAdd('BOMStruct'+ bo_id, 'BOM树形查看', "jsps/make/bom/BOMTree.jsp?whoami=BOMStruct!Struct!Query&condition=bo_idIS"+bo_id+" AND bo_mothercodeIS"+Ext.getCmp('pr_code').value );
					 
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
		var me = this;
		var specdescription = null; 
		Ext.getCmp('pr_refno').setValue(me.getDescription("product","pr_refno","pr_code='" +  Ext.getCmp('pr_code').value + "'"));
		 
		specdescription=me.getDescription("product","pr_specdescription","pr_code='" +  Ext.getCmp('pr_code').value + "'");
		if (specdescription=="" && specdescription==null){
			showError('没有可查看的特征值');
			return;
		}
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/loadNewGridStore.action",
        	params: {
        		caller: 'ProdFeature',
    			condition: "pf_prodcode='" + Ext.getCmp('pr_refno').value + "'"
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
        						pf_detno : d.pf_detno,
        						pf_fecode : d.pf_fecode,
        						pf_fename : d.fe_name 
        				};
        				var des = me.toArrays(null,null,null,specdescription);
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
		var result = [code,valuecode];
		var data = description==null ? this.getDescription(tn, field, con) : description;
		if(data != null && data != ''){
			var da = data.split('|');
			Ext.each(da, function(d, index){
				code[index] = d.split(':')[0];
				valuecode[index] = d.split(':')[1];
			});
		}
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
        			console.log(res.description);
        			des = res.description;
        		}
        	}
		});
		return des;
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
		console.log(result);
		return result;
	},
	getFdSpec: function(code, valuecode){//根据特征项code和特征值码获取特征值
		var result = '';
		Ext.Ajax.request({
        	url : basePath + "pm/bom/getDescription.action",
        	params: {
        		tablename: 'FeatureDetail',
        		field: 'fd_spec',
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
		console.log(result);
		return result;
	}
});