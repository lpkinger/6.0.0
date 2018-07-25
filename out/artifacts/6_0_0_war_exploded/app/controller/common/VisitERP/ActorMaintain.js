Ext.QuickTips.init();
Ext.define('erp.controller.common.VisitERP.ActorMaintain', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'core.form.Panel','common.VisitERP.ActorMaintain','core.grid.Panel2','core.toolbar.Toolbar', 'core.form.MultiField', 
     		'core.button.Save','core.button.Upload','core.button.Close','core.button.Update',
     		'core.button.Add','core.button.DeleteDetail','core.trigger.MultiDbfindTrigger',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger'
     	],
   init:function(){
	   	var me = this;
	   	me.allowinsert = true;
		this.control({
			'erpFormPanel': { 
				afterrender:function(f){
					f.add({
						margin:'5 0 0 0',
						xtype:'combo',
						fieldLabel: '角色权限类型',
						id: 'type',
                		displayField: 'display',
                		valueField: 'value',
                		mode : "local", 
                		editable:false,
                		triggerAction : "all", 
						store: new Ext.data.SimpleStore({ 
	            			 fields : ["display", "value"],
	            			 data :[['采购','A'],
	            			        ['PMC','B'],
	            			        ['研发','C']]
	            	    }),
	            	    listeners:{
	            	    	change:function(f){
	            	    		var grid = Ext.getCmp('grid');
	            	    		var gridParam = {};
	            	    		gridParam.caller = 'ActorMaintain';
	            	    		gridParam.condition = "rp_code = '"+f.value+"'";
								grid.setLoading(true);
								Ext.Ajax.request({//拿到grid的columns
						        	url : basePath + 'common/singleGridPanel.action',
						        	params: gridParam,
						        	async: false,
						        	method : 'post',
						        	callback : function(options,success,response){
						        		grid.setLoading(false);
						        		if (!response) return;
						        		var res = new Ext.decode(response.responseText);
						        		if(res.exceptionInfo){
						        			showError(res.exceptionInfo);return;
						        		}
					        			//data
					            		var data = [];
					            		if(!res.data || res.data.length == 2){
					            			grid.store.removeAll();
					            			var detno = grid.detno;
					            			if(detno){
												var index = data.length == 0 ? 0 : Number(data[data.length-1][detno]);
												for(var i=0;i<20;i++){
													var o = new Object();
													o[detno] = index + i + 1;
													data.push(o);
												}
											} else {
												for(var i=0;i<20;i++){
													var o = new Object();
													data.push(o);
												}
											}
					            		} else {
					            			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
					            			grid.store.removeAll();
					            		}
				            			grid.store.loadData(data);
						        	}
						        });
	            	    	}
	            	    }
					});
				}
			},
		    'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'erpUpdateButton': {
				click: function(btn){
					var type = Ext.getCmp('type');
					var form = Ext.getCmp('form');
					var grid = Ext.getCmp('grid');
					if(type.value){
						//form里面数据
						var r = form.getValues();
						Ext.each(form.items.items, function(item){
							if(item.xtype == 'itemgrid'){					
								if(item.value != null && item.value != ''){
									r[item.name]=item.value;
								}
							}
						});
						r['type'] = type.value;
						var param = grid.GridUtil.getGridStore();
						param = unescape("[" + param.toString() + "]");
						params = new Object();
						params.formStore = unescape(escape(Ext.JSON.encode(r)));
						param = Ext.decode(param);
						Ext.Array.each(param, function(item){
							item.rp_code = type.value;
						});
						params.param = Ext.JSON.encode(param);
						var form = Ext.getCmp('form'), url = form.updateUrl;
						grid.setLoading(true);//loading...
						Ext.Ajax.request({
							url : basePath + url,
							params: params,
							method : 'post',
							callback : function(options,success,response){
								var grid = Ext.getCmp('grid');
								var type = Ext.getCmp('type');
								grid.setLoading(false);
								var localJson = new Ext.decode(response.responseText);
								if(localJson.success){
									showMessage('提示', '保存成功!', 1000);
									//update成功后刷新页面进入可编辑的页面
									var u = String(window.location.href);
									if (u.indexOf('type') == -1) {
										window.location.href = window.location.href + '?type=' + type.value ;
									} else {
										window.location.href = u.split('?')[0] + '?type=' + type.value ;
									}
								} else if(localJson.exceptionInfo){
									var str = localJson.exceptionInfo;
									if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
										str = str.replace('AFTERSUCCESS', '');
										//update成功后刷新页面进入可编辑的页面 
										var u = String(window.location.href);
										if (u.indexOf('formCondition') == -1) {
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
										} else {
											window.location.reload();
										}
									}
									showError(str);return;
								} else {
									updateFailure();
								}
							}
						});
					}else{
						showError('请选择类型后再进行操作');
					}
				}
			},    		
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				},
				afterrender: function(g){
					setTimeout(function() {
                        if(getUrlParam('type')){
							Ext.getCmp('type').setValue(getUrlParam('type'));
						}
                    }, 500);
					
				}
			},
			'dbfindtrigger[name=rp_cnid]': {
				aftertrigger: function(db,row){
				}
			}
		});
	}, 
	onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});