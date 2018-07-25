Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.PreSale', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.sale.PreSale','core.form.Panel','core.form.MultiField',
    		'core.button.Add','core.button.Save','core.button.Update','core.button.Delete','core.button.Submit','core.button.ResSubmit',
    		'core.button.Audit','core.button.ResAudit','core.button.Close','core.button.TurnSale','core.form.FileField',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.form.CheckBoxGroup','core.form.CheckBoxContainer',
			'scm.sale.buttons.Buttons1','scm.sale.buttons.Buttons2','scm.sale.buttons.Buttons3','scm.sale.buttons.Buttons4','scm.sale.buttons.Buttons5',
			'scm.sale.buttons.Buttons6','scm.sale.buttons.Buttons7','scm.sale.buttons.Buttons8','scm.sale.buttons.Buttons9','scm.sale.buttons.Buttons10',
			'core.form.RadioGroup','core.form.SplitTextField','core.grid.ItemGrid','core.button.TurnSaleSelect','core.button.Print','core.button.PrintByCondition',
			'core.button.TurnNormalSale','core.button.RunATP'
		
    	],
    init:function(){
    	var me = this;
    	this.control({ 
			'erpFormPanel': {
				afterrender: function(){
					var panel = parent.Ext.getCmp('tree-tab');
					if(panel && !panel.collapsed) {
						panel.toggleCollapse();
					}
				}
			},
			'field[name=ps_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=ps_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'erpPreSaleButton1': {
    			click: function(){
    				me.updateButton(1);
    			}
    		},
    		'erpPreSaleButton2': {
    			click: function(){
    				me.updateButton(2);
    			}
    		},
    		'erpPreSaleButton3': {
    			click: function(){
    				me.updateButton(3);
    			}
    		},
    		'erpPreSaleButton4': {
    			click: function(){
    				me.updateButton(4);
    			}
    		},
    		'erpPreSaleButton5': {
    			click: function(){
    				me.updateButton(5);
    			}
    		},
    		'erpPreSaleButton6': {
    			click: function(){
    				me.updateButton(6);
    			}
    		},
    		'erpPreSaleButton7': {
    			click: function(){
    				me.updateButton(7);
    			}
    		},
    		'erpPreSaleButton8': {
    			click: function(){
    				me.updateButton(8);
    			}
    		},
    		'erpPreSaleButton9': {
    			click: function(){
    				me.updateButton(9);
    			}
    		},
    		'erpPreSaleButton10': {
    			click: function(){
    				me.updateButton(10);
    			}
    		},
    		
    		'erpTurnSaleSelectButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('ps_statuscode');
					if(status && status.value != 'AUDITED'&&status.value !='TURNSA'){
						
						btn.hide();
					}
    			}
    			
    		},
    		'erpRunATPButton':{
    			click: function(btn){ 
    				if(Ext.getCmp('ps_code').value != null){
    					var mb = new Ext.window.MessageBox();
    				    mb.wait('正在运算中','请稍后...',{
    					   interval: 10000, //bar will move fast!
    					   duration: 1000000,
    					   increment: 20, 
    					   scope: this
    					});
    					Ext.Ajax.request({//拿到grid的columns
    						url : basePath + "/pm/atp/runATPFromOther.action",
    						params: {
    							fromcode:Ext.getCmp('ps_code').value,
    							fromwhere:'PRESALE'
    						},
    						method : 'post', 
    						timeout: 600000,
    						callback : function(options,success,response){
    							mb.close();
    							var res = new Ext.decode(response.responseText);
    							if(res.exceptionInfo){
    								showError(res.exceptionInfo);return;
    							}
    							if(res.success){
    								if(res.atpid != '' && res.atpid != null && res.atpid>0){
    									me.FormUtil.onAdd(null, 'ATP运算', 'jsps/pm/atp/ATPMain.jsp?formCondition=am_id=' + res.atpid + '&&gridCondition=ad_amid='+res.atpid+'&_noc=1');
    								} else {
    									showError('无数据，运算失败');return;
    								}
    							}
    						}
    					});
    				}
    			}
    		},
    		'erpTurnNormalSaleButton':{
	        	click: function(m){

    				warnMsg("确定要转入销售单吗?", function(btn){
    					if(btn == 'yes'){
    			            Ext.Ajax.request({
    			                url: basePath + 'scm/sale/turnPreSaleToSale.action',
    			                params: {
    			                	type:'sale',
    			                	ps_id:Ext.getCmp('ps_id').value
    			                },
    			                waitMsg: '转单中...',
    			                method: 'post',
    			                callback: function(options, success, response) {
    			                    var localJson = new Ext.decode(response.responseText);
    			                    if (localJson.success) {
    			                    	
    			                    	turnSuccess(function() {
    			                            //add成功后刷新页面进入可编辑的页面 
//    			                            this.loadSplitData(originaldetno, said, record);
    			                        	if(localJson.clickurl){
    			                        		showError(localJson.clickurl);
    			                        		window.location.reload();
    			                        	}
    			                        });
    			                    } else if (localJson.exceptionInfo) {
    			                		showError(localJson.exceptionInfo);
    			                    } else {
    			                        saveFailure();
    			                    }
    			                }
    			            });
    			       }
    					
    				});
    			
	        		
	        		
	        	},
    			afterrender: function(btn){
    				var status = Ext.getCmp('ps_statuscode');
					if(status && status.value != 'AUDITED'&&status.value !='TURNSA'){
						
						btn.hide();
					}
    			}
	        },
    		'erpTurnSaleSelectButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('ps_statuscode');
					if(status && status.value != 'AUDITED'&&status.value !='TURNSA'){
						
						btn.hide();
					}
    			}
    			
    		},
    		
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			afterrender: function(btn){
					var status = Ext.getCmp('ps_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ps_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('ps_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
    			click: function(btn){
    				me.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addPreSale', '新增订单评审', 'jsps/scm/sale/preSale.jsp?v_width='+v_width);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ps_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.onSubmit(Ext.getCmp('ps_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ps_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ps_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ps_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('ps_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ps_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ps_id').value);
				}
			},
			'dbfindtrigger[name=ps_address]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='ps_custcode';
	    			trigger.mappingKey='cu_code';
	    			trigger.dbMessage='请先选客户编号！';
    			}
    		},
    		'combo[name=ps_type]': {
    			delay: 200,
    			change: function(m){
					this.hidecolumns(m);
//					var f = m.ownerCt, s = f.down('field[name=ar_sellercode]');
//					if (s) {
//						if(m.value == '应收款') {
//							s.allowBlank = false;
//						} else {
//							s.allowBlank = true;
//						}
//					}
				}
    		}/*,
    		'erpTurnSaleButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('ps_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入销售单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/sale/preSaleToSale.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('ps_id').value
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
    	    		    					var url = "jsps/scm/sale/sale.jsp?whoami=Sale&formCondition=sa_id=" + id + "&gridCondition=sd_said=" + id;
    	    		    					me.FormUtil.onAdd('Sale' + id, '销售单' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
			}*/
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},


	updateButton: function(type){
		var me = this;
//		var form = Ext.getCmp('form');
		
		if(type == 1){
			Ext.getCmp('rg_1').logic = '';
			Ext.getCmp('ta_1').logic = '';
			Ext.getCmp('tf_1').logic = '';
			Ext.getCmp('rg_2').logic = '';
			Ext.getCmp('ta_2').logic = '';
			Ext.getCmp('tf_2').logic = '';
		}
		if(type == 2){
			Ext.getCmp('rg_3').logic = '';
			Ext.getCmp('ta_3').logic = '';
			Ext.getCmp('tf_3').logic = '';
			
			Ext.getCmp('rg_4').logic = '';
			Ext.getCmp('ta_4').logic = '';
			Ext.getCmp('tf_4').logic = '';
			
			Ext.getCmp('tf_19').logic = '';
			Ext.getCmp('tf_20').logic = '';
			Ext.getCmp('tf_21').logic = '';
			Ext.getCmp('tf_22').logic = '';
			Ext.getCmp('tf_23').logic = '';
			Ext.getCmp('tf_24').logic = '';
			Ext.getCmp('tf_25').logic = '';
			Ext.getCmp('tf_26').logic = '';
			Ext.getCmp('tf_27').logic = '';
			Ext.getCmp('tf_28').logic = '';
			Ext.getCmp('tf_29').logic = '';
			Ext.getCmp('tf_30').logic = '';
			Ext.getCmp('tf_31').logic = '';
			Ext.getCmp('tf_32').logic = '';
		}
		if(type == 3){
			Ext.getCmp('rg_5').logic = '';
			Ext.getCmp('ta_5').logic = '';
			Ext.getCmp('tf_5').logic = '';
		}
		if(type == 4){
			
			Ext.getCmp('rg_6').logic = '';
			Ext.getCmp('ta_6').logic = '';
			Ext.getCmp('tf_6').logic = '';
			
			Ext.getCmp('rg_7').logic = '';
			Ext.getCmp('ta_7').logic = '';
			Ext.getCmp('tf_7').logic = '';
			
			Ext.getCmp('rg_8').logic = '';
			Ext.getCmp('ta_8').logic = '';
			Ext.getCmp('tf_8').logic = '';
		}
		
		if(type == 5){
			
			Ext.getCmp('rg_9').logic = '';
			Ext.getCmp('ta_9').logic = '';
			Ext.getCmp('tf_9').logic = '';
			
			Ext.getCmp('rg_10').logic = '';
			Ext.getCmp('ta_10').logic = '';
			Ext.getCmp('tf_10').logic = '';
		}
		if(type == 6){
			Ext.getCmp('rg_11').logic = '';
			Ext.getCmp('ta_11').logic = '';
			Ext.getCmp('tf_11').logic = '';
			
			Ext.getCmp('rg_12').logic = '';
			Ext.getCmp('ta_12').logic = '';
			Ext.getCmp('tf_12').logic = '';
			
			Ext.getCmp('rg_13').logic = '';
			Ext.getCmp('ta_13').logic = '';
			Ext.getCmp('tf_13').logic = '';
		}
		
		if(type == 7){
			Ext.getCmp('rg_14').logic = '';
			Ext.getCmp('ta_14').logic = '';
			Ext.getCmp('tf_14').logic = '';

		}
		if(type == 8){
			Ext.getCmp('rg_15').logic = '';
			Ext.getCmp('ta_15').logic = '';
			Ext.getCmp('tf_15').logic = '';

		}
		if(type == 9){
			Ext.getCmp('rg_16').logic = '';
			Ext.getCmp('ta_16').logic = '';
			Ext.getCmp('tf_16').logic = '';
			
			Ext.getCmp('rg_17').logic = '';
			Ext.getCmp('ta_17').logic = '';
			Ext.getCmp('tf_17').logic = '';

		}
		if(type == 10){
			Ext.getCmp('rg_18').logic = '';
			Ext.getCmp('ta_18').logic = '';
			Ext.getCmp('tf_18').logic = '';

		}
		this.FormUtil.onUpdate(this);
	},
	
	getOtherPreSaleValues: function () {
	
		var me = this;
		var form = Ext.getCmp('form');
		var param;
		if(formCondition && formCondition!= ''){
			var con = formCondition.split('=');
			if(con.length>1){
				param = {
						id : con[1]
				};
			}
		}
		Ext.Ajax.request({
	   		url : basePath + form.getOtherPreSaleValues,
	   		params : param,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				if(localJson.values){
    					var vs = localJson.values.replace('{','');
    					vs = vs.replace('}','');
    					vs = vs.replace(/"/gi,"");
    					var os =vs.split(',');
    					Ext.each(os,function(o,index){
    						var oo = o.split(':');
    						var fi = Ext.getCmp(oo[0]);
    						if(fi){
    							if(fi.xtype == 'radiogroup'){
    								var ob = new Object();
    								ob[oo[0]] = oo[1];
    								fi.setValue(ob);
    							}else {
    								fi.setValue(oo[1]);
    							}
    						}
    					}
    					);
    				}
    				
    			} else if(localJson.exceptionInfo){
    				
    			} else{
	   			}
	   		}
	   		
		});
	},
	
	/**
	 * 单据保存
	 * @param param 传递过来的数据，比如gridpanel的数据
	 */
	onSave: function(param){
		var me = this;
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
			});
			if(!me.FormUtil.contains(form.saveUrl, '?caller=', true)){
				form.saveUrl = form.saveUrl + "?caller=" + caller;
			}
			me.save(r, param);
		}else{
			me.FormUtil.checkForm();
		}
	},
	save: function(){
		var params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		var me = this;
		var form = Ext.getCmp('form');
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
	   		url : basePath + form.saveUrl,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			me.FormUtil.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				saveSuccess(function(){
    					//add成功后刷新页面进入可编辑的页面 
		   				var value = r[form.keyField];
		   		    	var formCondition = form.keyField + "IS" + value ;
		   		    	var gridCondition = '';
		   		    	if(me.FormUtil.contains(window.location.href, '?', true)){
			   		    	window.location.href = window.location.href + '&formCondition=' + 
			   					formCondition + '&gridCondition=' + gridCondition;
			   		    } else {
			   		    	window.location.href = window.location.href + '?formCondition=' + 
			   					formCondition + '&gridCondition=' + gridCondition;
			   		    }
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
			   		    	if(me.FormUtil.contains(window.location.href, '?', true)){
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
	},
	/**
	 * 检查form未完善的字段
	 */
	checkForm: function(){
		var s = '';
		var form = Ext.getCmp('form');
		form.getForm().getFields().each(function (item, index, length){
			if(!item.isValid()){
				if(s != ''){
					s += ',';
				}
				if(item.fieldLabel || item.ownerCt.fieldLabel){
					s += item.fieldLabel || item.ownerCt.fieldLabel;
				}
			}
		});
		if(s == ''){
			return true;
		}
		showError($I18N.common.form.necessaryInfo1 + '(<font color=green>' + s.replace(/&nbsp;/g,'') + 
				'</font>)' + $I18N.common.form.necessaryInfo2);
		return false;
	},
	/**
	 * 单据修改
	 * @param form formpanel表
	 * @param param 传递过来的数据，比如gridpanel的数据
	 */
	onUpdate: function(me){
		var mm = this;
		var form = Ext.getCmp('form');
//		var s1 = mm.checkFormDirty(form);
		var s2 = '';
		var grids = Ext.ComponentQuery.query('gridpanel');
		var removea = new Array();
		Ext.each(grids,function(g,index){
			if(g.xtype=='itemgrid'){
				g.updateValue();
				removea.push(g);
			}
		});
		
		Ext.each(removea,function(r,index){
			Ext.Array.remove(grids,r);
		});
		
		if(grids.length > 0){//check所有grid是否已修改
			Ext.each(grids, function(grid, index){
				if(grid.GridUtil){
					var msg = grid.GridUtil.checkGridDirty(grid);
					if(msg.length > 0){
						s2 = s2 + '<br/>' + grid.GridUtil.checkGridDirty(grid);
					}
				}
			});
		}
		if(form && form.getForm().isValid()){
			//form里面数据
			var r = form.getValues(false, true);
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
			});
			if(!mm.FormUtil.contains(form.updateUrl, '?caller=', true)){
				form.updateUrl = form.updateUrl + "?caller=" + caller;
			}
			var params = [];
			if(grids.length > 0){
				var param = grids[0].GridUtil.getGridStore();
				if(grids[0].necessaryField.length > 0 && (param == null || param == '')){
					warnMsg('明细表还未添加数据,是否继续?', function(btn){
						if(btn == 'yes'){
							params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
						} else {
							return;
						}
					});
				} else {
					params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
				}
			}
			mm.update(r, params);
		}else{
			mm.checkForm(form);
		}
	},
	update: function(){
		var me = this, params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param = unescape(arguments[1].toString().replace(/\\/g,"%"));
		for(var i=2; i<arguments.length; i++) {  //兼容多参数
			params['param' + i] = unescape(arguments[i].toString().replace(/\\/g,"%"));
		}
		var form = Ext.getCmp('form');
		me.FormUtil.setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + form.updateUrl,
	   		params: params,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			me.FormUtil.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				showMessage('提示', '保存成功!', 1000);
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
		   		    	if(me.FormUtil.contains(window.location.href, '?', true)){
			   		    	window.location.href = window.location.href + '&formCondition=' + 
			   					formCondition + '&gridCondition=' + gridCondition;
			   		    } else {
			   		    	window.location.href = window.location.href + '?formCondition=' + 
			   					formCondition + '&gridCondition=' + gridCondition;
			   		    }
    				} else {
    					window.location.reload();
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
			   		    	if(me.FormUtil.contains(window.location.href, '?', true)){
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
	},
	/**
	 * @param allowEmpty 是否允许Grid为空
	 */
	onSubmit: function(id, allowEmpty){
		var me = this;
		var form = Ext.getCmp('form');
		if(form && form.getForm().isValid()){
//			var s = '';
			var grids = Ext.ComponentQuery.query('gridpanel');
			if(grids.length > 0){//check所有grid是否已修改
				var param = grids[0].GridUtil.getAllGridStore(grids[0]);
				if(grids[0].necessaryField && grids[0].necessaryField.length > 0 && (param == null || param == '') && (allowEmpty !== true)){
					showError("明细表还未添加数据,无法提交!");
					return;
				}
				Ext.each(grids, function(grid, index){
					if(grid.GridUtil){
						var msg = grid.GridUtil.checkGridDirty(grid);
						if(msg.length > 0){
//							s = s + '<br/>' + grid.GridUtil.checkGridDirty(grid);
						}
					}
				});
			}
			me.submit(id);
//			if(s == '' || s == '<br/>'){
//				me.submit(id);
//			} else {
//				Ext.MessageBox.show({
//				     title:'保存修改?',
//				     msg: '该单据已被修改:<br/>' + s + '<br/>提交前要先保存吗？',
//				     buttons: Ext.Msg.YESNOCANCEL,
//				     icon: Ext.Msg.WARNING,
//				     fn: function(btn){
//				    	 if(btn == 'yes'){
//				    		 me.onUpdate(form);
//				    	 } else if(btn == 'no'){
//				    		 me.submit(id);	
//				    	 } else {
//				    		 return;
//				    	 }
//				     }
//				});
//			}
		} else {
			me.checkForm();
		}
	},
	submit: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(!me.FormUtil.contains(form.submitUrl, '?caller=', true)){
			form.submitUrl = form.submitUrl + "?caller=" + caller;
		}
		me.FormUtil.setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + form.submitUrl,
	   		params: {
	   			id: id
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			me.FormUtil.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				me.FormUtil.getMultiAssigns(id, caller,form);
    				//submit成功后刷新页面进入可编辑的页面 
    				
    			} else {
    				if(localJson.exceptionInfo){
    	   				var str = localJson.exceptionInfo;
    	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    	   					str = str.replace('AFTERSUCCESS', '');
    	   					me.FormUtil.getMultiAssigns(id, caller,form);
    	   				}
    	   				showMessage("提示", str);return;
    	   			}
    			}
	   		}
		});
	}
});