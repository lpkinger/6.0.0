Ext.QuickTips.init();
Ext.define('erp.controller.b2c.purchase.b2cPurchase', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['b2c.purchase.b2cPanel','b2c.common.b2cForm','b2c.common.Viewport','b2c.common.b2cGrid', 'core.toolbar.Toolbar', 
            'core.form.MultiField', 'core.button.Save', 'core.button.Add', 'core.button.Submit',
            'core.button.Print', 'core.button.PrintHK', 'core.button.PrintEn','core.button.Upload', 'core.button.ResAudit', 
            'core.button.Audit', 'core.button.Close', 'core.button.Delete', 'core.button.Update', 'core.button.B2B',
            'core.button.DeleteDetail', 'core.button.ResSubmit', 'core.button.End','core.button.Printyestax', 'core.button.Printnotax','core.button.AttendDataCom',
            'core.button.ResEnd', 'core.button.GetPrice', 'core.button.Export', 'core.button.StandardPrice',
            'core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger', 'core.form.YnField',
            'core.grid.YnColumn', 'core.form.StatusField', 'core.form.FileField', 'core.button.PrintByCondition',
            'core.button.CopyAll', 'core.button.ResetSync', 'core.button.RefreshSync','core.button.RefreshQty','core.button.Split','core.button.ModifyDetail','core.button.TurnBankRegister2',
            ],
    init: function() {
        var me = this;
        this.control({
            'erpGridPanel2': {
                afterrender: function(grid) {
                    var status = Ext.getCmp('pu_statuscode');
                    if (status && status.value != 'ENTERING' && status.value != 'COMMITED') {
                        Ext.each(grid.columns,
                        function(c) {
                            c.setEditor(null);
                        });
                    }  
                },
                itemclick: this.onGridItemClick
            },
            'field[name=pu_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=pu_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'field[name=pu_vendremarkcode]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=pu_vendcode]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = "vr_vendcode='" + value + "'";
    				}
    			}
    		},
    		'field[name=pu_outcredit]': {
    			afterrender: function(f){
    				if(f.value != null && f.value != '' && f.value != 0){
    					f.inputEl.setStyle({color: 'OrangeRed'});
    				}
    			}
            },
            'field[name=pu_outamount]': {
    			afterrender: function(f){
    				if(f.value != null && f.value != '' && f.value != 0){
    					f.inputEl.setStyle({color: 'OrangeRed'});
    				}
    			}
            },
            'field[name=pu_departmentcode]': {
    			beforetrigger:function(t){
    				var cop = Ext.getCmp('pu_cop').getValue();
    				if(cop&&cop!=''){
    					t.findConfig="ca_cop='"+cop+"'";
    				}else{
    					t.findConfig='';
    				}
    			}
    		},
            'dbfindtrigger[name=pd_price]': {
  			   focus: function(t){
  				   t.setHideTrigger(false);
  				   t.setReadOnly(false);
  				   if(Ext.getCmp('pu_vendcode')){
  					   var cucode = Ext.getCmp('pu_vendcode').value,
  					   	   currency = Ext.getCmp('pu_currency').value;
  					   	   record = Ext.getCmp('grid').selModel.lastSelected,
    				           prodcode = record.data['pd_prodcode'];
  					   if(Ext.isEmpty(cucode)){
 	    					 showError("请先选择供应商编号!");
 	    					 t.setHideTrigger(true);
 	    					 t.setReadOnly(true);
 	    			   } else if(Ext.isEmpty(currency)){
 	    					 showError("请先填写币别!");
 	    					 t.setHideTrigger(true);
 	    					 t.setReadOnly(true);  
 	    			   } else if(Ext.isEmpty(prodcode)){
 	    					 showError("请先选择物料编号!");
 	    					 t.setHideTrigger(true);
 	    					 t.setReadOnly(true);  
 	    			   } else {
 	    				   t.dbBaseCondition = "PPD_VENDCODE='" + cucode + "' and PPD_CURRENCY='" + currency + "' and ppd_prodcode='" + prodcode + "'";
  					   }
  				   }
  			   }
            },
            'erpSaveButton': {
                click: function(btn) {
                	var form = me.getForm(btn), codeField = Ext.getCmp(form.codeField);
					if(codeField.value == null || codeField.value == ''){
						me.BaseUtil.getRandomNumber(caller);//自动添加编号
						var res = me.getLeadCode(Ext.getCmp('pu_kind').value);
						if(res != null && res != ''){
							codeField.setValue(res + codeField.getValue());
						}
					}
                    //保存之前的一些前台的逻辑判定
                    this.beforeSavePurchase();
                }
            },
            'erpDeleteButton': {
                click: function(btn) {
                    me.FormUtil.onDelete(Ext.getCmp('pu_id').value);
                }
            },
            'erpDeleteDetailButton': {
                afterrender: function(btn){
                    btn.ownerCt.add({
						text: $I18N.common.button['updatevendorbackInfo'],
						id: 'updatevendorbackInfo',
						cls: 'x-btn-gray',
						disabled: true
					});
                }
            },
            'erpGetPriceButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pu_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    id = Ext.getCmp('pu_id').value;
                    Ext.Ajax.request({
                        url: basePath + "scm/purchase/getPrice.action",
                        params: {
                            id: id
                        },
                        method: 'post',
                        callback: function(options, success, response) {
                            var res = new Ext.decode(response.responseText);
                            if (res.exceptionInfo != null) {
                                showError(res.exceptionInfo);
                                return;
                            }
                            if (res.success) {
                                updateSuccess(function(btn) {
                                    //update成功后刷新页面进入可编辑的页面 
                                    window.location.reload();
                                });
                            }
                        }
                    });

                }
            },
            'erpStandardPriceButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pu_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    id = Ext.getCmp('pu_id').value;
                    Ext.Ajax.request({
                        url: basePath + "scm/purchase/getStandardPrice.action",
                        params: {
                            id: id
                        },
                        method: 'post',
                        callback: function(options, success, response) {
                            var res = new Ext.decode(response.responseText);
                            if (res.exceptionInfo != null) {
                                showError(res.exceptionInfo);
                                return;
                            }
                            if (res.success) {
                                updateSuccess(function(btn) {
                                    //update成功后刷新页面进入可编辑的页面 
                                    window.location.reload();
                                });
                            }
                        }
                    });

                }
            },
            'erpUpdateButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pu_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.beforeUpdate();
                }
            },
            'erpAddButton': {
                click: function() {
                    me.FormUtil.onAdd('addPurchase', '新增采购单', 'jsps/b2c/purchase/b2cPurchase.jsp');
                }
            },
            'erpCloseButton': {
                click: function(btn) {
                    me.FormUtil.beforeClose(me);
                }
            },
            'erpSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pu_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    var grid = Ext.getCmp('grid'), items = grid.store.data.items;
                    var bool = true, pudate = Ext.getCmp('pu_date').value;
                    //数量不能为空或0
                    Ext.each(items,
                    function(item) {
                        if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
                            if (item.data['pd_qty'] == null) {
                                bool = false;
                                showError('明细表第' + item.data['pd_detno'] + '行的数量为空');
                                return;
                            }  else if (Ext.Date.format(item.data['pd_delivery'],'Y-m-d') < Ext.Date.format(pudate,'Y-m-d')) {
			                    bool = false;
			                    showError('明细表第' + item.data['pd_detno'] + '行的交货日期小于单据日期');
			                    return;
                			}  else if (Ext.Date.format(item.data['pd_delivery'],'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')) {
			                    bool = false;
			                    showError('明细表第' + item.data['pd_detno'] + '行的交货日期小于当前日期');
			                    return;
                			}
                        }
                    });
                    //物料交货日期不能小于录入日期
                    Ext.each(items,
                    function(item) {
                        if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
                            if (item.data['pd_delivery'] == null) {
                                bool = false;
                                showError('明细表第' + item.data['pd_detno'] + '行的承诺日期为空');
                                return;
                            } else if (Ext.Date.format(item.data['pd_delivery'],'Y-m-d') < Ext.Date.format(Ext.getCmp('pu_indate').value,'Y-m-d')) {
                                bool = false;
                                showError('明细表第' + item.data['pd_detno'] + '行的承诺日期小于单据录入日期');
                                return;
                            }
                        }
                    });
                    if (bool) {
                        me.FormUtil.onSubmit(Ext.getCmp('pu_id').value);
                    }
                }
            },
            'erpResSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pu_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onResSubmit(Ext.getCmp('pu_id').value);
                }
            },
            'erpAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pu_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onAudit(Ext.getCmp('pu_id').value);
                }
            },
            'erpB2BButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pu_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onB2b(Ext.getCmp('pu_id').value);
                }
            },
            'erpResAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pu_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onResAudit(Ext.getCmp('pu_id').value);
                }
            },
            //生成银行转存
             'erpTurnBankRegisterButton2': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pu_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                	var departmentcode = Ext.getCmp('pu_departmentcode').value;
                	if(departmentcode){
                		departmentcode="'"+departmentcode+"'";
	                	var id = Ext.getCmp('pu_id').value;
	                	//转存前判断
	                    Ext.Ajax.request({
	    			   		url : basePath + 'scm/purchase/TurnBankRegister.action',
	    			   		params: {
	    			   			caller:'Purchase',
	    			   			id: id
	    			   		},
	    			   		method : 'post',
	    			   		callback : function(options,success,response){
	    			   			var res = new Ext.decode(response.responseText);
	    			   			if(res.exceptionInfo){
	    			   				showError(res.exceptionInfo);
	    			   			}
	    		    			if(res.success){
                				var caller = 'TurnRegister';
								var	urlCondition = '&formCondition=pu_idIS'+id+'&gridCondition=ca_departmentcodeIS'+departmentcode;
								var url = 'jsps/scm/purchase/turnRegister.jsp?whoami=' + caller+urlCondition+'&_noc=1';
	    		    			var win = parent.Ext.create('Ext.Window', {
		 						   	width : '50%',
									height : '85%',
									draggable : true,
									closable : true,
									modal : true,
									layout : 'fit',
									id : 'selectwindow',
									items: [{
								    	  tag : 'iframe',
								    	  frame : false,
								    	  layout : 'fit',
								    	  html : '<iframe src="'+ basePath + url
											+ '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>'
								    }],
									buttonAlign : 'center',
									buttons : [{
										text : '确认转存',
										cls : 'x-btn-save',
										id : 'confirmturnregister',
										handler : function(btn) {
											var win = btn.up('window');
											//iframe的window对象
											var cwin = win.items.items[0].body.dom.getElementsByTagName("iframe")[0].contentWindow;
									        var grid = cwin.Ext.getCmp('grid');
									        var form = cwin.Ext.getCmp('form');
									        var total= cwin.Ext.getCmp('pu_total').value;
									        var formStore = unescape(escape(Ext.JSON.encode(form.getValues())));
									        //勾选的明细行
									        var selects = grid.selModel.getSelection();
									        var gridStore = new Array();
									        if(selects.length<1){
									        	showError('未选择账户!');
									        	return;
									        }else{
									        	var selecttotal =0;
									        	for(i=0;i<selects.length;i++){
									        	if(selects[i].data['ca_id']&&selects[i].data['ca_tobalance']>0){
									        		if(!selects[i].data['ca_tocacode']){	
									        			showError('转存到的账户不能为空!');
									            		return;
									        		}								        			
									        		var data = selects[i].data;
									        		var dd = new Object();
													Ext.each(grid.columns, function(c){
														if((!c.isCheckerHd)&&(c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
															if(c.xtype == 'datecolumn'){
																c.format = c.format || 'Y-m-d';
																if(Ext.isDate(data[c.dataIndex])){
																	dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
																} else {
																	if(c.editor&&c.logic!='unauto'){
																		dd[c.dataIndex] = Ext.Date.format(new Date(), c.format);//如果用户没输入日期，或输入有误，就给个默认日期，
																	}else  dd[c.dataIndex]=null;
																}
															} else if(c.xtype == 'datetimecolumn'){
																if(Ext.isDate(data[c.dataIndex])){
																	dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
																} else {
																	if(c.editor&&c.logic!='unauto'){
																		dd[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d H:i:s');//默认日期，
																	}
																}
															} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
																if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
																	dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
																} else {
																	dd[c.dataIndex] = "" + selects[i].data[c.dataIndex];
																}
															} else {
																dd[c.dataIndex] = selects[i].data[c.dataIndex];
															}
															if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
																dd[c.dataIndex] = c.defaultValue;
															}
														}
													});
													gridStore.push(Ext.JSON.encode(dd));
													selecttotal += selects[i].data['ca_tobalance'];
									        		}
									        	}
									        	if(selecttotal!=total){
									        		showError('本次转存金额和采购金额不相等，不允许此操作!');
									        		return;
									        	}
									        	var gridStore= unescape("[" + gridStore.toString().replace(/\\/g,"%") + "]");
									        	Ext.Ajax.request({
									                url : basePath + 'scm/purchase/ConfirmTurnBankRegister.action',
									                params: {
									                	formStore:formStore,
									                    gridStore:gridStore
									                },
									                method: 'post',
									                callback: function(opt, s, res) {
									                    var r = new Ext.decode(res.responseText);
									                    if (r.success) {
									                    	showError(r.msg);
									                    	win.close();
						                    				win.destroy();
									                        
									                    } else{
									                        showError(r.exceptionInfo);
									                    }
									                }
									            });
									        }
										}
										},{
										text : $I18N.common.button.erpCloseButton,
										cls : 'x-btn-blue',
										id : 'close',
										handler : function(btn) {
											var win = btn.up('window');
						                    win.close();
						                    win.destroy();
										}
									}]
								});
								win.show();
								}
							}
						});
	                }else{
	                	Ext.Msg.alert("提示","部门未选,不允许转存！");
	                }
                }
            },
            'erpEndButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pu_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                	warnMsg("确定结案?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    						var grid = Ext.getCmp('grid'), jsonData=new Array();
    						grid.store.each(function(item){
    							if(item.get('pd_id') > 0)
    								jsonData.push({pd_id: item.get('pd_id')});
    						});
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/purchase/endPurchase.action',
    	    			   		params: {
    	    			   			caller: caller,
    	    			   			data: Ext.JSON.encode(jsonData)
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				alert("结案成功！");
    	    	        				window.location.reload();
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
                }
            },
            /**
	    		    * 更改供应商回复信息
	    		    */
            '#updatevendorbackInfo': {
                click: function(btn) {
                    var record = btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();
                    me.UpdateVendorInfo(record);
                }
            },
            'erpPrintButton': {
                click: function(btn) {
                    var reportName = '';
                    reportName = "PURCLIST";
                    var condition = '{Purchase.pu_id}=' + Ext.getCmp('pu_id').value + '';
                    var id = Ext.getCmp('pu_id').value;
                    me.FormUtil.onwindowsPrint2(id, reportName, condition);
                }
            },
            'erpPrintEnButton': {
                click: function(btn) {
                    var reportName = '';
                    reportName = "PURCLIST_EN";
                    var condition = '{Purchase.pu_id}=' + Ext.getCmp('pu_id').value + '';
                    var id = Ext.getCmp('pu_id').value;
                    me.FormUtil.onwindowsPrint2(id, reportName, condition);
                }
            },
            'erpPrintHKButton': {
                click: function(btn) {
                    var reportName = '';
                    reportName = "PURCLIST_HK";
                    var condition = '{Purchase.pu_id}=' + Ext.getCmp('pu_id').value + '';
                    var id = Ext.getCmp('pu_id').value;
                    me.FormUtil.onwindowsPrint2(id, reportName, condition);
                }
            },
            'erpPrintyestaxButton': {
                click: function(btn) {
                    var reportName = '';
                    reportName = "PURCLIST_yestax";
                    var condition = '{Purchase.pu_id}=' + Ext.getCmp('pu_id').value + '';
                    var id = Ext.getCmp('pu_id').value;
                    me.FormUtil.onwindowsPrint2(id, reportName, condition);
                }
            },
            'erpPrintnotaxButton': {
                click: function(btn) {
                    var reportName = '';
                    reportName = "PURCLIST_notax";
                    var condition = '{Purchase.pu_id}=' + Ext.getCmp('pu_id').value + '';
                    var id = Ext.getCmp('pu_id').value;
                    me.FormUtil.onwindowsPrint2(id, reportName, condition);
                }
            },
            'erpPrintByConditionButton': {
    			/*afterrender: function(btn){
					var status = Ext.getCmp('dgl_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				}*/
    		},
            'field[name=pu_vendcode]': {
                change: function(f) {
                    if (f.value != null && f.value != '') {
                        if (Ext.getCmp('pu_receivecode').value == null || Ext.getCmp('pu_receivecode').value.toString().trim() == '') {
                            Ext.getCmp('pu_receivecode').setValue(f.value);
                        }
                    }
                }
            },
            'field[name=pu_vendname]': {
                change: function(f) {
                    if (f.value != null && f.value != '') {
                        if (Ext.getCmp('pu_receivename').value == null || Ext.getCmp('pu_receivename').value.toString().trim() == '') {
                            Ext.getCmp('pu_receivename').setValue(f.value);
                        }
                    }
                }
            },
            'erpCopyButton': {
                click: function(btn) {
                    me.copy();
                }
            },
            'erpResetSyncButton': {
                afterrender: function(btn) {
                    var form = btn.ownerCt.ownerCt,
                    s = form.down('#pu_statuscode'),
                    v = form.down('#pu_receivecode');
                    if (s.getValue() != 'AUDITED' || v.getValue() != '02.01.028') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    var form = btn.ownerCt.ownerCt,
                    id = form.down('#pu_id').getValue();
                    btn.resetSyncStatus(basePath + 'scm/purchase/syncstatus.action', id);
                }
            },
            'erpRefreshSyncButton': {
            	afterrender: function(btn) {
                    var form = btn.ownerCt.ownerCt,
                    	s = form.down('#pu_statuscode'),
                    	v = form.down('#pu_sync');
                    if (s.getValue() != 'AUDITED' || (v && v.getValue() == null)) {
                        btn.hide();
                    }
                }
            },
            'dbfindtrigger[name=pu_vendcontact]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='pu_vendcode';
	    			trigger.mappingKey='vc_vecode';
	    			trigger.dbMessage='请先选供应商编号！';
    			}
    		},
            'erpRefreshQtyButton':{
    			click: function(btn){
    				var puid=Ext.getCmp('pu_id').value;
	    			Ext.Ajax.request({
	        			url : basePath + "scm/purchase/refreshqty.action",
	        			params:{
	        				id: puid
	        			},
	        			method:'post',
	        			callback:function(options,success,response){
	        				var localJson = new Ext.decode(response.responseText);
	            			if(localJson.success){
	            				Ext.Msg.alert("提示","刷新成功！");
	            				window.location.reload();
	            			} else {
	            				if(localJson.exceptionInfo){
	            	   				var str = localJson.exceptionInfo;
	            	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	            	   					str = str.replace('AFTERSUCCESS', '');
	            	   					showError(str);
	            	   				} else {
	            	   					showError(str);return;
	            	   				}
	            	   			}
	            			}
	        			}
	        		});
	    		}
    		},
    		/**
    		 * PO分拆
    		 */
    		'erpSplitButton': {
    			beforerender: function(btn) { 
                   btn.text="拆分及交期回复";
                   btn.width=130; 
                }, 
    			click: function(btn) {
    				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
    			    me.PurchaseSplit(record);
    			}
    		}
        });
    },
    UpdateVendorInfo: function(record) {
        win = this.getVendorInfoWindow(record);
        win.show();
    },
    getVendorInfoWindow: function(record) {
        var me = this;
        var date = record.data.pd_deliveryreply;
        if (date != null) {
            date = Ext.Date.parse(date, "Y-m-d");
        }
        return Ext.create('Ext.window.Window', {
            width: 430,
            height: 250,
            closeAction: 'destroy',
            cls: 'custom-blue',
            title: '<h1>更改供应商回信息</h1>',
            layout: {
                type: 'vbox'
            },
            items: [{
                width: '100%',
                html: '<div style="background:transparent;border:none;width:100%;height:30px;' + 'color:#036;vertical-align:middle;line-height:30px;font-size:14px;">' + '*注:修改采购单跟多信息请制作采购变更单<a style="float:right" href="javascript:' + 'openTable(\'采购变更\',\'jsps/scm/purchase/purchaseChange.jsp\',\'PurchaseChange\');">进入</a></div>'
            },
            {
                margin: '5 0 0 5',
                xtype: 'datefield',
                fieldLabel: '回复交期',
                name: 'DELIVERYREPLY',
                format: 'Y-m-d',
                value: date,
                id: 'DELIVERYREPLY'
            },
            {
                margin: '5 0 0 5',
                xtype: 'numberfield',
                fieldLabel: '回复数量',
                name: 'QTYREPLY',
                hideTrigger: true,
                value: record.data.pd_qtyreply,
                id: 'QTYREPLY'
            },{
            	margin: '5 0 0 5',
                xtype: 'checkbox',
                columnidth: 0.4,
                fieldLabel: '能否按时交货',
                name: 'isok',
                value:(record.data.pd_isok && record.data.pd_isok=='是'),
                id: 'isok'
            },
            {
                margin: '5 0 0 5',
                xtype: 'textfield',
                columnidth: 0.4,
                fieldLabel: '回复明细',
                name: 'replydetail',
                value:record.data.pd_replydetail,
                id: 'replydetail'
            }
            ],
            buttonAlign: 'center',
            buttons: [{
                xtype: 'button',
                text: '保存',
                width: 60,
                iconCls: 'x-button-icon-save',
                handler: function(btn) {
                    var w = btn.up('window');
                    me.saveVendorInfo(w);
                    win.close();
                    win.destroy();
                }
            },
            {
                xtype: 'button',
                columnWidth: 0.1,
                text: '关闭',
                width: 60,
                iconCls: 'x-button-icon-close',
                margin: '0 0 0 10',
                handler: function(btn) {
                    var win = btn.up('window');
                    win.close();
                    win.destroy();
                }
            }]
        });
    },
    saveVendorInfo: function(w) {
        var deliveryreply = w.down('field[name=DELIVERYREPLY]').getValue();
        var qty = w.down('field[name=QTYREPLY]').getValue();
        grid = Ext.getCmp('grid'),
        record = grid.getSelectionModel().getLastSelected();
        if (!deliveryreply && !qty && !Ext.getCmp('replydetail').value) {
            showError('请先设置回复信息');
            return;
        } else {
            var isok = w.down('field[name=isok]').getValue();
            var dd = {
                pd_id: record.data.pd_id,
                pd_qtyreply: qty ? qty: 0,
                pd_deliveryreply: deliveryreply ? Ext.Date.format(deliveryreply, 'Y-m-d') : null,
                pd_isok: isok ? '是': '否',
                pd_replydetail:Ext.getCmp('replydetail').value
            };
            Ext.Ajax.request({
                url: basePath + 'scm/purchase/updateVendorBackInfo.action',
                params: {
                    data: unescape(Ext.JSON.encode(dd)),
                    caller: caller
                },
                method: 'post',
                callback: function(opt, s, res) {
                    var r = new Ext.decode(res.responseText);
                    if (r.success) {
                    	grid.getData(gridCondition);
                        showMessage('提示', '更新成功!', 1000);
                    } else if (r.exceptionInfo) {
                        showError(r.exceptionInfo);
                    } else {
                        saveFailure();
                    }
                }
            });
        }
    },
    onGridItemClick: function(selModel, record) { //grid行选择	
        if (record.data.pd_id != 0 && record.data.pd_id != null && record.data.pd_id != '') {
            var btn = Ext.getCmp('updatevendorbackInfo');
            btn && btn.setDisabled(false);
            btn = Ext.getCmp('erpSplitButton');
			btn && btn.setDisabled(false);
        }
        this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn) {
        return btn.ownerCt.ownerCt;
    },
    //保存之前的一些逻辑
    beforeSavePurchase: function() {
        var grid = Ext.getCmp('grid'), items = grid.store.data.items,
        	c = Ext.getCmp('pu_code').value, pudate = Ext.getCmp('pu_date').value;
        var vend = Ext.getCmp('pu_vendid').value,
        	vendcode = Ext.getCmp('pu_vendcode').value,
        	vendname = Ext.getCmp('pu_vendname').value;
        if (vend == null || vend == '' || vend == '0' || vend == 0) {
            showError('未选择供应商，或供应商编号无效!');
            return;
        }
        Ext.Array.each(items,
        function(item) {
        	item['pd_code']=c;
        	item['pd_vendid']=vend;
        	item['pd_vendcode']=vendcode;
        	item['pd_vendname']=vendname;
        });
        //手工录入采购单,合同类型不能为标准
        var pu_kind = Ext.getCmp('pu_kind').value;
        if (pu_kind == null || pu_kind == '') {
            showError('合同类型不能为空');
            return;
        }
        if (pu_kind == '标准' || pu_kind == 'normal' || pu_kind == '標準') {
            showError('手工录入采购单,合同类型不能为标准');
            return;
        }
        var items = grid.store.data.items;
        var bool = true;
        //数量不能为空或0
        Ext.each(items,function(item) {
            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
                if (item.data['pd_qty'] == null || item.data['pd_qty'] == '' || item.data['pd_qty'] == '0' || item.data['pd_qty'] == 0) {
                    bool = false;
                    showError('明细表第' + item.data['pd_detno'] + '行的数量为空');
                    return;
                }
            }
        });
        //物料交货日期不能小于录入日期
        Ext.each(items, function(item) {
            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
                if (item.data['pd_delivery'] == null) {
                    item['pd_delivery'] = Ext.getCmp('pu_delivery');
                } else if (Ext.Date.format(item.data['pd_delivery'],'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')) {
                    bool = false;
                    showError('明细表第' + item.data['pd_detno'] + '行的交货日期小于当前日期');
                    return;
                } else if (Ext.Date.format(item.data['pd_delivery'],'Y-m-d') < Ext.Date.format(pudate,'Y-m-d')) {
                    bool = false;
                    showError('明细表第' + item.data['pd_detno'] + '行的交货日期小于单据日期');
                    return;
                }
            }
        });
        //保存purchase
        if (bool) this.FormUtil.beforeSave(this);
    },
    beforeUpdate: function() {
        var grid = Ext.getCmp('grid'), items = grid.store.data.items,
        	c = Ext.getCmp('pu_code').value, pudate = Ext.getCmp('pu_date').value;
        var vend = Ext.getCmp('pu_vendid').value,
        	vendcode = Ext.getCmp('pu_vendcode').value,
        	vendname = Ext.getCmp('pu_vendname').value;
        if (vend == null || vend == '' || vend == '0' || vend == 0) {
            showError('未选择供应商，或供应商编号无效!');
            return;
        }
        Ext.Array.each(items,function(item) {
        	item['pd_code']=c;
        	item['pd_vendid']=vend;
        	item['pd_vendcode']=vendcode;
        	item['pd_vendname']=vendname;
        });
        var items = grid.store.data.items;
        var bool = true;
        //数量不能为空或0
        Ext.each(items,function(item) {
            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
                if (item.data['pd_qty'] == null || item.data['pd_qty'] == '' || item.data['pd_qty'] == '0' || item.data['pd_qty'] == 0) {
                    bool = false;
                    showError('明细表第' + item.data['pd_detno'] + '行的数量为空');
                    return;
                }
            }
        });
        /*//采购价格不能为0
        if (Ext.getCmp('pu_getprice').value == 0) { //是否自动获取单价
            Ext.each(items,
            function(item) {
                if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
                    if (item.data['pd_price'] == null) {
                        bool = false;
                        showError('明细表第' + item.data['pd_detno'] + '行的价格为空');
                        return;
                    } else if (item.data['pd_price'] == 0 || item.data['pd_price'] == '0') {
                        bool = false;
                        showError('明细表第' + item.data['pd_detno'] + '行的价格为0');
                        return;
                    }
                }
            });
        }*/
        //物料交货日期不能小于录入日期
        Ext.each(items,function(item) {
            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
                if (item.data['pd_delivery'] == null) {
                    item.set('pd_delivery', Ext.getCmp('pu_delivery'));
                } else if (Ext.Date.format(item.data['pd_delivery'],'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')) {
                    bool = false;
                    showError('明细表第' + item.data['pd_detno'] + '行的交货日期小于当前日期');
                    return;
                } else if (Ext.Date.format(item.data['pd_delivery'],'Y-m-d') < Ext.Date.format(pudate,'Y-m-d')) {
                    bool = false;
                    showError('明细表第' + item.data['pd_detno'] + '行的交货日期小于单据日期');
                    return;
                }
            }
        });
        //更新
        if (bool) this.FormUtil.onUpdate(this);
    },
    /**
	        * 复制采购单
	        */
    copy: function() {
        var me = this,
        form = Ext.getCmp('form');
        var v = form.down('#pu_id').value;
        if (v > 0) {
            form.setLoading(true);
            Ext.Ajax.request({
                url: basePath + 'scm/purchase/copyPurchase.action',
                params: {
                    caller: caller,
                    id: v
                },
                callback: function(opt, s, r) {
                    form.setLoading(false);
                    var res = Ext.decode(r.responseText);
                    if (res.data) {
                        var url = 'jsps/scm/purchase/purchase.jsp?formCondition=pu_idIS' + res.data.id + '&gridCondition=pd_puidIS' + res.data.id;
                        showMessage('提示', '复制成功', 2000);
                        me.FormUtil.onAdd(null, '采购单', url);
                    } else {
                        showError(res.exceptionInfo);
                    }
                }
            });
        }
    },
    /**
	 *采购单拆分
	 * */
	PurchaseSplit:function(record){
		var me=this,originaldetno=Number(record.data.pd_detno);
		var puid=record.data.pd_puid;
		var pdid=record.data.pd_id;
		Ext.create('Ext.window.Window',{
    		width:850,
    		height:'95%',
    		iconCls:'x-grid-icon-partition',
    		title:'<h1>采购单拆分</h1>',
    		id:'win',
    		items:[{
    			xtype:'form',
    			layout:'column',
    			region:'north',
    			frame:true,
    			defaults:{
    				xtype:'textfield',
    				columnWidth:0.5,
    				readOnly:true,
    				fieldStyle:'background:#f0f0f0;border: 0px solid #8B8970;color:blue;'
    			},
    			items:[{
    			 fieldLabel:'采购单号',
    			 value:record.data.pd_code,
    			 id:'sacode'
    			},{
    			 fieldLabel:'物料编号'	,
    			 value:record.data.pd_prodcode
    			},{
    			 fieldLabel:'物料名称',
    			 value:record.data.pr_detail
    			},{
    			 fieldLabel:'原序号'	,
    			 value:record.data.pd_detno
    			},{
    		     fieldLabel:'原数量',
    		     value:record.data.pd_qty
    			}],
    			buttonAlign:'center',
    			buttons:[{
    				xtype:'button',
    				columnWidth:0.12,
    				text:'保存',
    				width:60,
    				iconCls: 'x-button-icon-save',
    				margin:'0 0 0 30',
    				handler:function(btn){
    				   var store=Ext.getCmp('smallgrid').getStore();
    				   var count=0;
    				   var jsonData=new Array();
    				   var dd;
    				   Ext.Array.each(store.data.items,function(item){
    					  if(item.data.pd_qty!=0&&item.data.pd_delivery!=null&&item.data.pd_qty>0){
    						  if(item.dirty){
    							  dd=new Object();
    							  //说明是新增批次
    							  dd['pd_qty']=item.data.pd_qty; 
    							  dd['pd_id']=item.data.pd_id;
    							  dd['pd_detno']=item.data.pd_detno;
    							  dd['pd_delivery']=Ext.Date.format(item.data.pd_delivery, 'Y-m-d'); 
    							  dd['pd_replydetail']=item.data.pd_replydetail; 
    							  dd['pd_isok']=item.data.pd_isok=='-1'?'是':'否'; 
    							  dd['pd_qtyreply']=item.data.pd_qtyreply; 
    							  if(item.data.pd_deliveryreply){
    								  dd['pd_deliveryreply']=Ext.Date.format(item.data.pd_deliveryreply, 'Y-m-d'); 
    							  }else
    								  dd['pd_deliveryreply']=null; 
    							  jsonData.push(Ext.JSON.encode(dd)); 
    						  }
    						  count+=Number(item.data.pd_qty);
    					  }
    				   });		   
    				   var assqty=Number(record.data.pd_qty);
    				   if(count!=assqty){
    					showError('分拆数量必须等于原数量!') ;  
    					return;
    				   }else{
    					   var r=new Object();
        				   r['pd_id']=record.data.pd_id;
        				   r['pd_puid']=record.data.pd_puid;
        				   r['pd_detno']=record.data.pd_detno;        
        				   var params=new Object();
        				   params.formdata = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
        					params.data = unescape(jsonData.toString().replace(/\\/g,"%"));
    					   Ext.Ajax.request({
    					   	  url : basePath +'scm/purchase/splitPurchase.action',
    					   	  params : params,
    					   	  waitMsg:'拆分中...',
    					   	  method : 'post',
    					   	  callback : function(options,success,response){
    					   		var localJson = new Ext.decode(response.responseText);
    					   		if(localJson.success){
    			    				saveSuccess(function(){
    			    					//add成功后刷新页面进入可编辑的页面 
    			    					Ext.getCmp('win').close();
    			    					 me.loadSplitData(originaldetno,puid,record); 
    			    				});
    				   			} else if(localJson.exceptionInfo){
    				   				var str = localJson.exceptionInfo;
    				   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    				   					str = str.replace('AFTERSUCCESS', '');
    				   					saveSuccess(function(){
    				    					//add成功后刷新页面进入可编辑的页面 
    				   					 me.loadSplitData(originaldetno,puid,record); 
    				    				});
    				   					showError(str);
    				   				} else {
    				   					showError(str);
    					   				return;
    				   				}
    					   			
    					   	 } else{
    				   				saveFailure();
    				   			}
    					   	  }
    					   });
    					   
    				   }
    				}
    			},{
    				xtype:'button',
    				columnWidth:0.1,
    				text:'关闭',
    				width:60,
    				iconCls: 'x-button-icon-close',
    				margin:'0 0 0 10',
    				handler:function(btn){
    					Ext.getCmp('win').close();
    				}
    			}]
    		},{
    		  xtype:'gridpanel',
    		  region:'south',
    		  id:'smallgrid',
    		  layout:'fit',
    		  height:'80%',
    		  columnLines:true,
    		  store:Ext.create('Ext.data.Store',{
					fields:[{name:'pd_delivery',type:'date'},{name:'pd_qty',type:'int'},{name:'pd_deliveryreply',type:'date'},{name:'pd_qtyreply',type:'int'},{name:'pd_replydetail',type:'string'},{name:'pd_isok',type:'int'},{name:'pd_acceptqty',type:'int'},{name:'pd_yqty',type:'int'},{name:'pd_id',type:'int'}],
				    data:[]
    		  }),
    		  plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
    		        clicksToEdit: 1,
    		        listeners:{
    		        	'edit':function(editor,e,Opts){
    		        		var record=e.record;
    		        		var version=record.data.ma_version;
    		        		if(version){
    		        			e.record.reject();
    		        		 Ext.Msg.alert('提示','不能修改已拆分明细!');
    		        		}
    		        		}
    		        	}
    		    })],
    		  tbar: [{
    			    tooltip: '添加批次',
    	            iconCls: 'x-button-icon-add',
    	            width:25,
    	            handler : function() {
    	            	var store = Ext.getCmp('smallgrid').getStore();
    	                var r = new Object();
    	                r.pd_delivery=record.get('pd_delivery');
    	                r.pd_qty=0; 
    	                r.pd_id=0;
    	                r.pd_detno=store.getCount()+1;
    	                store.insert(store.getCount(), r);
    	            }
    	        }, {
    	            tooltip: '删除批次',
    	            width:25,
    	            itemId: 'delete',
    	            iconCls: 'x-button-icon-delete',
    	            handler: function(btn) {
    	                var sm = Ext.getCmp('smallgrid').getSelectionModel();
    	                var record=sm.getSelection();
    	                var pd_id=record[0].data.pd_id;
    	                if(pd_id&&pd_id!=0){
    	                	Ext.Msg.alert('提示','不能删除已拆批次或原始行号!');
    	                	return;
    	                }
    	                var store=Ext.getCmp('smallgrid').getStore();
    	                store.remove(record);
    	                if (store.getCount() > 0) {
    	                    sm.select(0);
    	                }
    	            },
    	            disabled: true
    	        }],
    	      listeners:{
    	    	  itemmousedown:function(selmodel, record){
    	    		  selmodel.ownerCt.down('#delete').setDisabled(false);
    	    	  }
    	      }, 
    		  columns:[{
    			 dataIndex:'pd_detno',
    			 header:'序号',
    			 format:'0',
    			 xtype:'numbercolumn'
    		   },{
    			  dataIndex:'pd_delivery',
    			  header:'交货日期',
    			  xtype:'datecolumn',
    			  width:120,
    			  editable:true,
    			  renderer:function(val,meta,record){
    				   if(record.data.ma_version){
    					  meta.tdCls = "x-grid-cell-renderer-cl";
    				   }
    				   if(val)
    					   return Ext.Date.format(val, 'Y-m-d');
    				   else return null;
    			   },
    			  editor:{
    				  xtype: 'datefield',
    				  format:'Y-m-d'
    			  }
    		  },{
    			  dataIndex:'pd_qty',
    			  header:'数量',
    			  width:120,
    			  xtype:'numbercolumn',
    			  editable:true,
    			  renderer:function(val,meta,record){
   				   if(record.data.ma_version){
   					  meta.tdCls = "x-grid-cell-renderer-cl";
   				   }
   				   return val;
   			     },
    			  editor:{
    				  xtype:'numberfield',
    				  format:'0',
    				  hideTrigger: true
    			  }
    		  },{
    			dataIndex:'pd_deliveryreply',
    			header:'回复交期',
    			xtype:'datecolumn',
  			  	width:120,
	  			  editable:true,
	  			renderer:function(val,meta,record){ 
	  				   if(val)
	  					   return Ext.Date.format(val, 'Y-m-d');
	  				   else return null;
	  			   },
	  			  editor:{
	  				  xtype: 'datefield',
	  				  format:'Y-m-d'
	  			  }
    		  },{
    			dataIndex:'pd_qtyreply',
    			header:'回复数量',
    			 width:120,
	   			  xtype:'numbercolumn',
	   			  editable:true,
	   			  renderer:function(val,meta,record){
	  				   if(record.data.ma_version){
	  					  meta.tdCls = "x-grid-cell-renderer-cl";
	  				   }
	  				   return val;
	  			     },
	   			  editor:{
	   				  xtype:'numberfield',
	   				  format:'0',
	   				  hideTrigger: true
	   			  }
    		  },{
    			dataIndex:'pd_isok',
    			header:'是否准时',
    			xtype:'yncolumn',
    			width:100,
    			editable:true
    		  },{
    			dataIndex:'pd_replydetail',
    			header:'回复明细', 
    			width:100,
    			renderer:function(val,meta,record){
     				   if(record.data.originaldetno){
     					  meta.tdCls = "x-grid-cell-renderer-cl";
     				   }
     				  return val;
     			} ,
     			 editor:{
	   				  xtype:'textfield',
	   				  format:'0',
	   				  hideTrigger: true
	   			  },
     			editable:true
    		  },{
    			dataIndex:'pd_yqty',
    			header:'已转收料数',
    			xtype:'numbercolumn',
    			width:100,
    			editable:false
    		  },{
    			 dataIndex:'pd_acceptqty',
      			header:'已转验收数',
      			xtype:'numbercolumn',
      			width:100,
      			editable:false  
    		  },{
    			  dataIndex:'pd_id',
    			  header:'pdid',
    			  width:0,
    			  xtype:'numbercolumn',
    			  editable:true,
    			  editor:{
    				  xtype:'numberfield',
    				  format:'0',
    				  hideTrigger: true
    			  }
    		  }]
    		}] ,
    	listeners:{
		    'beforeclose':function(view ,opt){
		    	var grid = Ext.getCmp('grid');
				var value = Ext.getCmp('pu_id').value;
				var gridCondition = grid.mainField + '=' + value,
				gridParam = {caller: caller, condition: gridCondition};
				grid.getData(gridCondition);
		    } 
		  }
    	}).show();
         this.loadSplitData(originaldetno,puid,record); 
	},
    loadSplitData:function(detno,puid,record){
		 var grid=Ext.getCmp('smallgrid');
        grid.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/loadNewGridStore.action",
        	params:{
        	  caller:'PurchaseSplit',
        	  condition:"pd_detno="+detno+" AND pd_puid="+puid+" order by pd_id asc"
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		grid.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = res.data; 
        		 grid.store.loadData(data); 
        		//自定义event
        		grid.addEvents({
        		    storeloaded: true
        		});
        		grid.fireEvent('storeloaded', grid, data);
        	}
        });
	},
	getLeadCode: function(type) {
		var result = null;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		async: false,
	   		params: {
	   			caller: 'PurchaseKind',
	   			field: 'pk_excode',
	   			condition: 'pk_name=\'' + type + '\''
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			} else if(r.success){
	   				result = r.data;
	   			}
	   		}
		});
		return result;
	}
});