Ext.QuickTips.init();
Ext.define('erp.controller.drp.distribution.SaleAsk', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','drp.distribution.SaleAsk','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      			'core.button.TurnSale','core.button.End','core.button.ResEnd','core.button.TurnNotify','core.button.FeatureDefinition',
      			'core.button.FeatureView','core.button.OutSchedule',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
    					if(record.data.sd_id != 0 && record.data.sd_id != null && record.data.sd_id != ''){
    						Ext.getCmp('featuredefinition').setDisabled(false);
    						Ext.getCmp('featureview').setDisabled(false);
    						Ext.getCmp('outschedule').setDisabled(false);    						
    					}
    					this.onGridItemClick(selModel, record);
    				}
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
    				btn.ownerCt.add({
    					xtype: 'erpOutScheduleButton'
    				});
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.beforeSaveSale();
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete({id: Number(Ext.getCmp('sa_id').value)});
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.beforeUpdate();
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addSaleAsk', '待确认销售单', 'jsps/drp/distribution/saleAsk.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onEnd(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpResEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'FINISH'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResEnd(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpPrintButton':{
    			click:function(btn){
    				var reportName="salelist";
    				var condition='{Sale.sa_id}='+Ext.getCmp('sa_id').value+'';
    				var id=Ext.getCmp('sa_id').value;
    				me.FormUtil.onwindowsPrint(id,reportName,condition);
    			}
    		},
    		'field[name=sa_statuscode]': {
    			change: function(f){
    				var grid = Ext.getCmp('grid');
    				if(grid && f.value != 'ENTERING' && f.value != 'COMMITED'){
    					grid.setReadOnly(true);//只有未审核的订单，grid才能编辑
    				}
    			}
    		},
    		'erpTurnSaleButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
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
							   		url : basePath + 'common/getId.action?seq=SALE_SEQ',
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
						if(s[i].dirty){
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
				   		url : basePath + 'scm/sale/saveSale.action?caller=Sale',
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
    		'erpFeatureDefinitionButton':{
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var record = grid.selModel.lastSelected;
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
    			    							"jsps/pm/bom/FeatureValueSet.jsp?fromwhere=SaleDetail&condition=formidIS" + record.data.sd_id + ' AND pr_codeIS' + record.data.sd_prodcode + ' AND pr_nameIS' + record.data.pr_detail +'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
    			    						}]
    			    					});
    			    					win.show();    									
    								} else {
    									showError('物料特征必须为虚拟特征件');return;
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
    											"jsps/pm/bom/FeatureValueView.jsp?fromwhere=SaleDetail&condition=formidIS" + record.data.sd_id + ' AND pr_codeIS' + record.data.sd_prodcode + ' AND pr_nameIS' + record.data.pr_detail +'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
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
    		'dbfindtrigger[name=sd_batchcode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var pr = record.data['sd_prodcode'];
    				if(pr == null || pr == ''){
    					showError("请先选择料号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					var id = record.data['sd_warehouseid'];
        				if(id == null || id == ''){
        					id = Ext.getCmp('sd_warehouseid');
        					if(id == null || id == '' || id== 0 ){
        						showError("请先选择仓库!");
            					t.setHideTrigger(true);
            					t.setReadOnly(true);
        					}
        				} else {
        					t.dbBaseCondition = "ba_warehouseid='" + id + "' AND ba_prodcode='" + pr + "'";
        				}
    				}
    			}
    		},
    		'erpOutScheduleButton': {
    			click: function() {
    				var grid = Ext.getCmp('grid'),record = grid.selModel.lastSelected;
    				if(record) {
    					me.schedule(record);
    				}
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getRecordByPrCode: function(){
    	if(this.gridLastSelected && this.gridLastSelected.findable){
    		var data = Ext.getCmp('grid').store.data.items[this.gridLastSelected.index].data;
    		var code = data.pd_prodcode;
    		if(code != null && code!= ''){//看用户输入了编号没有
            	var str = "sd_prodcode='" + code + "'";
            	this.GridUtil.getRecordByCode({caller: 'Sale', condition: str});	
    		}
    	}
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSaveSale: function(){
		var cop = Ext.getCmp('sa_cop');
		if(cop) {
			cop.setValue(en_uu);
		}
		var grid = Ext.getCmp('grid');
		var cust = Ext.getCmp('sa_custid').value;
		if(cust == null || cust == '' || cust == '0' || cust == 0){
			showError('未选择客户，或客户编号无效!');
			return;
		}
		var items = grid.store.data.items;
		var bool = true;
		//数量不能为空或0
		Ext.each(items, function(item){
			item.set('sd_code', Ext.getCmp('sa_code').value);
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['sd_qty'] == null || item.data['sd_qty'] == '' || item.data['sd_qty'] == '0'
					|| item.data['sd_qty'] == 0){
					bool = false;
					showError('明细表第' + item.data['sd_detno'] + '行的数量为空');return;
				}
			}
		});
		/*//销售价格不能为0
		if(Ext.getCmp('sa_getprice').value == 0){//是否自动获取单价
			Ext.each(items, function(item){
				if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
					if(item.data['sd_price'] == null){
						bool = false;
						showError('明细表第' + item.data['sd_detno'] + '行的价格为空');return;
					} else if(item.data['sd_price'] == 0 || item.data['sd_price'] == '0'){
						bool = false;
						showError('明细表第' + item.data['sd_detno'] + '行的价格为0');return;
					}
				}
			});
		}*/
		//物料交货日期不能小于录入日期
		var recorddate = Ext.getCmp('sa_recorddate').value;
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['sd_delivery'] == null){
					item.set('sd_delivery', recorddate);
				} else if(item.data['sd_delivery'] < recorddate){
					bool = false;
					showError('明细表第' + item.data['sd_detno'] + '行的交货日期小于单据录入日期');return;
				}
			}
		});
		//保存sale
		if(bool)
			this.FormUtil.beforeSave(this);
	},
	beforeUpdate: function(){
		var grid = Ext.getCmp('grid');
		var cust = Ext.getCmp('sa_custid').value;
		var items = grid.store.data.items, sacode = Ext.getCmp('sa_code').value;
		var bool = true;
		if(cust == null || cust == '' || cust == '0' || cust == 0){
			showError('未选择客户，或客户编号无效!');
			return;
		}
		//数量不能为空或0
		Ext.each(items, function(item){
			item.set('sd_code',sacode);	  
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['sd_qty'] == null || item.data['sd_qty'] == '' || item.data['sd_qty'] == '0'
					|| item.data['sd_qty'] == 0){
					bool = false;
					showError('明细表第' + item.data['sd_detno'] + '行的数量为空');return;
				}
			}
		});
		//物料交货日期不能小于录入日期
		var recorddate = Ext.getCmp('sa_recorddate').value;
		Ext.each(items, function(item){
			item.set('sd_code',sacode);	  
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['sd_delivery'] == null){
					item.set('sd_delivery', recorddate);
				} else if(item.data['sd_delivery'] < recorddate){
					bool = false;
					showError('明细表第' + item.data['sd_detno'] + '行的交货日期小于单据录入日期');return;
				}
			}
		});
		//保存
		if(bool)
			this.FormUtil.onUpdate(this);
	},
	/**
	 * 排程
	 */
	schedule: function(record) {
		var width = Ext.isIE ? screen.width*0.7*0.9 : '80%',
	   		height = Ext.isIE ? screen.height*0.75 : '100%';
		var sd_id = record.get('sd_id');
		Ext.create('Ext.Window', {
			width: width,
			height: height,
			autoShow: true,
			items: [{
				tag : 'iframe',
				frame : true,
				anchor : '100% 100%',
				layout : 'fit',
				html : '<iframe id="iframe_dbfind" src="' + basePath + 'jsps/scm/sale/saleDetail.jsp?formCondition=sd_id=' 
					+ sd_id + '&gridCondition=sdd_sdid=' + sd_id + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			}]
		});
	}
});