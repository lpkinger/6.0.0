Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.APCheck', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.button.PrintByCondition','core.form.Panel','fa.arp.APCheck','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.button.ResSubmit','core.button.Flow','core.button.Post','core.button.ResPost', 'core.button.Confirm',
      		'core.button.Banned', 'core.button.ConfirmBill','core.button.TurnYHFKSQ','core.button.Cancel','core.button.ResSubmitTurnSale','core.button.SubmitTurnSale',
      		'core.trigger.MultiDbfindTrigger','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.getAmount();
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ac_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.getAmount();
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addAPCheck', '新增应付对账', 'jsps/fa/arp/apCheck.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ac_statuscode');
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
    				var status = Ext.getCmp('ac_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ac_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ac_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ac_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ac_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				var confirmstatus = Ext.getCmp('ac_confirmstatus');
					if(confirmstatus && confirmstatus.value == '已确认' ){
						btn.hide();
					}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ac_id').value);
    			}
    		},
    		'erpPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ac_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onPost(Ext.getCmp('ac_id').value);
    			}
    		},
    		'erpResPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ac_statuscode');
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResPost(Ext.getCmp('ac_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    			 var reportName = '';
                 reportName = "apcheck_voice";
                 var condition = '{apcheck.ac_id}=' + Ext.getCmp('ac_id').value + '';
                 var id = Ext.getCmp('ac_id').value;
                 me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    		'erpBannedButton': {
    			beforerender:function(btn){
    				btn.setText('不同意');
    			},
				afterrender: function(btn){
					var status = Ext.getCmp('ac_confirmstatus'), b2bid = Ext.getCmp('ac_b2bid');				
					if(b2bid && !Ext.isEmpty(b2bid.value) && b2bid.value != 0 && status.value != '已确认'){
						btn.show();
					} else {
						btn.hide();
					}
					if(status && (status.value == '已确认' || status.value == '不同意') ){
						btn.hide();
					}
				},
				click: function(btn){
					// confirm box modify
					// zhuth 2018-2-1
					Ext.Msg.confirm('提示', '确定不同意?', function(btn) {
						if(btn == 'yes') {
							//me.FormUtil.onBanned(Ext.getCmp('pr_id').value);
							//zhongyl 2014 03 13
							var reason = Ext.getCmp('ac_reason');
							if(reason && (reason.value == null||reason.value=='')){
								showError('请填写不同意原因!');
							}else{
								Ext.Ajax.request({
									url : basePath + 'fa/arp/resConfirmAPCheck.action',
									params: {
										id: Ext.getCmp('ac_id').value,
										reason:Ext.getCmp('ac_reason').value
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
						}
					});
				}
			},
    		'field[name=ac_reason]':{
    			beforerender:function(field){
    				var status = Ext.getCmp('ac_confirmstatus');
    				if(status && status.value != '已确认'){
    					field.readOnly=false;
    				}
    			}
    		},
    		'erpConfirmButton': {
    			afterrender: function(btn){
    				var confirmstatus = Ext.getCmp('ac_confirmstatus');
					if(confirmstatus && (confirmstatus.value == '已确认' || confirmstatus.value == '不同意') ){
						btn.hide();
					}
    				var status = Ext.getCmp('ac_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定对账吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'fa/arp/confirmAPCheck.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('ac_id').value
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
    		'erpCancelButton': {
    			beforerender:function(btn){
    				btn.setText('取消确认');
    			},
    			afterrender: function(btn){
    				var confirmstatus = Ext.getCmp('ac_confirmstatus'), status = Ext.getCmp('ac_statuscode');
					if(confirmstatus && confirmstatus.value != '已确认' ){
						btn.hide();
					}
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定取消确认吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'fa/arp/cancelAPCheck.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('ac_id').value
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
    		'erpConfirmBillButton':{
 			   afterrender: function(btn){
 				  var status = Ext.getCmp("ac_confirmstatus"), auditstatus = Ext.getCmp('ac_statuscode');
				   if(status && status.value != '已确认'){
					   btn.hide();
				   }
				   if(auditstatus && auditstatus.value != 'AUDITED'){
					   btn.hide();
				   }
 			   },
 			   click: function(m){
 			   	   me.BaseUtil.getSetting('sys', 'needCheck', function(bool) {
 			   		   var condition = ' ad_acid=' + Ext.getCmp('ac_id').value +' and abs(nvl(ad_yqty,0)) < abs(nvl(ad_qty,0)) and nvl(ad_abclass,\' \')<>\'其它应付单\' and nvl(pd_piclass,\' \') not in (\'不良品入库单\',\'不良品出库单\')';
 			   		   if (bool){
 			   			   condition = ' ad_acid=' + Ext.getCmp('ac_id').value +' and abs(nvl(ad_yqty,0)) < abs(nvl(ad_qty,0)) and nvl(pd_piclass,\' \') not in (\'不良品入库单\',\'不良品出库单\')'; 
 			   		   }
 			   		   var b2bid = Ext.getCmp('ac_b2bid').value;
	 			   	   if(b2bid){
	 			   	   		condition += " and nvl(ad_sourcetype,' ')<>'APBILL'";
	 			   	   }
	  				   me.batchdeal('APCheck!ToBill!Deal', condition, 'fa/arp/apCheckTurnBill.action');
	 			   });
 			   }
 		   },
 		  'erpTurnYHFKSQButton':{
			   afterrender: function(btn){
				   me.BaseUtil.getSetting('sys', 'autoCreateApBill', function(bool) {
   					   if(!bool){
   						   btn.hide();
   					   } 
   					   var status = Ext.getCmp("ac_confirmstatus"), auditstatus = Ext.getCmp('ac_statuscode');
	 				   if(status && status.value != '已确认'){
	 					   btn.hide();
	 				   }
	 				   if(auditstatus && auditstatus.value != 'AUDITED'){
	 					   btn.hide();
	 				   }
                   },false);
			   },
			   click: function(m){
				   warnMsg("确定生成付款申请?", function(btn){
					   if(btn == 'yes'){
   							me.FormUtil.getActiveTab().setLoading(true);//loading...
   							Ext.Ajax.request({
   								url : basePath + 'fa/arp/turnPayPlease.action',
   								params: {
	   	    			   			id: Ext.getCmp('ac_id').value,
	   	    			   			caller: caller
	   	    			   		},
	   	    			   		method : 'post',
		   	    			   	callback : function(options,success,response){
		   	    		   			me.FormUtil.getActiveTab().setLoading(false);
		   	    		   			var r = new Ext.decode(response.responseText);
		   	    		   			if(r.exceptionInfo){
		   	    		   				showError(r.exceptionInfo);
		   	    		   			}
		   	    		   			if(r.success){
		   	    		   				if(r.content && r.content.pp_id){
		   	    	    					showMessage("提示", "转入成功,付款申请单号: <a href=\"javascript:openUrl2('jsps/fa/arp/payplease.jsp?formCondition=pp_idIS" + r.content.pp_id
		   	    	    							 + "&gridCondition=ppd_ppidIS" + r.content.pp_id + "','付款申请单','pp_id'," + r.content.pp_id
		   	    	    							 + ")\">" + r.content.pp_code + "</a>");
		   	    	    				}
		   	    	    				window.location.reload();
		   	    		   			}
		   	    		   		}
	   	    				});
	   					}
   					});
			   }
		   },
		   'erpSubmitTurnSaleButton':{
	   			click:function(btn){
	   				Ext.Ajax.request({//拿到grid的columns
						url : basePath + "fa/arp/submitAPCheckConfirm.action",
						params: {
							id : Ext.getCmp('ac_id').getValue()
						},
						method : 'post',
						callback : function(options,success,response){
							var res = new Ext.decode(response.responseText);
							if(res.exceptionInfo){
								var str = res.exceptionInfo;
			    	   			if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
			    	   				str = str.replace('AFTERSUCCESS', '');
			    	   				me.FormUtil.getMultiAssigns(Ext.getCmp('ac_id').getValue(),'APCheck!Confirm',btn.ownerCt.ownerCt);
			    	   			}
			    	   			showMessage("提示", str);return;
							} else if(res.success){
								me.FormUtil.getMultiAssigns(Ext.getCmp('ac_id').getValue(),'APCheck!Confirm',btn.ownerCt.ownerCt);
							}
						}
	   				});
	   			},
	   			afterrender:function(btn){
	   				btn.setText("提交(确认)");
	   				var ac_confirmstatus=Ext.getCmp('ac_confirmstatus').getValue();
	   				var ac_statuscode=Ext.getCmp('ac_statuscode').getValue();
	   				var bool=true;
	   				if((Ext.isEmpty(ac_confirmstatus)||ac_confirmstatus=='未确认') && ac_statuscode=='AUDITED'){
	   					bool=false;
	   				}
	   				if(bool) btn.hide();
	   			}
	   		},
	   		'erpResSubmitTurnSaleButton':{
	   			click:function(btn){
	   				Ext.Ajax.request({//拿到grid的columns
						url : basePath + "fa/arp/resSubmitAPCheckConfirm.action",
						params: {
							id : Ext.getCmp('ac_id').getValue()
						},
						method : 'post',
						callback : function(options,success,response){
							var res = new Ext.decode(response.responseText);
							if(res.exceptionInfo){
								showError(res.exceptionInfo);return;
							}
							if(res.success){
								resSubmitSuccess();
								window.location.reload();
							}
						}
	   				});
	   			},
	   			afterrender:function(btn){
	   				btn.setText("反提交(确认)");
	   				var ac_confirmstatus = Ext.getCmp('ac_confirmstatus').getValue();
	   				if(ac_confirmstatus != '已提交'){
	   					btn.hide();
	   				}
	   			}
	   		}
    	});
    }, 
    //计算金额   并写入主表对账总额字段
	getAmount: function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var amount = 0;
		Ext.each(items,function(item,index){
			if(!Ext.isEmpty(item.data['ad_amount'])){
				amount= amount + Number(item.data['ad_amount']);
			}
		});
		Ext.getCmp('ac_checkamount').setValue(Ext.Number.toFixed(amount, 2));
	},
    beforeSubmit:function(btn){
    	var me = this;
    	var grid = Ext.getCmp('grid'),items=grid.store.data.items;
    	var amount = Number(Ext.getCmp('ac_checkamount').getValue());
    	var detailamount = 0;
    	me.FormUtil.onSubmit(Ext.getCmp('ac_id').value);
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	batchdeal: function(nCaller, condition, url){
 	   var win = new Ext.window.Window({
 		   id : 'win',
 		   height: "100%",
 		   width: "80%",
 		   maximizable : true,
 		   buttonAlign : 'center',
 		   layout : 'anchor',
 		   items: [{
 			   tag : 'iframe',
 			   frame : true,
 			   anchor : '100% 100%',
 			   layout : 'fit',
 			   html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/editorColumn.jsp?caller=' + nCaller 
 			   + "&condition=" + condition +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
 		   }],
 		   buttons : [{
 			   name: 'confirm',
 			   text : $I18N.common.button.erpConfirmButton,
 			   iconCls: 'x-button-icon-confirm',
 			   cls: 'x-btn-gray',
 			   listeners: {
 				   buffer: 500,
 				   click: function(btn) {
 					   var grid = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
 					   btn.setDisabled(true);
 					   grid.updateAction(url);
 				   }
 			   }
 		   }, {
 			   text : $I18N.common.button.erpCloseButton,
 			   iconCls: 'x-button-icon-close',
 			   cls: 'x-btn-gray',
 			   handler : function(){
 				   Ext.getCmp('win').close();
 			   }
 		   }]
 	   });
 	   win.show();
    }
});