Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.Dispatch', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.make.Dispatch','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.button.ResSubmit','core.button.Scan','core.button.CopyAll','core.trigger.MultiDbfindTrigger2',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.LoadProcess'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			},
    			afterrender : function(grid){
    				//did_stepcode --->工序编号
    				grid.plugins[0].on('beforeedit',function(e){
    					if(e.field == 'did_stepcode'){  
    						var record = e.record;
    						var column = e.column;
    						var trigger = column.editor?column.editor:column.field;
    						//如果did_craftcode ---->工艺路线
    						if(record.data.hasOwnProperty('did_craftcode')){
								var did_craftcode = record.data['did_craftcode'];
								trigger.dbBaseCondition = "cd_crid = (select cr_id from craft where cr_code = '"+did_craftcode+"')";
    						}
    					}
    				});
    			}
    		},
    		'erpLoadProcessButton':{
    			afterrender: function(btn){
					var status = Ext.getCmp('di_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var id = Ext.getCmp('di_id').value;
    				var makecode = Ext.getCmp('di_makecode').value;
    				if(makecode!=''){
    					var grid = Ext.getCmp('grid');
    					var url = 'pm/make/selectDispatchDetail.action';
    					Ext.Ajax.request({//拿到form的items
							url : basePath + url,
							params: {
								makecode : makecode,
								id : id
							},
							method : 'post',
							callback : function(options, success, response){
								var localJson = new Ext.decode(response.responseText);
								if(localJson.exceptionInfo){
									showError(localJson.exceptionInfo);return;
								}else{
								    grid.GridUtil.loadNewStore(grid, {
	                                    caller: caller,
	                                    condition: "did_diid="+id
	                                });
							    }
							}
						});
    				}else{
    					showError("制造单号为空，请填写制造单号！");
    				}
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					me.setDetailEmcode();
					me.FormUtil.beforeSave(this);
					//me.beforeSaveUpdateSubmit('save');
//					if(r){
//						this.FormUtil.beforeSave(this);
//					}
				
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('di_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					me.setDetailEmcode();
					me.FormUtil.onUpdate(this);
					//me.beforeSaveUpdateSubmit('update');
//					console.log(r);
//					if(r){
//						this.FormUtil.onUpdate(this);
//					}
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addDispatch', '新增生产日报', 'jsps/pm/make/dispatch.jsp?whoami=' + caller);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('di_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('di_id').value);
					//me.beforeSaveUpdateSubmit('submit');
//					if(r){
//						me.FormUtil.onSubmit(Ext.getCmp('di_id').value);
//					}
				}
			},
			'dbfindtrigger[name=did_stepcode]': {
                focus: function(t) {
                	t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var grid=Ext.getCmp('grid');
    				var dbfind='';
					Ext.Array.each(grid.columns,function(column) {
						if (column.dataIndex == 'did_stepcode' ) {
							dbfind=column.dbfind.split('|')[0];
						}
					});
					if(dbfind=='Ration'){
						var record = Ext.getCmp('grid').selModel.lastSelected;
	    				var code = record.data['did_prodcode'];
	    				if(code == null || code == ''){
	    					showError("请先选择关联单号!");
	    					t.setHideTrigger(true);
	    					t.setReadOnly(true);
	    				} else {    					
	    					t.dbBaseCondition = "ra_topmothercode='" + code + "'";    					
	    				}
					}
    			}
            },
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('di_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('di_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('di_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('di_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('di_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('di_id').value);
				}
			},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('di_id').value);
				}
			},
			'erpCopyButton': {
				click:function(btn){
					warnMsg("确定复制?", function(btn){
						if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/make/copyDispatch.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('di_id').getValue()
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				turnSuccess(function(){
    	    		    					var id = localJson.id;
    	    		    					var url = "jsps/pm/make/dispatch.jsp?whoami=Dispatch!Base&formCondition=di_id=" + id + "&gridCondition=did_diid=" + id;
    	    		    					me.FormUtil.onAdd('Dispatch' + id, '生产日报' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
					});
				}
			},
			'dbfindtrigger[name=did_scrapreason]': {
				focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				t.dbBaseCondition = "nr_caller=\'Dispatch\' and nr_kind=\'报废原因\'";
				}
    		},
    		'dbfindtrigger[name=did_stopreason]': {
				focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				t.dbBaseCondition = "nr_caller=\'Dispatch\' and nr_kind=\'停线原因\'";
				}
    		},
    		'field[name=did_jobcode]': {
				focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').store.first();
    				if(!record || !record.data['did_makecode']) {
    					showError("请先选择制造单!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "mc_makecode='"+record.data['did_makecode']+"'";
    				}
				}
    		}
		});
	}, 
	
	beforeSaveUpdateSubmit:function(type){
		var me = this;
		var grid = Ext.getCmp('grid');
		var o = new Object();
		var b = true;    //true的时候   没有重复数据
		
		Ext.each(grid.getStore().data.items,function(item,index){
			if(!me.GridUtil.isBlank(grid,item.data)){
//				item.data['did_devicecode'];                          //流程单号
//				item.data['did_stepcode'];								//工序编号
				var search_code = item.data['did_devicecode'] + item.data['did_stepcode'];
				if(!o.hasOwnProperty(search_code)){
					o[search_code] = true;	
					//这个合成编号不存在
				}else {
					//这个合成编号存在   返回单号重复录入提示 
					//b == false 存在重复数据
					b = false;
					return;
				}
			}
		});
		if(!b){
			showError('不能存在流程单号重复,并且工序编号重复的明细行!');
		}else{
			
			if (type == 'save'){
				me.FormUtil.beforeSave(me);
			} else if(type == 'update'){
				me.FormUtil.onUpdate(me);
			} else if(type == 'submit'){
				me.FormUtil.onSubmit(Ext.getCmp('di_id').value);
			}
		}
	},
	
	onGridItemClick: function(selModel, record){//grid行选择
	    this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	setDetailEmcode: function(){ 
		var grid = Ext.getCmp('grid');
		var firstItem = grid.store.getAt(0);
    	if(firstItem) {
    		var emcode = firstItem.get('did_emcode'), macode = firstItem.get('did_makecode'),
    			processtype = firstItem.get('did_processtype'),st_code= firstItem.get('did_stepcode') ,
    			st_name = firstItem.get('did_stepname'),price = firstItem.get('did_price'),
    			did_processtype = firstItem.get('did_processtype');
    			grid.store.each(function(item){  //如果加工人不为空，数据未填写，自动将第一行数据覆盖
	    			if(!Ext.isEmpty(item.get('did_emcode'))){
	    				if(Ext.isEmpty(item.get('did_makecode'))) {
		    				item.set('did_makecode', macode);
		    			}
		    			if(Ext.isEmpty(item.get('did_stepcode'))) {
		    				item.set('did_stepcode', st_code);
		    				item.set('did_stepname', st_name);
		    			}
		    			if(Ext.isEmpty(item.get('did_processtype'))) {
		    				item.set('did_processtype', processtype);
		    			}
		    			
		    			if(Ext.isEmpty(item.get('did_price'))) {
		    				item.set('did_price', price);
		    			}
	    			}
	    			//如果计价类型为空，取第一行的计价类型
	    			if(Ext.isEmpty(item.get('did_processtype'))){
	    				item.set('did_processtype', did_processtype);
	    			}
    		});
    	}
    	
		if(Ext.getCmp('di_emcode')){   	//di_emcode ---->员工编号
			var grid = Ext.getCmp('grid'), items = grid.store.data.items; 
			var emcode = Ext.getCmp('di_emcode'), emname = Ext.getCmp('di_emname');   //员工编号  员工姓名
			if(emcode && emname) {
				Ext.Array.each(items, function(item){
			    	if(!Ext.isEmpty(item.data['did_makecode'])){
			    		item.set('pd_whcode', emcode.value);
			    		item.set('pd_whname', emname.value);
					}
				});
			}
		}
	}
	
});