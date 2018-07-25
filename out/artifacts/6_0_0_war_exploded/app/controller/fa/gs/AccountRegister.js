Ext.define('erp.controller.fa.gs.AccountRegister', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'fa.gs.AccountRegister','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar', 'core.form.FileField', 'fa.gs.AccountRegisterBillGrid',
    		'fa.gs.DetailAssGrid','core.form.MultiField','core.button.TurnPayBalance','core.button.TurnRecBalance', 'core.button.Source', 
    		'core.button.Add','core.button.Save','core.button.Close','core.button.Accounted','core.button.ResAccounted',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit','core.button.UpdateRemark',
    		'core.button.Update','core.button.Delete','core.button.DeleteDetail','core.button.AssMain','core.button.Print',
    		'core.button.CopyAll','core.button.confirmType','core.button.TurnRecBalanceIMRE', 'core.button.End','core.button.ExportExcelButton',
    		'core.trigger.DbfindTrigger','core.form.YnField','core.grid.YnColumn','core.trigger.TextAreaTrigger', 'core.form.SeparNumber'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'textfield[name=ar_memo]':{
				beforerender: function(field){
					field.setReadOnly(false);
				}
			},
			'textfield[name=ar_deposit]':{
				beforerender: function(field){
					var type = Ext.getCmp('ar_type');
					if(type && type.value=="暂收款"){
						if(Ext.getCmp('ar_source')&&Ext.getCmp('ar_source').value!=""){
							field.setReadOnly(true);
						}
					}
				}
			},
			'field[name=ar_vendcode]':{
				beforerender: function(field){
					if(Ext.getCmp('ar_sourcetype')&&Ext.getCmp('ar_sourcetype').value=="付款申请"){
						field.setReadOnly(true);
					}
				}
			},
			'textfield[name=ar_payment]':{
				beforerender: function(field){
					var type = Ext.getCmp('ar_type');
					if(type &&type.value=="暂收款"){
						if(Ext.getCmp('ar_source')&&Ext.getCmp('ar_source').value!=""){
							field.setReadOnly(true);
						}
					}
					if(type&&(type.value=="应付款"||type.value=="预付款")){
						if(Ext.getCmp('ar_source')&&Ext.getCmp('ar_source').value!=""){
							if(Ext.getCmp('ar_sourcetype')&&Ext.getCmp('ar_sourcetype').value=="付款申请"){
								field.setReadOnly(false);
							} else {
								field.setReadOnly(true);
							}
						}
					}
					if(type &&type.value=="保理付款"){
						if(Ext.getCmp('ar_sourcetype')&&Ext.getCmp('ar_sourcetype').value!=""){
							field.setReadOnly(true);
						}
					}
				}
			},
			'field[name=ar_accountcurrency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=ar_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'field[name=ar_accountcode]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=ar_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'field[name=ar_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=ar_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'field[name=ar_date]':{
				beforerender: function(field){
					if(Ext.getCmp('ar_type') && (Ext.getCmp('ar_type').value=="应收票据收款" || Ext.getCmp('ar_type').value=="应付票据付款")){
						field.readOnly=true;
					}
				},
				change: function(f){
    				if (!f.value) {
    					f.setValue(new Date());
    				}
    			}
			},
    		'field[name=ar_precurrency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=ar_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'erpFormPanel' : {
    			afterload : function(form) {
    				form.getForm().getFields().each(function() {
						var val = getUrlParam(this.name);
						if(!Ext.isEmpty(val) && Ext.isEmpty(this.getValue())) {
							this.setValue(decodeURIComponent(val));
						}
					});
    				var t = form.down('#ar_type');
    				if(t.value == '应收票据收款'||t.value == '应付票据付款'){
    					form.getForm().getFields().each(function(field) {
    						if(typeof field.setReadOnly == 'function' && field.name != 'ar_memo' && field.name != 'ar_accountcurrency' && field.name != 'ar_accountrate' && field.name != 'ar_deposit' && field.name != 'ar_payment' && field.name != 'ar_departmentcode' && field.name != 'ar_sellercode')
    							field.setReadOnly(true);
    					});
    				}
    				this.hidecolumns(t);
				}
    		},
    		'erpCopyButton': {
    			afterrender: function(btn){
    				var type = Ext.getCmp('ar_type');
    				if(type && (type.value == '应收票据收款' || status.value == '应付票据付款' || status.value == '保理付款' || status.value == '保理收款')){
    					btn.hide();
    				} else {
    					btn.show();
    				}
    			},
    			click: function(btn) {
    				this.copy();
    			}
    		},
    		//查看来源
    		'erpSourceButton': {
    			afterrender: function(btn){
    				Ext.defer(function(){
        				var t = Ext.getCmp('ar_type').value;
        				if(t && t != '保理收款'){
        					btn.hide();
        				}
    				}, 200);
    			},
    			click: function(){
    				var id = Ext.getCmp('ar_id').value;
    				if(id != null && id > 0) {
    					me.showSource(id);
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.getApamount();
    				//保存之前的一些前台的逻辑判定
    				this.beforeSave();
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ar_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					btn.hide();
    				} else {
    					btn.show();
    				}
    			},
    			click: function(btn){
    				this.getApamount();
    				this.beforeUpdate();
    			}
    		},
    		'erpPrintButton': {
                click: function(btn) {
                    var reportName = '';
                    reportName = "AccountCount";
                    var condition = '{AccountRegister.ar_id}=' + Ext.getCmp('ar_id').value + '';
                    var id = Ext.getCmp('ar_id').value;
                    me.FormUtil.onwindowsPrint(id, reportName, condition);
                }
            },
    		'erpExportExcelButton':{
    			afterrender:function(btn){
    				//btn.exportCaller="AccountRegisterDetailAss!Export";
    				btn.exportCaller="AccountRegister!DetailAss!Export";
    			    var status = Ext.getCmp('ar_statuscode').value;
    				if(status&&status!='ENTERING'&&status!='COMMITED'){
    					btn.hide();
    				}
    			}
    		},
    		'filefield[id=excelfile]':{
  			   change: function(field){
  					warnMsg('确认要重新导入吗?', function(btn){
  						if(btn == 'yes'){
  							if(contains(field.value, "\\", true)){
  		  			    		filename = field.value.substring(field.value.lastIndexOf('\\') + 1);
  		  			    	} else {
  		  			    		filename = field.value.substring(field.value.lastIndexOf('/') + 1);
  		  			    	}
  		  					field.ownerCt.getForm().submit({
  		  	            	    url: basePath + 'common/upload.action?em_code=' + em_code,
  		  	            		waitMsg: "正在解析文件信息",
  		  	            		success: function(fp,o){
  		  	            			if(o.result.error){
  		  	            				showError(o.result.error);
  		  	            			} else {	            				
  		  	            				var filePath=o.result.filepath;	
  		  	            				var keyValue=Ext.getCmp('ar_id').getValue();
  		  	            				var button=Ext.getCmp('exportexcel'); 
  		  	            				Ext.Ajax.request({//拿到form的items
  		  	            		        	url : basePath + 'fa/gs/ImportExcel.action',  		  	            		  
  		  	            		        	params:{
  		  	            		        		  caller:button.exportCaller,
  		  		            					  id:keyValue,
  		  		            					  fileId:filePath
  		  		            				  },
  		  	            		        	method : 'post',
  		  	            		        	callback : function(options,success,response){
  		  	            		        		var result=Ext.decode(response.responseText);
  		  	            		        		if(result.success){
  		  	            		        			Ext.Msg.alert('提示','导入成功!');
  		  	            		        			window.location.reload();
  		  	            		        		}else{
  		  	            		        			var err = result.exceptionInfo || result.error;
  		  	            		        			if(err != null){
  		  	            		            			showError(err);
  		  	            		            		}
  		  	            		        		}
  		  	            		        	}
  		  	            				});	            				
  		  	            			}
  		  	            		}	
  		  	            	});
  						}
  					});
  			   }
  		   },
    		'erpDeleteButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ar_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var source = Ext.getCmp('ar_sourcetype'), type = Ext.getCmp('ar_type');
    				if(!Ext.isEmpty(source.value)){
    					if(source.value =='应收票据异动' || source.value =='应付票据异动' || source.value =='应收款' || source.value =='应付款'){
    						showError("请在来源:"+source.value+",单号："+Ext.getCmp('ar_source').value+"中进行反记账操作！");
        					return;
    					}
    					if(source.value == '应收发票'){
    						showError("请在来源:"+source.value+",单号："+Ext.getCmp('ar_source').value+"中进行取消收款操作！");
        					return;
    					}
    				}
    				me.FormUtil.onDelete(Ext.getCmp('ar_id').value);
    			}
    		},
    		'erpAddButton': {
    			afterrender: function(btn){
    				var type = Ext.getCmp('ar_type');
    				if(type && (type.value == '应收票据收款' || status.value == '应付票据付款' )){
    					btn.hide();
    				} else {
    					btn.show();
    				}
    			},
    			click: function(b){
    				url = 'jsps/fa/gs/accountRegister.jsp?whoami=' + caller;
    				var cacode = Ext.getCmp('ar_accountcode').value;
    				if(cacode != null && cacode != ""){
    					url += '&ar_cateid=' + Ext.getCmp('ar_cateid').value;
    					url += '&ar_accountcode=' + cacode;
    					url += '&ar_accountname=' + encodeURIComponent(Ext.getCmp('ar_accountname').value);
    					url += '&ar_accountcurrency=' + Ext.getCmp('ar_accountcurrency').value;
    					url += '&ar_accountrate=' +Ext.getCmp('ar_accountrate').value;
    				}
    				var tab = me.FormUtil.getActiveTab();
    				me.FormUtil.onAdd('addAccountRegister'+Ext.getCmp('ar_id').value, '新增银行存款登记', url);
    				/*setTimeout(function(){
        				if(tab) {
        					tab.close();
        				}
    				}, 200);*/
    			}
    		},
    		 /*'filefield[id=excelfile]':{
  			   change: function(field){
  					warnMsg('确认要重新导入吗?', function(btn){
  						if(btn == 'yes'){
	            				var keyValue=Ext.getCmp('ar_id').getValue();
  							if(contains(field.value, "\\", true)){
  		  			    		filename = field.value.substring(field.value.lastIndexOf('\\') + 1);
  		  			    	} else {
  		  			    		filename = field.value.substring(field.value.lastIndexOf('/') + 1);
  		  			    	}
  		  					field.ownerCt.getForm().submit({
  		  	            	    url: basePath + 'common/upload.action?em_code=' + em_code,
  		  	            		waitMsg: "正在解析文件信息",
  		  	            		success: function(fp,o){
  		  	            			if(o.result.error){
  		  	            				showError(o.result.error);
  		  	            			} else {	            				
  		  	            				var filePath=o.result.filepath;	

  		  	            				Ext.Ajax.request({//拿到form的items
  		  	            		        	url : basePath + 'fa/gs/ImportRegisterExcel.action',
  		  	            		        	params:{
  		  		            					  id:keyValue,
  		  		            					  fileId:filePath
  		  		            				  },
  		  	            		        	method : 'post',
  		  	            		        	callback : function(options,success,response){
  		  	            		        		var result=Ext.decode(response.responseText);
  		  	            		        		if(result.success){
  		  	            		        			Ext.Msg.alert('提示','导入成功!');
  		  	            		        			window.location.reload();
  		  	            		        		}else{
  		  	            		        			var err = result.exceptionInfo || result.error;
  		  	            		        			if(err != null){
  		  	            		            			showError(err);
  		  	            		            		}
  		  	            		        		}
  		  	            		        	}
  		  	            				});	            				
  		  	            			}
  		  	            		}	
  		  	            	});
  						}
  					});
  			   }
  		   },*/
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ar_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.beforeSubmit(btn);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ar_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ar_id').value);
    			}
    		},
    		'erpAccountedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ar_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: {
    				fn:function(btn){
        				me.beforeAccount(btn);
        			},
        			lock:2000
    			}
    		},
    		'erpResAccountedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ar_statuscode');
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAccounted(Ext.getCmp('ar_id').value);
    			}
    		},
    		'erpEndButton':{
    			afterrender: function(btn){
    				if(Ext.getCmp('ar_type').value != '暂收款'){
    					btn.hide();
    				}
    				var status = Ext.getCmp('ar_statuscode');
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			},
	    		click: function(btn){
	    			warnMsg("确定结案吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'fa/gs/endRecAmount.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('ar_id').value,
    	    			   			caller: caller
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				window.location.reload();
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
				}
    		},
    		'erpTurnPayBalanceButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ar_statuscode');
    				if(status && status.value != 'ACCOUNT'){
    					btn.hide();
    				}
    			},
    			click: function(b){
    				warnMsg("确定要转入付款单吗?", function(btn){
    					if(btn == 'yes'){
    						me.turnPayBalance(b.ownerCt.ownerCt);
    					}
    				});
    			}
    		},
    		'erpTurnRecBalanceIMREButton':{
	    		afterrender: function(btn){
	    			var status = Ext.getCmp('ar_statuscode'), type = Ext.getCmp('ar_type');
	    			if(status && status.value != 'POSTED'){
	    				btn.hide();
	    			}
	    			if(type && type.value != '暂收款'){
	    				btn.hide();
	    			}
	    		},
	    		click: function(btn){
	    			var me = this, win = Ext.getCmp('Complaint-win');
	    			if(!win) {
						var amount = Ext.getCmp('ar_deposit'), yamount = Ext.getCmp('ar_recamount'),
							val1 = amount ? (amount.value-yamount.value) : 0;
						win = Ext.create('Ext.Window', {
							id: 'Complaint-win',
							title: '转冲应收款单',
							height: 200,
							width: 400,
							items: [{
								xtype: 'form',
								height: '100%',
								width: '100%',
								bodyStyle: 'background:#f1f2f5;',
								items: [{
									margin: '10 0 0 0',
									xtype: 'dbfindtrigger',
									fieldLabel: '客户编号',
									id: 'cu_code',
									name:'cu_code',
									allowBlank: false,
									listeners:{
										aftertrigger:function(t, d){
											t.ownerCt.down('textfield[name=cu_name]').setValue(d.get('cu_name'));
										}
									}
								},{
									margin: '3 0 0 0',
									xtype: 'textfield',
									fieldLabel: '客户名称',
									readOnly:true,
									id: 'cu_name',
									name:'cu_name',
									allowBlank: false
								},{
									margin: '3 0 0 0',
									xtype: 'textfield',
									fieldLabel: '本次转金额',
									id: 'ar_thisamount',
									readOnly:false,
									allowBlank: false,
									value: val1
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
										var thisform = btn.ownerCt.ownerCt;
										Ext.Ajax.request({
											url: basePath + '/fa/gs/arTurnRecBalanceIMRE.action',
											params: {
												id: Ext.getCmp('ar_id').value,
												custcode: thisform.down("#cu_code").value,
												thisamount: thisform.down("#ar_thisamount").value
											},
											callback : function(options,success,response){
									   			thisform.setLoading(false);
									   			var localJson = new Ext.decode(response.responseText);
									   			if(localJson.exceptionInfo){
									   				showError(localJson.exceptionInfo);
									   			}
								    			if(localJson.success){
								    				turnSuccess(function(){
								    					var id = localJson.id;
								    					var url = "jsps/fa/ars/recBalance.jsp?formCondition=rb_id=" + id +"&whoami=RecBalance!IMRE";
								    					me.FormUtil.onAdd('RecBalance' + id, '冲应收款' + id, url);
								    				});
								    				window.location.reload();
									   			}
									   		}
										});
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
					}
					win.show();
				}
    		},
    		'erpTurnRecBalanceButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ar_statuscode');
    				if(status && status.value != 'ACCOUNT'){
    					btn.hide();
    				}
    			},
    			click: function(b){
    				warnMsg("确定要转入收款单吗?", function(btn){
    					if(btn == 'yes'){
    						me.turnRecBalance(b.ownerCt.ownerCt);
    					}
    				});
    			}
    		},
    		'erpUpdateRemarkButton':{
    			click:function(){
    				var remark=Ext.getCmp('ar_memo');
    					me.updateRemark(remark.value,Ext.getCmp('ar_id').value);
    				
    			}
    		},
    		'field[name=ar_errstring]': {
    			afterrender: function(f){
    				if(f.value != null && f.value != ''){
    					f.inputEl.setStyle({color: 'OrangeRed'});
    				} else {
    					f.setValue('正常');
    					f.inputEl.setStyle({color: '#0A85D7'});
    					f.originalValue = f.value;
    				}
    			},
    			change: function(f){
    				if(f.value != null && f.value != ''){
    					f.inputEl.setStyle({color: 'OrangeRed'});
    				} else {
    					f.setValue('正常');
    					f.inputEl.setStyle({color: '#0A85D7'});
    					f.originalValue = f.value;
    				}
    			}
    		},
    		'field[name=ard_explanation]': {
    			change: function(f) {
    				if(f.value == '=') {
    					f.setValue(Ext.getCmp('ar_memo').value);
    				}
    			}
    		},
    		//计算转存汇率
    		'field[name = ar_payment]' : {
    			change : function(f){
    				var payment = f.ownerCt.down('#ar_payment').getValue();		//支出金额
    				var preamount = f.ownerCt.down('#ar_preamount').getValue(); //转存金额
    				var rate = f.ownerCt.down('#ar_prerate');
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items, form = Ext.getCmp('form');
    				var apamount = 0;
    				Ext.each(items,function(item,index){
    					if(!Ext.isEmpty(item.data['ard_explanation']) || !Ext.isEmpty(item.data['ard_catecode'])){
    						apamount= apamount + Number(item.data['ard_debit']);
    					}
    				});
    				if(!Ext.isEmpty(payment) && payment != 0){
    					var prerate = form.BaseUtil.numberFormat((preamount+apamount)/payment,15);
    					if(rate.value !=prerate ){
    						rate.setValue(prerate);
    					}
    				}
    			}
    		},
    		'field[name = ar_preamount]' : {
    			change : function(f){
    				var payment = f.ownerCt.down('#ar_payment').value;		//支出金额
    				var preamount = f.ownerCt.down('#ar_preamount').value; //转存金额
    				var rate = f.ownerCt.down('#ar_prerate');
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items, form = Ext.getCmp('form');
    				var apamount = 0;
    				Ext.each(items,function(item,index){
    					if((item.data['ard_explanation']!=null&&item.data['ard_explanation']!="")|| (item.data['ard_catecode']!=null&&item.data['ard_catecode']!="")){
    						apamount= apamount + Number(item.data['ard_debit']);
    					}
    				});
    				if(!Ext.isEmpty(payment) && payment != 0){
    					var prerate = form.BaseUtil.numberFormat((preamount+apamount)/payment,15);
    					if(rate.value !=prerate ){
    						rate.setValue(prerate);
    					}
    				}
    			}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				//将当前行的 借方/贷方 以及外币借方/外币贷方 互换
    				btn.ownerCt.add({
    					text: '借贷调换',
    					width: 85,
    					disabled: true,
    			    	cls: 'x-btn-gray',
    			    	id: 'replace'
    				});
    				//当前行的借方 = 其它行的贷方总额-其它行的借方总额
    				btn.ownerCt.add({
    					text: '找平',
    					width: 65,
    					disabled: true,
    			    	cls: 'x-btn-gray',
    			    	id: 'level'
    				});
    				//复制一个相同的纪录，id不同， 但借方/贷方 互换
    				btn.ownerCt.add({
    					text: '冲红',
    					width: 65,
    					disabled: true,
    			    	cls: 'x-btn-gray',
    			    	id: 'bonus'
    				});
    			},
    			afterdelete: function(d, r, btn){
    				//更新状态
    				Ext.Ajax.request({
    					url: basePath + 'fa/gs/validAccountRegister.action',
    					params: {
    						id: d.ard_arid
    					},
    					callback: function(opt, s, r){
    						var res = Ext.decode(r.responseText);
    						if(res.success) {
    							var f = Ext.getCmp('ar_errstring');
    							f.setValue(res.errstring);
    							f.dirty = false;
    							f.originalValue = f.value;
    						}
    					}
    				});
    			}
    		},
    		'erpGridPanel2': {
    			afterrender: function(g) {
                    g.plugins[0].on('beforeedit', function(args) {
                    	if(args.field == "ard_catecode") {
                    		var bool = true;
                    		if (!Ext.isEmpty(args.record.get('ard_ordercode')) || !Ext.isEmpty(args.record.get('ard_makecode'))){
                    			bool = false;
                    		}
                    		return bool;
                    	}
                    	if(args.field == "ard_nowbalance") {
                    		if(Ext.getCmp('ar_type').value == '预付款'){
                    			return true;
        					} else if (Ext.getCmp('ar_type').value == '转存' || Ext.getCmp('ar_type').value == '其它收款' || Ext.getCmp('ar_type').value == '其它付款'){
        						return false;
        					}
                    	}
                    	if (args.field == "ard_debit") {
                    		var bool = true;
                    		if (args.record.get('ard_credit') != null && args.record.get('ard_credit') > 0){
                    			bool = false;
                    		}
                    		if (args.record.get('ard_doublecredit') != null && args.record.get('ard_doublecredit') > 0){
                    			bool = false;
                    		}
                    		return bool;
                        }
                        if (args.field == "ard_credit") {
                        	var bool = true;
                        	if (args.record.get('ard_debit') != null && args.record.get('ard_debit') > 0){
                    			bool = false;
                    		}
                    		if (args.record.get('ard_doubledebit') != null && args.record.get('ard_doubledebit') > 0){
                    			bool = false;
                    		}
                    		return bool;
                        }
                        if (args.field == "ard_doubledebit") {
                        	var bool = true;
                        	if (args.record.get('ard_doublecredit') != null && args.record.get('ard_doublecredit') > 0){
                    			bool = false;
                    		}
                    		if (args.record.get('ard_credit') != null && args.record.get('ard_credit') > 0){
                    			bool = false;
                    		}
                    		return bool;
                        }
                        if (args.field == "ard_doublecredit") {
                        	var bool = true;
                        	if (args.record.get('ard_doubledebit') != null && args.record.get('ard_doubledebit') > 0){
                    			bool = false;
                    		}
                    		if (args.record.get('ard_debit') != null && args.record.get('ard_debit') > 0){
                    			bool = false;
                    		}
                    		return bool;
                        }
                    });
                    var f = Ext.getCmp('ar_currencytype');
    				if(f) {
    					Ext.defer( function(){
    						me.changeCurrencyType(f);
    					},200);    					
    				}
    				Ext.defer(function(){
    					Ext.EventManager.addListener(document.body, 'keydown', function(e){
        					if(e.getKey() == 187 && ['ard_debit', 'ard_credit'].indexOf(e.target.name) > -1) {
        						me.levelOut(e.target);
        					}
        				});
    				}, 200);
                },
    			reconfigure: function(grid){
    				Ext.defer(function(){
    					var form = grid.ownerCt.down('form'), status = form.down('#' + form.statuscodeField);
    						source = Ext.getCmp('ar_source'), type = Ext.getCmp('ar_type');
        				if(status && (status.value == 'ENTERING' || status.value == 'COMMITED')) {
        					if(type && (type.value == '预付款' || type.value == '应付款')){
        						if(source && source.value){
	        						grid.readOnly = false;
	        					} else {
	        						grid.readOnly = true;
	        					}
        					} else if (type && (type.value == '应收票据收款'||type.value == '应付票据付款')){
        						grid.readOnly = true;
        					} else {
        						grid.readOnly = false;
        					}
        				}
    				}, 500);
    			},
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    				var btn = Ext.getCmp('replace');
    				btn.setDisabled(false);
    				btn = Ext.getCmp('level');
    				btn.setDisabled(false);
    				btn = Ext.getCmp('bonus');
    				btn.setDisabled(false);
    			}
    		},
    		'combo[name=ar_type]': {
    			beforerender: function(field){
					if(Ext.getCmp('ar_code')&&Ext.getCmp('ar_code').value){
						field.readOnly=true;
					}
				},
    			delay: 200,
    			change: function(m){
					this.hidecolumns(m);
    				var	source = Ext.getCmp('ar_source'), grid = Ext.getCmp('grid');
        			if(m.value == '应付款' || m.value == '预付款'){
	        			if(source && source.value){
	        				grid.readOnly = false;
	        			} else {
	        				grid.readOnly = true;
	        			}
        			} else if(m.value == '应收款' || m.value == '预收款'|| m.value == '应付退款'|| m.value == '预付退款'||m.value == '应收退款'||m.value == '预收退款'){
        				grid.readOnly = true;
        			} else {
        				grid.readOnly = false;
        			}
        			if(m.value == '应付票据付款' || m.value == '应收票据收款'){
        				showError('应付票据付款/应收票据收款不能手工新增!');
        			}
        			if(m.value == '自动转存'){
        				showError('自动转存不能手工新增!');
        			}
				}
    		},
    		'field[name=ar_currencytype]': {
    			beforerender : function(f) {
    				f.readOnly = false;
    			},
    			change: function(c){
    				me.changeCurrencyType(c);
    			}
    		},
    		/**
    		 * 借调互换
    		 */
    		'button[id=replace]': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt;
    				var record = grid.selModel.lastSelected;
    				if(record){
    					var v1 = record.data['ard_debit'];//借方
    					var v2 = record.data['ard_credit'];//贷方
    					var v3 = record.data['ard_doubledebit'];//原币借方
    					var v4 = record.data['ard_doublecredit'];//原币贷方
    					record.set('ard_debit', v2);
    					record.set('ard_credit', v1);
    					record.set('ard_doubledebit', v4);
    					record.set('ard_doublecredit', v3);
    				}
    			}
    		},
    		/**
    		 * 找平
    		 */
    		'button[id=level]': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt;
    				var record = grid.selModel.lastSelected;
    				if(record){
    					var items = grid.store.data.items;
    					var f = Ext.getCmp('ar_currencytype'),
    						debitF = 'ard_debit',creditF = 'ard_credit';
    					if(f.checked) {
    						debitF = 'ard_doubledebit';
    						creditF = 'ard_doublecredit';
    					}
    					var debit = 0;
    					var credit = 0;
    					Ext.each(items, function(item, index){
    						if(item.id != record.id){
    							debit += item.data[debitF];
    							credit += item.data[creditF];
    						}
    					});
    					if(credit > debit) {
    						record.set(debitF, credit - debit);
    					} else {
    						record.set(creditF, debit - credit);
    					}
    				}
    			}
    		},
    		/**
    		 * 冲红
    		 */
    		'button[id=bonus]': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt;
    				var record = grid.selModel.lastSelected;
    				if(record){
    					var keys = Ext.Object.getKeys(record.data);
    					var values = Ext.Object.getValues(record.data);
    					var o = new Object();
    					var ard_debit = 0;
    					var ard_credit = 0;
    					Ext.each(keys, function(key, index){
    						if(key != grid.detno && key != grid.keyField){//排序字段和主键字段的值均不复制
    							var v = values[index];
    							if(key == 'ard_debit'){
    								ard_debit = v;
    							}
    							if(key == 'ard_credit'){
    								ard_credit = v;
    							}
    							o[key] = v;
    						}
    					});
    					o.ard_debit = ard_credit;//借方/贷方互换
    					o.ard_credit = ard_debit;
    					grid.copyData = o;//需要粘贴时，直接取grid.copyData即可
    				}
    			}
    		},
    		/**
    		 * 辅助核算
    		 */
    		'button[id=assdetail]': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt;
    				var record = grid.selModel.lastSelected;
    				if(record){
    					var id = record.data[grid.keyField] || (-record.index), win = Ext.getCmp('dass-win');
    					if(win){
    						win.show();
    						Ext.getCmp('win-form').removeAll(true);
    						Ext.getCmp('win-form').add(me.createForm(grid));
    						Ext.getCmp('assgrid').cacheAss[id] = record.data['ca_asstype'].toString();
    						Ext.getCmp('assgrid').asstype = record.data['ca_asstype'].toString().split('#');
    						Ext.getCmp('assgrid').getMyData(id);
    					} else {
    						win = Ext.create('Ext.window.Window', {
        						id: 'dass-win',
            					height: "100%",
        		   				width: "80%",
        		   				iconCls: 'x-button-icon-set',
           			    		closeAction: 'hide',
           			    		title: '银行登记明细辅助核算',
           			    		maximizable : true,
           			    		layout : 'anchor',
        	   				    items: [{
        	   				    	anchor: '100% 30%',
        	   				    	xtype: 'form',
        	   				    	id: 'win-form',
        	   				    	layout : 'column',
        	   				    	autoScroll: true,
        	   				    	labelSeparator : ':',
        	   				    	bodyStyle: 'background:#f1f1f1;',
        	   				    	fieldDefaults : {
        	   					       labelAlign : "left"
        	   				    	},
        	   				    	items: me.createForm(grid),
        	   				    	buttonAlign: 'center',
        	   				    	buttons: [{
        	   				    		name: 'confirm',
        	   				    		cls: 'x-btn-gray',
        	   				    		text: $I18N.common.button.erpConfirmButton
        	   				    	},{
        	   				    		cls: 'x-btn-gray',
        	   				    		text: $I18N.common.button.erpOffButton,
        	   				    		handler: function(btn){
        	   				    			btn.ownerCt.ownerCt.ownerCt.close();
        	   				    		}
        	   				    	},{
        	   				    		cls: 'x-btn-gray',
        	   				    		text: "上一条",
        	   				    		handler: function(btn){
        	   				    			me.prev(grid);
        	   				    		}
        	   				    	},{
        	   				    		cls: 'x-btn-gray',
        	   				    		text: "下一条",
        	   				    		handler: function(btn){
        	   				    			me.next(grid);
        	   				    		}
        	   				    	}]
        	   				    },{
        	   				    	anchor: '100% 70%',
        	   				    	xtype: 'detailassgrid',
        	   				    	asstype: record.data['ca_asstype'].toString().split('#')
        	   				    }]
            				}).show();
        					Ext.getCmp('assgrid').getMyData(id, caller);
    					}
    				}
    			}
    		},
       		'button[name=confirm]': {
    			click: function(btn){
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var data = new Array();
    				Ext.each(Ext.getCmp('assgrid').store.data.items, function(){
    					data.push(this.data);
    				});
    				if(data.length > 0){
    					Ext.getCmp('assgrid').cacheStore[record.data[Ext.getCmp('grid').keyField] || (-record.index)] = data;
    				}
    				btn.ownerCt.ownerCt.ownerCt.close();
    			}
    		},
    		'dbfindtrigger[name=ard_currency]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var value = Ext.getCmp('ar_date').value;
    				if(value) {
    					t.dbBaseCondition = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'field[name=ard_doubledebit]': {//原币借方
    			change: function(f){
    				if(!f.ownerCt && f.value != null && f.value != 0 ){
    					var grid = Ext.getCmp('grid');
    					var record = grid.selModel.lastSelected,
    						rate = record.data['ard_rate'];
    					if(rate != null && rate > 0){
    						var val = grid.BaseUtil.numberFormat(grid.BaseUtil.multiply(f.value, rate), 2);
    						if(record.data['ard_debit'] != val) {
    							record.set('ard_debit', val);//本币
    						}
    					}
    				}
    			}
    		},
    		'field[name=ard_doublecredit]': {//原币贷方
    			change: function(f){
    				if(!f.ownerCt && f.value != null && f.value != 0 ){
    					var grid = Ext.getCmp('grid'), record = grid.selModel.lastSelected,
							rate = record.data['ard_rate'];
						if(rate != null && rate > 0){
							var val = grid.BaseUtil.numberFormat(grid.BaseUtil.multiply(f.value, rate), 2);
							if(record.data['ard_credit'] != val) {
								record.set('ard_credit', val);//本币
							}
						}
    				}
    			}
    		},
    		'field[name=ard_currency]': {
    			aftertrigger: function(f){
    				if(f.value != null && f.value != '' ){
    					var grid = Ext.getCmp('grid');
    					var record = grid.selModel.lastSelected;
    					if(record.data['ard_rate'] != null && record.data['ard_rate'] > 0){
    						if(record.data['ard_doubledebit'] != null){
            					record.set('ard_debit', 
            							grid.BaseUtil.numberFormat(grid.BaseUtil.multiply(record.data['ard_doubledebit'], record.data['ard_rate']), 2));//原币计算本币
            				}
            				if(record.data['ard_doublecredit'] != null){
            					record.set('ard_credit', 
            							grid.BaseUtil.numberFormat(grid.BaseUtil.multiply(record.data['ard_doublecredit'], record.data['ard_rate']), 2));//原币计算本币
            				}
    					}
    				}
    			}
    		},
    		'field[name=ar_deposit]' : {
    			afterrender : function(f) {
    				f.setFieldStyle({
    					'color' : 'blue'
    				});
    			},
    			change: me.changecmrate
    		},
    		'field[name=ar_payment]' : {
    			afterrender : function(f) {
    				f.setFieldStyle({
    					'color' : 'red'
    				});
    			},
    			change: me.changecmrate
    		},
    		'field[name=ar_aramount]':{
    			change: me.changecmrate
    		},
    		'field[name=ca_asstype]':{
    			change: function(f){
    				var btn = Ext.getCmp('assmainbutton');
    				btn && btn.setDisabled(Ext.isEmpty(f.value));
    			}
    		},
    		'erpAssMainButton':{
    			afterrender:function(btn){
    				if(Ext.isEmpty(Ext.getCmp('ca_asstype').getValue())){
    					btn.setDisabled(true);
    				} else {
    					btn.setDisabled(false);
    				}
    			}
    		},
    		'dbfindtrigger[name=ard_catecode]': {
    			aftertrigger: function(f){
    				var grid = Ext.getCmp('grid'),
    					record = grid.selModel.lastSelected;
    				var type = record.get('ca_assname'), ass = record.get('ass') || [];
    				if(!Ext.isEmpty(type)){
    					var oldType = Ext.Array.concate(ass, '#', 'ars_asstype');
    					if(type != oldType) {
    						var idx = me.getRecordIndex(grid, record), dd = [];
        					Ext.Array.each(type.split('#'), function(t){
        						dd.push({
        							ars_ardid: idx,
        							ars_asstype: t
        						});
        					});
        					record.set('ass', dd);
        					var view = grid.view, idx = grid.store.indexOf(record), rowNode = view.getNode(idx),
        						expander = grid.plugins[2], row = Ext.fly(rowNode, '_rowExpander'), 
        						isCollapsed = row.hasCls(expander.rowCollapsedCls);
        					if(isCollapsed)
        						expander.toggleRow(idx, record);
    					}
    				} else
    					record.set('ass', null);
    			}
    		},
    		'cateTreeDbfindTrigger[name=ard_catecode]': {
    			aftertrigger: function(f, d){
    				var grid = Ext.getCmp('grid'),
						record = grid.selModel.lastSelected;
					var type = record.get('ca_assname'), ass = record.get('ass') || [];
					if(!Ext.isEmpty(type)){
						var oldType = Ext.Array.concate(ass, '#', 'ars_asstype');
						if(type != oldType) {
							var idx = me.getRecordIndex(grid, record), dd = [];
	    					Ext.Array.each(type.split('#'), function(t){
	    						dd.push({
	    							ars_ardid: idx,
	    							ars_asstype: t
	    						});
	    					});
	    					record.set('ass', dd);
						}
					} else
						record.set('ass', null);
    			},
    			afterrender: function(f){
    				f.onTriggerClick = function(){
    					me.showCateTree(f);
    				};
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getRecordIndex: function(grid, record) {
		var me = this, id = record.get(grid.keyField);
		if(!id || id == 0) {
			me.rowCounter = me.rowCounter || 0;
			id = --me.rowCounter;
			record.set(grid.keyField, id);
		}
		return id;
	},
	//冲账汇率计算  = 冲账金额/预收金额
    changecmrate: function(){
    	var form = Ext.getCmp('form');
    	if(Ext.getCmp('ar_payment') && Ext.getCmp('ar_araprate') && Ext.getCmp('ar_deposit')) {
    		var rbamount = Ext.Number.from(Ext.getCmp('ar_payment').getValue(), 0) + Ext.Number.from(Ext.getCmp('ar_deposit').getValue(), 0);
    		var cmamount = Ext.Number.from(Ext.getCmp('ar_aramount').getValue(), 0);
    		if(rbamount != 0){
    			if(cmamount !=0){
    				Ext.getCmp('ar_araprate').setValue(form.BaseUtil.numberFormat(cmamount/rbamount, 15));
    			}
    		}
    	}
    },
	changeCurrencyType: function(c){
		var grid = Ext.getCmp('grid');
		if(grid) {
			var cols = grid.headerCt.getGridColumns();
			if(c.checked){
				Ext.each(cols, function(cn){
					if(cn.dataIndex == 'ard_doubledebit' || cn.dataIndex == 'ard_doublecredit'){
						cn.width = 110;
						cn.setVisible(true);
					}
					if(cn.dataIndex == 'ard_currency' || cn.dataIndex == 'ard_rate'){
						cn.width = 60;
						cn.setVisible(true);
					}
					if(cn.dataIndex == 'ard_debit'){
						cn.setText('本币借方');
					}
					if(cn.dataIndex == 'ard_credit'){
						cn.setText('本币贷方');
					}
				});
			} else {
				Ext.each(cols, function(cn){
					if(cn.dataIndex == 'ard_currency' || cn.dataIndex == 'ard_rate'
						|| cn.dataIndex == 'ard_doubledebit' || cn.dataIndex == 'ard_doublecredit'){
						cn.setVisible(false);
					}
					if(cn.dataIndex == 'ard_debit'){
						cn.setText('借方');
					}
					if(cn.dataIndex == 'ard_credit'){
						cn.setText('贷方');
					}
				});
			}	
		}
	},
	beforeSave: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var ar_accountcurrency = Ext.getCmp('ar_accountcurrency').value,
			ar_arapcurrency = Ext.getCmp('ar_arapcurrency').value,
			deposit = Ext.getCmp('ar_deposit').value,		//收入 
			payment = Ext.getCmp('ar_payment').value,		//支出
			type = Ext.getCmp('ar_type').value,
			sellercode = Ext.getCmp('ar_sellercode').value;
			
		var grid = Ext.getCmp('grid'), items = grid.store.data.items, bool = true,
			ar_accountrate = Ext.getCmp('ar_accountrate').value,	//账户汇率
			ar_aramount = Ext.getCmp('ar_aramount').value,			//冲账金额
			ar_preamount = Ext.getCmp('ar_preamount').value,		//转存金额
			ar_prerate = Ext.getCmp('ar_prerate').value,			//转存汇率
		    ctype = Ext.getCmp('ar_currencytype');
		var debit = 0, credit = 0, doubledebit=0, doublecredit=0;
		var hasdetail = false;
		Ext.each(items,function(item,index){
			if(!Ext.isEmpty(item.data['ard_explanation']) || !Ext.isEmpty(item.data['ard_catecode']) || !Ext.isEmpty(item.data['ard_ordercode']) || !Ext.isEmpty(item.data['ard_makecode'])){
				hasdetail = true;
				debit= debit + Number(item.data['ard_debit']);
				credit= credit + Number(item.data['ard_credit']);
				doubledebit= doublecredit + Number(item.data['ard_doubledebit']);
				doublecredit= doublecredit + Number(item.data['ard_doublecredit']);
			}
		});
		if(type != null && type != '' ){
			if(type == '应付款' || type == '预付款' || type == '应收款' || type == '预收款' || type == '应付退款' || type == '预付退款' || type == '应收退款' || type == '预收退款'){
				if(ar_arapcurrency == null || ar_arapcurrency==''){
					Ext.getCmp('ar_arapcurrency').setValue(ar_accountcurrency);
					ar_arapcurrency = Ext.getCmp('ar_arapcurrency').value;
				}
			}
			if(type == '应付票据付款' || type == '应收票据收款' || type == '自动转存'){
        		showError('该类型单据不能手工新增!');
        		return;
        	}
			if(type == '应收款'){
				if(Ext.isEmpty(sellercode)){
					showError('业务员编号不能为空!');
					return;
				}
			}
			if(type == '应收款' || type == '预收款' ||  type == '应付退款' ||  type == '预付退款'){
				if(deposit == 0 || deposit == null || deposit == '' ){
					showError('收入金额不能为空!');
					return;
				}
			}
			if(type == '应收款' || type == '预收款' ||  type == '应付退款' ||  type == '预付退款') {
				if(ar_accountcurrency != ar_arapcurrency){
					if(Ext.getCmp('ar_araprate').value == '1'){
						showError('币别不一致，冲账汇率为1，请修改!');
						return;
					}
				}
				if(ar_accountcurrency == ar_arapcurrency){
					Ext.getCmp('ar_aramount').setValue(form.BaseUtil.multiply(Number(deposit), Number(Ext.getCmp('ar_araprate').value)));
				}
			}
			if (type == '预付款' || type == '应付款' ||  type == '应收退款' ||  type == '预收退款'){
				if(payment == 0 || payment == null || payment == '' ){
					showError('支出金额不能为空!');
					return;
				}
			}
			if(type == '预付款' || type == '应付款' ||  type == '应收退款' ||  type == '预收退款'){
				if(ar_accountcurrency != ar_arapcurrency){
					if(Ext.getCmp('ar_araprate').value == '1'){
						showError('币别不一致，冲账汇率为1，请修改!');
						return;
					}
				}
				if(ar_accountcurrency == ar_arapcurrency){
					Ext.getCmp('ar_aramount').setValue(form.BaseUtil.multiply(Number(payment), Number(Ext.getCmp('ar_araprate').value)));
				}
			}
			if(type == '应收票据收款' || type == '应付票据付款'){
				if(type == '应收票据收款') {
					if(deposit == 0 || deposit == null || deposit == '' ){
						showError('收入金额不能为空!');
						return;
					}
					if(form.BaseUtil.numberFormat(form.BaseUtil.multiply(deposit, ar_accountrate), 2)!= form.BaseUtil.numberFormat(credit-debit, 2)){
						showError('收入金额' + form.BaseUtil.numberFormat(form.BaseUtil.multiply(deposit, ar_accountrate), 2) + '与从表金额' + form.BaseUtil.numberFormat(credit-debit, 2) + '不一致!');
						return;
					}
				} else if(type == '应付票据付款'){
					if(payment == 0 || payment == null || payment == '' ){
						showError('支出金额不能为空!');
						return;
					}
				}
			}
			if(type == '转存'){
				if(!Ext.isEmpty(debit)){
					if(payment-deposit != 0){
						var prerate = form.BaseUtil.numberFormat((ar_preamount+(debit-credit))/(payment-deposit),15);
						if(form.BaseUtil.numberFormat(ar_prerate,10) != form.BaseUtil.numberFormat(prerate,10) ){
							Ext.getCmp('ar_prerate').setValue(prerate);
						}
					}
				}
			}
			if(!Ext.isEmpty(Ext.getCmp('ar_source').value)){
				if(type == '应收款' || type == '应付退款' || type == '预付退款'){
					if(form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) != 0){
						if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(doublecredit-doubledebit, 2)){
							showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(doublecredit-doubledebit, 2) + '不一致!');
							return;
						}
					} else {
						if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(credit-debit, 2)){
							showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(credit-debit, 2) + '不一致!');
							return;
						}
					}
				}
				if((type == '预收款' || type == '预收退款') && hasdetail){
					var ardamount = 0;
					if(form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) != 0){
						if (type == '预收款') {
							ardamount = form.BaseUtil.numberFormat(doublecredit-doubledebit, 2);
						} else {
							ardamount = form.BaseUtil.numberFormat(doubledebit-doublecredit, 2)
						}
						if(form.BaseUtil.numberFormat(ar_aramount, 2)!= ardamount){
							showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + ardamount + '不一致!');
							return;
						}
					} else {
						if (type == '预收款') {
							ardamount = form.BaseUtil.numberFormat(credit-debit, 2);
						} else {
							ardamount = form.BaseUtil.numberFormat(debit-credit, 2);
						}
						if(form.BaseUtil.numberFormat(ar_aramount, 2)!= ardamount){
							showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + ardamount + '不一致!');
							return;
						}
					}
				}
				if(type == '应付款' || type == '应收退款' ){
					if(form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) != 0){
						if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(doubledebit-doublecredit, 2)){
							showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) + '不一致!');
							return;
						}
					} else {
						if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(debit-credit, 2)){
							showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(debit-credit, 2) + '不一致!');
							return;
						}
					}
				}
				if(type == '预付款'){
					if(Ext.getCmp('ar_sourcetype')&&Ext.getCmp('ar_sourcetype').value!="付款申请"){
						if(form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) != 0){
							if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(doubledebit-doublecredit, 2)){
								showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) + '不一致!');
								return;
							}
						} else {
							if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(debit-credit, 2)){
								showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(debit-credit, 2) + '不一致!');
								return;
							}
						}
					}
				}
			}		
		}
		var detail = Ext.getCmp('grid'), items = detail.store.data.items, bool = true;
		var ass = [];
		detail.store.each(function(record){
			if(record.get('ca_assname')) {
				var s = record.get('ass') || [];
				Ext.Array.each(s, function(t, i){
					t.ars_id = t.ars_id || 0;
					t.ars_detno = i + 1;
					t.ars_ardid = String(t.ars_ardid);
					ass.push(t);
				});
			}
		});
		var param3 = Ext.encode(ass);
		/*Ext.each(items, function(item){
			if(item.data.ard_id == null || item.data.ard_id == 0){
				item.data.ard_id = -item.index;
			}
		});*/
		Ext.Array.each(items, function(item){
			if(!Ext.isEmpty(item.data['ard_catecode'])){
				if(item.data['ca_currencytype'] != 0){
					if(ctype && ctype.value != '1'){
						ctype.setValue('1');
					}
					if(Ext.isEmpty(item.data['ard_currency'])){
						bool = false;
						showError('明细表第' + item.data['ard_detno'] + '行的科目是外币科目，币别不能为空！');
						return;
					}
					if(Ext.isEmpty(item.data['ard_rate']) || item.data['ard_rate'] == 0){
						bool = false;
						showError('明细表第' + item.data['ard_detno'] + '行的科目是外币科目，汇率不能为0！');
						return;
					}
				}
				if(item.data['ard_doubledebit'] != 0 && item.data['ard_rate'] != 0 && item.data['ard_debit'] != 0){
					if (me.BaseUtil.numberFormat(me.BaseUtil.multiply(item.data['ard_doubledebit'], item.data['ard_rate']),2) != me.BaseUtil.numberFormat(item.data['ard_debit'],2)) {
						bool = false;
						showError('明细表第' + item.data['ard_detno'] + '行，原币借方*汇率不等于本币借方！');
						 return;
					}
				}
				if(item.data['ard_doublecredit'] != 0 && item.data['ard_rate'] != 0 && item.data['ard_credit'] != 0){
					if (me.BaseUtil.numberFormat(me.BaseUtil.multiply(item.data['ard_doublecredit'], item.data['ard_rate']),2) != me.BaseUtil.numberFormat(item.data['ard_credit'],2)) {
						bool = false;
						showError('明细表第' + item.data['ard_detno'] + '行，原币贷方*汇率不等于本币贷方！');
						return;
					}
				}
			}
		});
		var param1 = me.GridUtil.getGridStore(detail);
		if(detail.necessaryField.length > 0 && (param1.length == 0)){
			showError($I18N.common.grid.emptyDetail);
			return;
		}
		var ex = new Array(),d;
		Ext.each(param1, function(){//摘要未填写
			d = Ext.decode(this);
			if(Ext.isEmpty(d.ard_explanation)) {
				ex.push(d.ard_detno);
			}
		});
		if(ex.length > 0) {
			warnMsg("摘要未填写，序号:" + ex.join(',') + " 是否继续保存?", function(btn){
				if(btn == 'yes') {
					me.onSave(form, param1, param3);;
				}
			});
		} else {
			if(bool)
				me.onSave(form, param1, param3);
		}
	},
	onSave: function(form, param1, param3) {
		var me = this;
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param3 = param3 == null ? [] : param3.toString().replace(/\\/g,"%");
		if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			form.getForm().getFields().each(function(){
				if(this.logic == 'ignore') {
					delete r[this.name];
				}
			});
			var ctype = Ext.getCmp('ar_currencytype');
			r.ar_currencytype = (typeof ctype.value == 'boolean' && ctype.value) ? -1 : 0;
			r.ar_errstring = r.ar_errstring == '正常' ? '' : r.ar_errstring;
			me.FormUtil.save(r, param1, param3);
		}else{
			me.FormUtil.checkForm();
		}
	},
	beforeUpdate: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		
		var ar_accountcurrency = Ext.getCmp('ar_accountcurrency').value,
		ar_arapcurrency = Ext.getCmp('ar_arapcurrency').value,
		deposit = Ext.getCmp('ar_deposit').value,				//收入 
		payment = Ext.getCmp('ar_payment').value,				//支出
		type = Ext.getCmp('ar_type').value,
		sellercode = Ext.getCmp('ar_sellercode').value,
		ctype = Ext.getCmp('ar_currencytype');
		var grid = Ext.getCmp('grid'), items = grid.store.data.items, bool = true;
		ar_accountrate = Ext.getCmp('ar_accountrate').value;	//账户汇率
		ar_aramount = Ext.getCmp('ar_aramount').value;			//冲账金额
		var debit = 0, credit = 0, doubledebit=0, doublecredit=0, balance=0, hasdetail= false;
		Ext.each(items,function(item,index){
			if(!Ext.isEmpty(item.data['ard_explanation']) || !Ext.isEmpty(item.data['ard_catecode']) || !Ext.isEmpty(item.data['ard_ordercode']) || !Ext.isEmpty(item.data['ard_makecode'])){
				hasdetail = true;
				if(Ext.getCmp('ar_sourcetype')&&Ext.getCmp('ar_sourcetype').value == "付款申请"&&!Ext.isEmpty(item.data['ard_nowbalance'])&&(!Ext.isEmpty(item.data['ard_ordercode']) || !Ext.isEmpty(item.data['ard_makecode']))){
					 item.set('ard_debit', item.data['ard_nowbalance']);
				}
				debit= debit + Number(item.data['ard_debit']);
				credit= credit + Number(item.data['ard_credit']);
				doubledebit= doubledebit + Number(item.data['ard_doubledebit']);
				doublecredit= doublecredit + Number(item.data['ard_doublecredit']);
				balance= balance + Number(item.data['ard_nowbalance']);
			}
		});
		if(type != null && type != '' ){
			if(type == '应付款' || type == '预付款' || type == '应收款' || type == '预收款' || type == '应付退款' || type == '预付退款' || type == '应收退款' || type == '预收退款'){
				if(ar_arapcurrency == null || ar_arapcurrency==''){
					Ext.getCmp('ar_arapcurrency').setValue(ar_accountcurrency);
					ar_arapcurrency = Ext.getCmp('ar_arapcurrency').value;
				}
			}
			if(type == '应收款'){
				if(Ext.isEmpty(sellercode)){
					showError('业务员编号不能为空!');
					return;
				}
			}
			if(type == '应收款' || type == '预收款' ||  type == '应付退款'){
				if(deposit == 0 || deposit == null || deposit == '' ){
					showError('收入金额不能为空!');
					return;
				}
			}
			if(type == '应收款' || type == '预收款' ||  type == '应付退款'){
				if(ar_accountcurrency != ar_arapcurrency){
					if(Ext.getCmp('ar_araprate').value == '1'){
						showError('币别不一致，冲账汇率为1，请修改!');
						return;
					}
				}
				if(ar_accountcurrency == ar_arapcurrency){
					Ext.getCmp('ar_aramount').setValue(form.BaseUtil.multiply(Number(deposit),Number(Ext.getCmp('ar_araprate').value)));
				}
			}
			if (type == '预付款' || type == '应付款' ||  type == '应收退款'){
				if(payment == 0 || payment == null || payment == '' ){
					showError('支出金额不能为空!');
					return;
				}
			}
			if(type == '预付款' || type == '应付款' ||  type == '应收退款'){
				if(ar_accountcurrency != ar_arapcurrency){
					if(Ext.getCmp('ar_araprate').value == '1'){
						showError('币别不一致，冲账汇率为1，请修改!');
						return;
					}
				}
				if(ar_accountcurrency == ar_arapcurrency){
					Ext.getCmp('ar_aramount').setValue(form.BaseUtil.multiply(Number(payment), Number(Ext.getCmp('ar_araprate').value)));
				}
			}
			if(type == '应收票据收款' || type == '应付票据付款'){
				if(type == '应收票据收款') {
					if(deposit == 0 || deposit == null || deposit == '' ){
						showError('收入金额不能为空!');
						return;
					}
					if(form.BaseUtil.numberFormat(form.BaseUtil.multiply(deposit, ar_accountrate), 2)!= form.BaseUtil.numberFormat(credit-debit, 2)){
						showError('收入金额' + form.BaseUtil.numberFormat(form.BaseUtil.multiply(deposit, ar_accountrate), 2) + '与从表金额' + form.BaseUtil.numberFormat(credit-debit, 2) + '不一致!');
						return;
					}
				} else if(type == '应付票据付款'){
					if(payment == 0 || payment == null || payment == '' ){
						showError('支出金额不能为空!');
						return;
					}
				}
			}
			if(!Ext.isEmpty(Ext.getCmp('ar_source').value) && Ext.getCmp('ar_sourcetype').value !='支票' && Ext.getCmp('ar_sourcetype').value !='应付支票'){
				if(type == '应收款' || type == '应付退款' || type == '预付退款'){
					if(form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) != 0){
						if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(doublecredit-doubledebit, 2)){
							showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(doublecredit-doubledebit, 2) + '不一致!');
							return;
						}
					} else {
						if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(credit-debit, 2)){
							showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(credit-debit, 2) + '不一致!');
							return;
						}
					}
				}
				if((type == '预收款' || type == '预收退款') && hasdetail){
					var ardamount = 0;
					if(form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) != 0){
						if (type == '预收款') {
							ardamount = form.BaseUtil.numberFormat(doublecredit-doubledebit, 2);
						} else {
							ardamount = form.BaseUtil.numberFormat(doubledebit-doublecredit, 2)
						}
						if(form.BaseUtil.numberFormat(ar_aramount, 2)!= ardamount){
							showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + ardamount + '不一致!');
							return;
						}
					} else {
						if (type == '预收款') {
							ardamount = form.BaseUtil.numberFormat(credit-debit, 2);
						} else {
							ardamount = form.BaseUtil.numberFormat(debit-credit, 2);
						}
						if(form.BaseUtil.numberFormat(ar_aramount, 2)!= ardamount){
							showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + ardamount + '不一致!');
							return;
						}
					}
				}
				if(type == '应付款' || type == '应收退款'){
					if(form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) != 0){
						if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(doubledebit-doublecredit, 2)){
							showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) + '不一致!');
							return;
						}
					} else {
						if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(debit-credit, 2)){
							showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(debit-credit, 2) + '不一致!');
							return;
						}
					}
				}
				if(type == '预付款' || type == '应付款'){
					if(Ext.getCmp('ar_sourcetype')){
						if(Ext.getCmp('ar_sourcetype').value == "付款申请"){
							var bool = false;
							Ext.each(items,function(item,index){
								if(!Ext.isEmpty(item.data['ard_ordercode']) || !Ext.isEmpty(item.data['ard_makecode'])){
									bool = true;
									return;
								}
							});
							if(bool && type == '应付款'){
								if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(balance, 2)){
									showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表本次收款/付款金额合计' + form.BaseUtil.numberFormat(balance, 2) + '不一致!');
									return;
								}
							}
						} else {
							if(form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) != 0){
								if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(doubledebit-doublecredit, 2)){
									showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) + '不一致!');
									return;
								}
							} else {
								if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(debit-credit, 2)){
									showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(debit-credit, 2) + '不一致!');
									return;
								}
							}
						}
					}
					var apamount = 0;
					Ext.each(items,function(item,index){
						if(Ext.isEmpty(item.data['ard_ordercode']) && Ext.isEmpty(item.data['ard_makecode'])){
							apamount= apamount + Number(item.data['ard_nowbalance']);
						}
					});
					Ext.getCmp('ar_apamount').setValue(Ext.util.Format.number(ar_aramount-apamount,'0.00'));
				}
			}
		}
		
		var detail = Ext.getCmp('grid'), bool = true,
			items = detail.store.data.items;
		/*Ext.each(items, function(item){
			if(item.data.ard_id == null || item.data.ard_id == 0){
				item.data.ard_id = -item.index;
			}
		});*/
		var ass = [];
		detail.store.each(function(record){
			if(record.get('ca_assname')) {
				var s = record.get('ass') || [];
				Ext.Array.each(s, function(t, i){
					t.ars_id = t.ars_id || 0;
					t.ars_detno = i + 1;
					t.ars_ardid = String(t.ars_ardid);
					ass.push(t);
				});
			}
		});
		Ext.Array.each(items, function(item){
			if(!Ext.isEmpty(item.data['ard_catecode'])){
				if(item.data['ca_currencytype'] != 0){
					if(ctype && ctype.value != '1'){
						ctype.setValue('1');
					}
					if(Ext.isEmpty(item.data['ard_currency'])){
						bool = false;
						showError('明细表第' + item.data['ard_detno'] + '行的科目是外币科目，币别不能为空！');
						return;
					}
					if(Ext.isEmpty(item.data['ard_rate']) || item.data['ard_rate'] == 0){
						bool = false;
						showError('明细表第' + item.data['ard_detno'] + '行的科目是外币科目，汇率不能为0！');
						return;
					}
				}
				if(item.data['ard_doubledebit'] != 0 && item.data['ard_rate'] != 0 && item.data['ard_debit'] != 0){
					if (me.BaseUtil.numberFormat(me.BaseUtil.multiply(item.data['ard_doubledebit'], item.data['ard_rate']),2) != me.BaseUtil.numberFormat(item.data['ard_debit'],2)) {
						bool = false;
						showError('明细表第' + item.data['ard_detno'] + '行，原币借方*汇率不等于本币借方！');
						return;
					}
				}
				if(item.data['ard_doublecredit'] != 0 && item.data['ard_rate'] != 0 && item.data['ard_credit'] != 0){
					if (me.BaseUtil.numberFormat(me.BaseUtil.multiply(item.data['ard_doublecredit'], item.data['ard_rate']),2) != me.BaseUtil.numberFormat(item.data['ard_credit'],2)) {
						bool = false;
						showError('明细表第' + item.data['ard_detno'] + '行，原币贷方*汇率不等于本币贷方！');
						return;
					}
				}
			}
		});
		var param1 = me.GridUtil.getGridStore(detail);
		var param2 = Ext.encode(ass);
		var param3 = new Array();
		
		if(Ext.getCmp('assmainbutton')){
			Ext.each(Ext.Object.getKeys(Ext.getCmp('assmainbutton').cacheStoreForm), function(key){
				Ext.each(Ext.getCmp('assmainbutton').cacheStoreForm[key], function(d){
					d['ass_conid'] = key;
					param3.push(d);
				});
			});
		}
		
		if(me.FormUtil.checkFormDirty(form) == '' && detail.necessaryField.length > 0 && (param1.length == 0) && param2.length == 0 && param3.length == 0){
			showError($I18N.common.grid.emptyDetail);
			return;
		} else {
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : param2.toString().replace(/\\/g,"%");
			param3 = param3 == null ? [] : Ext.encode(param3).replace(/\\/g,"%");
			
			var f = form.getForm();
			//导入excel按钮在流程审批界面,需要忽略它以通过流程按钮（更新）的表单验证
			var arry = f.getFields().items;
			Ext.Array.each(arry,function(item,i){
				if (item.id=='excelfile' && item.xtype=='filefield'){
					f.getFields().items.splice(i, 1);
					return false;
				}
			});
			
			if(f.isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				form.getForm().getFields().each(function(){
					if(this.logic == 'ignore') {
						delete r[this.name];
					}
				});
				var ctype = Ext.getCmp('ar_currencytype');
				r.ar_currencytype = (typeof ctype.value == 'boolean' && ctype.value) ? -1 : 0;
				r.ar_errstring = r.ar_errstring == '正常' ? '' : r.ar_errstring;
				if(bool)
					me.FormUtil.update(r, param1, param2, param3);
			}else{
				me.FormUtil.checkForm();
			}
		}
	},
	createForm: function(grid){
		var record = grid.selModel.lastSelected;
		var data = new Object();
		if(record){
			data = record.data;
		}
		var items = new Array();
		var item;
		Ext.each(grid.columns, function(c){
			item = new Object();
			item.id = c.dataIndex;
			item.name = c.dataIndex;
			item.fieldLabel = c.text || c.header;
			item.value = data[c.dataIndex];
			item.columnWidth = .33;
			item.readOnly = true;
			item.xtype = 'textfield';
			item.fieldStyle = 'background:#f0f0f0;border: 1px solid #8B8970';
			item.cls = 'form-field-border';
			if(c.hidden){
				item.xtype = 'hidden';
				item.cls = "";
			}
			if(c.dataIndex == grid.detno){
				item.fieldStyle = 'background:#e0f0f0;font-weight:bold;border: 1px solid #8B8970';
			}
			items.push(item);
		});
		return items;
	},
	/**
	 * 明细辅助核算的上一条
	 */
	prev: function(grid, record){
		record = record || grid.selModel.lastSelected;
		if(record){
			//先保存当前数据
			var data = new Array();
			Ext.each(Ext.getCmp('assgrid').store.data.items, function(){
				data.push(this.data);
			});
			if(data.length > 0){
				Ext.getCmp('assgrid').cacheStore[record.data[grid.keyField] || (-record.index)] = data;
			}
			//递归查找上一条，并取到数据
			var d = grid.store.getAt(record.index - 1);
			if(d){
				Ext.getCmp('win-form').getForm().setValues(d.data);
				var idx = d.data[grid.keyField] || (-d.index);
				Ext.getCmp('assgrid').cacheAss[idx] = d.data['ca_asstype'];
				Ext.getCmp('assgrid').asstype = Ext.isEmpty(d.data['ca_asstype']) ?
						new Array() : d.data['ca_asstype'].toString().split('#');
				Ext.getCmp('assgrid').getMyData(idx, caller);
				grid.selModel.select(d);
			} else {
				if(record.index - 1 > 0){
					this.prev(grid, d);
				}
			}
		}
	},
	/**
	 * 明细辅助核算的下一条
	 */
	next: function(grid, record){
		record = record || grid.selModel.lastSelected;
		if(record){
			//先保存当前数据
			var data = new Array();
			Ext.each(Ext.getCmp('assgrid').store.data.items, function(){
				data.push(this.data);
			});
			if(data.length > 0){
				Ext.getCmp('assgrid').cacheStore[record.data[grid.keyField] || (-record.index)] = data;
			}
			//递归查找下一条，并取到数据
			var d = grid.store.getAt(record.index + 1);
			if(d){
				Ext.getCmp('win-form').getForm().setValues(d.data);
				var idx = d.data[grid.keyField] || (-d.index);
				Ext.getCmp('assgrid').cacheAss[idx] = d.data['ca_asstype'];
				Ext.getCmp('assgrid').asstype = Ext.isEmpty(d.data['ca_asstype']) ?
						new Array() : d.data['ca_asstype'].toString().split('#');
				Ext.getCmp('assgrid').getMyData(idx, caller);
				grid.selModel.select(d);
			} else {
				if(record.index + 1 < grid.store.data.items.length){
					this.next(grid, d);
				}
			}
		}
	},
	turnPayBalance: function(form) {
		var me = this;
		form.setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + 'fa/gs/arTurnPayBalance.action',
	   		params: {
	   			id: Ext.getCmp('ar_id').value
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
    					var url = "jsps/fa/arp/paybalance.jsp?formCondition=pb_id=" + id + "&whoami=PayBalance";
    					me.FormUtil.onAdd('PayBalance' + id, '付款单' + id, url);
    				});
	   			}
	   		}
		});
	},
	turnRecBalance: function(form) {
		var me = this;
		form.setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + 'fa/gs/arTurnRecBalance.action',
	   		params: {
	   			id: Ext.getCmp('ar_id').value
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
    					var url = "jsps/fa/ars/recBalance.jsp?formCondition=rb_id=" + id +"&whoami=RecBalance!PBIL";
    					me.FormUtil.onAdd('RecBalance' + id, '收款单' + id, url);
    				});
	   			}
	   		}
		});
	},
	updateRemark:function(remark,id){
		Ext.Ajax.request({
        	url : basePath + 'fa/gs/updateRemark.action',
        	params: {remark:remark,id:id},
        	method : 'post',
        	async:false,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		}
        		showMessage("提示", '更新成功！');
        		window.location.reload();
        	}
        });
	},
	hidecolumns:function(m){
		if(!Ext.isEmpty(m.getValue())) {
			var form = Ext.getCmp('form');
			form.down('#ar_fscucode') && form.down('#ar_fscucode').hide();
			form.down('#ar_fscuname') && form.down('#ar_fscuname').hide();
			form.down('#ar_truster') && form.down('#ar_truster').hide();
			form.down('#ar_aacode') && form.down('#ar_aacode').hide();
			if(m.value == '应付款' || m.value == '预付款' || m.value == '预付退款'|| m.value == '应付退款'){
				form.down('#ar_custcode').hide();
				form.down('#ar_custname').hide();
				form.down('#ar_sellercode').hide();
				form.down('#ar_sellername').hide();
				if(m.value == '应付款' || m.value == '预付款'){
					form.down('#ar_deposit').hide();
					form.down('#ar_payment').show();
					form.down('#ar_apamount') && form.down('#ar_apamount').show();
				} else if(m.value == '预付退款'|| m.value == '应付退款'){
					form.down('#ar_deposit').show();
					form.down('#ar_payment').hide();
					form.down('#ar_apamount') && form.down('#ar_apamount').hide();
				}
				form.down('#ar_arapcurrency').show();
				form.down('#ar_araprate').show();
				form.down('#ar_aramount').show();
				form.down('#ar_vendcode').show();
				form.down('#ar_vendname').show();
				form.down('#ar_category').hide();
				form.down('#ar_catedesc').hide();
				form.down('#ar_precurrency').hide();
				form.down('#ar_prerate').hide();
				form.down('#ar_preamount').hide();
				form.down('#ar_recamount') && form.down('#ar_recamount').hide();
			} else if(m.value == '应收款' || m.value == '预收款' || m.value == '预收退款' || m.value == '应收退款'){
				form.down('#ar_custcode').show();
				form.down('#ar_custname').show();
				form.down('#ar_sellercode').show();
				form.down('#ar_sellername').show();
				form.down('#ar_arapcurrency').show();
				form.down('#ar_araprate').show();
				form.down('#ar_aramount').show();
				form.down('#ar_vendcode').hide();
				form.down('#ar_vendname').hide();
				form.down('#ar_category').hide();
				form.down('#ar_catedesc').hide();
				form.down('#ar_precurrency').hide();
				form.down('#ar_prerate').hide();
				form.down('#ar_preamount').hide();
				form.down('#ar_apamount') && form.down('#ar_apamount').hide();
				if(m.value == '应收款' || m.value == '预收款'){
					form.down('#ar_payment').hide();
					form.down('#ar_deposit').show();
				} else if(m.value == '预收退款' || m.value == '应收退款'){
					form.down('#ar_payment').show();
					form.down('#ar_deposit').hide();
				}
				form.down('#ar_recamount') && form.down('#ar_recamount').hide();
			} else if(m.value == '应收票据收款' || m.value == '应付票据付款'){
				form.down('#ar_arapcurrency').show();
				form.down('#ar_araprate').show();
				form.down('#ar_aramount').show();
				form.down('#ar_category').hide();
				form.down('#ar_catedesc').hide();
				form.down('#ar_precurrency').hide();
				form.down('#ar_prerate').hide();
				form.down('#ar_preamount').hide();
				form.down('#ar_arapcurrency').hide();
				form.down('#ar_araprate').hide();
				form.down('#ar_aramount').hide();
				form.down('#ar_apamount') && form.down('#ar_apamount').hide();
				if(m.value == '应收票据收款'){
					form.down('#ar_payment').hide();
					form.down('#ar_deposit').show();
					form.down('#ar_vendcode').hide();
					form.down('#ar_vendname').hide();
					form.down('#ar_custcode').show();
					form.down('#ar_custname').show();
					form.down('#ar_sellercode').show();
					form.down('#ar_sellername').show();
				} else if (m.value == '应付票据付款'){
					form.down('#ar_payment').show();
					form.down('#ar_deposit').hide();
					form.down('#ar_vendcode').show();
					form.down('#ar_vendname').show();
					form.down('#ar_custcode').hide();
					form.down('#ar_custname').hide();
					form.down('#ar_sellercode').hide();
					form.down('#ar_sellername').hide();
				}
				form.down('#ar_checkno') && form.down('#ar_checkno').hide();
				form.down('#ar_recamount') && form.down('#ar_recamount').hide();
			} else if(m.value == '转存' || m.value == '自动转存'){
				form.down('#ar_custcode').hide();
				form.down('#ar_custname').hide();
				form.down('#ar_sellercode').hide();
				form.down('#ar_sellername').hide();
				form.down('#ar_arapcurrency').hide();
				form.down('#ar_araprate').hide();
				form.down('#ar_aramount').hide();
				form.down('#ar_vendcode').hide();
				form.down('#ar_vendname').hide();
				form.down('#ar_category').show();
				form.down('#ar_catedesc').show();
				form.down('#ar_precurrency').show();
				form.down('#ar_prerate').show();
				form.down('#ar_preamount').show();
				form.down('#ar_apamount') && form.down('#ar_apamount').hide();
				if(m.value == '转存'){
					form.down('#ar_payment').show();
					form.down('#ar_deposit').hide();
				} 
				if(m.value == '自动转存'){
					form.down('#ar_payment').hide();
					form.down('#ar_deposit').show();
				}
				form.down('#ar_checkno') && form.down('#ar_checkno').hide();
				form.down('#ar_recamount') && form.down('#ar_recamount').hide();
			} else if(m.value == '暂收款'){ 
				form.down('#ar_custcode').hide();
				form.down('#ar_custname').hide();
				form.down('#ar_sellercode').hide();
				form.down('#ar_sellername').hide();
				form.down('#ar_arapcurrency').hide();
				form.down('#ar_araprate').hide();
				form.down('#ar_aramount').hide();
				form.down('#ar_vendcode').hide();
				form.down('#ar_vendname').hide();
				form.down('#ar_category').hide();
				form.down('#ar_catedesc').hide();
				form.down('#ar_precurrency').hide();
				form.down('#ar_prerate').hide();
				form.down('#ar_preamount').hide();
				form.down('#ar_recamount') && form.down('#ar_recamount').show();
				form.down('#ar_apamount') && form.down('#ar_apamount').hide();
			} else if(m.value == '其它付款' || m.value == '其它收款'){
				form.down('#ar_custcode').hide();
				form.down('#ar_custname').hide();
				form.down('#ar_sellercode').hide();
				form.down('#ar_sellername').hide();
				form.down('#ar_arapcurrency').hide();
				form.down('#ar_araprate').hide();
				form.down('#ar_aramount').hide();
				form.down('#ar_vendcode').hide();
				form.down('#ar_checkno').hide();
				form.down('#ar_vendname').hide();
				form.down('#ar_apamount') && form.down('#ar_apamount').hide();
				if(m.value == '其它付款'){
					form.down('#ar_deposit').hide();
					form.down('#ar_payment').show();
				}
				if(m.value == '其它收款'){
					form.down('#ar_deposit').show();
					form.down('#ar_payment').hide();
				}
				form.down('#ar_category').hide();
				form.down('#ar_catedesc').hide();
				form.down('#ar_precurrency').hide();
				form.down('#ar_prerate').hide();
				form.down('#ar_preamount').hide();
				form.down('#ar_recamount') && form.down('#ar_recamount').hide();
				form.down('#ar_pleaseman') && form.down('#ar_pleaseman').show();
			}  else if(m.value == '保理付款' || m.value == '保理收款'){
				form.down('#ar_custcode').hide();
				form.down('#ar_custname').hide();
				form.down('#ar_sellercode').hide();
				form.down('#ar_sellername').hide();
				form.down('#ar_arapcurrency').hide();
				form.down('#ar_araprate').hide();
				form.down('#ar_aramount').hide();
				form.down('#ar_vendcode').hide();
				form.down('#ar_checkno').hide();
				form.down('#ar_vendname').hide();
				form.down('#ar_apamount') && form.down('#ar_apamount').hide();
				form.down('#ar_category').hide();
				form.down('#ar_catedesc').hide();
				form.down('#ar_precurrency').hide();
				form.down('#ar_prerate').hide();
				form.down('#ar_preamount').hide();
				form.down('#ar_recamount') && form.down('#ar_recamount').hide();
				form.down('#ar_prjcode') && form.down('#ar_prjcode').hide();
				form.down('#ar_prjname') && form.down('#ar_prjname').hide();
				form.down('#ar_departmentcode') && form.down('#ar_departmentcode').hide();
				form.down('#ar_departmentname') && form.down('#ar_departmentname').hide();
				form.down('#ar_fscucode') && form.down('#ar_fscucode').show();
				form.down('#ar_fscuname') && form.down('#ar_fscuname').show();
				form.down('#ar_truster') && form.down('#ar_truster').show();
				form.down('#ar_aacode') && form.down('#ar_aacode').show();
				if(m.value == '保理付款'){
					form.down('#ar_deposit').hide();
					form.down('#ar_payment').show();
				}
				if(m.value == '保理收款'){
					form.down('#ar_deposit').show();
					form.down('#ar_payment').hide();
				}
			} else {
				form.down('#ar_custcode').hide();
				form.down('#ar_custname').hide();
				form.down('#ar_sellercode').hide();
				form.down('#ar_sellername').hide();
				form.down('#ar_arapcurrency').hide();
				form.down('#ar_araprate').hide();
				form.down('#ar_aramount').hide();
				form.down('#ar_vendcode').hide();
				form.down('#ar_vendname').hide();
				form.down('#ar_category').hide();
				form.down('#ar_catedesc').hide();
				form.down('#ar_precurrency').hide();
				form.down('#ar_prerate').hide();
				form.down('#ar_preamount').hide();
				form.down('#ar_payment').show();
				form.down('#ar_deposit').hide();
				form.down('#ar_checkno') && form.down('#ar_checkno').hide();
				form.down('#ar_recamount') && form.down('#ar_recamount').hide();
				form.down('#ar_apamount') && form.down('#ar_apamount').hide();
			}
			if(m.value == '费用'||m.value == '应付款'||m.value == '预付款'){
				form.down('#ar_pleaseman') && form.down('#ar_pleaseman').show();
			}else{
				form.down('#ar_pleaseman') && form.down('#ar_pleaseman').hide();
			}
		}
	},
	beforeAccount:function(){
		var ar_accountcurrency = Ext.getCmp('ar_accountcurrency').value,
			ar_arapcurrency = Ext.getCmp('ar_arapcurrency').value,
			ar_araprate = Ext.getCmp('ar_araprate').value,	        //冲账汇率
			ar_accountrate = Ext.getCmp('ar_accountrate').value,	//账户汇率
			ar_aramount = Ext.getCmp('ar_aramount').value			//冲账金额
			ar_category = Ext.getCmp('ar_category').value			//转存科目
			ar_precurrency = Ext.getCmp('ar_precurrency').value		//转存币别
			ar_preamount = Ext.getCmp('ar_preamount').value		    //转存金额
			ar_prerate = Ext.getCmp('ar_prerate').value             //转存汇率
			f = Ext.getCmp('ar_aramount'),
			form = Ext.getCmp('form');
		var deposit = Ext.getCmp('ar_deposit').value == null ? 0 : Ext.getCmp('ar_deposit').value,
			payment = Ext.getCmp('ar_payment').value == null ? 0 : Ext.getCmp('ar_payment').value,
			type = Ext.getCmp('ar_type').value;
		var grid = Ext.getCmp('grid'), items = grid.store.data.items;
		var me = this;
		var debit = 0, credit = 0, doubledebit=0, doublecredit=0, balance=0,hasdetail=false;
		Ext.each(items,function(item,index){
			if(!Ext.isEmpty(item.data['ard_explanation']) || !Ext.isEmpty(item.data['ard_catecode']) || !Ext.isEmpty(item.data['ard_ordercode']) || !Ext.isEmpty(item.data['ard_makecode'])){
				hasdetail = true;
				debit= debit + Number(item.data['ard_debit']);
				credit= credit + Number(item.data['ard_credit']);
				doubledebit= doubledebit + Number(item.data['ard_doubledebit']);
				doublecredit= doublecredit + Number(item.data['ard_doublecredit']);
				balance= balance + Number(item.data['ard_nowbalance']);
			}
		});
		if(type == '转存'){
			if(!Ext.isEmpty(debit)){
				if(payment-deposit != 0){
					var prerate = form.BaseUtil.numberFormat((ar_preamount+(debit-credit))/(payment-deposit),15);
					if(form.BaseUtil.numberFormat(ar_prerate,10) != form.BaseUtil.numberFormat(prerate,10) ){
						Ext.getCmp('ar_prerate').setValue(prerate);
					}
				}
			}
		}
		if(type != null && type != '' ){
			if(type == '应收款' || type == '预收款' ||  type == '应付退款' || type == '应收票据收款'){
				if(deposit == 0 || deposit == null || deposit == '' ){
					showError('收入金额不能为空!');
					return;
				}
				if(type == '应收款' || type == '预收款' ||  type == '应付退款'){
					if(form.BaseUtil.numberFormat(ar_aramount,2) != form.BaseUtil.numberFormat(form.BaseUtil.multiply(ar_araprate, deposit),2)){
						showError('冲账汇率不正确!');
						return;
					}
				}
			} else if (type == '预付款' || type == '应付款' ||  type == '应收退款' || type == '应付票据付款'){
				if(payment == 0 || payment == null || payment == '' ){
					showError('支出金额不能为空!');
					return;
				}
				if(type == '预付款' || type == '应付款' ||  type == '应收退款'){
					if(form.BaseUtil.numberFormat(ar_aramount,2) != form.BaseUtil.numberFormat(form.BaseUtil.multiply(ar_araprate, payment),2)){
						showError('冲账汇率不正确!');
						return;
					}
				}
			} else if(type == '转存') {
				if(payment == 0 || payment == null || payment == '' ){
					showError('支出金额不能为空!');
					return;
				}
				if(Ext.isEmpty(ar_category)){
					showError('转存科目不能为空!');
					return;
				}
				if(Ext.isEmpty(ar_precurrency)){
					showError('转存币别不能为空!');
					return;
				}
				if(ar_preamount == 0 || ar_preamount == null || ar_preamount == '' ){
					showError('转存金额不能为空!');
					return;
				}
				if(ar_accountcurrency != ar_precurrency){
					if(ar_prerate == 1){
						showError('币别不一致，转存汇率为1，不能记账!');
						return;
					}
				}
			}
			if(type != '应收票据收款'  && type != '应付票据付款' && type != '费用' && type != '转存' && type != '其它收款' && type != '其它付款' && type != '保理收款' && type != '保理付款'){
				if(ar_accountcurrency != ar_arapcurrency){
					if(Ext.getCmp('ar_araprate').value == '1'){
						showError('币别不一致，冲账汇率为1，不能记账!');
						return;
					}
				}
			}
			if(type == '应收票据收款' || type == '其它收款' || type == '保理收款'){
				if(form.BaseUtil.numberFormat(form.BaseUtil.multiply(deposit, ar_accountrate), 2)!= form.BaseUtil.numberFormat(credit-debit, 2)){
					showError('收入金额' + form.BaseUtil.numberFormat(form.BaseUtil.multiply(deposit, ar_accountrate), 2) + '与从表金额' + form.BaseUtil.numberFormat(credit-debit, 2) + '不一致，不能记账!');
					return;
				}
			}
			if(type == '应付票据付款' || type == '其它付款' || type == '保理付款' || type == '费用'){
				if(Math.abs(form.BaseUtil.numberFormat(form.BaseUtil.numberFormat(form.BaseUtil.multiply(payment, ar_accountrate), 2)- form.BaseUtil.numberFormat(debit-credit, 2),2)) > 0.01){
					showError('支出金额' + form.BaseUtil.numberFormat(form.BaseUtil.multiply(payment, ar_accountrate), 2) + '与从表金额' + form.BaseUtil.numberFormat(debit-credit, 2) + '不一致，不能记账!');
					return;
				}
			}
			if(!Ext.isEmpty(Ext.getCmp('ar_source').value) && Ext.getCmp('ar_sourcetype').value !='支票' && Ext.getCmp('ar_sourcetype').value !='应付支票'){
				if(type == '应收款' || type == '应付退款' || type == '预付退款'){
					if(form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) != 0){
						if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(doublecredit-doubledebit, 2)){
							showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(doublecredit-doubledebit, 2)+ '不一致!');
							return;
						}
					} else {
						if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(credit-debit, 2)){
							showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(credit-debit, 2)+ '不一致!');
							return;
						}
					}
				}
				if((type == '预收款' || type == '预收退款') && hasdetail){
					var ardamount = 0;
					if(form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) != 0){
						if (type == '预收款') {
							ardamount = form.BaseUtil.numberFormat(doublecredit-doubledebit, 2);
						} else {
							ardamount = form.BaseUtil.numberFormat(doubledebit-doublecredit, 2)
						}
						if(form.BaseUtil.numberFormat(ar_aramount, 2)!= ardamount){
							showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + ardamount + '不一致!');
							return;
						}
					} else {
						if (type == '预收款') {
							ardamount = form.BaseUtil.numberFormat(credit-debit, 2);
						} else {
							ardamount = form.BaseUtil.numberFormat(debit-credit, 2);
						}
						if(form.BaseUtil.numberFormat(ar_aramount, 2)!= ardamount){
							showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + ardamount + '不一致!');
							return;
						}
					}
				}
				if(type == '应付款' || type == '应收退款'){
					if(form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) != 0){
						if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(doubledebit-doublecredit, 2)){
							showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) + '不一致!');
							return;
						}
					} else {
						if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(debit-credit, 2)){
							showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(debit-credit, 2) + '不一致!');
							return;
						}
					}
				}
				if(type == '预付款' || type == '应付款'){
					if(Ext.getCmp('ar_sourcetype')){
						if(Ext.getCmp('ar_sourcetype').value == "付款申请"){
							var bool = false;
							Ext.each(items,function(item,index){
								if(!Ext.isEmpty(item.data['ard_ordercode']) || !Ext.isEmpty(item.data['ard_makecode'])){
									bool = true;
									return;
								}
							});
							if(bool){
								if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(balance, 2)){
									showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表本次收款/付款金额合计' + form.BaseUtil.numberFormat(balance, 2) + '不一致!');
									return;
								}
							}
						} else {
							if(form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) != 0){
								if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(doubledebit-doublecredit, 2)){
									showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) + '不一致!');
									return;
								}
							} else {
								if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(debit-credit, 2)){
									showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(debit-credit, 2) + '不一致!');
									return;
								}
							}
						}
					}
				}
			}
			var errs = [];
			Ext.Array.each(items, function(item){
				if(!Ext.isEmpty(item.data['ard_catecode'])){
					if(item.data['ca_currencytype'] != 0){
						if(Ext.isEmpty(item.data['ard_currency'])){
							errs.push('明细表第' + item.data['ard_detno'] + '行的科目是外币科目，币别不能为空！');
							return;
						}
						if(Ext.isEmpty(item.data['ard_rate']) || item.data['ard_rate'] == 0){
							errs.push('明细表第' + item.data['ard_detno'] + '行的科目是外币科目，汇率不能为0！');
							return;
						}
					}
					if(item.data['ard_doubledebit'] != 0 && item.data['ard_rate'] != 0 && item.data['ard_debit'] != 0){
						if (me.BaseUtil.numberFormat(me.BaseUtil.multiply(item.data['ard_doubledebit'], item.data['ard_rate']),2) != me.BaseUtil.numberFormat(item.data['ard_debit'],2)) {
							errs.push('明细表第' + item.data['ard_detno'] + '行，原币借方*汇率不等于本币借方！');
							return;
						}
					}
					if(item.data['ard_doublecredit'] != 0 && item.data['ard_rate'] != 0 && item.data['ard_credit'] != 0){
						if (me.BaseUtil.numberFormat(me.BaseUtil.multiply(item.data['ard_doublecredit'], item.data['ard_rate']),2) != me.BaseUtil.numberFormat(item.data['ard_credit'],2)) {
							errs.push('明细表第' + item.data['ard_detno'] + '行，原币贷方*汇率不等于本币贷方！');
							return;
						}
					}
				} else {
					if(!Ext.isEmpty(item.data['ard_nowbalance'])){
						if(type == '其它收款' || type == '其它付款' ||  type == '费用' || type == '保理付款'|| type == '保理收款'){
							errs.push('明细表第' + item.data['ard_detno'] + '行，科目未填写！');
							return;
						}
					}
				}
			});
			if(errs.length > 0) {
				showError(errs.join('<br>'));
				return;
			}
		}
		this.onAccounted(Ext.getCmp('ar_id').value);
	},
	onAccounted: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(!me.FormUtil.contains(form.accountedUrl, '?caller=', true)){
			form.accountedUrl = form.accountedUrl + "?caller=" + caller;
		}
		me.FormUtil.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.accountedUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					me.BaseUtil.getSetting(caller, 'autoToNext', function(v) {
						var btn = Ext.getCmp('next'),next = v&&v==1&&btn;
						if(next&&parent.Ext) {
							var datalistId = getUrlParam('datalistId');
							var datalist = parent.Ext.getCmp(datalistId);
							if(datalist){
								var datalistStore = datalist.currentStore;
								if(!datalistStore){
									next = false;
								}
								Ext.each(datalistStore, function(){
									if(this.selected == true){
										if(this.next == null){
											next = false;
										}
									}
								});
							} else {
								next = false;
							}
						}
						if(next){
							btn.fireEvent('click', btn);
						}else{
							//记账成功后刷新页面进入不可编辑的页面 
							accountedSuccess(function(){
								window.location.reload();
							});
						}
 				   });
				} else {
					if(localJson.exceptionInfo){
						var str = localJson.exceptionInfo;
						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
							str = str.replace('AFTERSUCCESS', '');
							showMessage("提示", str);
							accountedSuccess(function(){
								window.location.reload();
							});
						} else {
							showError(str);return;
						}
					}
				}
			}
		});
	},
	 beforeSubmit:function(){
		var ar_accountcurrency = Ext.getCmp('ar_accountcurrency').value,
			ar_arapcurrency = Ext.getCmp('ar_arapcurrency').value,
			ar_araprate = Ext.getCmp('ar_araprate').value,	        //冲账汇率
			ar_accountrate = Ext.getCmp('ar_accountrate').value,	//账户汇率
			ar_aramount = Ext.getCmp('ar_aramount').value,			//冲账金额
			ar_category = Ext.getCmp('ar_category').value,			//转存科目
			ar_precurrency = Ext.getCmp('ar_precurrency').value,		//转存币别
			ar_preamount = Ext.getCmp('ar_preamount').value,		    //转存金额
			ar_prerate = Ext.getCmp('ar_prerate').value,             //转存汇率
			f = Ext.getCmp('ar_aramount'),
			form = Ext.getCmp('form');
		var deposit = Ext.getCmp('ar_deposit').value,
			payment = Ext.getCmp('ar_payment').value,
			type = Ext.getCmp('ar_type').value;
		var grid = Ext.getCmp('grid'), items = grid.store.data.items;
		var me = this;
		if(type != null && type != '' ){
			if(type == '应收款' || type == '预收款' || type == '应付退款' || type == '应收票据收款'){
				if(deposit == 0 || deposit == null || deposit == '' ){
					showError('收入金额不能为空!');
					return;
				}
				if(type == '应收款' || type == '预收款' ||  type == '应付退款'){
					if(form.BaseUtil.numberFormat(ar_aramount,2) != form.BaseUtil.numberFormat(form.BaseUtil.multiply(ar_araprate, deposit),2)){
						showError('冲账汇率不正确!');
						return;
					}
				}
			} else if (type == '预付款' || type == '应付款' ||  type == '应收退款' || type == '应付票据付款'){
				if(payment == 0 || payment == null || payment == '' ){
					showError('支出金额不能为空!');
					return;
				}
				if( type == '应付款' ||  type == '应收退款'){
					if(form.BaseUtil.numberFormat(ar_aramount,2) != form.BaseUtil.numberFormat(form.BaseUtil.multiply(ar_araprate, payment),2)){
						showError('冲账汇率不正确!');
						return;
					}
				}
			} else if(type == '转存') {
				if(payment == 0 || payment == null || payment == '' ){
					showError('支出金额不能为空!');
					return;
				}
				if(Ext.isEmpty(ar_category)){
					showError('转存科目不能为空!');
					return;
				}
				if(Ext.isEmpty(ar_precurrency)){
					showError('转存币别不能为空!');
					return;
				}
				if(ar_preamount == 0 || ar_preamount == null || ar_preamount == '' ){
					showError('转存金额不能为空!');
					return;
				}
				if(ar_accountcurrency != ar_precurrency){
					if(ar_prerate == 1){
						showError('币别不一致，转存汇率为1，不能提交!');
						return;
					}
				}
			}
			if(type != '应收票据收款'  && type != '应付票据付款' && type != '费用' && type != '转存' && type != '其它收款' && type != '其它付款' && type != '保理收款' && type != '保理付款'){
				if(ar_accountcurrency != ar_arapcurrency){
					if(ar_araprate == '1'){
						showError('币别不一致，冲账汇率为1，不能提交!');
						return;
					}
				}
 			}
		}
		var debit = 0, credit = 0, doubledebit=0, doublecredit=0, balance=0;
		var hasdetail = false;
		Ext.each(items,function(item,index){
			if(!Ext.isEmpty(item.data['ard_explanation']) || !Ext.isEmpty(item.data['ard_catecode']) || !Ext.isEmpty(item.data['ard_ordercode']) || !Ext.isEmpty(item.data['ard_makecode'])){
				hasdetail = true;
				debit= debit + Number(item.data['ard_debit']);
				credit= credit + Number(item.data['ard_credit']);
				doubledebit= doubledebit + Number(item.data['ard_doubledebit']);
				doublecredit= doublecredit + Number(item.data['ard_doublecredit']);
				balance= balance + Number(item.data['ard_nowbalance']);
			}
		});
		if(type == '转存'){
			if(!Ext.isEmpty(debit)){
				if(payment-deposit != 0){
					var prerate = form.BaseUtil.numberFormat((ar_preamount+(debit-credit))/(payment-deposit),15);
					if(form.BaseUtil.numberFormat(ar_prerate,10) != form.BaseUtil.numberFormat(prerate,10) ){
						Ext.getCmp('ar_prerate').setValue(prerate);
					}
				}
			}
		}
		if(!Ext.isEmpty(Ext.getCmp('ar_source').value) && Ext.getCmp('ar_sourcetype').value !='支票' && Ext.getCmp('ar_sourcetype').value !='应付支票'){
			if(type == '应收款' || type == '应付退款' || type == '预付退款'){
				if(form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) != 0){
					if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(doublecredit-doubledebit, 2)){
						showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(doublecredit-doubledebit, 2) + '不一致!');
						return;
					}
				} else {
					if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(credit-debit, 2)){
						showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(credit-debit, 2) + '不一致!');
						return;
					}
				}
			}
			if((type == '预收款' || type == '预收退款') && hasdetail){
				var ardamount = 0;
				if(form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) != 0){
					if (type == '预收款') {
						ardamount = form.BaseUtil.numberFormat(doublecredit-doubledebit, 2);
					} else {
						ardamount = form.BaseUtil.numberFormat(doubledebit-doublecredit, 2)
					}
					if(form.BaseUtil.numberFormat(ar_aramount, 2)!= ardamount){
						showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + ardamount + '不一致!');
						return;
					}
				} else {
					if (type == '预收款') {
						ardamount = form.BaseUtil.numberFormat(credit-debit, 2);
					} else {
						ardamount = form.BaseUtil.numberFormat(debit-credit, 2);
					}
					if(form.BaseUtil.numberFormat(ar_aramount, 2)!= ardamount){
						showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + ardamount + '不一致!');
						return;
					}
				}
			}
			if(type == '应付款' || type == '应收退款'){
				if(form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) != 0){
					if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(doubledebit-doublecredit, 2)){
						showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' +  form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) + '不一致!');
						return;
					}
				} else {
					if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(debit-credit, 2)){
						showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' +  form.BaseUtil.numberFormat(debit-credit, 2) + '不一致!');
						return;
					}
				}
			}
			if(type == '预付款' || type == '应付款'){
				if(Ext.getCmp('ar_sourcetype')){
					if(Ext.getCmp('ar_sourcetype').value == "付款申请"){
						var bool = false;
						Ext.each(items,function(item,index){
							if(!Ext.isEmpty(item.data['ard_ordercode']) || !Ext.isEmpty(item.data['ard_makecode'])){
								bool = true;
								return;
							}
						});
						if(bool && type == '应付款'){
							if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(balance, 2)){
								showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表本次收款/付款金额合计' + form.BaseUtil.numberFormat(balance, 2) + '不一致!');
								return;
							}
						}
					} else {
						if(form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) != 0){
							if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(doubledebit-doublecredit, 2)){
								showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(doubledebit-doublecredit, 2) + '不一致!');
								return;
							}
						} else {
							if(form.BaseUtil.numberFormat(ar_aramount, 2)!= form.BaseUtil.numberFormat(debit-credit, 2)){
								showError('冲账金额' + form.BaseUtil.numberFormat(ar_aramount, 2) + '与从表金额' + form.BaseUtil.numberFormat(debit-credit, 2) + '不一致!');
								return;
							}
						}
					}
				}
			}
			if(type == '应收票据收款'){
				if(form.BaseUtil.numberFormat(form.BaseUtil.multiply(deposit, ar_accountrate), 2)!= form.BaseUtil.numberFormat(credit-debit, 2)){
					showError('收入金额' + form.BaseUtil.numberFormat(form.BaseUtil.multiply(deposit, ar_accountrate), 2) + '与从表金额' +  form.BaseUtil.numberFormat(credit-debit, 2) + '不一致!');
					return;
				}
			}
			if(type == '应付票据付款'){
				if(form.BaseUtil.numberFormat(form.BaseUtil.multiply(payment, ar_accountrate), 2)!= form.BaseUtil.numberFormat(debit-credit, 2)){
					showError('支出金额' + form.BaseUtil.numberFormat(form.BaseUtil.multiply(payment, ar_accountrate), 2) + '与从表金额' +  form.BaseUtil.numberFormat(debit-credit, 2) + '不一致!');
					return;
				}
			}
		}	
		var errs = [];
		Ext.Array.each(items, function(item){
			if(!Ext.isEmpty(item.data['ard_catecode'])){
				if(item.data['ca_currencytype'] != 0){
					if(Ext.isEmpty(item.data['ard_currency'])){
						errs.push('明细表第' + item.data['ard_detno'] + '行的科目是外币科目，币别不能为空！');
						return;
					}
					if(Ext.isEmpty(item.data['ard_rate']) || item.data['ard_rate'] == 0){
						errs.push('明细表第' + item.data['ard_detno'] + '行的科目是外币科目，汇率不能为0！');
						return;
					}
				}
				if(item.data['ard_doubledebit'] != 0 && item.data['ard_rate'] != 0 && item.data['ard_debit'] != 0){
					if (me.BaseUtil.numberFormat(me.BaseUtil.multiply(item.data['ard_doubledebit'], item.data['ard_rate']),2) != me.BaseUtil.numberFormat(item.data['ard_debit'],2)) {
						errs.push('明细表第' + item.data['ard_detno'] + '行，原币借方*汇率不等于本币借方！');
						return;
					}
				}
				if(item.data['ard_doublecredit'] != 0 && item.data['ard_rate'] != 0 && item.data['ard_credit'] != 0){
					if (me.BaseUtil.numberFormat(me.BaseUtil.multiply(item.data['ard_doublecredit'], item.data['ard_rate']),2) != me.BaseUtil.numberFormat(item.data['ard_credit'],2)) {
						errs.push('明细表第' + item.data['ard_detno'] + '行，原币贷方*汇率不等于本币贷方！');
						return;
					}
				}
			}
		});
		if(errs.length > 0) {
			showError(errs.join('<br>'));
			return;
		}
		this.FormUtil.onSubmit(Ext.getCmp('ar_id').value, false, this.beforeUpdate, this);
	 },
	 copy: function(){
		 	var me = this;
			var form = Ext.getCmp('form');
			var v = form.down('#ar_id').value;
			if(v > 0) {
				form.setLoading(true);
				Ext.Ajax.request({
					url: basePath + 'fa/gs/copyAccountRegister.action',
					params: {
						id: v
					},
					callback: function(opt, s, r){
						form.setLoading(false);
						var res = Ext.decode(r.responseText);
						if(res.ar) {
							turnSuccess(function(){
		    					var id = res.ar.ar_id;
		    					var url = "jsps/fa/gs/accountRegister.jsp?formCondition=ar_idIS" + 
									 + id + "&gridCondition=ard_aridIS" + id + "&whoami=AccountRegister!Bank";
		    					me.FormUtil.onAdd('accountRegister' + id, '银行登记' + id, url);
		    				});
						} else {
							showError(res.exceptionInfo);
						}
					}
				});
			}
		},
		getApamount: function(){
			var grid = Ext.getCmp('grid');
			var items = grid.store.data.items;
			var apamount = 0, type = Ext.getCmp('ar_type').value, thisamount = 0, bool = false;
			if(type != null && type != '' ){
				if(type == '应付款'){
					Ext.each(items,function(item,index){
						if(!Ext.isEmpty(item.data['ard_ordercode'])){
							apamount= apamount + Number(item.data['ard_nowbalance']);
						}
					});
					Ext.getCmp('ar_apamount').setValue(Ext.util.Format.number(apamount,'0.00'));
				}
				if(type == '预付款'){
					Ext.each(items,function(item,index){
						if(Ext.isEmpty(item.data['ard_ordercode']) && Ext.isEmpty(item.data['ard_makecode'])){
							apamount = apamount + Number(item.data['ard_nowbalance']);
						} else {						
							bool = true;
						}
						thisamount = thisamount + Number(item.data['ard_nowbalance']);
					});
					if(bool){
						Ext.getCmp('ar_aramount').setValue(thisamount);
					}
					var cmamount = Ext.Number.from(Ext.getCmp('ar_aramount').getValue(), 0);
					Ext.getCmp('ar_apamount').setValue(Ext.util.Format.number(cmamount-apamount,'0.00'));
				}
			}
		},
		showSource: function(id){
			var win = Ext.getCmp('flow_win');
			if(!win) {
				Ext.create('Ext.Window', {
					id: 'source_win',
					height: 400,
					width: 580,
					iconCls: 'x-button-icon-set',
					autoShow: true,
					title: '银行登记来源',
					maximizable : true,
					layout : 'anchor',
					items: [{
						anchor: '100% 100%',
						xtype: 'accountregisterbill',
						listeners: {
							afterrender: function(grid){
			    				if(formCondition == null || formCondition.toString().trim() == ''){
									grid.getMyData(-1);
								} else {
									grid.getMyData(id);
								}
			    			}
						}
					}],
					buttonAlign: 'center',
					buttons: [{
						text: $I18N.common.button.erpCloseButton,
						cls: 'x-btn-gray',
						iconCls: 'x-button-icon-close',
						handler: function(btn) {
							btn.ownerCt.ownerCt.close();
						}
					}]
				});
			} else {
				win.show();
			}
		},
		levelOut:function (target){
			var grid = Ext.getCmp('grid'), me = this;
			var record = grid.selModel.lastSelected;
			if(record){
				var f = Ext.getCmp('ar_currencytype');
				var debit = 0;
				var credit = 0;
				var rate = record.get('ard_rate');
				rate = rate == 0 ? 1 : rate;
				grid.getStore().each(function(item){
					if(item.id != record.id){
						debit += item.get('ard_debit');
						credit += item.get('ard_credit');
					}
				});
				var targetName = target.name;
				if(record.get('ard_debit') != 0)
					targetName = 'ard_debit';
				else if(record.get('ard_credit') != 0)
					targetName = 'ard_credit';
				if(targetName && typeof targetName == 'string') {
					if(targetName == 'ard_debit') {
						debit = credit - debit;
						record.set('ard_debit', me.BaseUtil.numberFormat(debit, 4));
						if(f.checked) {
							record.set('ard_doubledebit', me.BaseUtil.numberFormat(debit/rate, 4));
						}
					} else if(targetName == 'ard_credit'){
						credit = debit - credit;
						record.set('ard_credit', me.BaseUtil.numberFormat(credit, 4));
						if(f.checked) {
							record.set('ard_doublecredit', me.BaseUtil.numberFormat(credit/rate, 4));
						}
					}
					if(target.name == targetName)
						target.value = record.get(targetName);
				} else {
					if(credit > debit) {
						record.set('ard_debit', credit - debit);
						if(f.checked) {
							record.set('ard_doubledebit', me.BaseUtil.numberFormat((credit - debit)/rate, 4));
						}
					} else {
						record.set('ard_credit', debit - credit);
						if(f.checked) {
							record.set('ard_doublecredit', me.BaseUtil.numberFormat((debit - credit)/rate, 4));
						}
					}
				}
			}
		}
});