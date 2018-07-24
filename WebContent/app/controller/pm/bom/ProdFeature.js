Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.ProdFeature', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.bom.ProdFeature','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Upload','core.form.YnField',
  			'core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.LoadFeature',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.grid.YnColumn','core.trigger.MultiDbfindTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpFormPanel' : {
    			afterload : function(form) {
    				form.getForm().getFields().each(function() {
						var val = getUrlParam(this.name);
						if(!Ext.isEmpty(val) && Ext.isEmpty(this.getValue())) {
							this.setValue(val);
						}
					});
    			}
    		},
			'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    					this.onGridItemClick(selModel, record);
    			},
    			afterrender:function(grid){
    				grid.setReadOnly(false);
    			}
    		},
    		'erpLoadFeatureButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pr_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(getUrlParam('formCondition') != null && Ext.getCmp('grid').getStore().getCount() != 0){
    					showError('载入前请删除明细，以免数据重复');return;
    				}
    				var refno = Ext.getCmp('pr_refno').value;
    				if(refno == null || refno == ''){
    					showError('没有模板编号');return;
    				} else {
    					me.loadFeature(refno);
    				}
    			}
    		},
    		'erpSaveButton': {
				afterrender:function(btn){
					var status = Ext.getCmp('pr_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
					btn.ownerCt.insert(2,{					
						text: $I18N.common.button.erpCloseButton,
						iconCls: 'x-button-icon-close',
				    	cls: 'x-btn-gray',
				    	width: 60,
				    	style: {
				    		marginLeft: '10px'
				        },
				        handler:function(){
				        	var win=Ext.getCmp('win');
				        	if(win)  win.close();      						        
				        }
					});
					var form=Ext.getCmp('form');
					data=form.getValues();
					Ext.getCmp('pf_prodcode').setValue(data.pr_code);
					Ext.getCmp('pf_prid').setValue(data.pr_id);
				},
				click:function(btn){
					var form=btn.ownerCt.ownerCt;
					r=form.getValues();
					var params = new Object();
					params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
					var me = this;
					Ext.Ajax.request({
				   		url : basePath + form.saveUrl,
				   		params : params,
				   		method : 'post',
				   		callback : function(options,success,response){
				   			var localJson = new Ext.decode(response.responseText);
			    			if(localJson.success){
			    				saveSuccess(function(){
			    					var grid=Ext.getCmp('grid');
			    					var param={
			    						caller:caller,
			    						condition:'pf_prid='+r.pf_prid
			    					};
			    					btn.ownerCt.ownerCt.ownerCt.close();
			    					grid.GridUtil.loadNewStore(grid,param);
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
			'erpDeleteButton' : {
				afterrender: function(btn){
					if(Ext.getCmp('pr_id').value != null && Ext.getCmp('pr_id').value != ''){
						var status = Ext.getCmp('pr_statuscode');
	    				if(status && status.value != 'ENTERING'){
	    					btn.hide();
	    				} else {
	    					btn.show();
	    				}
					} else {
						btn.hide();
					}
    			},
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('pr_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
					if(Ext.getCmp('pr_id').value != null && Ext.getCmp('pr_id').value != ''){
						var status = Ext.getCmp('pr_statuscode');
	    				if(status && status.value != 'ENTERING'){
	    					btn.hide();
	    				} else {
	    					btn.show();
	    				}
					} else {
						btn.hide();
					} 
    			},
				click: function(btn){
					if(Ext.getCmp('pr_statuscode').value!='ENTERING'){
						 showError('只能修改在录入物料的特征');
				    	 return;
					}
					var grid = Ext.getCmp('grid');
    				Ext.each(grid.store.data.items, function(item){
    					if(item.dirty == true){
    						item.set('pf_prodcode', Ext.getCmp('pr_code').value);    						
    					}
    				});
					me.GridUtil.onUpdate(Ext.getCmp('grid'));
				}
			},
			'erpAddButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('pr_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
				click: function(){
					me.FormUtil.onAdd('addProdFeature', '新增BOM物料位号', 'jsps/pm/bom/ProdFeature.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){			
					/*me.FormUtil.beforeClose(me);*/
				}
			},
			'button[id=addFeatrue]':{
				click:function(btn){
			     var value=Ext.getCmp('pr_id').getValue();
			     if(!value) {
			    	 showError('请先选择需要添加的物料');
			    	 return;
			     }
				 var win=new Ext.window.Window({
						id : 'win',
						title: "新增特征明细",
						height: "50%",
						width: "40%",
						maximizable : false,
					    layout: 'anchor',
					    items: {  // Let's put an empty grid in just to illustrate fit layout
					       xtype:'erpFormPanel',
					       id:'baseform',
					       _noc: 1,
					       params:{
					    	  caller:'AddProdFeature', 
					    	  condition:'',
					    	  _noc:1	  
					       },
					       anchor:'100% 100%',
					       saveUrl:'pm/feature/addProdFeature.action?_noc=1'
		    			   }  
					});
				 win.show();
				}
			},
			'dbfindtrigger[name=fe_valuecode]':{
				afterrender:function(t){
					 var  value=Ext.getCmp('pf_fecode').getValue();	
					 if(value){
						 t.dbBaseCondition="fd_code like '%"+value+"'%";
					 }
				}
			   
			},
			'multidbfindtrigger[name=fe_valuecode]':{
				afterrender:function(t){
					 var  value=Ext.getCmp('pf_fecode').getValue();	
					 if(value){
						 t.dbBaseCondition="fd_code like '%"+value+"'%";
					 }
				}
			   
			},
			'field[name=pr_id]': {
				change: function(f){
					if(f.value != null && f.value != ''){
						me.GridUtil.loadNewStore(Ext.getCmp('grid'), {
							caller: caller,
							condition: 'pf_prid=' + f.value
						});
					} else {
						Ext.getCmp('deletebutton').hide();
						Ext.getCmp('updatebutton').hide();
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
        						pf_prodcode :Ext.getCmp('pr_code').value,
            					pf_prid : Ext.getCmp('pr_id').value,
            					pf_detno : d.fd_detno,
            					pf_fecode : d.fd_fecode,
            					pf_fename : d.fd_fename
        				};
        				fpd[index] = da;
        			});
        			Ext.getCmp('grid').store.loadData(fpd);
        		} else {
        			showError('没有可载入的特征');return;
        		}
        	}
		});
	}
});