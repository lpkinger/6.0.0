Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.PreProduct', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.product.PreProduct','core.form.Panel','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close',
    		'core.button.ResSubmit','core.button.Update','core.button.Delete','core.button.TurnCustomer','core.button.Sync',
    		'core.button.ResAudit','core.button.Temporary','core.button.Flow','core.button.TurnBorrow','core.button.FeatureDefinition',
    		'core.trigger.MultiDbfindTrigger','core.trigger.AddDbfindTrigger','scm.product.GetUUid.ComponentGrid','core.button.CopyByConfigs',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.form.CheckBoxGroup'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'field[name=pre_code]': {
    			beforerender: function(f){
    				f.regex=/^[^!@#$%^&*()'":,\/?\t\s\r]*$/;
    				f.regexText='不能输入特殊字符';
    			}
    		},
    		'textfield[name=pre_self]': {
    			change: function(field,newval,oldval){
    				var pre_orispeccode = Ext.getCmp('pre_orispeccode');
    				var pre_brand = Ext.getCmp('pre_brand');
    				if(newval=='-1'){
    					pre_brand.setReadOnly(false);
    					pre_orispeccode.setReadOnly(false);
    					pre_brand.setFieldStyle('background:#FFFAFA;color:#515151;');
    					pre_orispeccode.setFieldStyle('background:#FFFAFA;color:#515151;');
    					pre_brand.allowBlank = false;
    					pre_orispeccode.allowBlank = false;
    					pre_brand.getEl().dom.firstChild.style = 'margin-right:5px;width:100px;color:#FF0000';
    					pre_orispeccode.getEl().dom.firstChild.style = 'margin-right:5px;width:100px;color:#FF0000';
    				}else{
    					pre_orispeccode.allowBlank = true;
    					pre_brand.allowBlank = true;
    					pre_brand.getEl().dom.firstChild.style = 'margin-right:5px;width:100px;';
    					pre_orispeccode.getEl().dom.firstChild.style = 'margin-right:5px;width:100px;';
    				}
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
    		'erpSaveButton': {
    			click: function(btn){
    				//申请编号
    				if(Ext.getCmp('pre_thisid').value == null || Ext.getCmp('pre_thisid').value == ''){
    					me.BaseUtil.getRandomNumber(null, null, 'pre_thisid');
    				}
					var pre_spec = Ext.getCmp('pre_spec').value;
					if(pre_spec!=""||pre_spec!=null){
						Ext.Ajax.request({
							url:basePath+'scm/product/getPreCount.action',
							params:{pre_spec:pre_spec,pre_id:0},
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
					}else{
						this.FormUtil.beforeSave(me);
					}
    				
    			}
    		},
    		'#pre_leadtime':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pre_precision':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'mfilefield':{
    			beforerender:function(f){
    				f.readOnly=false;
    			}
    		},
    		'#pre_gdtqq':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pre_ltwarndays':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pre_ltinstock':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pre_purcmergedays':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pre_purchasedays':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pre_period':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pre_validdays':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addPreProduct', '新增物料申请', 'jsps/scm/product/preProduct.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var pre_spec = Ext.getCmp('pre_spec').value;
    				var pre_id=Ext.getCmp('pre_id').value;
	    				if(pre_spec!=""||pre_spec!=null){
							Ext.Ajax.request({
								url:basePath+'scm/product/getPreCount.action',
								params:{
									pre_spec:pre_spec,
									pre_id:pre_id
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
    				//this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpSyncButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('pre_statuscode');
    				if(status && status.value != 'TURNFM'){
    					btn.hide();
    				}
    			}
    		},
    		'erpTurnBorrowButton':{
    			beforerender:function(btn){
    				btn.setText('转询价');
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp('pre_statuscode');
    				if(status && status.value != 'TURNFM'){
    					btn.hide();
    				}
    			},
    			click:function(btn){
    				warnMsg("确定要转询价吗?", function(b){
    					if(b == 'yes'){
    						me.turn(btn.ownerCt.ownerCt,'scm/product/turninquiry.action',
    								'jsps/scm/purchase/inquiry.jsp?formCondition=in_idIS@@&gridCondition=id_inidIS@@','inquiry','采购询价单维护');
    					}
    				});
    			}
    		},
    		'erpTurnCustomerButton':{
    			beforerender:function(btn){
    				btn.setText('转打样');
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp('pre_statuscode'),tosample = Ext.getCmp('pre_tosample');
    				if((status && status.value != 'TURNFM') || (tosample && tosample.value == '-1')){
    					btn.hide();
    				}
    			},
    			click:function(btn){
    				warnMsg("确定要转打样吗?", function(b){//url,jsp,pageid,title
    					if(b == 'yes'){
    						me.turn(btn.ownerCt.ownerCt,'scm/product/turnsample.action',
    								'jsps/scm/product/ProductSample.jsp?formCondition=ps_idIS@@&gridCondition=pd_psidIS@@','ProductSample','打样申请单');
    					}
    				});
    			}
    		},
    		'combobox[name=pre_manutype]': {
    			change: function(btn){
    				/*var value = Ext.getCmp('pre_manutype').value;
    				if(value =="MAKE" || value =="PURCHASE"){
    					Ext.getCmp('pre_leadtime').allowBlank = false;
    				}else{
    					Ext.getCmp('pre_gdtqq').allowBlank = false;
    				}*/
    			}
    		},
    		'autocodetrigger': {
    			aftertrigger: function(trigger, val, path, item) {
    				if(path) {    		
    					var data = item.get('data') || item.raw.data, f;
    					if(path[0]) {
    						Ext.getCmp('pre_kind').setValue(path[0]);
    					}
    					if(path[1]) {
    						Ext.getCmp('pre_kind2').setValue(path[1]);
    					}
    					if(path[2]) {
    						Ext.getCmp('pre_kind3').setValue(path[2]);
    					}
    					if(path[3]) {
    						if(Ext.getCmp('pre_xikind')){
    							Ext.getCmp('pre_xikind').setValue(path[3]);
    						}
    						if(!Ext.isEmpty(data.pk_prname)){
    							Ext.getCmp('pre_detail').setValue(data.pk_prname);
    						}else{
    							Ext.getCmp('pre_detail').setValue(('无' == path[3] ? '' : path[3]) + 
    									('无' == path[2] ? '' : path[2]) + ('无' == path[1] ? '' : path[1]));
    						}
    					}
    					//添加了一个复检周期的自动赋值
    					if(typeof (f = Ext.getCmp('pre_ltqc')) !== 'undefined')
    						f.setValue(data.pk_ltqc);
    					//添加了一个ABC分类的自动赋值
    					if(typeof (f = Ext.getCmp('pre_abc')) !== 'undefined')
    						f.setValue(data.pk_abc);
    					if(typeof (f = Ext.getCmp('pre_namerule')) !== 'undefined')
    						f.setValue(data.pk_namerule);
    					if(typeof (f = Ext.getCmp('pre_nameeg')) !== 'undefined')
    						f.setValue(data.pk_nameeg);	
    					if(typeof (f = Ext.getCmp('pre_specrule')) !== 'undefined')
    						f.setValue(data.pk_specrule);
    					if(typeof (f = Ext.getCmp('pre_speceg')) !== 'undefined')
    						f.setValue(data.pk_speceg);
    					if(typeof (f = Ext.getCmp('pre_parameterrule')) !== 'undefined')
    						f.setValue(data.pk_parameterrule);
    					if(typeof (f = Ext.getCmp('pre_parametereg')) !== 'undefined')
    						f.setValue(data.pk_parametereg);
    					if(typeof (f = Ext.getCmp('pre_dhzc')) !== 'undefined')
    						f.setValue(data.pk_dhzc);
    					if(typeof (f = Ext.getCmp('pre_supplytype')) !== 'undefined')
    						f.setValue(data.pk_supplytype);
    					if(typeof (f = Ext.getCmp('pre_manutype')) !== 'undefined'){
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
    					if(typeof (f = Ext.getCmp('pre_acceptmethod')) !== 'undefined')
    						f.setValue(['检验','0'].indexOf(data.pk_acceptmethod) > -1 ? '0' : '1');
    					if(typeof (f = Ext.getCmp('pre_stockcatecode')) !== 'undefined'){
    						f.setValue(data.pk_stockcatecode);
    						Ext.getCmp('pre_stockcate').setValue(data.pk_stockcate);
    					}
    					if(typeof (f = Ext.getCmp('pre_stockcatecode')) !== 'undefined'){
    						f.setValue(data.pk_stockcatecode);
    						Ext.getCmp('pre_stockcate').setValue(data.pk_stockcate);
    					}
    					if(typeof (f = Ext.getCmp('pre_incomecatecode')) !== 'undefined'){
    						f.setValue(data.pk_incomecatecode);
    						Ext.getCmp('pre_incomecate').setValue(data.pk_incomecate);
    					}
    					if(typeof (f = Ext.getCmp('pre_costcatecode')) !== 'undefined'){
    						f.setValue(data.pk_costcatecode);
    						Ext.getCmp('pre_costcate').setValue(data.pk_costcate);
    					}
    					if(typeof (f = Ext.getCmp('pre_whcode')) !== 'undefined'){
    						f.setValue(data.pk_whcode);
    						Ext.getCmp('pre_whname').setValue(data.pk_whname);
    					}
    					if(typeof (f = Ext.getCmp('pre_wccode')) !== 'undefined'){
    						f.setValue(data.pk_wccode);
    						Ext.getCmp('pre_wcname').setValue(data.pk_wcname);
    					}
    					if(typeof (f = Ext.getCmp('pre_material')) !== 'undefined'){
    						f.setValue(data.pk_material);// 认可状态
    					}
    					if(typeof (f = Ext.getCmp('pre_level')) !== 'undefined'){
    						f.setValue(data.pk_priority);// 优先等级
    					}
    					if(typeof (f = Ext.getCmp('pre_testlossrate')) !== 'undefined'){
    						f.setValue(data.pk_testlossrate);// 试产损耗
    					}
    					if(typeof (f = Ext.getCmp('pre_lossrate')) !== 'undefined'){
    						f.setValue(data.pk_lossrate);// 量产
    					}
    					if(typeof (f = Ext.getCmp('pre_exportlossrate')) !== 'undefined'){
    						f.setValue(data.pk_exportlossrate);// 委外
    					}
    					if(typeof (f = Ext.getCmp('pre_validdays')) !== 'undefined'){
    						f.setValue(data.pk_validdays);// 有效期（天）
    					}
    					if(typeof (f = Ext.getCmp('pre_jyfa')) !== 'undefined'){
    						f.setValue(data.pk_qualmethod);// 检验方案
    					}
    					if(typeof (f = Ext.getCmp('pre_purclosstate')) !== 'undefined'){
    						f.setValue(data.pk_purclossrate);// 采购损耗率%
    					}
    					if(typeof (f = Ext.getCmp('pre_precision')) !== 'undefined'){
    						f.setValue(data.pk_precision); //计算精度
    					}
    					if(typeof (f = Ext.getCmp('pre_purchasedays')) !== 'undefined'){
    						f.setValue(data.pk_purchasedays);// 交货合并天数
    					}
    					if(typeof (f = Ext.getCmp('pre_purcmergedays')) !== 'undefined'){
    						f.setValue(data.pk_purcmergedays);// 采购合并天数
    					}
    					if(typeof (f = Ext.getCmp('pre_ltwarndays')) !== 'undefined'){
    						f.setValue(data.pk_ltwarndays);// 提前预警天数
    					}
    					if(typeof (f = Ext.getCmp('pre_ltinstock')) !== 'undefined'){
    						f.setValue(data.pk_ltinstock);// 送货提前天数
    					}
    					if(typeof (f = Ext.getCmp('pre_gdtqq')) !== 'undefined'){
    						f.setValue(data.pk_leadtime);// 固定提前期
    					}
    					if(typeof (f = Ext.getCmp('pre_location')) !== 'undefined'){
    						f.setValue(data.pk_location);// 仓位
    					}
    					if(typeof (f = Ext.getCmp('pre_serial')) !== 'undefined'){
    						f.setValue(data.pk_serial);// 种类
    					}
    					if(typeof (f = Ext.getCmp('pre_isstandardpr')) !== 'undefined'){
    						f.setValue(data.pk_isstandardpr);// 是否标准化物料
    					}
    					if(typeof (f = Ext.getCmp('pre_aql')) !== 'undefined'){
    						f.setValue(data.pk_aql);// AQL
    					}
    					if(typeof (f = Ext.getCmp('pre_plzl')) !== 'undefined'){
    						f.setValue(data.pr_plzl);// 提前期批量
    					}
    					if(typeof (f = Ext.getCmp('pre_period')) !== 'undefined'){
    						f.setValue(data.pk_period);// 生产周期
    					}
    					if(typeof (f = Ext.getCmp('pre_isgrouppurc')) !== 'undefined'){
    						f.setValue(data.pk_isgrouppurc);// 是否集团采购
    					}
    					if(typeof (f = Ext.getCmp('pre_cgy')) !== 'undefined'){
    						f.setValue(data.pk_whmancode);// 仓管员号
    					}
    					if(typeof (f = Ext.getCmp('pre_cgyname')) !== 'undefined'){
    						f.setValue(data.pk_whmanname);// 仓管员名
    					}
    					if(typeof (f = Ext.getCmp('pre_buyercode')) !== 'undefined'){
    						f.setValue(data.pk_buyercode);// 采购员编号
    					}
    					if(typeof (f = Ext.getCmp('pre_buyername')) !== 'undefined'){
    						f.setValue(data.pk_buyername);// 采购员名
    					}
    					if(typeof (f = Ext.getCmp('pre_cggdycode')) !== 'undefined'){
    						f.setValue(data.pk_cggdycode);// 采购员名
    					}
    					if(typeof (f = Ext.getCmp('pre_cggdy')) !== 'undefined'){
    						f.setValue(data.pk_cggdy);// 采购员名
    					}
    					if(typeof (f = Ext.getCmp('pre_sqecode')) !== 'undefined'){
    						f.setValue(data.pk_sqecode);// 采购员名
    					}
    					if(typeof (f = Ext.getCmp('pre_sqename')) !== 'undefined'){
    						f.setValue(data.pk_sqename);// 采购员名
    					}
    					if(typeof (f = Ext.getCmp('pre_msdlevel')) !== 'undefined'){
    						f.setValue(data.pk_msdlevel);// 湿敏等级
    					}
    					if(typeof (f = Ext.getCmp('pre_self')) !== 'undefined'){
    						f.setValue(data.pk_self);// 是否标准件
    					}
    					if(typeof (f = Ext.getCmp('pre_cop')) !== 'undefined'){
    						f.setValue(data.pk_cop);// 所属公司
    					}
    					if(typeof (f = Ext.getCmp('pre_pkid')) !== 'undefined'){
    						f.setValue(data.pk_id);// 种类ID
    					}
    					if(typeof (f = Ext.getCmp('pre_engremark')) !== 'undefined'){
    						f.setValue(data.pk_engremark);// engremark
    					}
    					if(typeof (f = Ext.getCmp('pre_jyy')) !== 'undefined'){
    						f.setValue(data.pk_inspectorcode);// 检验员编号
    					}
    					if(typeof (f = Ext.getCmp('pre_jyyname')) !== 'undefined'){
    						f.setValue(data.pk_inspector);// 检验员
    					}
    					if(typeof (f = Ext.getCmp('pre_jhyname')) !== 'undefined'){
    						f.setValue(data.pk_jhyname);//计划员
    					}
    					if(typeof (f = Ext.getCmp('pre_jhy')) !== 'undefined'){
    						f.setValue(data.pk_jhy);//计划员
    					}
    					if(typeof (f = Ext.getCmp('pre_tracekind')) !== 'undefined'){
    						f.setValue(data.pk_tracekind);// 管控类型
    					}
    					if(typeof (f = Ext.getCmp('pre_firsttype')) !== 'undefined'){
    						f.setValue(data.pk_firsttype);//量产
    					}
    					if(typeof (f = Ext.getCmp('pre_secondtype')) !== 'undefined'){
    						f.setValue(data.pk_secondtype);// 试产
    					}
    				}
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('pre_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pre_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('pre_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pre_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pre_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pre_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var id = Ext.getCmp('pre_id').value;
    				var form = Ext.getCmp('form');
    				if(form && form.getForm().isValid()){
    					if(!me.FormUtil.contains(form.auditUrl, '?caller=', true)){
    						form.auditUrl = form.auditUrl + "?caller=" + caller;
    					}
    					me.FormUtil.setLoading(true);//loading...
    					//清除流程
    					Ext.Ajax.request({
    						url : basePath + me.FormUtil.deleteProcess,
    						params: {
    							keyValue:id,
    							caller:caller,
    							_noc:1
    						},
    						method:'post',
    						async:false,
    						callback : function(options,success,response){
    			
    						}
    					});
    					Ext.Ajax.request({
    						url : basePath + form.auditUrl,
    						params: {
    							id: id
    						},
    						method : 'post',
    						callback : function(options,success,response){
    							me.FormUtil.setLoading(false);
    							var localJson = new Ext.decode(response.responseText);
    							if(localJson.success){
    								//audit成功后刷新页面进入可编辑的页面 
    								var tosample = Ext.getCmp('pre_tosample'), log = localJson.log;
    								if (tosample && tosample.value == -1 && log) {
    							 		showMessage('提示', log, 2500);
    									window.location.reload();
    								} else {
    									showMessage('提示', '审核成功!', 1000);
    									window.location.reload();
    								}
    							} else {
    								if(localJson.exceptionInfo){
    									var str = localJson.exceptionInfo;
    									if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    										str = str.replace('AFTERSUCCESS', '');
    										showMessage("提示", str);
    										auditSuccess(function(){
    											window.location.reload();
    										});
    									} else {
    										showError(str);return;
    									}
    								}
    							}
    						}
    					});
    				} else {
    					me.FormUtil.checkForm();
    				}
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pre_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pre_id').value);
    			}
    		},
    		'erpTemporaryButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pre_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(b){
    				warnMsg("确定要转入正式物料吗?", function(btn){
    					if(btn == 'yes'){
    						me.turnFormal(b.ownerCt.ownerCt);
    					}
    				});
    			}
    		},
    		'erpFeatureDefinitionButton':{
				afterrender: function(btn){
					var refo = Ext.getCmp('pre_refno');
					if(refo && Ext.isEmpty(refo.value)){
						btn.hide();
					}
				},
    			click: function(btn){  
    				var prcode=Ext.getCmp('pre_code').value;
    				var formCondition="pre_code='"+prcode+"'";
					var gridCondition="ppf_prodcode='"+prcode+"'";
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
    						'jsps/pm/bom/PreProdFeature.jsp?formCondition='+formCondition+'&&gridCondition='+gridCondition+'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
    					}]
    				});
    				win.show();  
    			}
    		},
    		'#pre_uuid':{
    			beforerender:function(e){//不允许手动编辑，但可以通过trigger调用的方式来取值。
    				e.editable = false;
    			}
    		},
    		'#pre_orispeccode':{//原厂型号发生改变自动获取标准料号
    			blur:function(e){//失去焦点,并且和上次的值不一样
    				if(e.value != null && e.value !='' && e.isDirty() && da!= e.value){//值发生改变，发送请求至标准器件库
    					me.getUUIdByCode(e.value);
    				}else if(e.value == null || e.value ==''){
    					var uuid = Ext.getCmp("pre_uuid");
    					if(uuid){
    						Ext.getCmp("pre_uuid").setValue('');
    					}
    				}
    			},
    			focus:function(e){
    				 da = e.value;
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	turnFormal: function(form) {
		var me = this;
		form.setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + 'scm/product/turnFormal.action',
	   		params: {
	   			id: form.down('#pre_id').value
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
    					var url = "jsps/scm/product/product.jsp?formCondition=pr_id=" + id;
    					me.FormUtil.onAdd('Product' + id, '物料资料' + id, url);
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
	   			id: form.down('#pre_id').value
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
	   				var da = Ext.getCmp("pre_uuid");
	   				if(da){
	   					Ext.getCmp("pre_uuid").setValue(store[0].uuid);
	   				}
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
	}
});