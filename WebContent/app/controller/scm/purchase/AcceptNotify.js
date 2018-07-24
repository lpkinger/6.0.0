Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.AcceptNotify', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.purchase.AcceptNotify','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.button.ResSubmit','core.button.Flow','core.trigger.MultiDbfindTrigger','core.button.VastTurnAccept',
      		'core.button.GridWin','core.button.TurnPurcProdIO','core.button.BackAll','core.button.Barcode',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'#updateacceptqty': {
                  click: function(btn) {
                      var record = btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();
                      me.updatebackqty(record);
                  }
             },
             'erpBackAllButton':{
            	 afterrender:function(btn){
            		 var an_status=Ext.getCmp("an_status").value;
            		 if(an_status=="已转收料"){
            			 btn.hide();
            		 }
            	 },
            	 click:function(btn){
            		 var grid = Ext.getCmp('grid'),items = grid.store.data.items;
            		 var flag = false;
            		 Ext.Array.each(items, function(item){
     			    	if(item.data['and_inqty'] != 0){
     			    		flag = true;
     			    	}
     				});
            		if(!flag){
            			showError('该张单据所有明细行数量均为0,不能拒收');
            			return;
            		}
        			warnMsg('该操作将拒收明细所有物料，确认拒收?', function(btn){
        				if(btn == 'yes'){
        					me.FormUtil.setLoading(true);
        					var id=Ext.getCmp('an_id').getValue();
        					Ext.Ajax.request({
        						url : basePath + 'scm/purchase/backAll.action',
        						params: {
        							id: id
        						},
        						method : 'post',
        						callback : function(options,success,response){
        							me.FormUtil.setLoading(false);
        							var localJson = new Ext.decode(response.responseText);
        							if(localJson.exceptionInfo){
        								showError(localJson.exceptionInfo);return;
        							}
        							if(localJson.success){
        								showMessage('提示', '拒收成功!', 1000);
        								window.location.reload();
        							} else {
        								delFailure();
        							}
        						}
        					});
        				}
        			});
            	 }
             },
             'erpBarcodeButton':{
                 click: function(btn) {
                 	 var me= this;
                     var id = Ext.getCmp("an_id").value;
                     var inoutNo=Ext.getCmp("an_code").value;
                     var status = 'AUDITED';
                     var formCondition1 = "an_idIS" + id +"and an_codeIS '"+inoutNo+"'";
                     var gridCondition1 = "ban_anidIS" + id +" order by ban_id asc";
                     var linkCaller = 'Vendor!Baracceptnotify';
                     var ac_class ='Accept';
                     me.FormUtil.onAdd('addBarcode'+id, '条形码维护('+inoutNo+')', 'jsps/vendbarcode/setBarcode.jsp?_noc=1&whoami=' + linkCaller +'&key='+id+'&inoutno='+inoutNo+'&status='+status+'&ac_class='+ac_class+'&formCondition=' + formCondition1 + '&gridCondition=' + gridCondition1);
                    }
 			},
    		'erpGridWinButton':{
    			afterrender: function(btn){
    				var id  = Ext.getCmp('an_id').value;
    				btn.setConfig({
    					text: '费用明细',
    					caller: 'ProdChargeDetail!AN',
    					condition: 'pd_anid=' + id,
    					paramConfig: {
    						pd_anid: id
    					}
    				});
    			},
    			beforesave : function(btn) {
    				var f = btn.ownerCt.ownerCt, p = f.down('field[name=an_statuscode]');
    				if (p && 'TURNIN' == p.getValue()) {
    					Ext.Msg.alert("提示","该单据已入库,不能修改费用明细！");
    					return false;
    				}
    				return true;
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var me = this;
    				var form = me.getForm(btn);
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				var whcode = Ext.getCmp('an_whcode'), whname = Ext.getCmp('an_whname');
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    			    Ext.Array.each(items, function(item){
    			    	if(!Ext.isEmpty(item.data['and_prodcode'])){
    				    	if(whcode && item.data['and_whcode'] == null || item.data['and_whcode'] == ''){
    				    		item.set('and_whcode', whcode.value);
    				    		item.set('and_whname', whname.value);
    				    	}
    			    	}
    				});
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('an_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var me = this;
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				var whcode = Ext.getCmp('an_whcode'), whname = Ext.getCmp('an_whname');
    				 Ext.Array.each(items, function(item){
     			    	if(!Ext.isEmpty(item.data['and_prodcode'])){
     				    	if(whcode && item.data['and_whcode'] == null || item.data['and_whcode'] == ''){
     				    		item.set('and_whcode', whcode.value);
     				    		item.set('and_whname', whname.value);
     				    	}
     			    	}
     				});
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addAcceptNotify', '新增采购通知单', 'jsps/scm/purchase/acceptNotify.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('an_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('an_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('an_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('an_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('an_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('an_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('an_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('an_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    			 var reportName = '';
                 reportName = "vendororder";
                 var condition = '{AcceptNotify.an_id}=' + Ext.getCmp('an_id').value + '';
                 var id = Ext.getCmp('an_id').value;
                 me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    		'field[name=an_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=an_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'erpVastTurnAcceptButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('an_statuscode');
    				if((status && status.value != 'AUDITED' && status.value != 'PART2VA')){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				 var grid = Ext.getCmp('grid'),items = grid.store.data.items;
	           		 var flag = false;
	           		 Ext.Array.each(items, function(item){
	    			    	if(item.data['and_inqty'] != 0){
	    			    		flag = true;
	    			    	}
	    				});
	           		if(!flag){
	           			showError('该张单据所有明细行数量均为0,不能转收料单');
	           			return;
	           		}
    				warnMsg("确定要转入采购收料单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/purchase/turnVerifyApply.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('an_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var r = new Ext.decode(response.responseText);
    	    			   			if(r.exceptionInfo){
    	    			   				showError(r.exceptionInfo);
    	    			   			}
    	    		    			if(r.success){
    	    		    				turnSuccess(function(){
    	    		    					var id = r.id;
    	    		    					var url = "jsps/scm/purchase/verifyApply.jsp?whoami=VerifyApply&formCondition=va_id=" + id + "&gridCondition=vad_vaid=" + id;
    	    		    					me.FormUtil.onAdd('VerifyApply' + id, '采购收料单' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
    		'erpTurnPurcProdIOButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('an_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入采购验收单吗?", function(btn){
    					if(btn == 'yes'){
    						var id = Ext.getCmp('an_id').value;
    						me.FormUtil.setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/purchase/turnProdio.action',
    	    			   		params: {
    	    			   			id: id
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.setLoading(false);
    	    		         		var r = new Ext.decode(response.responseText);
    	    		         		if(r.exceptionInfo){
    	    		         			showError(r.exceptionInfo);
    	    		         		} else {
    									if(r.log) {
    										showMessage('提示', r.log);
    										var grid = Ext.getCmp('grid');
    										grid.GridUtil.loadNewStore(grid, {
    											caller: caller, 
    											condition: 'and_anid=' + id
    										});
    									}
    								}
    	    		         	}
    	    				});
    					}
    				});
    			}
    		},
    		'dbfindtrigger[name=and_ordercode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.getCmp('an_vendcode')){
    					var code = Ext.getCmp('an_vendcode').value;
    					if(code != null && code != ''){
    						t.dbBaseCondition = "pu_vendcode" + "='" + code + "'";
        				}
    				}
    			},
    			aftertrigger: function(t, r) {
    				if(Ext.getCmp('an_vendcode')){
    					var obj = me.getCodeCondition();
    					if(obj && obj.fields){
    						me.FormUtil.getFieldsValue(obj.tablename, obj.fields, obj.myfield + "='" + t.value + "'", obj.tFields);
    					}
    				}
    			}
    		},
    		'multidbfindtrigger[name=and_orderdetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var code = record.data['and_ordercode'];
    				if(code == null || code == ''){
    					showError("请先选择关联单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "pd_code='" + code + "'";
    				}
    			}
    		},
    		'dbfindtrigger[name=and_orderdetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var code = record.data['and_ordercode'];
    				if(code == null || code == ''){
    					showError("请先选择关联单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "pd_code='" + code + "'";
    				}
    			}
    		}
    	});
    }, 
    getCodeCondition: function(){
		var	field = "pu_vendcode";
		var	tFields = 'an_cardcode,an_vendname,an_currency,an_rate,an_paymentcode,an_payment,an_transport,an_paydate,an_receivecode,an_receivename';
		var	fields = 'pu_vendcode,pu_vendname,pu_currency,pu_rate,pu_paymentscode,pu_payments,pu_transport,pu_suredate,pu_receivecode,pu_receivename';
		var	tablename = 'Purchase';
		var	myfield = 'pu_code';
		var obj = new Object();
		obj.field = field;
		obj.fields = fields;
		obj.tFields = tFields;
		obj.tablename = tablename;
		obj.myfield = myfield;
		return obj;
	},
    updatebackqty: function(record) {
   	 var me = this;
        var win=Ext.create('Ext.window.Window', {
            width: 430,
            height: 250,
            closeAction: 'destroy',
            title: '<h1>更改收料数量</h1>',
            layout: {
                type: 'vbox'
            },
            items: [{
                margin: '5 0 0 5',
                xtype: 'numberfield',
                fieldLabel: '数量',
                name: 'QTYREPLY',
                hideTrigger: true,
                value: record.data.and_inqty,
                id: 'QTYREPLY'
            }],
            buttonAlign: 'center',
            buttons: [{
                xtype: 'button',
                text: '保存',
                width: 60,
                iconCls: 'x-button-icon-save',
                handler: function(btn) {
                    var w = btn.up('window');
                    me.saveInfo(w);
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
        win.show();
    },
    saveInfo: function(w) {
        var qty = w.down('field[name=QTYREPLY]').getValue();
        grid = Ext.getCmp('grid'),
        record = grid.getSelectionModel().getLastSelected();
        if (qty == null || qty < 0) {
            showError('请先设置修改数量');
            return;
        } else {
            var dd = {
                and_id: record.data.and_id,
                and_inqty: qty ? qty: 0
            };
            Ext.Ajax.request({
                url: basePath + 'scm/purchase/saveAcceptNotifyQty.action',
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
                         var btn = Ext.getCmp('updateacceptqty');
                        btn.setDisabled(true);
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
    	if (record.data.and_id && (!record.data.and_yqty || (record.data.and_yqty != record.data.and_inqty))) {
    		var btn = Ext.getCmp('updateacceptqty');
    		btn && btn.setDisabled(false);
    	}
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
    	return btn.ownerCt.ownerCt;
    }
});