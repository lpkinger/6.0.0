Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.NonPreSale', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.sale.NonPreSale','core.form.Panel','core.form.MultiField',
    		'core.button.Add','core.button.Save','core.button.Update','core.button.Delete','core.button.Submit','core.button.ResSubmit',
    		'core.button.Audit','core.button.ResAudit','core.button.Close','core.button.TurnSale','core.form.FileField',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.form.CheckBoxGroup','core.form.CheckBoxContainer',
			'scm.sale.buttons.Buttons1','scm.sale.buttons.Buttons2','scm.sale.buttons.Buttons3','scm.sale.buttons.Buttons4','scm.sale.buttons.Buttons5',
			'scm.sale.buttons.Buttons6','scm.sale.buttons.Buttons7','scm.sale.buttons.Buttons8','scm.sale.buttons.Buttons9','scm.sale.buttons.Buttons10',
			'scm.sale.buttons.Buttons11','scm.sale.buttons.Buttons12','scm.sale.buttons.Buttons13','scm.sale.buttons.Buttons14',
			'core.form.RadioGroup','core.form.SplitTextField','core.button.TurnAbNormalSale'
		
    	],
    init:function(){
    	var me = this;
    	this.control({
			'erpFormPanel': {/*
				afterrender: function(){
					var panel = parent.Ext.getCmp('tree-tab');
					if(panel && !panel.collapsed) {
						panel.toggleCollapse();
						
					}
				}
			*/
				
    /*			beforerender : function(){

					var panel = parent.Ext.getCmp('tree-tab');
					if(panel && !panel.collapsed) {
						panel.toggleCollapse();
//						window.location.reload();
						
					}
    			
    			}*/
			
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
    		
    		
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber('Sale');//自动添加编号
    				}
//    				me.beforeSave(form);
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
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addPreSale', '新增订单评审', 'jsps/scm/sale/preSale.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			},
    			afterrender: function(){
					var panel = parent.Ext.getCmp('tree-tab');
					if(panel && !panel.collapsed) {
						panel.toggleCollapse();
						
					}
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
					me.FormUtil.onSubmit(Ext.getCmp('ps_id').value);
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

				}
    		},
    		'erpTurnAbNormalSaleButton':{
	        	click: function(m){

    				warnMsg("确定要转入销售单吗?", function(btn){
    					if(btn == 'yes'){
    			            Ext.Ajax.request({
    			                url: basePath + 'scm/sale/turnPreSaleToSale.action',
    			                params: {
    			                	type:'nonsale',
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
			}
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
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
	getAddItems:function(){
		var returnItems = [
				             {
			  					   xtype:'fieldset',
			  					   title:'<h2>销售部</h2>',
			  					   columnWidth:1,
			  					   collapsible: true,
			  					   frame:true,
								   group:2,
								   groupName:'评审状态',
			  					   items:[
			  					          {
//			  					        	 frame:true,
			  					          layout:'column',
			  					          style: {background:'#FFFAFA'},
			  					          items:[ {
					    							 xtype : 'radiogroup',
					    							 name : 'rg_1',
					    							 id : 'rg_1',
					    							 logic:'ignore',
					    							  frame:true,
					    							 columnWidth:0.4,
					    							 fieldLabel:'价格',
					    							 items: [
					    							         { boxLabel:'通过',name:'rg_1',inputValue:'1'},
					    							         { boxLabel:'不通过',name:'rg_1',inputValue:'2'},
					    							         { boxLabel:'条件通过',name:'rg_1',inputValue:'3'}
					    							         ],
					    							 listeners:{
					    								 change: function(rg){
					    									 var v = rg.getValue();
					    									 if(v.rg_1 == '3'){
					    										 if(Ext.getCmp('ta_1')&&Ext.getCmp('tf_1')){
					    											 Ext.getCmp('ta_1').setReadOnly(false);
					    											 Ext.getCmp('tf_1').setReadOnly(false);
					    										 }
					    									 }else {
				    											 Ext.getCmp('ta_1').setReadOnly(true);
				    											 Ext.getCmp('tf_1').setReadOnly(true);
					    									 }
					    									 
					    								 }
					    							 }
					    						 },
					    						 
					    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_1',id:'tf_1' },
					    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_1',id:'ta_1'}]
			  					          },{
			  					        	  layout:'column',
			  					        	 frame:false,
			  					        	  items:[ {
						    							 xtype : 'radiogroup',
						    							 name : 'rg_2',
						    							 id : 'rg_2',
						    							 logic:'ignore',
						    							 columnWidth:0.4,
						    							 fieldStyle: "background:#FFFAFA;color:#515151;",
						    							 fieldLabel:'交货方式',
						    							 items: [
						    							         { boxLabel:'通过',name:'rg_2',inputValue:'1'},
						    							         { boxLabel:'不通过',name:'rg_2',inputValue:'2'},
						    							         { boxLabel:'条件通过',name:'rg_2',inputValue:'3'}],
						    							 listeners:{
						    								 change: function(rg){
						    									 var v = rg.getValue();
						    									 if(v.rg_2 == '3'){
						    										 if(Ext.getCmp('ta_2')&&Ext.getCmp('tf_2')){
						    											 Ext.getCmp('ta_2').setReadOnly(false);
						    											 Ext.getCmp('tf_2').setReadOnly(false);
						    										 }
						    									 }else {
					    											 Ext.getCmp('ta_2').setReadOnly(true);
					    											 Ext.getCmp('tf_2').setReadOnly(true);
						    									 }
						    									 
						    								 }
						    							 }
			  					        	  
						    						 },
						    						 
						    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_2',id:'tf_2' },
						    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_2',id:'ta_2'}]
			  					          }
			  					         ]
			  				  }, {
			  					   xtype:'fieldset',
			  					   title:'<h2>研发中心(硬件)</h2>',
			  					   columnWidth:1,
			  					   collapsible: true,
			  					   frame:true,
			  					   name:'nongroup',
								   group:2,
								   groupName:'评审状态',
			  					   items:[
			  					          {
			  					          layout:'column',
			  					          style: {background:'#FFFAFA'},
			  					          items:[ {
					    							 xtype : 'radiogroup',
					    							 name : 'rg_33',
					    							 id : 'rg_33',
					    							 logic:'ignore',
					    							 columnWidth:0.4,
					    							 fieldLabel:'产品符合客户要求',
					    							 items: [
					    							         { boxLabel:'通过',name:'rg_33',inputValue:'1'},
					    							         { boxLabel:'不通过',name:'rg_33',inputValue:'2'},
					    							         { boxLabel:'条件通过',name:'rg_33',inputValue:'3'}
					    							         ],
					    							 listeners:{
					    								 change: function(rg){
					    									 var v = rg.getValue();
					    									 if(v.rg_33 == '3'){
					    										 if(Ext.getCmp('ta_33')&&Ext.getCmp('tf_33')){
					    											 Ext.getCmp('ta_33').setReadOnly(false);
					    											 Ext.getCmp('tf_33').setReadOnly(false);
					    										 }
					    									 }else {
				    											 Ext.getCmp('ta_33').setReadOnly(true);
				    											 Ext.getCmp('tf_33').setReadOnly(true);
					    									 }
					    									 
					    								 }
					    							 }
					    						 },
					    						 
					    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_33',id:'tf_33' },
					    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_33',id:'ta_33'}]
			  					          }
			  					         ]
			  				  },{
			  					   xtype:'fieldset',
			  					   title:'<h2>研发中心(软件)</h2>',
			  					   columnWidth:1,
			  					   collapsible: true,
			  					   frame:true,
			  					   name:'nongroup',
								   group:2,
								   groupName:'评审状态',
			  					   items:[
			  					          {
			  					          layout:'column',
			  					          style: {background:'#FFFAFA'},
			  					          items:[ {
					    							 xtype : 'radiogroup',
					    							 name : 'rg_34',
					    							 id : 'rg_34',
					    							 logic:'ignore',
					    							 columnWidth:0.4,
					    							 fieldLabel:'软件可达成客户要求',
					    							 items: [
					    							         { boxLabel:'通过',name:'rg_34',inputValue:'1'},
					    							         { boxLabel:'不通过',name:'rg_34',inputValue:'2'},
					    							         { boxLabel:'条件通过',name:'rg_34',inputValue:'3'}
					    							         ],
					    							 listeners:{
					    								 change: function(rg){
					    									 var v = rg.getValue();
					    									 if(v.rg_34 == '3'){
					    										 if(Ext.getCmp('ta_34')&&Ext.getCmp('tf_33')){
					    											 Ext.getCmp('ta_34').setReadOnly(false);
					    											 Ext.getCmp('tf_34').setReadOnly(false);
					    										 }
					    									 }else {
				    											 Ext.getCmp('ta_34').setReadOnly(true);
				    											 Ext.getCmp('tf_34').setReadOnly(true);
					    									 }
					    									 
					    								 }
					    							 }
					    						 },
					    						 
					    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_34',id:'tf_34' },
					    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_34',id:'ta_34'}]
			  					          }
			  					         ]
			  				  },{
			  					   xtype:'fieldset',
			  					   title:'<h2>研发中心(包装)</h2>',
			  					   columnWidth:1,
			  					   collapsible: true,
			  					   frame:true,
			  					   name:'nongroup',
								   group:2,
								   groupName:'评审状态',
			  					   items:[
			  					          {
			  					          layout:'column',
			  					          style: {background:'#FFFAFA'},
			  					          items:[ {
					    							 xtype : 'radiogroup',
					    							 name : 'rg_35',
					    							 id : 'rg_35',
					    							 logic:'ignore',
					    							 columnWidth:0.4,
					    							 fieldLabel:'结构承认',
					    							 items: [
					    							         { boxLabel:'通过',name:'rg_35',inputValue:'1'},
					    							         { boxLabel:'不通过',name:'rg_35',inputValue:'2'},
					    							         { boxLabel:'条件通过',name:'rg_35',inputValue:'3'}
					    							         ],
					    							 listeners:{
					    								 change: function(rg){
					    									 var v = rg.getValue();
					    									 if(v.rg_35 == '3'){
					    										 if(Ext.getCmp('ta_35')&&Ext.getCmp('tf_33')){
					    											 Ext.getCmp('ta_35').setReadOnly(false);
					    											 Ext.getCmp('tf_35').setReadOnly(false);
					    										 }
					    									 }else {
				    											 Ext.getCmp('ta_35').setReadOnly(true);
				    											 Ext.getCmp('tf_35').setReadOnly(true);
					    									 }
					    									 
					    								 }
					    							 }
					    						 },
					    						 
					    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_35',id:'tf_35' },
					    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_35',id:'ta_35'}]
			  					          }, {
				  					          layout:'column',
				  					          style: {background:'#FFFAFA'},
				  					          items:[ {
						    							 xtype : 'radiogroup',
						    							 name : 'rg_36',
						    							 id : 'rg_36',
						    							 logic:'ignore',
						    							 columnWidth:0.4,
						    							 fieldLabel:'包材承认',
						    							 items: [
						    							         { boxLabel:'通过',name:'rg_36',inputValue:'1'},
						    							         { boxLabel:'不通过',name:'rg_36',inputValue:'2'},
						    							         { boxLabel:'条件通过',name:'rg_36',inputValue:'3'}
						    							         ],
						    							 listeners:{
						    								 change: function(rg){
						    									 var v = rg.getValue();
						    									 if(v.rg_36 == '3'){
						    										 if(Ext.getCmp('ta_36')&&Ext.getCmp('tf_33')){
						    											 Ext.getCmp('ta_36').setReadOnly(false);
						    											 Ext.getCmp('tf_36').setReadOnly(false);
						    										 }
						    									 }else {
					    											 Ext.getCmp('ta_36').setReadOnly(true);
					    											 Ext.getCmp('tf_36').setReadOnly(true);
						    									 }
						    									 
						    								 }
						    							 }
						    						 },
						    						 
						    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_36',id:'tf_36' },
						    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_36',id:'ta_36'}]
				  					          }
			  					         ]
			  				  },{
			  					   xtype:'fieldset',
			  					   title:'<h2>研发中心(项目)</h2>',
			  					   columnWidth:1,
			  					   collapsible: true,
			  					   frame:true,
			  					   name:'nongroup',
								   group:2,
								   groupName:'评审状态',
			  					   items:[
			  					          {
			  					          layout:'column',
			  					          style: {background:'#FFFAFA'},
			  					          items:[ {
					    							 xtype : 'radiogroup',
					    							 name : 'rg_37',
					    							 id : 'rg_37',
					    							 logic:'ignore',
					    							 columnWidth:0.4,
					    							 fieldLabel:'开发完成时间',
					    							 items: [
					    							         { boxLabel:'通过',name:'rg_37',inputValue:'1'},
					    							         { boxLabel:'不通过',name:'rg_37',inputValue:'2'},
					    							         { boxLabel:'条件通过',name:'rg_37',inputValue:'3'}
					    							         ],
					    							 listeners:{
					    								 change: function(rg){
					    									 var v = rg.getValue();
					    									 if(v.rg_37 == '3'){
					    										 if(Ext.getCmp('ta_37')&&Ext.getCmp('tf_33')){
					    											 Ext.getCmp('ta_37').setReadOnly(false);
					    											 Ext.getCmp('tf_37').setReadOnly(false);
					    										 }
					    									 }else {
				    											 Ext.getCmp('ta_37').setReadOnly(true);
				    											 Ext.getCmp('tf_37').setReadOnly(true);
					    									 }
					    									 
					    								 }
					    							 }
					    						 },
					    						 
					    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_37',id:'tf_37' },
					    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_37',id:'ta_37'}]
			  					          }, {
				  					          layout:'column',
				  					          style: {background:'#FFFAFA'},
				  					          items:[ {
						    							 xtype : 'radiogroup',
						    							 name : 'rg_38',
						    							 id : 'rg_38',
						    							 logic:'ignore',
						    							 columnWidth:0.4,
						    							 fieldLabel:'BOM完成时间',
						    							 items: [
						    							         { boxLabel:'通过',name:'rg_38',inputValue:'1'},
						    							         { boxLabel:'不通过',name:'rg_38',inputValue:'2'},
						    							         { boxLabel:'条件通过',name:'rg_38',inputValue:'3'}
						    							         ],
						    							 listeners:{
						    								 change: function(rg){
						    									 var v = rg.getValue();
						    									 if(v.rg_38 == '3'){
						    										 if(Ext.getCmp('ta_38')&&Ext.getCmp('tf_33')){
						    											 Ext.getCmp('ta_38').setReadOnly(false);
						    											 Ext.getCmp('tf_38').setReadOnly(false);
						    										 }
						    									 }else {
					    											 Ext.getCmp('ta_38').setReadOnly(true);
					    											 Ext.getCmp('tf_38').setReadOnly(true);
						    									 }
						    									 
						    								 }
						    							 }
						    						 },
						    						 
						    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_38',id:'tf_38' },
						    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_38',id:'ta_38'}]
				  					          }, {
					  					          layout:'column',
					  					          style: {background:'#FFFAFA'},
					  					          items:[ {
							    							 xtype : 'radiogroup',
							    							 name : 'rg_39',
							    							 id : 'rg_39',
							    							 logic:'ignore',
							    							 columnWidth:0.4,
							    							 fieldLabel:'产品认证要求',
							    							 items: [
							    							         { boxLabel:'通过',name:'rg_39',inputValue:'1'},
							    							         { boxLabel:'不通过',name:'rg_39',inputValue:'2'},
							    							         { boxLabel:'条件通过',name:'rg_39',inputValue:'3'}
							    							         ],
							    							 listeners:{
							    								 change: function(rg){
							    									 var v = rg.getValue();
							    									 if(v.rg_39 == '3'){
							    										 if(Ext.getCmp('ta_39')&&Ext.getCmp('tf_33')){
							    											 Ext.getCmp('ta_39').setReadOnly(false);
							    											 Ext.getCmp('tf_39').setReadOnly(false);
							    										 }
							    									 }else {
						    											 Ext.getCmp('ta_39').setReadOnly(true);
						    											 Ext.getCmp('tf_39').setReadOnly(true);
							    									 }
							    									 
							    								 }
							    							 }
							    						 },
							    						 
							    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_39',id:'tf_39' },
							    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_39',id:'ta_39'}]
					  					          }
			  					         ]
			  				  },
			  				 {
			  					   xtype:'fieldset',
			  					   title:'<h2>PMC</h2>',
			  					   columnWidth:1,
			  					   collapsible: true,
			  					   frame:false,
			  					   name:'psgroup',
								   group:2,
								   groupName:'评审状态',
			  					   items:[
			  					          {
			  			    					   layout:'column',
			  			    					   columnWidth:1, 
			  			    					   xtype:'form',
			  			    					   frame:true,
			  			    					   id:'panel1',
			  			    					   bodyStyle:'border:1px solid #CDC5BF;background-color:#f0f0f0',
			  			    					   bodyBorder:true,
			  			    					   border:true, 
			  			    					   columnWidth:1,
			  			    					   title:'长交期物料评审',
			  			    					   bodyPadding: 5, 
			  			    					   items:[{ xtype : 'textfield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'LCD',maxLength:100,name:'tf_19',id:'tf_19' },
			  			    					        { xtype : 'textfield', logic:'ignore',columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'TP',maxLength:100,name:'tf_20',id:'tf_20' },
			  			    					      { xtype : 'textfield',logic:'ignore',columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'电源',maxLength:100,name:'tf_21',id:'tf_21' },
			  			    					    { xtype : 'textfield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'壳料',maxLength:100,name:'tf_22',id:'tf_22' },
			  			    					  { xtype : 'textfield', logic:'ignore',columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'电池',maxLength:100,name:'tf_23',id:'tf_23' },
			  			    					{ xtype : 'textfield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'摄像头',maxLength:100,name:'tf_24',id:'tf_24' },
			  			    					{ xtype : 'textfield', logic:'ignore',columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'PCB',maxLength:100,name:'tf_25',id:'tf_25' },
			  			    					{
					    							 xtype : 'radiogroup',
					    							 id:'rg_3',
					    							 name:'rg_3',
					    							 logic:'ignore',
					    							 columnWidth:0.4,
					    							 fieldLabel:'评审',
					    							 items: [
					    							         { boxLabel:'通过',name:'rg_3',inputValue:'1'},
					    							         { boxLabel:'不通过',name:'rg_3',inputValue:'2'},
					    							         { boxLabel:'条件通过',name:'rg_3',inputValue:'3'}],
					    							 listeners:{
					    								 change: function(rg){
					    									 var v = rg.getValue();
					    									 if(v.rg_3 == '3'){
					    										 if(Ext.getCmp('ta_3')&&Ext.getCmp('tf_3')){
					    											 Ext.getCmp('ta_3').setReadOnly(false);
					    											 Ext.getCmp('tf_3').setReadOnly(false);
					    										 }
					    									 }else {
				    											 Ext.getCmp('ta_3').setReadOnly(true);
				    											 Ext.getCmp('tf_3').setReadOnly(true);
					    									 }
					    									 
					    								 }
					    							 }
					    						 },
					    						 
					    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_3',id:'tf_3' },
					    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_3',id:'ta_3'}
					    						 ]
			  			    				  
			  					          },{
		  			    					   layout:'column',
		  			    					   columnWidth:1, 
		  			    					   xtype:'form',
		  			    					   frame:true,
		  			    					   id:'panel2',
		  			    					   bodyStyle:'border:1px solid #CDC5BF;background-color:#f0f0f0',
		  			    					   bodyBorder:true,
		  			    					   border:true, 
		  			    					   columnWidth:1,
		  			    					   title:'生产交期评估',
		  			    					   bodyPadding: 5, 
		  			    					   items:[{ xtype : 'textfield', logic:'ignore',columnWidth:0.20, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'产能(组装)',maxLength:100,name:'tf_26',id:'tf_26' },
		  			    					        { xtype : 'label', logic:'ignore',columnWidth:0.05, text:'(K/天)'},
		  			    					      { xtype : 'textfield', logic:'ignore',columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'SMT时间',maxLength:100,name:'tf_27',id:'tf_27' },
		  			    					    { xtype : 'textfield', logic:'ignore',columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'后焊时间',maxLength:100,name:'tf_28',id:'tf_28' },
		  			    					  { xtype : 'textfield', logic:'ignore',columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'前加工时间',maxLength:100,name:'tf_29',id:'tf_29' },
		  			    					{ xtype : 'textfield', logic:'ignore',columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'组装时间',maxLength:100 ,name:'tf_30',id:'tf_30'},
		  			    					{ xtype : 'textfield',logic:'ignore',columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'包装时间',maxLength:100 ,name:'tf_31',id:'tf_31'},
		  			    					{ xtype : 'textfield', logic:'ignore',columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'可验货时间',maxLength:100 ,name:'tf_32',id:'tf_32'},
		  			    					{
				    							 xtype : 'radiogroup',
				    							 name : 'rg_4',
				    							 id : 'rg_4',
				    							 logic:'ignore',
				    							 columnWidth:0.4,
				    							 fieldLabel:'评审',
				    							 items: [
				    							         { boxLabel:'通过',name:'rg_4',inputValue:'1'},
				    							         { boxLabel:'不通过',name:'rg_4',inputValue:'2'},
				    							         { boxLabel:'条件通过',name:'rg_4',inputValue:'3'}],
						    							 listeners:{
						    								 change: function(rg){
						    									 var v = rg.getValue();
						    									 if(v.rg_4 == '3'){
						    										 if(Ext.getCmp('ta_4')&&Ext.getCmp('tf_4')){
						    											 Ext.getCmp('ta_4').setReadOnly(false);
						    											 Ext.getCmp('tf_4').setReadOnly(false);
						    										 }
						    									 }else {
					    											 Ext.getCmp('ta_4').setReadOnly(true);
					    											 Ext.getCmp('tf_4').setReadOnly(true);
						    									 }
						    									 
						    								 }
						    							 }				    							         
				    						 },
				    						 
				    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_4',id:'tf_4' },
				    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_4',id:'ta_4'}
				    						 ]
		  			    				  
		  					          }
			  					         ]
			  				  },
			  				 {
			  					   xtype:'fieldset',
			  					   title:'<h2>采购</h2>',
			  					   columnWidth:1,
			  					   collapsible: true,
			  					   frame:false,
								   group:2,
								   groupName:'评审状态',
			  					   items:[
			  					          {
			  					          layout:'column',
			  					          style: {background:'#FFFAFA'},
			  					          items:[ {
					    							 xtype : 'radiogroup',
					    							 name : 'rg_5',
					    							 id : 'rg_5',
					    							 logic:'ignore',
					    							 columnWidth:0.4,
					    							 fieldLabel:'物料供应评估',
					    							 items: [
					    							         { boxLabel:'通过',name:'rg_5',inputValue:'1'},
					    							         { boxLabel:'不通过',name:'rg_5',inputValue:'2'},
					    							         { boxLabel:'条件通过',name:'rg_5',inputValue:'3'}],
							    							 listeners:{
							    								 change: function(rg){
							    									 var v = rg.getValue();
							    									 if(v.rg_5 == '3'){
							    										 if(Ext.getCmp('ta_5')&&Ext.getCmp('tf_5')){
							    											 Ext.getCmp('ta_5').setReadOnly(false);
							    											 Ext.getCmp('tf_5').setReadOnly(false);
							    										 }
							    									 }else {
						    											 Ext.getCmp('ta_5').setReadOnly(true);
						    											 Ext.getCmp('tf_5').setReadOnly(true);
							    									 }
							    									 
							    								 }
							    							 }
					    						 },
					    						 
					    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_5',id:'tf_5' },
					    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_5',id:'ta_5'}]
			  					          }
			  					         ]
			  				   },
			  				 {
			  					   xtype:'fieldset',
			  					   title:'<h2>生产</h2>',
			  					   columnWidth:1,
			  					   collapsible: true,
			  					   frame:false,
								   group:2,
								   groupName:'评审状态',
			  					   items:[
			  					          {
			  					          layout:'column',
			  					          style: {background:'#FFFAFA'},
			  					          items:[ {
					    							 xtype : 'radiogroup',
					    							 name : 'rg_6',
					    							 id : 'rg_6',
					    							 logic:'ignore',
					    							 columnWidth:0.4,
					    							 fieldLabel:'人员准备',
					    							 items: [
					    							         { boxLabel:'通过',name:'rg_6',inputValue:'1'},
					    							         { boxLabel:'不通过',name:'rg_6',inputValue:'2'},
					    							         { boxLabel:'条件通过',name:'rg_6',inputValue:'3'}],
							    							 listeners:{
							    								 change: function(rg){
							    									 var v = rg.getValue();
							    									 if(v.rg_6 == '3'){
							    										 if(Ext.getCmp('ta_6')&&Ext.getCmp('tf_6')){
							    											 Ext.getCmp('ta_6').setReadOnly(false);
							    											 Ext.getCmp('tf_6').setReadOnly(false);
							    										 }
							    									 }else {
						    											 Ext.getCmp('ta_6').setReadOnly(true);
						    											 Ext.getCmp('tf_6').setReadOnly(true);
							    									 }
							    									 
							    								 }
							    							 }
					    						 },
					    						 
					    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_6',id:'tf_6' },
					    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_6',id:'ta_6'}]
			  					          },{
			  					        	  layout:'column',
			  					        	  items:[ {
						    							 xtype : 'radiogroup',
						    							 name : 'rg_7',
						    							 id : 'rg_7',
						    							 logic:'ignore',
						    							 columnWidth:0.4,
						    							 fieldStyle: "background:#FFFAFA;color:#515151;",
						    							 fieldLabel:'产品培训',
						    							 items: [
						    							         { boxLabel:'通过',name:'rg_7',inputValue:'1'},
						    							         { boxLabel:'不通过',name:'rg_7',inputValue:'2'},
						    							         { boxLabel:'条件通过',name:'rg_7',inputValue:'3'}],
								    							 listeners:{
								    								 change: function(rg){
								    									 var v = rg.getValue();
								    									 if(v.rg_7 == '3'){
								    										 if(Ext.getCmp('ta_7')&&Ext.getCmp('tf_7')){
								    											 Ext.getCmp('ta_7').setReadOnly(false);
								    											 Ext.getCmp('tf_7').setReadOnly(false);
								    										 }
								    									 }else {
							    											 Ext.getCmp('ta_7').setReadOnly(true);
							    											 Ext.getCmp('tf_7').setReadOnly(true);
								    									 }
								    									 
								    								 }
								    							 }
						    						 },
						    						 
						    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_7',id:'tf_7' },
						    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_7',id:'ta_7'}]
			  					          },{
			  					        	  layout:'column',
			  					        	  items:[ {
						    							 xtype : 'radiogroup',
						    							 name : 'rg_8',
						    							 id : 'rg_8',
						    							 logic:'ignore',
						    							 columnWidth:0.4,
						    							 fieldStyle: "background:#FFFAFA;color:#515151;",
						    							 fieldLabel:'产能预估',
						    							 items: [
						    							         { boxLabel:'通过',name:'rg_8',inputValue:'1'},
						    							         { boxLabel:'不通过',name:'rg_8',inputValue:'2'},
						    							         { boxLabel:'条件通过',name:'rg_8',inputValue:'3'}],
								    							 listeners:{
								    								 change: function(rg){
								    									 var v = rg.getValue();
								    									 if(v.rg_8 == '3'){
								    										 if(Ext.getCmp('ta_8')&&Ext.getCmp('tf_8')){
								    											 Ext.getCmp('ta_8').setReadOnly(false);
								    											 Ext.getCmp('tf_8').setReadOnly(false);
								    										 }
								    									 }else {
							    											 Ext.getCmp('ta_8').setReadOnly(true);
							    											 Ext.getCmp('tf_8').setReadOnly(true);
								    									 }
								    									 
								    								 }
								    							 }
						    						 },
						    						 
						    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_8',id:'tf_8' },
						    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_8',id:'ta_8'}]
			  					          }
			  					         ]
			  				   },
							 {
		  					   xtype:'fieldset',
		  					   title:'<h2>配套中心</h2>',
		  					   columnWidth:1,
		  					   collapsible: true,
		  					   frame:false,
							   group:2,
							   groupName:'评审状态',
		  					   items:[
		  					          {
		  					          layout:'column',
		  					          style: {background:'#FFFAFA'},
		  					          items:[ {
				    							 xtype : 'radiogroup',
				    							 name : 'rg_9',
				    							 id : 'rg_9',
				    							 logic:'ignore',
				    							 columnWidth:0.4,
				    							 fieldLabel:'已量产',
				    							 items: [
				    							         { boxLabel:'通过',name:'rg_9',inputValue:'1'},
				    							         { boxLabel:'不通过',name:'rg_9',inputValue:'2'},
				    							         { boxLabel:'条件通过',name:'rg_9',inputValue:'3'}],
						    							 listeners:{
						    								 change: function(rg){
						    									 var v = rg.getValue();
						    									 if(v.rg_9 == '3'){
						    										 if(Ext.getCmp('ta_9')&&Ext.getCmp('tf_9')){
						    											 Ext.getCmp('ta_9').setReadOnly(false);
						    											 Ext.getCmp('tf_9').setReadOnly(false);
						    										 }
						    									 }else {
					    											 Ext.getCmp('ta_9').setReadOnly(true);
					    											 Ext.getCmp('tf_9').setReadOnly(true);
						    									 }
						    									 
						    								 }
						    							 }
				    						 },
				    						 
				    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_9',id:'tf_9' },
				    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_9',id:'ta_9'}]
		  					          },{
		  					        	  layout:'column',
		  					        	  items:[ {
					    							 xtype : 'radiogroup',
					    							 name : 'rg_10',
					    							 id : 'rg_10',
					    							 logic:'ignore',
					    							 columnWidth:0.4,
					    							 fieldStyle: "background:#FFFAFA;color:#515151;",
					    							 fieldLabel:'模具产能',
					    							 items: [
					    							         { boxLabel:'通过',name:'rg_10',inputValue:'1'},
					    							         { boxLabel:'不通过',name:'rg_10',inputValue:'2'},
					    							         { boxLabel:'条件通过',name:'rg_10',inputValue:'3'}],
							    							 listeners:{
							    								 change: function(rg){
							    									 var v = rg.getValue();
							    									 if(v.rg_10 == '3'){
							    										 if(Ext.getCmp('ta_10')&&Ext.getCmp('tf_10')){
							    											 Ext.getCmp('ta_10').setReadOnly(false);
							    											 Ext.getCmp('tf_10').setReadOnly(false);
							    										 }
							    									 }else {
						    											 Ext.getCmp('ta_10').setReadOnly(true);
						    											 Ext.getCmp('tf_10').setReadOnly(true);
							    									 }
							    									 
							    								 }
							    							 }
					    						 },
					    						 
					    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_10',id:'tf_10' },
					    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_10',id:'ta_10'}]
		  					          }
		  					         ]
		  				   },
		  				   {
		  					   xtype:'fieldset',
		  					   title:'<h2>工程部</h2>',
		  					   columnWidth:1,
		  					   collapsible: true,
		  					   frame:false,
							   group:2,
							   groupName:'评审状态',
		  					   items:[
		  					          {
		  					          layout:'column',
		  					          style: {background:'#FFFAFA'},
		  					          items:[ {
				    							 xtype : 'radiogroup',
				    							 name : 'rg_11',
				    							 id : 'rg_11',
				    							 logic:'ignore',
				    							 columnWidth:0.4,
//				    							 fieldStyle: "background:#FFFAFA;color:#515151;",
				    							 fieldLabel:'已完成PP',
				    							 items: [
				    							         { boxLabel:'通过',name:'rg_11',inputValue:'1'},
				    							         { boxLabel:'不通过',name:'rg_11',inputValue:'2'},
				    							         { boxLabel:'条件通过',name:'rg_11',inputValue:'3'}],
						    							 listeners:{
						    								 change: function(rg){
						    									 var v = rg.getValue();
						    									 if(v.rg_11 == '3'){
						    										 if(Ext.getCmp('ta_11')&&Ext.getCmp('tf_11')){
						    											 Ext.getCmp('ta_11').setReadOnly(false);
						    											 Ext.getCmp('tf_11').setReadOnly(false);
						    										 }
						    									 }else {
					    											 Ext.getCmp('ta_11').setReadOnly(true);
					    											 Ext.getCmp('tf_11').setReadOnly(true);
						    									 }
						    									 
						    								 }
						    							 }
				    						 },
				    						 
				    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_11',id:'tf_11' },
				    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_11',id:'ta_11'}]
		  					          },{
		  					        	  layout:'column',
		  					        	  items:[ {
					    							 xtype : 'radiogroup',
					    							 name : 'rg_12',
					    							 id : 'rg_12',
					    							 logic:'ignore',
					    							 columnWidth:0.4,
					    							 fieldStyle: "background:#FFFAFA;color:#515151;",
					    							 fieldLabel:'具备量产性',
					    							 items: [
					    							         { boxLabel:'通过',name:'rg_12',inputValue:'1'},
					    							         { boxLabel:'不通过',name:'rg_12',inputValue:'2'},
					    							         { boxLabel:'条件通过',name:'rg_12',inputValue:'3'}],
							    							 listeners:{
							    								 change: function(rg){
							    									 var v = rg.getValue();
							    									 if(v.rg_12 == '3'){
							    										 if(Ext.getCmp('ta_12')&&Ext.getCmp('tf_12')){
							    											 Ext.getCmp('ta_12').setReadOnly(false);
							    											 Ext.getCmp('tf_12').setReadOnly(false);
							    										 }
							    									 }else {
						    											 Ext.getCmp('ta_12').setReadOnly(true);
						    											 Ext.getCmp('tf_12').setReadOnly(true);
							    									 }
							    									 
							    								 }
							    							 }
					    						 },
					    						 
					    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_12',id:'tf_12' },
					    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_12',id:'ta_12'}]
		  					          },{
		  					        	  layout:'column',
		  					        	  items:[ {
					    							 xtype : 'radiogroup',
					    							 name : 'rg_13',
					    							 id : 'rg_13',
					    							 logic:'ignore',
					    							 columnWidth:0.4,
					    							 fieldStyle: "background:#FFFAFA;color:#515151;",
					    							 fieldLabel:'治具等已准备',
					    							 items: [
					    							         { boxLabel:'通过',name:'rg_13',inputValue:'1'},
					    							         { boxLabel:'不通过',name:'rg_13',inputValue:'2'},
					    							         { boxLabel:'条件通过',name:'rg_13',inputValue:'3'}],
							    							 listeners:{
							    								 change: function(rg){
							    									 var v = rg.getValue();
							    									 if(v.rg_13 == '3'){
							    										 if(Ext.getCmp('ta_13')&&Ext.getCmp('tf_13')){
							    											 Ext.getCmp('ta_13').setReadOnly(false);
							    											 Ext.getCmp('tf_13').setReadOnly(false);
							    										 }
							    									 }else {
						    											 Ext.getCmp('ta_13').setReadOnly(true);
						    											 Ext.getCmp('tf_13').setReadOnly(true);
							    									 }
							    									 
							    								 }
							    							 }
					    						 },
					    						 
					    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_13',id:'tf_13' },
					    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_13',id:'ta_13'}]
		  					          }
		  					         ]

		  				   },{
		  					   xtype:'fieldset',
		  					   title:'<h2>法务</h2>',
		  					   columnWidth:1,
		  					   collapsible: true,
		  					   frame:false,
							   group:2,
							   groupName:'评审状态',
		  					   items:[
		  					          {
		  					          layout:'column',
		  					          style: {background:'#FFFAFA'},
		  					          items:[ {
				    							 xtype : 'radiogroup',
				    							 name : 'rg_14',
				    							 id : 'rg_14',
				    							 logic:'ignore',
				    							 columnWidth:0.4,
				    							 fieldLabel:'订单合同',
				    							 items: [
				    							         { boxLabel:'通过',name:'rg_14',inputValue:'1'},
				    							         { boxLabel:'不通过',name:'rg_14',inputValue:'2'},
				    							         { boxLabel:'条件通过',name:'rg_14',inputValue:'3'}],
						    							 listeners:{
						    								 change: function(rg){
						    									 var v = rg.getValue();
						    									 if(v.rg_14 == '3'){
						    										 if(Ext.getCmp('ta_14')&&Ext.getCmp('tf_14')){
						    											 Ext.getCmp('ta_14').setReadOnly(false);
						    											 Ext.getCmp('tf_14').setReadOnly(false);
						    										 }
						    									 }else {
					    											 Ext.getCmp('ta_14').setReadOnly(true);
					    											 Ext.getCmp('tf_14').setReadOnly(true);
						    									 }
						    									 
						    								 }
						    							 }
				    						 },
				    						 
				    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_14',id:'tf_14' },
				    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_14',id:'ta_14'}]
		  					          }
		  					         ]
		  				   },{
		  					   xtype:'fieldset',
		  					   title:'<h2>质量部</h2>',
		  					   columnWidth:1,
		  					   collapsible: true,
		  					   frame:false,
		  					  
							   group:2,
							   groupName:'评审状态',
		  					   items:[
		  					          {
		  					          layout:'column',
		  					          style: {background:'#FFFAFA'},
		  					          items:[ {
				    							 xtype : 'radiogroup',
				    							 name : 'rg_15',
				    							 id : 'rg_15',
				    							 logic:'ignore',
				    							 columnWidth:0.4,
				    							 fieldLabel:'产品标准是否明确',
				    							 items: [
				    							         { boxLabel:'通过',name:'rg_15',inputValue:'1'},
				    							         { boxLabel:'不通过',name:'rg_15',inputValue:'2'},
				    							         { boxLabel:'条件通过',name:'rg_15',inputValue:'3'}],
						    							 listeners:{
						    								 change: function(rg){
						    									 var v = rg.getValue();
						    									 if(v.rg_15 == '3'){
						    										 if(Ext.getCmp('ta_15')&&Ext.getCmp('tf_15')){
						    											 Ext.getCmp('ta_15').setReadOnly(false);
						    											 Ext.getCmp('tf_15').setReadOnly(false);
						    										 }
						    									 }else {
					    											 Ext.getCmp('ta_15').setReadOnly(true);
					    											 Ext.getCmp('tf_15').setReadOnly(true);
						    									 }
						    									 
						    								 }
						    							 }
				    						 },
				    						 
				    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_15',id:'tf_15' },
				    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_15',id:'ta_15'}]
		  					          },
		  					          {
			  					          layout:'column',
			  					          style: {background:'#FFFAFA'},
			  					          items:[ {
					    							 xtype : 'radiogroup',
					    							 name : 'rg_40',
					    							 id : 'rg_40',
					    							 logic:'ignore',
					    							 name:'nongroup',
					    							 columnWidth:0.4,
					    							 fieldLabel:'满足环保要求',
					    							 items: [
					    							         { boxLabel:'通过',name:'rg_40',inputValue:'1'},
					    							         { boxLabel:'不通过',name:'rg_40',inputValue:'2'},
					    							         { boxLabel:'条件通过',name:'rg_40',inputValue:'3'}],
							    							 listeners:{
							    								 change: function(rg){
							    									 var v = rg.getValue();
							    									 if(v.rg_40 == '3'){
							    										 if(Ext.getCmp('ta_40')&&Ext.getCmp('tf_15')){
							    											 Ext.getCmp('ta_40').setReadOnly(false);
							    											 Ext.getCmp('tf_40').setReadOnly(false);
							    										 }
							    									 }else {
						    											 Ext.getCmp('ta_40').setReadOnly(true);
						    											 Ext.getCmp('tf_40').setReadOnly(true);
							    									 }
							    									 
							    								 }
							    							 }
					    						 },
					    						 
					    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_40',id:'tf_40' },
					    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_40',id:'ta_40'}]
			  					          }
			  					         
		  					         ]
		  				   },{
		  					   xtype:'fieldset',
		  					   title:'<h2>财务部</h2>',
		  					   columnWidth:1,
		  					   collapsible: true,
		  					   frame:false,
							   group:2,
							   groupName:'评审状态',
		  					   items:[
		  					          {
		  					          layout:'column',
		  					          style: {background:'#FFFAFA'},
		  					          items:[ {
				    							 xtype : 'radiogroup',
				    							 name : 'rg_16',
				    							 id : 'rg_16',
				    							 logic:'ignore',
				    							 columnWidth:0.4,
				    							 fieldLabel:'价格',
				    							 items: [
				    							         { boxLabel:'通过',name:'rg_16',inputValue:'1'},
				    							         { boxLabel:'不通过',name:'rg_16',inputValue:'2'},
				    							         { boxLabel:'条件通过',name:'rg_16',inputValue:'3'}],
						    							 listeners:{
						    								 change: function(rg){
						    									 var v = rg.getValue();
						    									 if(v.rg_16 == '3'){
						    										 if(Ext.getCmp('ta_16')&&Ext.getCmp('tf_16')){
						    											 Ext.getCmp('ta_16').setReadOnly(false);
						    											 Ext.getCmp('tf_16').setReadOnly(false);
						    										 }
						    									 }else {
					    											 Ext.getCmp('ta_16').setReadOnly(true);
					    											 Ext.getCmp('tf_16').setReadOnly(true);
						    									 }
						    									 
						    								 }
						    							 }
				    						 },
				    						 
				    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_16',id:'tf_16' },
				    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_16',id:'ta_16'}]
		  					          }, {
			  					          layout:'column',
			  					          style: {background:'#FFFAFA'},
			  					          items:[ {
					    							 xtype : 'radiogroup',
					    							 name : 'rg_17',
					    							 id : 'rg_17',
					    							 logic:'ignore',
					    							 columnWidth:0.4,
					    							 fieldLabel:'付款方式',
					    							 items: [
					    							         { boxLabel:'通过',name:'rg_17',inputValue:'1'},
					    							         { boxLabel:'不通过',name:'rg_17',inputValue:'2'},
					    							         { boxLabel:'条件通过',name:'rg_17',inputValue:'3'}],
							    							 listeners:{
							    								 change: function(rg){
							    									 var v = rg.getValue();
							    									 if(v.rg_17 == '3'){
							    										 if(Ext.getCmp('ta_17')&&Ext.getCmp('tf_17')){
							    											 Ext.getCmp('ta_17').setReadOnly(false);
							    											 Ext.getCmp('tf_17').setReadOnly(false);
							    										 }
							    									 }else {
						    											 Ext.getCmp('ta_17').setReadOnly(true);
						    											 Ext.getCmp('tf_17').setReadOnly(true);
							    									 }
							    									 
							    								 }
							    							 }
					    						 },
					    						 
					    						 { xtype : 'datefield',logic:'ignore', columnWidth:0.25, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'完成时间',maxLength:100,name:'tf_17',id:'tf_17' },
					    						 { xtype : 'textareafield', logic:'ignore',columnWidth:0.75, fieldStyle: "background:#FFFAFA;color:#515151;", fieldLabel:'条件说明',maxLength:1000,name: 'ta_17',id:'ta_17'}]
			  					          }
		  					         ]
		  				   },{
		  					   xtype:'fieldset',
		  					   title:'<h2>总经理</h2>',
		  					   columnWidth:1,
		  					   collapsible: true,
		  					   frame:false,
//		  					   name:'psgroup',
							   group:2,
							   groupName:'评审状态',
		  					   items:[
		  					          {
		  					          layout:'column',
		  					          style: {background:'#FFFAFA'},
		  					          items:[ {
				    							 xtype : 'radiogroup',
				    							 name : 'rg_18',
				    							 id : 'rg_18',
				    							 logic:'ignore',
				    							 columnWidth:0.4,
				    							 fieldLabel:'评审',
				    							 items: [
				    							         { boxLabel:'通过',name:'rg_18',inputValue:'1'},
				    							         { boxLabel:'不通过',name:'rg_18',inputValue:'2'}]
				    						 }]
		  					          }
		  					         ]
		  				   }
							 
							 ];
		return returnItems;
	}
});