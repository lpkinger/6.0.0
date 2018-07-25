Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.TenderEstimate', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'scm.purchase.TenderEstimate','scm.purchase.TenderEstimateFormPanel','core.button.Submit','core.button.Audit','core.button.Save',
    		'core.button.Close','core.button.Delete','core.button.ResAudit','core.button.ResSubmit','core.trigger.TextAreaTrigger','core.form.FileField2'
    ],
    init:function(){
    	var me = this;
    	this.control({ 
    		'#content': {
    			afterrender: function(panel){
    				Ext.Ajax.request({
			        	url : basePath + 'scm/purchase/getTenderEstimate.action',
			        	params: {
			        		id:id
			        	},
			        	method : 'post',
			        	callback : function(options,success,response){
			        		var res = new Ext.decode(response.responseText);
			        		if(res.exception || res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        			return;
			        		}else{
			        			var main = parent.Ext.getCmp("content-panel");
								if(main){
									var panel1 = main.getActiveTab(); 
									if(panel1&&panel1.title.substring(0,3)=='招标单'){
										code = res.purchaseTender.code;
										panel1.tabConfig.tooltip = '评标单('+code+')';
										panel1.setTitle('评标单('+code+')');
									}
								}
								turns = res.turns;
								var form = panel.ownerCt.down('form');
				        		Ext.Array.each(form.items.items,function(field){
				        			if(typeof(field.setValue)=='function'){
				        				field.setValue(res.purchaseTender[field.name]);
				        			}
				        			if(field.xtype=='mfilefield2'){
			        					field.items.items[0].resetOriginalValue();
			        				}else if(typeof(field.resetOriginalValue)=='function'){
			        					field.resetOriginalValue();
			        				}
				        		});
				        		status = res.purchaseTender['pt_statuscode'];
				        		var status1 = res.purchaseTender['status'];
				        		overdue = res.purchaseTender['overdue'];
				        		var result = res.purchaseTender['result'];
				        		if (overdue==1) {
									var open = Ext.getCmp('open');
									if(open){
										open.hide();
									}
								}
								if (overdue&&!result&&status == 'ENTERING'&&status1!='流标') {
									var save = Ext.getCmp('save');
				        			var submit = Ext.getCmp('submit');
				        			if(save){
				        				save.show();
				        			}
				        			if(submit){
				        				submit.show();
				        			}
								}
								if (overdue&&!result &&status == 'COMMITED'&&status1!='流标') {
									var resSubmit = Ext.getCmp('resSubmit');
				        			var audit = Ext.getCmp('auditbutton');
				        			var save = Ext.getCmp('save');
				        			var search = window.location.search;
				        			if(save&&contains(search, 'datalistId', true)){
				        				save.show();
				        			}
				        			if(resSubmit){
										resSubmit.show();}
									if(audit){
										audit.show();
									}
								}
								if(turns&&turns.length>0){
									var turnPurchase = Ext.getCmp('turnPurchase');
									if(turnPurchase){
										turnPurchase.show();
									}
								}
								if(res.purchaseTender.ifAll&&res.purchaseTender.ifAll==1){
									Ext.Array.sort(res.prods,function(prod1,prod2){
										if(prod1.index>prod2.index){
											return 1;
										}else{
											return -1;
										}
									});
									panel.add({
											xtype: 'grid',
											id: 'saleTendorGrid',
											columnLines:true,
											emptyText:'暂无企业投标',
											width: 912,
											style:{
												margin:'16px 0 16px 16px'
											},
											plugins: [
										        Ext.create('Ext.grid.plugin.CellEditing', {
										            clicksToEdit: 1
										        })
										    ],
										    store:Ext.create('Ext.data.Store',{
										    	fields:['saleTenderId','enName','vendUU','cycle',{name:'totalMoney',convert: function(value, record) {
													if(value){
														return Math.round(value*100)/100;
													}
									            }},'taxrate',{name:'applyStatus',defaultValue:0},'reason'],
										    	data:res.vendors
										    }),
										    listeners:{
										    	beforeedit:function(editor, e, eOpts ){
										    		var record = editor.record;
										    		if(!record.data.totalMoney||!record.data.taxrate||!record.data.cycle){
										    			return false;
										    		}
										    		if(overdue==0){
										    			return false;
										    		}
										    	}
										    },
											columns:{
												defaults:{
													align:'center'
												},
												items:[{
													header:'ID',
													dataIndex:'saleTenderId',
													hidden:true,
													width:300
												},{
													header:'投标供应商',
													dataIndex:'enName',
													logic:'ignore',
													width:300,
													renderer:function(val, meta, record, x, y, store, view){
													 	meta.style="padding-right:0px!important";
													 	meta.tdAttr = 'data-qtip="' + Ext.String.htmlEncode(val) + '"';  
													 	if(val&&overdue){
													 		return  '<a href="javascript:openUrl(\'jsps/scm/sale/tenderSubmission.jsp?formCondition=readOnlyidEQ' + record.data['saleTenderId'] + '\')">' + Ext.String.htmlEncode(val) + '</a>';
													 	}
													 	return val;
												 	}
												},{
													header:'投标供应商UU',
													dataIndex:'vendUU',
													logic:'ignore',
													hidden:true,
													width:300
												},{
													header:'税率(%)',
													dataIndex:'taxrate',
													xtype:'numbercolumn',
													width:80,
													logic:'ignore',
													renderer:function(val, meta, record, x, y, store, view){
													 	if(overdue==0){
													 		return '*'
													 	}
													 	return val;
												 	}
												},{
													header:'合计金额(含税)',
													dataIndex:'totalMoney',
													align:'right',
													logic:'ignore',
													xtype:'numbercolumn',
													width:140,
													renderer:function(val, meta, record, x, y, store, view){
													 	if(overdue==0){
													 		return '*'
													 	}
													 	return val;
												 	}
												},{
													header:'周期(天数)',
													dataIndex:'cycle',
													xtype:'numbercolumn',
													logic:'ignore',
													width:80,
													renderer:function(val, meta, record, x, y, store, view){
													 	if(overdue==0){
													 		return '*'
													 	}
													 	return val;
												 	}
												},{
													header:'说明',
													align:'left',
													dataIndex:'reason',
													width:250,
													editor:{
														xtype:'textareatrigger'
													}
												},{
													xtype: 'checkcolumn',
													text: '定标',
													width: 60,
													dataIndex: 'applyStatus'
												}]
											}
				    					});
				    					panel.add({
											xtype: 'grid',
											id: 'prodGrid',
											title: '项目清单',
											width: 917,
											columnLines:true,
											style:{
												margin:'16px 0 16px 16px'
											},
										    store:Ext.create('Ext.data.Store',{
										    	fields:['id','index','prodTitle','prodCode','brand','unit','qty','prodSpec'],
										    	data:res.prods
										    }),
											columns:{
												defaults:{
													align:'center'
												},
												items:[{
													header:'ID',
													dataIndex:'id',
													hidden:true
												},{
													header:'编号',
													xtype:'numbercolumn',
													dataIndex:'index',
													width:65
												},{
													header:'项目名称',
													align:'left',
													dataIndex:'prodTitle',
													width:150
												},{
													header:'项目描述',
													align:'left',
													dataIndex:'prodCode',
													width:400,
													renderer:function(val, meta, record, x, y, store, view){
													 	var grid = view.ownerCt,column = grid.columns[y];
													 	meta.style="padding-right:0px!important";
													 	if(val){
													 		return  '<span style="display:inline-block;padding-left:2px;width:94%; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;">'+Ext.String.htmlEncode(val)+'</span>'+
													 				'<span><img src="'+basePath+'resource/images/renderer/texttrigger.png" style="display: inline; float: right;"' +
													 				'onClick="showTrigger(\'' + grid.id + '\',\''+escape(val)+'\',\''+column.dataIndex+'\','+x+','+y+');"></span>';
													 	
													 	}
													 	return '';
													}
												},{
													header:'品牌',
													dataIndex:'brand',
													width:100
												},{
													header:'单位',
													dataIndex:'unit',
													width:100
												},{
													header:'数量',
													align:'right',
													xtype: 'numbercolumn',
													dataIndex:'qty',
													width:100
												},{
													header: '规格',
													width: 120,
													dataIndex: 'prodSpec',
													hidden:true
												}]
											}
				    					});
								}else{
									Ext.Array.sort(res.purchaseTender.purchaseTenderProds,function(prod1,prod2){
										if(prod1.index>prod2.index){
											return 1;
										}else{
											return -1;
										}
									});
				        			Ext.Array.each(res.purchaseTender.purchaseTenderProds,function(prod,index){
				        				var prefix = prod.id;
				        				panel.add({
											xtype:'tbtext',
											id:prefix,
											name:prefix,
											width:1046,
											text:'<i>'+(index+1)+'</i><div style="line-height:27px;><span id="prodCode"><lable>产品型号 ：</lable><text>'+(prod.prodCode==null?'':prod.prodCode)+'</text></span><span id="prodTitle" style="width:30%;float:right;"><lable>产品名称：</lable><text>'+(prod.prodTitle==null?'':prod.prodTitle)+'</text></span><span id="brand" style="width:40%;float:right;"><lable>品牌：</lable><text>'+(prod.brand==null?'':prod.brand)+'</text></span></div>'+
											'<div style="line-height:27px;><span id="qty"><lable>采购数量：</lable><text>'+(prod.qty==null?0:prod.qty)+'</text></span></span><span id="unit" style="width:70%;float:right"><lable>单位：</lable><text>'+(prod.unit==null?'':prod.unit)+'</text></span></div>',
											style:{
												fontSize:'14px',
												fontWeight:'bold',
												padding:'2px 0px 0px 19px'
											}
										});
										var datas = new Array();
										Ext.Array.each(prod.saleTenderItems,function(item){
											var data = new Object();
											data = item;
											if(!item.price||!item.taxrate||!item.cycle){
												data.price = 0;
												data.taxrate = 0;
												data.cycle = 0;
												data.amount = 0;
											}else{
												if(!Ext.isEmpty(item.price)&&!Ext.isEmpty(prod.qty)){
													data.amount = prod.qty*item.price;
												}
											}
											datas.push(data);
										});
										panel.add({
											xtype:'grid',
											id:prefix+'roleGrid',
											collapsible:true,
											collapsed:false,
											columnLines:true,
											emptyText:'暂无企业投标',
											width:1012,
											style:{
												margin:'16px 0 16px 16px'
											},
											plugins: [
										        Ext.create('Ext.grid.plugin.CellEditing', {
										            clicksToEdit: 1
										        })
										    ],
										    store:Ext.create('Ext.data.Store',{
										    	fields:['id','enName','vendUU','cycle',{name:'price',convert: function(value, record) {
													if(value){
														return Math.round(value*10000)/10000;
													}
									            }},{name:'amount',convert: function(value, record) {
													if(value){
														return Math.round(value*100)/100;
													}
									            }},'taxrate',{name:'applyStatus',defaultValue:0},'description','saleId'],
										    	data:datas
										    }),
										    listeners:{
										    	beforeedit:function(editor, e, eOpts ){
										    		var record = editor.record;
										    		if(!record.data.price||!record.data.taxrate||!record.data.cycle){
										    			return false;
										    		}
										    		if(overdue==0){
										    			return false;
										    		}
										    	}
										    },
											columns:{
												defaults:{
													align:'center'
												},
												items:[{
													header:'ID',
													dataIndex:'id',
													hidden:true,
													width:300
												},{
													header:'投标供应商',
													dataIndex:'enName',
													logic:'ignore',
													width:300,
													renderer:function(val, meta, record, x, y, store, view){
													 	meta.style="padding-right:0px!important";
													 	meta.tdAttr = 'data-qtip="' + Ext.String.htmlEncode(val) + '"';  
													 	if(val&&overdue){
													 		return  '<a href="javascript:openUrl(\'jsps/scm/sale/tenderSubmission.jsp?formCondition=readOnlyidEQ' + record.data['saleId'] + '\')">' + Ext.String.htmlEncode(val) + '</a>';
													 	}
													 	return val;
												 	}
												},{
													header:'投标供应商UU',
													dataIndex:'vendUU',
													hidden:true,
													logic:'ignore',
													width:300
												},{
													header:'税率(%)',
													dataIndex:'taxrate',
													xtype:'numbercolumn',
													width:100,
													renderer:function(val, meta, record, x, y, store, view){
													 	if(overdue==0){
													 		return '*'
													 	}
													 	return val;
												 	}
												},{
													header:'单价',
													dataIndex:'price',
													xtype:'numbercolumn',
													width:100,
													renderer:function(val, meta, record, x, y, store, view){
													 	if(overdue==0){
													 		return '*'
													 	}
													 	return val;
												 	}
												},{
													header:'含税金额',
													align:'right',
													dataIndex:'amount',
													logic:'ignore',
													xtype:'numbercolumn',
													width:100,
													renderer:function(val, meta, record, x, y, store, view){
													 	if(overdue==0){
													 		return '*'
													 	}
													 	return val;
												 	}
												},{
													header:'采购周期',
													dataIndex:'cycle',
													xtype:'numbercolumn',
													width:100,
													renderer:function(val, meta, record, x, y, store, view){
													 	if(overdue==0){
													 		return '*'
													 	}
													 	return val;
												 	}
												},{
													header:'说明',
													align:'left',
													dataIndex:'description',
													width:250,
													editor:{
														xtype:'textareatrigger'
													}
												},{
													xtype: 'checkcolumn',
													text: '定标',
													width: 60,
													dataIndex: 'applyStatus'
												}]
											}
				    					});
				        			});
				        		}
				        	}
			        	}
			        });
    			}
    		},
    		'erpSaveButton' : {
				click : function(btn) {
					var form = me.getForm(btn);
					me.beforeSave(form);
				}
			},
			'erpSubmitButton' : {
				click : function(btn) {
					var form = me.getForm(btn);
					me.onSubmit(form,'scm/purchase/submitEstimateTender.action');
				}
			},
			'erpResSubmitButton' : {
				click : function(btn) {
					me.submit('scm/purchase/resSubmitEstimateTender.action',2);
				}
			},
			'erpAuditButton' : {
				click : function(btn) {
					me.submit('scm/purchase/auditEstimateTender.action',3);
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'#audithistory' : {
				click : function(btn) {
					me.auditHistory();
				}
			},
			'#turnPurchase': {
				click : function(btn) {
					me.createWindow();
				}
			}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSave: function(form,s,param1){
		var me = this,field = Ext.getCmp(form.keyField);
		field.originalValue = field.value;
		var params = new Object();
		if(!s){
			var s = me.FormUtil.checkFormDirty(form);
			var param1 = new Array();
		}
		
		var ifAll = Ext.getCmp('ifAll').value;
		var param = new Array();
		if(ifAll&&ifAll==1){
			var grid = Ext.getCmp('saleTendorGrid');
			param = me.getAllGridStore(grid);
			if(Ext.isEmpty(s)&&param1.length<1){
				param1 = me.GridUtil.getGridStore(grid);
			}
		}else{
			var grids = Ext.ComponentQuery.query('gridpanel');
			Ext.Array.each(grids,function(grid){
				var data = me.getAllGridStore(grid);
				param = Ext.Array.merge(data,param,param);
				if(Ext.isEmpty(s)&&param1.length<1){
					var data1 = me.GridUtil.getGridStore(grid);
					param1 = Ext.Array.merge(data1,param1,param1);
				}
			});
		}
		
		if(Ext.isEmpty(s)&&param1.length<1){
			showError('未修改数据！');
			return;
		}
		
		var formStore = me.getFormStore(form);
		params.formStore=unescape(escape(Ext.JSON.encode(formStore)));
		params.param = unescape("[" + param.toString() + "]");
		params.caller = caller;
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'scm/purchase/saveEstimateTender.action',
			params : params,
			method : 'post',
			callback : function(options,success,response){
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					saveSuccess(function(){
						window.location.reload();
					});
				} else if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
					return;
				} else{
					saveFailure();
				}
			}
		});
	},
	getFormStore:function(form){
		var r = form.getValues();
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, '-', true) && !contains(k,'-new',true)){
				delete r[k];
			}
			f = form.down('#' + k);
			if(f && f.logic == 'ignore') {
				delete r[k];
			}
			if(f && f.xtype == 'checkboxgroup'){
				var values = '';
				Ext.Array.each(r[f.name],function(value){
					if(value!='0'){
						values +=','+value;
					}
				});
				r[f.name] = values.substring(1);
			}
		});
		return r;
	},//提交系列操作
	onSubmit: function(form,url){
		var me = this;
		var field = Ext.getCmp(form.keyField);
		field.originalValue = field.value;
		var params = new Object();
		var s = me.FormUtil.checkFormDirty(form);
		var ifAll = Ext.getCmp('ifAll').value;
		var param = new Array();
		if(ifAll&&ifAll==1){
			var grid = Ext.getCmp('saleTendorGrid');
			param = me.GridUtil.getGridStore(grid);
		}else{
			var grids = Ext.ComponentQuery.query('gridpanel');
			Ext.Array.each(grids,function(grid){
				var data = me.GridUtil.getGridStore(grid);
				param = Ext.Array.merge(data,param,param);
			});
		}
		if(!Ext.isEmpty(s)||param.length>0){
			Ext.MessageBox.show({
				title:'保存修改?',
				msg: '该单据已被修改,提交前要先保存吗？',
				buttons: Ext.Msg.YESNOCANCEL,
				icon: Ext.Msg.WARNING,
				fn: function(btn){
					if(btn == 'yes'){
						me.beforeSave(form,s,param);
					} else if(btn == 'no'){
						me.submit(url,1);	
					} else {
						return;
					}
				}
			});
		}else{
			me.submit(url,1);
		}
	},
	submit:function(url,submit){
		var me = this;
		Ext.Ajax.request({
			url : basePath + url,
			params : {id:id,caller:caller},
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					if(submit==1){
						me.FormUtil.getMultiAssigns(id, caller,form);
					}else{
						var str = '';
						if(submit==2){
							str = '反提交成功!';
						}else if(submit==3){
							str = '定标成功!';
						}
						Ext.Msg.alert('提示', str, function(){
							window.location.reload();
						});
					}
					
				} else if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
					return;
				} 
			}
		});
	},
	getAllGridStore: function(grid){
		if(grid!=null){
			var GridData = new Array();
			var s = grid.getStore().data.items;//获取store里面的数据
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				dd = new Object();
				Ext.each(grid.columns, function(c){
					if(c.logic != 'ignore' && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
						if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
							if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
								dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
							} else {
								dd[c.dataIndex] = "" + s[i].data[c.dataIndex];
							}
						}else if(c.xtype == 'checkcolumn'){
							if(data[c.dataIndex] != null&&(data[c.dataIndex]==1||data[c.dataIndex])){
								dd[c.dataIndex] = 1;
							}else{
								dd[c.dataIndex] = 0;
							}
						}else {
							dd[c.dataIndex] = s[i].data[c.dataIndex];
						}
						if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
							dd[c.dataIndex] = c.defaultValue;
						}
					}
				});
				GridData.push(Ext.JSON.encode(dd));
			}
		}
		return GridData;
	},
	createWindow: function(){
		var me = this;
		Ext.create('Ext.window.Window', {
			title : '转采购合同',
			height : "95%",
			width : "90%",
			closeAction : 'destroy',
			maximizable : true,
			modal : true,
			buttonAlign : 'center',
			layout : 'fit',
			items : [{
				xtype:'gridpanel',
				id: 'turnPurchaseGrid',
				columnLines:true,
				selModel: Ext.create('Ext.selection.CheckboxModel'),
			    store:Ext.create('Ext.data.Store',{
			    	fields:['enName','vendUU','prodTitle','prodCode','brand','unit','prodSpec','currency','qty','cycle','taxrate',{name:'price',convert: function(value, record) {
								if(value){
									return Math.round(value*10000)/10000;
								}
				            }},{name:'amount',convert: function(value, record) {
								if(value){
									return Math.round(value*100)/100;
								}
				            }},'description'],
			    	data:turns
			    }),
				columns:{
					defaults:{
						align:'center'
					},
					items:[{
						header:'中标公司',
						dataIndex:'enName',
						width:200
					},{
						header:'中标公司UU',
						dataIndex:'vendUU',
						hidden:true
					},{
						header:'项目名称',
						align:'left',
						dataIndex:'prodTitle',
						width:150
					},{
						header:'项目描述',
						align:'left',
						dataIndex:'prodCode',
						width:250,
						renderer:function(val, meta, record, x, y, store, view){
						 	var grid = view.ownerCt,column = grid.columns[y];
						 	meta.style="padding-right:0px!important";
						 	if(val){
						 		return  '<span style="display:inline-block;padding-left:2px;width:95%; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;">'+Ext.String.htmlEncode(val)+'</span>'+
						 				'<span><img src="'+basePath+'resource/images/renderer/texttrigger.png" style="display: inline; float: right;"' +
						 				'onClick="showTrigger(\'' + grid.id + '\',\''+escape(val)+'\',\''+column.dataIndex+'\','+x+','+y+');"></span>';
						 	
						 	}
						 	return '';
						}
					},{
						header:'品牌',
						dataIndex:'brand',
						width:100
					},{
						header:'单位',
						dataIndex:'unit',
						width:100
					},{
						header:'币种',
						dataIndex:'currency',
						width:60
					},{
						header:'数量',
						align:'right',
						xtype: 'numbercolumn',
						dataIndex:'qty',
						width:100
					},{
						header: '规格',
						width: 120,
						dataIndex: 'prodSpec',
						hidden:true
					},{
						header:'采购周期',
						xtype: 'numbercolumn',
						dataIndex:'cycle',
						width:80
					},{
						header:'税率(%)',
						dataIndex:'taxrate',
						xtype:'numbercolumn',
						width:80
					},{
						header:'单价',
						align:'right',
						dataIndex:'price',
						format:'0.0000',
						xtype:'numbercolumn',
						width:100
					},{
						header:'含税金额',
						align:'right',
						dataIndex:'amount',
						format:'0.00',
						xtype:'numbercolumn',
						width:100
					},{
						header:'说明',
						align:'left',
						dataIndex:'description',
						width:200,
						renderer:function(val, meta, record, x, y, store, view){
						 	var grid = view.ownerCt,column = grid.columns[y];
						 	meta.style="padding-right:0px!important";
						 	if(val){
						 		return  '<span style="display:inline-block;padding-left:2px;width:95%; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;">'+Ext.String.htmlEncode(val)+'</span>'+
						 				'<span><img src="'+basePath+'resource/images/renderer/texttrigger.png" style="display: inline; float: right;"' +
						 				'onClick="showTrigger(\'' + grid.id + '\',\''+escape(val)+'\',\''+column.dataIndex+'\','+x+','+y+');"></span>';
						 	
						 	}
						 	return '';
						}
					}]
				}
			}],
			buttons : [{
				text : $I18N.common.button.erpConfirmButton,
				iconCls : 'x-button-icon-save',
				cls : 'x-btn-gray',
				style : 'margin-left:20px;',
				handler : function(btn) {
					var grid = btn.ownerCt.ownerCt.down('grid'),params = new Object();
					var selects = grid.getSelectionModel().selected;
					var datas = new Array(),venduus = new Array();
					selects.each(function(item,index,length){
						datas.push(Ext.JSON.encode(item.data));
						venduus.push(item.data.vendUU);
					});
					venduus = Ext.Array.unique(venduus);
					var formStore = {
						'id':Ext.getCmp('id').value,
						'code':Ext.getCmp('code').value,
						'currency':Ext.getCmp('currency').value,
						'shipAddress':Ext.getCmp('shipAddress').value
					};
					params.fromStore = unescape(escape(Ext.JSON.encode(formStore)));
					params.caller = caller;
					params.param = unescape("[" + datas.toString() + "]");
					params.vendUUs = unescape("[" + venduus.toString() + "]");
					
					me.FormUtil.setLoading(true);
					Ext.Ajax.request({
						url : basePath + 'scm/purchase/tenderTurnPurchase.action',
						params : params,
						method : 'post',
						callback : function(options,success,response){
							me.FormUtil.setLoading(false);
							var localJson = new Ext.decode(response.responseText);
							if(localJson.success){
								showMessage('提示',localJson.msg);
								btn.ownerCt.ownerCt.close();
							} else if(localJson.exceptionInfo){
								showError(localJson.exceptionInfo);
								return;
							}
						}
					});
				}
			}, {
				text : $I18N.common.button.erpCloseButton,
				iconCls : 'x-button-icon-close',
				cls : 'x-btn-gray',
				style : 'margin-left:20px;',
				handler : function(btn) {
					btn.ownerCt.ownerCt.close();
				}
			}]
		}).show();
	},
	auditHistory : function(){
		var me = this;
		var finds = '[{caller:Tender,keyValue:'+id+'},{caller:'+caller+',keyValue:'+id+'}]';
		Ext.Ajax.request({
			url : basePath + 'scm/purchase/getJProcessByForm.action',
			params: {
				finds: finds,
				_noc:1
			},
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);return;
				}else{
					Ext.create('Ext.window.Window', {
						title : "审批记录",
						height : "90%",
						width : "90%",
						id:'win',
						closeAction : 'destroy',
						maximizable : true,
						modal : true,
						layout : 'fit',
						items : [{
							xtype: 'tabpanel',
							id:'tab',
							items:[
								Ext.create("erp.view.common.JProcess.AllHistoryGridPanel",{
									title: '招标单',
									autoScroll : true,
									id: 'tenderhistory',
									plugins: null,
									nodeId: localJson.Tender.node
								}),
								Ext.create("erp.view.common.JProcess.AllHistoryGridPanel",{
									title: '评标单',
									autoScroll : true,
									id: 'TenderEstimatehistory',
									plugins: null,
									nodeId: localJson[caller].node
								})
							]
						}],
						buttonAlign : 'center',
						buttons : [{
							text : '关  闭',
							iconCls: 'x-button-icon-close',
							cls: 'x-btn-gray',
							handler : function(btn){
								btn.ownerCt.ownerCt.close();
							}
						}]
					}).show();
				}
			}
		});
		
	}
});