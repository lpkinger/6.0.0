Ext.QuickTips.init();
Ext.define('erp.controller.ma.createAccountBook.CreateAccountBook', {
    extend: 'Ext.app.Controller',
    views: ['ma.createAccountBook.CreateAccountBook',
	    'ma.createAccountBook.InfoCardPanel', 
	    'ma.createAccountBook.CompanyInfoForm',
	    'ma.createAccountBook.AccountBookInfoForm',
	    'ma.createAccountBook.ActivityAccountBookForm',
	    'ma.createAccountBook.NavigationBar',
	    'ma.createAccountBook.reserveInfoForm.HRInfoForm',
	    'ma.createAccountBook.reserveInfoForm.FAInfoForm',
	    'ma.createAccountBook.reserveInfoForm.ProInfoForm',
	    'ma.createAccountBook.reserveInfoForm.SCInfoForm',
	    'core.form.FileField',
	    ],
    requires: ['erp.util.BaseUtil'],
    init:function(){
    	var self = this;
    	this.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({
    		'infocard': {
    			afterrender: function(th, eOpts) {
    				var form = Ext.getCmp('createAccountBook_companyInfoForm');
    				var me = this;
    				// 获得当前步骤以及数据
    				this.ajaxRequest.bind(this)('ma/createAccountBook/getAccountInfo.action', null, true, function(response, options) {
    					var res = new Ext.decode(response.responseText);
	    				me.autoLoad(res.data);
	    				
	    				var currentStep = res.data ? (res.data.ML_BUSINESSUUID ? 2 : 1): 0;
	    				if(currentStep > 0) {
	    					Ext.MessageBox.show({
						        title: '警告',
						        msg: '存在未完成操作记录！',
						        buttons: Ext.MessageBox.OK,
						        icon: Ext.MessageBox.WARNING
						    });
	    				}
	    				th.activeItem = 0;
	    				for(var i = 0; i < currentStep; i++) {
		    				me.stepJump(th, 'next');
		    				me.validBusinessName();
		    				me.validBusinessCode();
	    				}
    				});
    			}
    		},
    		'mfilefield filefield': {
    			beforerender: function(th, eOpts) {
    				th.buttonText = '选择...'
    			},
    			change: function(th, fileStr, eOpts){
					var fs = th.ownerCt.ownerCt.ownerCt;
					var textfield = fs.down('textfield');
					textfield.setValue(fileStr);
				}
    		},
    		'companyinfo textfield[id=createAccountBook_companyInfo_name]': {
    			change: function(th, newValue, oldValue, eOpts){
    				th.clearInvalid( );
    			},
    			blur: function(th, eOpts) {
					this.validBusinessName();
    			}
    		},
    		'companyinfo textfield[id=companyInfo_licenseNo]': {
    			change: function(th, newValue, oldValue, eOpts){
    				th.clearInvalid( );
    			},
    			blur: function(th, eOpts) {
    				this.validBusinessCode();
    			}
    		},
    		'companyinfo button[id=companyInfoNextBtn]': {
    			click: function(btn, event, eOpts) {
					this.stepJump(btn.up('infocard'), 'next');
    			}
    		},
    		'accountbookinfo radiogroup[id=refer_sys]': {
    			afterrender: function(th) {
    				var current_sys_field = th.items.items[0];
    				var current_sys_info;
    				var me = this;
    				this.ajaxRequest.bind(this)('/ma/createAccountBook/getSource.action', null, true, function(response, options) {
	    				var res = new Ext.decode(response.responseText);
	    				current_sys_info = {
		   					boxLabel: res.sourceName,
		   					name: 'refer_sys',
	            			inputValue: res.sourceId,
	            			checked: true
		   				}
	    				current_sys_field.boxLabel = current_sys_info.boxLabel; 
	    				current_sys_field.inputValue = current_sys_info.inputValue;
	    				current_sys_field.checked = current_sys_info.checked;
	    				
					    if (current_sys_field.rendered) {  
					        current_sys_field.getEl().down('.x-form-cb-label').update(current_sys_field.boxLabel);
					        
					    } 
	    			});
            	}
    		},
    		'accountbookinfo button[id=accountBookInfoConfirmBtn]': {
    			click: function(btn, event, eOpts) {
    				var companyInfoForm = Ext.getCmp('createAccountBook_companyInfoForm');
    				var accountInfoForm = Ext.getCmp('createAccountBook_accountBookInfoForm');
    				var acitivityForm = Ext.getCmp('createAccountBook_activityAccountBookForm');
    				
					var mf = companyInfoForm.down('mfilefield');
    				var filepath = mf.items.items[2] ? mf.items.items[2].filepath : companyInfoForm.getForm().getValues().licensePath;
    				var hidden = companyInfoForm.down('hidden');
					hidden.setValue(filepath);
    				// 得到数据 
					var companyInfo = Ext.JSON.encode(companyInfoForm.getForm().getValues());
					var accountInfo = Ext.JSON.encode(accountInfoForm.getForm().getValues());
    				var inCloud = false;
    				var me = this;
    				// 显示进度条
			        Ext.Msg.wait("此过程大概需要等待30秒，请勿关闭此界面！","开立账套中...",{text:"正在加载。。。"});
					this.ajaxRequest.bind(this)('ma/createAccountBook/applyUsoftCloud.action',
						{
							businessInfo: companyInfo,
							accountInfo: accountInfo
						},
						true,
						function(response, options) {
							var res = new Ext.decode(response.responseText);
							if(!res.inCloud) {
								showError('开立账套失败： 注册优软云失败！');
							}else {
								accountInfoForm.inCloud = true;
								accountInfoForm.down('textfield').setValue(res.uuid);
								inCloud = true;
							}
						},
						function(response, options) {
							var res = new Ext.decode(response.responseText);
							showError('开立账套失败： 注册优软云失败！');
						},
						function() {
							me.ajaxRequest.bind(me)('ma/createAccountBook/saveAccountInfo.action', 
		    					{
			    					businessInfo: companyInfo,
			    					accountInfo: accountInfo
			    				},
		    					true,
		    					function(response, options) {
		    						var res = new Ext.decode(response.responseText);
		    						accountInfoForm.inCloud = true;
		    						var data = res.data;
					    			if(inCloud && data) {
				    					acitivityForm.getForm().loadRecord({data:data,getData: function(){return this.data}});
					    				me.stepJump(btn.up('infocard'), 'next');
				    				}
		    					}, null, function() {
		    						// 隐藏进度条
		    						Ext.Msg.hide();
		    					}
		    				);
						}
					);
    			}
    		},
    		'accountbookinfo button[id=accountBookInfoPrevBtn]': {
    			click: function(btn, event, eOpts) {
    				var card = btn.up('infocard');
    				this.stepJump(card, 'prev');
    			}
    		},
    		'activityaccountbook button[id=activityAccountBook_activeBtn]': {
    			click: function(btn, event, eOpts) {
    				var form = btn.ownerCt.ownerCt;
    				var id = form.getForm().getValues().newAccountBookID;
    				// 激活
    				this.ajaxRequest.bind(this)('/ma/createAccountBook/active.action', {accountID: id}, false, function(response, options) {
    					var res = new Ext.decode(response.responseText);
    					if(res.success) {
		    				//成功之后不可重复点击
		    				btn.setDisabled(true);
    					}
    				}, function(res) {
    					showError("激活账套失败。");
    				});
    			}
    		},
    		'activityaccountbook button[id=activityAccountBook_closeBtn]': {
    			click: function(btn, event, eOpts) {
    				this.BaseUtil.getActiveTab().close();
    			}
    		}
    	});
    },
    
    /**  
     * 将数据填入form表单
     */
    autoLoad: function(data) {
    	if(!data) {
    		return;
    	}
    	var companyInfo = {
    		data:{
				companyName : data.ML_BUSINESSNAME,
				companyShortName : data.ML_BUSINESSSHORTNAME,
				companyAddr : data.ML_ADDR,
				licenseName: data.ML_LICENSENAME,
				licensePath: data.ML_LICENSE,
				licenseNo : data.ML_LICENSENO,
				managerEmail : data.ML_RECORDEREMAIL,
				managerName : data.ML_RECORDER,
				managerPhone : data.ML_RECORDERTEL
    		},
    		getData: function() {
    			return this.data;
    		}
    	}
    	var accountInfoData = Object.assign({}, {
    		refer_sys: data.ML_SOURCE,
    		fa_account_period: data.ML_ACOUNTDATE
    	}, this.getAccountTables(data.ML_TABLES + ' '));
    	
    	var accountInfo = {
    		data: accountInfoData,
    		getData: function() {
    			return this.data;
    		}
    	}
    	
    	var activityInfo = {
    		data: {
    			newAccountBookID: data.ML_ID,
    			newAccountBookDesc: data.ML_ACCOUNTDESC,
    			newAccountBookName: data.ML_ACCOUNTNAME,
    			managerID: data.ML_RECORDERTEL,
    			managerName: data.ML_RECORDER,
    			managerPassword: data.ML_PASSWORD
    		},
    		getData: function() {
    			return this.data;
    		}
    	}
		
		var companyInfoForm = Ext.getCmp('createAccountBook_companyInfoForm');
		var accountInfoForm = Ext.getCmp('createAccountBook_accountBookInfoForm');
		var activityForm = Ext.getCmp('createAccountBook_activityAccountBookForm');
		companyInfoForm.getForm().loadRecord(companyInfo);
		accountInfoForm.getForm().loadRecord(accountInfo);
		var referRadioGroup = accountInfoForm.getForm().findField('refer_sys');
		// 为radioGroup赋值
		for (var k in referRadioGroup.items.items) {
			if(referRadioGroup.items.items[k].inputValue == accountInfo.data.refer_sys) {
	            referRadioGroup.items.items[k].setValue(referRadioGroup.items.items[k].inputValue);     
			}
        }
		activityForm.getForm().loadRecord(activityInfo);
    },
    
    getAccountTables: function(tables) {
    	tables = tables.trim();
    	tables = tables + ',';
    	var accountInfoForm = Ext.getCmp('createAccountBook_accountBookInfoForm');
    	var accountInfo = accountInfoForm.getForm().getValues();
    	var tablesInfo = {};
    	for(var key in accountInfo) {
    		// 如果该字段field在tables字符串中存在则该项为选中
    		if(tables.indexOf(key + ',') != -1) {
    			tablesInfo[key] = '1';
    		}
    	}
    	return tablesInfo;
    },
    
    
    /** 
     * 根据公司名检验是否已注册优软云以及是否已开立过账套 
     */
    validBusinessName: function() {
    	var form = Ext.getCmp('createAccountBook_companyInfoForm');
    	var namefield = Ext.getCmp('createAccountBook_companyInfo_name');
    	var codefield = Ext.getCmp('companyInfo_licenseNo');
    	var companyName = namefield.getValue();
    	
    	this.ajaxRequest.bind(this)('ma/createAccountBook/validBusinessName.action',
			{businessName: companyName},
			true,
			function(response, options) {
				var res = new Ext.decode(response.responseText);
			    
			    if(res.exceptionInfo){
			    	form.error = true;
			   	}
			   	if(res.success){
		   			form.inCloud = res.inCloud;
			   		form.licenseNo = res.businessCode;
			   		form.hasCreate = res.hasCreate;
			   		form.error = false;
			   	}
   			}, function() {
   				form.error = false;
   			}, function() {
   				namefield.isValid();
   				codefield.isValid();
   			}
		);
    },
    
    /** 
     * 检验营业执照号 
     */
    validBusinessCode: function() {
    	var form = Ext.getCmp('createAccountBook_companyInfoForm');
    	var field = Ext.getCmp('companyInfo_licenseNo');
    	var licenseNo = field.getValue();
    	this.ajaxRequest.bind(this)('ma/createAccountBook/validBusinessCode.action',
    		{
				businessCode: licenseNo
			},
			true,
			function(response, options) {
	   			var res = new Ext.decode(response.responseText);
			    if(res.exceptionInfo){
			    	form.error = true;
			   	}
			   	if(res.success){
			   		form.companyName = res.companyName;
			   		form.error = false;
			   	}
			},
			function(response, options) {
				form.error = true;
			},
			function() {
				field.isValid();
			}
		);
    },
    
    
    /**
     * 步骤跳转
     */
	stepJump: function(card, dir) {
		card.getLayout()[dir]();
		var current_page = card.getLayout().getActiveItem();
		var tplitem = card.up('viewport').down('navigationbar').items.items[0];
    	var steps = tplitem.el.dom.getElementsByTagName('a');
    	for(var i = 0; i < steps.length; i++) {
    		var step = steps[i];
    		step.classList.remove('done');
    		step.classList.remove('current');
    		if(i < current_page.pageIndex) {
    			step.classList.add('done');
    		}else if(i == current_page.pageIndex) {
    			step.classList.add('current');
    		}
    	}
	},
	
	/**
	 * Ajax请求
	 */ 	
	ajaxRequest: function(url, params, async, successFunc, failFunc, callback) {
		Ext.Ajax.request({
    		url: basePath + url,
    		params: params,
			method: 'post',
			async: async,
			timeout: 7000,
			success: function(response, options){
				if(successFunc) {
					successFunc(response, options);
				}
			},
			failure: function(response, options) {
				if(failFunc) {
					failFunc(response, options);
				}
			},
	   		callback: function(options,success,response){
	   			if(callback){
		   			callback(options,success,response);
	   			}
		    }
		});
	}
 });