Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.Product', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','scm.product.Product','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print','core.button.TurnCustomer',
			'core.button.Upload','core.button.Update','core.button.FeatureDefinition','core.button.FeatureView','core.button.Delete','core.button.ResAudit','core.button.ForBidden',
			'core.button.ResForBidden','core.button.Banned','core.button.ResBanned','core.button.CopyAll','core.button.ProductStatus','core.button.CreateFeatrue',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','scm.product.GetUUid.ComponentGrid',
			'core.button.Sync','core.button.SubmitStandard','core.button.ResSubmitNoStandard','core.button.UpdatePrLevel','core.button.SendEdi','core.button.CancelEdi','core.button.TurnTender'
			,'core.button.CopyByConfigs','core.form.PhotoField','core.button.Modify'
	],
	init:function(){
		var me = this;
		this.control({ 
			'#pr_leadtime':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
			'#pr_validdays':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pr_precision':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pr_purcmergedays':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'textfield[name=pr_self]': {
    			change: function(field,newval,oldval){
    				var pr_orispeccode = Ext.getCmp('pr_orispeccode');
    				var pr_brand = Ext.getCmp('pr_brand');
    				if(newval=='-1'){
    					pr_brand.setReadOnly(false);
    					pr_orispeccode.setReadOnly(false);
    					pr_brand.setFieldStyle('background:#FFFAFA;color:#515151;');
    					pr_orispeccode.setFieldStyle('background:#FFFAFA;color:#515151;');
    					pr_brand.allowBlank = false;
    					pr_orispeccode.allowBlank = false;
    					pr_brand.getEl().dom.firstChild.style = 'margin-right:5px;width:100px;color:#FF0000';
    					pr_orispeccode.getEl().dom.firstChild.style = 'margin-right:5px;width:100px;color:#FF0000';
    				}else{
    					pr_orispeccode.allowBlank = true;
    					pr_brand.allowBlank = true;
    					pr_brand.getEl().dom.firstChild.style = 'margin-right:5px;width:100px;';
    					pr_orispeccode.getEl().dom.firstChild.style = 'margin-right:5px;width:100px;';
    				}
    			}
    		},
    		'#pr_purchasedays':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pr_ltinstock':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pr_ltwarndays':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pr_gdtqq':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pr_uuid':{
    			beforerender:function(e){//不允许手动编辑，但可以通过trigger调用的方式来取值。
    				e.editable = false;
    			}
    		},
    		'#pr_orispeccode':{//原厂型号发生改变自动获取标准料号
    			blur:function(e){//失去焦点
    				if(e.value != null && e.value !='' && e.isDirty() && da!= e.value){//值发生改变，发送请求至标准器件库
    					me.getUUIdByCode(e.value);
    				}else if(e.value == null || e.value ==''){
    					var uuid = Ext.getCmp("pr_uuid");
    					if(uuid){
    						Ext.getCmp("pr_uuid").setValue('');
    					}
    				}
    			},
    			focus:function(e){
    				 da = e.value;
    			}
    		},
    		'field[name=file]' : {
    			change : function(c){
    				var s=c.value;
    				if(s){
    					s=s.substr(s.lastIndexOf('\\')+1);
    					s=s.substr(0,s.lastIndexOf('.'));
        				var pr_piccode = Ext.getCmp('pr_piccode');
        				if(pr_piccode){
        					pr_piccode.setValue(s);
        				}
    				}
    			}
    		},
			'erpTurnCustomerButton':{
    			beforerender:function(btn){
    				btn.setText('转打样');
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp('pr_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click:function(btn){
    				warnMsg("确定要转打样吗?", function(b){//url,jsp,pageid,title
    					if(b == 'yes'){
    						me.turn(btn.ownerCt.ownerCt,'scm/product/prodturnsample.action',
    								'jsps/scm/product/ProductSample.jsp?formCondition=ps_idIS@@&gridCondition=pd_psidIS@@','ProductSample','打样申请单');
    					}
    				});
    			}
    		},
			'combobox[name=pr_maketype]': {
				change: function(m){
					if(!Ext.isEmpty(m.value)) {
						var f = Ext.getCmp('pr_purchasepolicy');
						switch (f.value) {
						case 'MRP':
							if(m.value != '外购' && m.value != '客供') {
								showError('计划类型为MRP时，生产类型只能为 外购 和 客供');
								m.reset();
							}
							break;
						case 'MPS':
							if(m.value != '制造 ' && m.value != '委外') {
								showError('计划类型为MPS时，生产类型只能为 制造 和 委外');
								m.reset();
							}
							break;
						}
					}
				}
			},
			'autocodetrigger': {
    			aftertrigger: function(trigger, val, path, item) {
    				if(path) {
    					if(path[0]) {
    						Ext.getCmp('pr_kind').setValue(path[0]);
    					}
    					if(path[1]) {
    						Ext.getCmp('pr_kind2').setValue(path[1]);
    					}
    					if(path[2]) {
    						Ext.getCmp('pr_kind3').setValue(path[2]);
    					}
    					var data = item.get('data') || item.raw.data, f;
    					if(path[3]) {
    						if(Ext.getCmp('pr_xikind')){
    							Ext.getCmp('pr_xikind').setValue(path[3]);
    						}
    						if(!Ext.isEmpty(data.pk_prname)){
    							Ext.getCmp('pr_detail').setValue(data.pk_prname);
    						} else {
    							Ext.getCmp('pr_detail').setValue(('无' == path[3] ? '' : path[3]) + 
    	    							('无' == path[2] ? '' : path[2]) + ('无' == path[1] ? '' : path[1]));
    						}
    					}
    					//添加了一个复检周期的自动赋值
    					if(typeof (f = Ext.getCmp('pr_ltqc')) !== 'undefined')
    						f.setValue(data.pk_ltqc);
    					//增加了一个abc分类的自动赋值
    					if(typeof (f = Ext.getCmp('pr_abc')) !== 'undefined')
    						f.setValue(data.pk_abc);
    					if(typeof (f = Ext.getCmp('pr_namerule')) !== 'undefined')
    						f.setValue(data.pk_namerule);
    					if(typeof (f = Ext.getCmp('pr_nameeg')) !== 'undefined')
    						f.setValue(data.pk_nameeg);	
    					if(typeof (f = Ext.getCmp('pr_specrule')) !== 'undefined')
    						f.setValue(data.pk_specrule);
    					if(typeof (f = Ext.getCmp('pr_speceg')) !== 'undefined')
    						f.setValue(data.pk_speceg);
    					if(typeof (f = Ext.getCmp('pr_parameterrule')) !== 'undefined')
    						f.setValue(data.pk_parameterrule);
    					if(typeof (f = Ext.getCmp('pr_parametereg')) !== 'undefined')
    						f.setValue(data.pk_parametereg);
    					if(typeof (f = Ext.getCmp('pr_lossrate')) !== 'undefined')
    						f.setValue(data.pk_lossrate);//量产损耗率
    					if(typeof (f = Ext.getCmp('pr_testlossrate')) !== 'undefined')
    						f.setValue(data.pk_testlossrate);//试产损耗率
    					if(typeof (f = Ext.getCmp('pr_exportlossrate')) !== 'undefined')
    						f.setValue(data.pk_exportlossrate);// 委外损耗率
    					if(typeof (f = Ext.getCmp('pr_whcode')) !== 'undefined')
    						f.setValue(data.pk_whcode);// 仓库编号
    					if(typeof (f = Ext.getCmp('pr_whname')) !== 'undefined')
    						f.setValue(data.pk_whname);// 仓库名称
    					if(typeof (f = Ext.getCmp('pr_location')) !== 'undefined')
    						f.setValue(data.pk_location);// 诸位
    					if(typeof (f = Ext.getCmp('pr_acceptmethod')) !== 'undefined')
    						f.setValue(data.pk_acceptmethod);// 接收方式
    					if(typeof (f = Ext.getCmp('pr_wccode')) !== 'undefined')
    						f.setValue(data.pk_wccode);// 工作中心号
    					if(typeof (f = Ext.getCmp('pr_wcname')) !== 'undefined')
    						f.setValue(data.pk_wcname);// 工作中心名称
    					if(typeof (f = Ext.getCmp('pr_stockcatecode')) !== 'undefined')
    						f.setValue(data.pk_stockcatecode);// 存货科目编号
    					if(typeof (f = Ext.getCmp('pr_stockcatename')) !== 'undefined')
    						f.setValue(data.pk_stockcate);// 存货科目名称
    					if(typeof (f = Ext.getCmp('pr_costcatecode')) !== 'undefined')
    						f.setValue(data.pk_costcatecode);// 成本科目编号
    					if(typeof (f = Ext.getCmp('pr_costcatename')) !== 'undefined')
    						f.setValue(data.pk_costcate);// 成本科目名称
    					if(typeof (f = Ext.getCmp('pr_incomecatecode')) !== 'undefined')
    						f.setValue(data.pk_incomecatecode);// 收入科目编号
    					if(typeof (f = Ext.getCmp('pr_incomecatename')) !== 'undefined')
    						f.setValue(data.pk_incomecate);// 收入科目名称
    					if(typeof (f = Ext.getCmp('pr_aql')) !== 'undefined')
    						f.setValue(data.pk_aql);// AQL抽样标准
    					if(typeof (f = Ext.getCmp('pr_dhzc')) !== 'undefined')
    						f.setValue(data.pk_dhzc);// 计划类型
    					if(typeof (f = Ext.getCmp('pr_validdays')) !== 'undefined'){
    						f.setValue(data.pk_validdays);// 有效期（天）
    					}
    					if(typeof (f = Ext.getCmp('pr_qualmethod')) !== 'undefined'){
    						f.setValue(data.pk_qualmethod);// 检验方案
    					}
    					if(typeof (f = Ext.getCmp('pr_purclossrate')) !== 'undefined'){
    						f.setValue(data.pk_purclossrate);// 采购损耗率%
    					}
    					if(typeof (f = Ext.getCmp('pr_precision')) !== 'undefined'){
    						f.setValue(data.pk_precision); //计算精度
    					}
    					if(typeof (f = Ext.getCmp('pr_purcmergedays')) !== 'undefined'){
    						f.setValue(data.pk_purcmergedays);// 采购合并天数
    					}
    					if(typeof (f = Ext.getCmp('pr_purchasedays')) !== 'undefined'){
    						f.setValue(data.pk_purchasedays);// 交货合并天数
    					}
    					if(typeof (f = Ext.getCmp('pr_ltwarndays')) !== 'undefined'){
    						f.setValue(data.pk_ltwarndays);// 提前预警天数
    					}
    					if(typeof (f = Ext.getCmp('pr_ltinstock')) !== 'undefined'){
    						f.setValue(data.pk_ltinstock);// 送货提前天数
    					}
    					if(typeof (f = Ext.getCmp('pr_gdtqq')) !== 'undefined'){
    						f.setValue(data.pk_leadtime);// 固定提前期
    					}
    					if(typeof (f = Ext.getCmp('pr_location')) !== 'undefined'){
    						f.setValue(data.pk_location);// 仓位
    					}
    					if(typeof (f = Ext.getCmp('pr_serial')) !== 'undefined'){
    						f.setValue(data.pk_serial);// 种类
    					}
    					if(typeof (f = Ext.getCmp('pr_material')) !== 'undefined'){
    						f.setValue(data.pk_material);// 认可状态
    					}
    					if(typeof (f = Ext.getCmp('pr_aql')) !== 'undefined'){
    						f.setValue(data.pk_aql);// AQL
    					}
    					if(typeof (f = Ext.getCmp('pr_isgrouppurc')) !== 'undefined'){
    						f.setValue(data.pk_isgrouppurc);// 是否集团采购
    					}if(typeof (f = Ext.getCmp('pr_supplytype')) !== 'undefined')//供应类型
    						f.setValue(data.pk_supplytype);
    					if(typeof (f = Ext.getCmp('pr_manutype')) !== 'undefined'){//生产类型
    						var d = data.pk_manutype, v = d;
    						switch(d){
    							case '制造':
    							v = 'MAKE';break;
    							case '委外':
    							v = 'OSMAKE';break;
    							case '外购':
    							v = 'PURCHASE';break;
    							case '客供':
    							v = 'CUSTOFFER';break;
    						}
    						f.setValue(v);
    					}
    					if(typeof (f = Ext.getCmp('pr_whmancode')) !== 'undefined'){
    						f.setValue(data.pk_whmancode);// 仓管员号
    					}
    					if(typeof (f = Ext.getCmp('pr_whmanname')) !== 'undefined'){
    						f.setValue(data.pk_whmanname);// 仓管员名
    					}
    					if(typeof (f = Ext.getCmp('pr_buyercode')) !== 'undefined'){
    						f.setValue(data.pk_buyercode);// 采购员编号
    					}
    					if(typeof (f = Ext.getCmp('pr_buyername')) !== 'undefined'){
    						f.setValue(data.pk_buyername);// 采购员名
    					}
    					if(typeof (f = Ext.getCmp('pr_cggdycode')) !== 'undefined'){
    						f.setValue(data.pk_cggdycode);// 采购员名
    					}
    					if(typeof (f = Ext.getCmp('pr_cggdy')) !== 'undefined'){
    						f.setValue(data.pk_cggdy);// 采购员名
    					}
    					if(typeof (f = Ext.getCmp('pr_sqecode')) !== 'undefined'){
    						f.setValue(data.pk_sqecode);// 采购员名
    					}
    					if(typeof (f = Ext.getCmp('pr_sqename')) !== 'undefined'){
    						f.setValue(data.pk_sqename);// 采购员名
    					}
    					if(typeof (f = Ext.getCmp('pr_msdlevel')) !== 'undefined'){
    						f.setValue(data.pk_msdlevel);// 采购员名
    					}
    					if(typeof (f = Ext.getCmp('pr_self')) !== 'undefined'){
    						f.setValue(data.pk_self);// 采购员名
    					}
    					if(typeof (f = Ext.getCmp('pr_cop')) !== 'undefined'){
    						f.setValue(data.pk_cop);// 所属公司
    					}
    					if(typeof (f = Ext.getCmp('pr_pkid')) !== 'undefined'){
    						f.setValue(data.pk_id);// 种类ID
    					}
    					if(typeof (f = Ext.getCmp('pr_engremark')) !== 'undefined'){
    						f.setValue(data.pk_engremark);// engremark
    					}
    					if(typeof (f = Ext.getCmp('pr_inspector')) !== 'undefined'){
    						f.setValue(data.pk_inspector);// 检验员编号
    					}
    					if(typeof (f = Ext.getCmp('pr_inspectorcode')) !== 'undefined'){
    						f.setValue(data.pk_inspectorcode);// 检验员
    					}
    					if(typeof (f = Ext.getCmp('pr_planercode')) !== 'undefined'){
    						f.setValue(data.pk_jhy);// 计划员
    					}
    					if(typeof (f = Ext.getCmp('pr_planner')) !== 'undefined'){
    						f.setValue(data.pk_jhyname);// 计划员
    					}
    					if(typeof (f = Ext.getCmp('pr_tracekind')) !== 'undefined'){
    						f.setValue(data.pk_tracekind);// 管控类型
    					}
    					if(typeof (f = Ext.getCmp('pr_firsttype')) !== 'undefined'){
    						f.setValue(data.pk_firsttype);//量产
    					}
    					if(typeof (f = Ext.getCmp('pr_secondtype')) !== 'undefined'){
    						f.setValue(data.pk_secondtype);// 试产
    					}
    				}
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
					var pr_spec = Ext.getCmp('pr_spec').value;
					if(pr_spec!=""||pr_spec!=null){
						Ext.Ajax.request({
							url:basePath+'common/getCountByTable.action',
							params:{
								condition:"pr_spec='"+pr_spec+"'",
								tablename:'product'
							},
							method:'post',
							callback:function(options,success,response){
								var res = new Ext.decode(response.responseText);
								if(res.success){
									if(res.count>0){
										Ext.Msg.confirm('提示','规格型号已经存在，确认要继续保存吗？',function(btn){
											if(btn=='yes'){
												me.FormUtil.beforeSave(this);
											}
										});
									}else{
										me.FormUtil.beforeSave(this);
									}
								}
							}
						});
					}else{me.FormUtil.beforeSave(this);}
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('pr_id').value);
				}
			},
			'erpUpdateButton': { 
				click: function(btn){
					var pr_spec = Ext.getCmp('pr_spec').value;
					var pr_id=Ext.getCmp('pr_id').value;
						if(pr_spec!=""||pr_spec!=null){
							Ext.Ajax.request({
								url:basePath+'common/getCountByTable.action',
								params:{
									condition:"pr_spec='"+pr_spec+"' and pr_id <>"+pr_id,
									tablename:'product'
								},
								method:'post',
								callback:function(options,success,response){
									var res = new Ext.decode(response.responseText);
									if(res.success){
										if(res.count>0){
											Ext.Msg.confirm('提示','规格型号已经存在，确认要继续更新吗？',function(btn){
												if(btn=='yes'){
													me.FormUtil.onUpdate(this);
												}
											});
										}else{
											me.FormUtil.onUpdate(this);
										}
									}
								}
							});
							}else{me.FormUtil.onUpdate(this);}
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addProduct'+new Date().getTime(), '新增物料', 'jsps/scm/product/product.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('pr_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('pr_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('pr_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('pr_id').value);
				}
			},
			'erpTurnTender': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.TurnTender();
				}
			},
			'erpBannedButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && (status.value == 'BANNED' || status.value == 'DELETED' || status.value == 'DISABLE') && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					//hey start 取消提示弹窗
					/*if (!confirm('确定要禁用此物料?')){
						return;
					} */
					//hey end 
					//me.FormUtil.onBanned(Ext.getCmp('pr_id').value);
					me.toDisable();//zhongyl 2014 03 13
				}
			},
			'erpResBannedButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'DISABLE'){
						btn.hide();
					}
				},
				click: function(btn){
					// confirm box modify
					// zhuth 2018-2-1
					Ext.Msg.confirm('提示', '确定要反禁用此物料?', function(btn) {
						if(btn == 'yes') {
							me.FormUtil.onResBanned(Ext.getCmp('pr_id').value);
						}
					});
				}
			},
			'erpFeatureViewButton':{
    			click: function(btn){
    				var code=Ext.getCmp('pr_code').value;
    				var id=Ext.getCmp('pr_id').value;
    				var name=Ext.getCmp('pr_detail').value;
    				if(code != null){
    					Ext.Ajax.request({//拿到grid的columns
    						url : basePath + "pm/bom/getDescription.action",
    						params: {
    							tablename: 'Product',
    							field: 'pr_specvalue',
    							condition: "pr_code='" + code + "'"
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
    										id : 'win' + id,
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
    											html : '<iframe id="iframe_' + id + '" src="' + basePath + 
    											"jsps/pm/bom/FeatureValueView.jsp?fromwhere=SaleDetail&formid=" + id + '&pr_code=' + code +'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
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
    		'erpCreateFeatrueButton':{
    			afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					var code=Ext.getCmp('pr_code').value;
					var id = Ext.getCmp('pr_id').value;
					var name=Ext.getCmp('pr_detail').value;
					Ext.Ajax.request({//拿到grid的columns
						url : basePath + "pm/bom/getDescription.action",
						params: {
							tablename: 'Product',
							field: 'pr_specvalue',
							condition: "pr_code='" + code + "'"
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
			    						id : 'win',
			    						title: '生成特征料号',
			    						height: "90%",
			    						width: "95%",
			    						maximizable : true,
			    						buttonAlign : 'center',
			    						layout : 'anchor',
			    						items: [{
			    							tag : 'iframe',
			    							frame : true,
			    							anchor : '100% 100%',
			    							layout : 'fit',
			    							html : '<iframe id="iframe_' + id + '" src="' + basePath + 
			    							"jsps/pm/bom/FeatureValueSet.jsp?fromwhere=SaleDetail&condition=formidIS" + id + ' AND pr_codeIS' + code + ' AND pr_nameIS' + name +'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
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
    		 },
			'erpFeatureDefinitionButton':{
				afterrender: function(btn){
					var refo = Ext.getCmp('pr_refno');
					if(refo && Ext.isEmpty(refo.value)){
						btn.hide();
					}
				},
    			click: function(btn){  
    				var prcode=Ext.getCmp('pr_code').value;
    				var formCondition="pr_code='"+prcode+"'";
					var gridCondition="pf_prodcode='"+prcode+"'";
					var win = new Ext.window.Window({
    					id : 'win',
    					title: '物料特征项设置',
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
    						html : '<iframe id="iframe_' + prcode + '" src="' + basePath + 
    						'jsps/pm/bom/ProdFeature.jsp?formCondition='+formCondition+'&&gridCondition='+gridCondition+'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
    					}]
    				});
    				win.show();  
    			}
    		},
    		'erpCopyButton':{
    			click: function(btn){
    				var me = this, win = Ext.getCmp('copyProduct-win'), k3 = Ext.getCmp('pr_kind3'), k4 = Ext.getCmp('pr_xikind');
 				   	if(!win){
 				   		var prcode = Ext.getCmp('pr_code'), prname = Ext.getCmp('pr_detail'),
 					   	   	prspec = Ext.getCmp('pr_spec'),
 					   	   	val1 = prcode ? prcode.value : '', val2 =  prname ? prname.value : '',
 					   	   	val3 = prspec ? prspec.value : '';
 				   		win = Ext.create('Ext.Window', {
 						   id: 'copyProduct-win',
 						   title: '复制物料 ' + val1,
 						   height: 300,
 						   width: 400,
 						   items: [{
 							   xtype: 'form',
 							   height: '100%',
 							   width: '100%',
 							   bodyStyle: 'background:#f1f2f5;',
 							   items: [{
 								   margin: '10 0 0 0',
 								   xtype: 'textfield',
 								   fieldLabel: '新料编号',
 								   name:'pr_newcode',
 								   allowBlank: false
 							   },{
 								   margin: '10 0 0 0',
 								   xtype: 'textfield',
 								   fieldLabel: '旧料编号',
 								   name:'pr_oldcode',
 								   allowBlank: false,
 								   readOnly : true,
 								   value: val1
 							   },{
 								   margin: '3 0 0 0',
 								   xtype: 'textfield',
 								   name:'pr_newname',
 								   fieldLabel: '物料名称',
 								   value: val2,
 								   allowBlank: false
 							   },{
 								   margin: '3 0 0 0',
 								   xtype: 'textfield',
 								   name:'pr_newspec',
 								   fieldLabel: '物料规格',
 								   value: val3,
 								   allowBlank: false
 							   }],
 							   closeAction: 'hide',
 							   buttonAlign: 'center',
 							   layout: {
 								   type: 'vbox',
 								   align: 'center'
 							   },
 							   buttons: [{
 								   text: $I18N.common.button.erpConfirmButton,
 								   cls: 'x-btn-blue',
 								   handler: function(btn) {
 									   var form = btn.ownerCt.ownerCt,
 									   a = form.down('textfield[name=pr_newcode]'),
 									   b = form.down('textfield[name=pr_newname]');
 									   c = form.down('textfield[name=pr_newspec]');
 									   if(form.getForm().isDirty()) {
 										   if(a.value == val1){
 											   showError("新物料编号不能与旧料编号相同");
 											   return;
 										   }
 										   me.copyProduct(Ext.getCmp('pr_id').value, a.value, b.value, c.value);
 									   }
 								   }
 							   }, {
 								   text: $I18N.common.button.erpCloseButton,
 								   cls: 'x-btn-blue',
 								   handler: function(btn) {
 									   btn.up('window').hide();
 								   }
 							   }]
 						   }]
 					   });
	 				   	me.autoCode(function(code){
							win.down('textfield[name=pr_newcode]').setValue(code);
						});
 				   }
 				   win.show();
 			   }
    		},
            'erpSyncButton': {
            	afterrender: function(btn) {
                    var form = btn.ownerCt.ownerCt, s = form.down('#pr_statuscode');
                    if (s.getValue() != 'AUDITED' && s.getValue() != 'DISABLE')
                        btn.hide();
                }
            },
            'erpProductStatusButton':{
            	afterrender: function(btn) {
                    var status = Ext.getCmp('pr_statuscode');
                    if (status && (status.value == 'ENTERING' || status.value == 'DISABLE'))
                        btn.hide();
                }
            },
            'erpUpdatePrLevelButton':{
            	afterrender: function(btn) {
                    var status = Ext.getCmp('pr_statuscode');
                    if (status && (status.value == 'ENTERING' || status.value == 'DISABLE'))
                        btn.hide();
                }
            }
		});
	},
	copyProduct: function(prid, val1, val2, val3) {
		var me = this;
 	   	Ext.Ajax.request({
 		   url: basePath + 'scm/product/copyProduct.action',
 		   params: {
 			   caller: caller,
 			   id: prid,
 			   newcode: val1,
 			   newname: val2,
 			   newspec: val3
 		   },
 		   callback : function(options,success,response){
 			   me.FormUtil.getActiveTab().setLoading(false);
 			   var localJson = new Ext.decode(response.responseText);
 			   if(localJson.exceptionInfo){
 				   showError(localJson.exceptionInfo);
 			   }
 			   if(localJson.success){
 				   Ext.getCmp('copyProduct-win').hide();
 				   turnSuccess(function(){
 					   var id = localJson.id;
 					   var url = "jsps/scm/product/product.jsp?formCondition=pr_id=" + id;
 					   me.FormUtil.onAdd('Product' + id, '物料基本资料' + id, url);
 				   });
 			   }
 		   }
 	   	});
    },
	turn: function(form,url,jsp,pageid,title) {
		var me = this;
		form.setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + url,
	   		params: {
	   			id: form.down('#pr_id').value
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			form.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   			}
    			if(localJson.success){
    				turnSuccess(function(){
    					var id = localJson.id;
    					var url2 = jsp.replace(/@@/g,id);
    					me.FormUtil.onAdd(pageid + id, title + id, url2);
    				});
	   			}
	   		}
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	toDisable:function(){
		Ext.create('Ext.window.Window',{
	       	 width:350,
	       	 height:185,
	       	 id:'win',
	       	 title:'<h1>禁用物料</h1>',
	       	 layout:'column',
	       	 items:[{
					margin: '10 0 0 0',
					xtype: 'textfield',
					fieldLabel: '<font style="color:#F00">禁用备注</font>',
					name:'disremark',
					allowBlank:false,
					value: '' 
				}],
	       	 buttonAlign:'center',
	       	 buttons:[{
	 				xtype:'button',
	 				columnWidth:0.12,
	 				text:'确定',
	 				width:60,
	 				iconCls: 'x-button-icon-save',
	 				handler:function(btn){ 
	 					var remark=btn.ownerCt.ownerCt.down('textfield[name=disremark]').value;
	 					if (remark==null || remark==''){
	 						showError('禁用备注必须填写');
	 						return;
	 					}
 						Ext.Ajax.request({
 					   		url : basePath + 'scm/product/bannedProduct.action',
 					   		params: {
 					   			id: Ext.getCmp('pr_id').value, 
 					   			remark: remark,
 					   			caller: caller
 					   		},
 					   		method : 'post',
 					   	callback: function(opt, s, r) {
 							var rs = Ext.decode(r.responseText);
 							if(rs.exceptionInfo) {
 								showError(rs.exceptionInfo);
 							} else {
 								alert('更新成功!');
 								window.location.reload();
 							}
 					   	}
 						});
	 				}
	 			},{
	 				xtype:'button',
	 				columnWidth:0.1,
	 				text:'取消',
	 				width:60,
	 				iconCls: 'x-button-icon-close',
	 				margin:'0 0 0 10',
	 				handler:function(btn){
	 					Ext.getCmp('win').close();
	 				}
	 			}]
	        }).show(); 
	},
	getUUIdByCode:function(v){		
		var me = this;
		//不能跨域访问
		Ext.Ajax.request({
	   		url : basePath+"scm/product/getUUIdByCode.action",
	   		method : 'post',
	   		params:{
	   			code:v
	   		},
	   		callback : function(result,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   			}
                var store = localJson.gridStore;
                if(store.length > 1){
	   				//多条弹出grid 提供选择
                	me.createSigWin(store);
	   			}else if(store.length == 1){
	   				var da = Ext.getCmp("pr_uuid");
	   				if(da){
	   					Ext.getCmp("pr_uuid").setValue(store[0].uuid);
	   				}
	   			}
	   		}
		});
	 },
	autoCode : function(callback) {
		var me = this;
		Ext.Ajax.request({
			url : basePath + 'scm/product/getProductKindNum.action',
			params : {
				k1 : (Ext.getCmp('pr_kind') && !Ext.isEmpty(Ext.getCmp('pr_kind').getValue())) ? Ext.getCmp('pr_kind').getValue(): null,
				k2 : (Ext.getCmp('pr_kind2') && !Ext.isEmpty(Ext.getCmp('pr_kind2').getValue())) ? Ext.getCmp('pr_kind2').getValue(): null,
				k3 : (Ext.getCmp('pr_kind3') && !Ext.isEmpty(Ext.getCmp('pr_kind3').getValue())) ? Ext.getCmp('pr_kind3').getValue(): null,
				k4 : (Ext.getCmp('pr_xikind') && !Ext.isEmpty(Ext.getCmp('pr_xikind').getValue())) ? Ext.getCmp('pr_xikind').getValue(): null
			},
			callback : function(opt, s, r) {
				var r = Ext.decode(r.responseText);
				if (r.exceptionInfo) {
					showError(r.exceptionInfo);
				} else if (r.success && r.number) {
					callback.call(null, r.number);
				}
			}
		});
	},
	 createSigWin:function(data){
		 var win = new Ext.window.Window({  
				id : 'wind',
				title:'标准料号',
				height : '65%',
				width : '65%',
				maximizable : true,
				buttonAlign : 'center',
				layout : 'anchor',
				items : [{
					xtype:'erpComponentGrid',
					anchor: '100% 100%',
					listeners:{
						beforerender:function(g){
							g.store.loadData(data);
						}
					}
				}],
				bbar: ['->',{
						text:'关闭',
						cls: 'x-btn-gray',
						iconCls: 'x-button-icon-close',
						listeners: {
							click: function(){
								win.close();
							}
						}
					},'->']
			});
    	win.show(); 
	},
	TurnTender: function(){
		var me = this;
		Ext.create('Ext.window.Window', {
				title : '转招标',
				closeAction: 'destroy',
				modal : true,
				width : 300,
				height: 150,
				layout: {
			        type: 'vbox',
			        pack: 'center',
			        align: 'middle'
	    		},
				items: [{
					fieldLabel: '招标标题',
					xtype: 'textfield',
					width: 270
				},{
					fieldLabel: '招标项目数量',
					xtype:'numberfield',
					hideTrigger:true,
					regex: /^\+?[1-9][0-9]*$/,
					regexText:'必须为正整数',
					value: 1,
					minValue:1,
					width:270
				}],
				buttonAlign : 'center',
				buttons : [{
					text : $I18N.common.button.erpConfirmButton,
					height : 26,
					handler : function(b) {
						var title = b.ownerCt.ownerCt.items.items[0].value;
						var qty = b.ownerCt.ownerCt.items.items[1].value;
						var id = Ext.getCmp('pr_id').value;
						if(qty&&title){
							me.FormUtil.setLoading(true);
							Ext.Ajax.request({
					        	url : basePath + 'scm/product/turnTender.action',
					        	params: {
					        		id: id,
					        		caller: caller,
					        		title: title,
					        		qty: qty
					        	},
					        	method : 'post',
					        	callback : function(options,success,response){
					        		me.FormUtil.setLoading(false);
					        		var res = new Ext.decode(response.responseText);
					        		if(res.exception || res.exceptionInfo){
					        			showError(res.exceptionInfo);
					        			return;
					        		}else{
					        			showMessage('提示',res.msg);
					        			b.ownerCt.ownerCt.close();
					        		}
					        	}
							});
						}else{
							showError('招标标题和招标项目数量不能为空！');
						}
					}
				}, {
					text : $I18N.common.button.erpCloseButton,
					height : 26,
					handler : function(b) {
						b.ownerCt.ownerCt.close();
					}
				}]
			}).show();
	}
});