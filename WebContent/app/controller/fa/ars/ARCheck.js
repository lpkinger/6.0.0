Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.ARCheck', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.button.PrintByCondition','core.form.Panel','fa.ars.ARCheck','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.button.ResSubmit','core.button.Flow','core.button.Post','core.button.ResPost','core.button.Confirm','core.button.Cancel',
      		'core.button.ConfirmBill','core.button.ResSubmitTurnSale','core.button.SubmitTurnSale','core.button.TurnRecBalanceNotice',
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
    				me.FormUtil.onAdd('addARCheck', '新增应收对账', 'jsps/fa/ars/arCheck.jsp');
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
    		'#updateDetailInfo': {
                 click: function(btn) {
                     var record = btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();
                     me.UpdateDetailInfo(record);
                 }
            },
    		'erpPrintButton': {
    			click: function(btn){
    			 var reportName = '';
                 reportName = "archeck_voice";
                 var condition = '{archeck.ac_id}=' + Ext.getCmp('ac_id').value + '';
                 var id = Ext.getCmp('ac_id').value;
                 me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    		'erpConfirmButton': {
    			afterrender: function(btn){
    				var confirmstatus = Ext.getCmp('ac_confirmstatus');
					if(confirmstatus && confirmstatus.value == '已确认' ){
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
    	    			   		url : basePath + 'fa/ars/confirmARCheck.action',
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
    				btn.setWidth(100);
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
    	    			   		url : basePath + 'fa/ars/cancelARCheck.action',
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
 					   var condition = ' ad_acid=' + Ext.getCmp('ac_id').value +' and abs(nvl(ad_yqty,0)) < abs(nvl(ad_qty,0)) and nvl(ad_abclass,\' \')<>\'其它应收单\'';
			   		   if (bool){
			   			   condition = ' ad_acid=' + Ext.getCmp('ac_id').value +' and abs(nvl(ad_yqty,0)) < abs(nvl(ad_qty,0))'; 
			   		   }
			   		   me.batchdeal('ARCheck!ToBill!Deal', condition, 'fa/ars/arCheckTurnBill.action');
	 			   });
 			   }
 		   },
 		   'erpSubmitTurnSaleButton':{
	   			click:function(btn){
	   				Ext.Ajax.request({//拿到grid的columns
						url : basePath + "fa/ars/submitARCheckConfirm.action",
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
			    	   				consolve.log(btn.ownerCt.ownerCt);
			    	   				me.FormUtil.getMultiAssigns(Ext.getCmp('ac_id').getValue(),'ARCheck!Confirm',btn.ownerCt.ownerCt);
			    	   			}
			    	   			showMessage("提示", str);return;
							} else if(res.success){
								me.FormUtil.getMultiAssigns(Ext.getCmp('ac_id').getValue(),'ARCheck!Confirm',btn.ownerCt.ownerCt);
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
						url : basePath + "fa/ars/resSubmitARCheckConfirm.action",
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
	   		},
	   		'erpTurnRecBalanceNoticeButton':{
	   			afterrender: function(btn){
	   				me.BaseUtil.getSetting('sys', 'autoCreateArBill', function(bool) {
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
                    });
				},
				click: function(m){
					var id = Ext.getCmp("ac_id").value;
					me.batchdeal('ARCheck!turnRecBalanceNotice!Deal', ' ad_acid=' + Ext.getCmp('ac_id').value +' and abs(nvl(ad_zqty,0)) < abs(nvl(ad_qty,0))', 'fa/ars/turnRecBalanceNotice.action?id='+id);
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
    	if (record.data.ad_id != 0 && record.data.ad_id != null && record.data.ad_qty != record.data.ad_confirmqty) {
            var btn = Ext.getCmp('updateDetailInfo');
            btn && btn.setDisabled(false);
        }else{
        	var btn = Ext.getCmp('updateDetailInfo');
            btn && btn.setDisabled(true);
        }
    	
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	UpdateDetailInfo: function(record) {
	        win = this.getDetailInfoWindow(record);
	        win.show();
	    },
	getDetailInfoWindow: function(record) {
	        var me = this;
	        return Ext.create('Ext.window.Window', {
	            width: 430,
	            height: 250,
	            closeAction: 'destroy',
	            cls: 'custom-blue',
	            title: '<h1>更改对账数量信息</h1>',
	            layout: {
	                type: 'vbox'
	            },
	            items: [{
	                width: '100%',
	                html: '<div style="background:transparent;border:none;width:100%;height:30px;' + 'color:#036;vertical-align:middle;line-height:30px;font-size:14px;">' + '*注:只能修改对账数量</div>'
	            },
	            {
	                margin: '5 0 0 5',
	                xtype: 'textfield',
	                fieldLabel: '来源单号',
	                name: 'sourcecode',
	                value: record.data.ad_sourcecode,
	                readOnly:true,
	                id: 'sourcecode'
	            },
	            {
	                margin: '5 0 0 5',
	                xtype: 'numberfield',
	                fieldLabel: '来源序号',
	                name: 'sourcedetno',
	                value: record.data.ad_sourcedetno,
	                readOnly:true,
	                id: 'sourcedetno'
	            },{
	            	margin: '5 0 0 5',
	                xtype: 'textfield',
	                columnidth: 0.4,
	                fieldLabel: '出入库单号',
	                name: 'prodinoutno',
	                readOnly:true,
	                value:record.data.ad_inoutno,
	                id: 'prodinoutno'
	            },
	            {
	                margin: '5 0 0 5',
	                xtype: 'numberfield',
	                columnidth: 0.4,
	                fieldLabel: '本次对账数量',
	                name: 'thisqty',
	                value:record.data.ad_qty,
	                id: 'thisqty'
	            }],
	            buttonAlign: 'center',
	            buttons: [{
	                xtype: 'button',
	                text: '保存',
	                width: 60,
	                iconCls: 'x-button-icon-save',
	                handler: function(btn) {
	                    var w = btn.up('window');
	                    me.saveDetailInfo(w);
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
	    saveDetailInfo: function(w) {
	    	 var qty = w.down('field[name=thisqty]').getValue();
	    	 grid = Ext.getCmp('grid'),record = grid.getSelectionModel().getLastSelected();
	    	 if(typeof record.data.ad_yqty != "undefined" ) {
	    		 if(!Ext.isEmpty(record.data.ad_yqty)){
	    			 if(qty < record.data.ad_yqty){
	    				 showError('新数量不能小于已转发票数量');
	    				 return;
			    	 } 
	    		 }
	    	 }
	    	 var dd = {
	                 ad_id: record.data.ad_id,
	                 ad_qty: qty ? qty: 0
	             };
	            Ext.Ajax.request({
	                url: basePath + 'fa/ars/saveDetailInfo.action',
	                params: {
	                    data: unescape(Ext.JSON.encode(dd)),
	                    caller: caller
	                },
	                method: 'post',
	                callback: function(opt, s, res) {
	                    var r = new Ext.decode(res.responseText);
	                    if (r.success) {
	                        grid.GridUtil.loadNewStore(grid, {
	                            caller: caller,
	                            condition: gridCondition
	                        });
	                        showMessage('提示', '更新成功!', 1000);
	                    } else if (r.exceptionInfo) {
	                        showError(r.exceptionInfo);
	                    } else {
	                        saveFailure();
	                    }
	                }
	            });
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