Ext.QuickTips.init();
Ext.define('erp.controller.b2c.purchase.b2cProdInOut', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views: [
        'b2c.purchase.b2cPanel','b2c.common.b2cForm','b2c.common.Viewport','b2c.common.b2cGrid',
        'core.toolbar.Toolbar', 'core.form.MultiField', 'core.form.YnField', 'core.form.SpecialContainField',
        'core.button.Save', 'core.button.Add', 'core.button.Submit', 'core.button.Print', 'core.button.PrintA4', 'core.button.Upload', 'core.button.ResAudit',
        'core.button.Audit', 'core.button.Close', 'core.button.Delete', 'core.button.Update', 'core.button.DeleteDetail', 'core.button.ResSubmit',
        'core.button.Banned', 'core.button.ResBanned', 'core.button.Post', 'core.button.ResPost', 'core.button.Query', 'core.button.GetPrice',
        'core.button.RePrice', 'core.button.BussAccount', 'core.button.Export', 'core.form.FtFindField', 'core.form.ConDateField', 'core.button.UpdateWHCode',
        'core.button.FeeShare', 'core.button.TurnDefectOut', 'core.button.Resetbatch', 'core.button.PrintwithPrice', 'core.button.tecai', 'core.button.GridWin',
        'core.button.TurnExOut', 'core.trigger.DbfindTrigger', 'core.trigger.MultiDbfindTrigger', 'core.trigger.TextAreaTrigger', 'core.button.TurnProdinoutReturn',
        'core.button.TurnProdinoutIn', 'core.button.UpdatePdprice', 'core.button.PrintNoPrice', 'core.button.PrintBKT', 'core.button.PrintPrice', 'core.form.FileField', 'core.button.TurnOutReturn',
        'core.button.Split', 'core.button.TurnReturn', 'core.button.PrintBar', 'core.button.Printotherin', 'core.button.Printotherout', 'core.button.TurnProdOut', 'core.button.TurnYPOutReturn',
        'core.button.TurnOtherIn', 'core.button.TurnOtherOut', 'core.button.TurnOtherPurcOut', 'core.button.TurnAppropriationOut', 'core.button.TurnCustReturnOut', 'core.button.TurnPurcOut',
        'core.button.ClearSubpackage', 'core.button.Subpackage', 'core.grid.YnColumn', 'core.button.CatchBatch', 'core.button.Printnosale', 'core.button.PrintNoCustomer', 'core.button.PrintBZT', 'core.button.PrintInvoice',
        'core.button.SetMMQTY', 'core.button.TurnSaleReturn', 'core.button.TurnRenewApply', 'core.button.TurnMrb', 'core.button.UpdateInfo', 'core.button.Barcode', 'core.button.UpdateRemark', 'core.button.ZxbzsBarcode',
        'core.button.PrintAll', 'core.form.CheckBoxGroup', 'core.button.PrintByCondition', 'core.button.CreateBill', 'core.button.TurnGoodsOut', 'core.button.PrintMT', 'core.button.CatchBatchByOrder', 
        'core.button.CatchBatchByClient', 'core.button.CatchBatchByCust','core.button.TurnPaIn','core.button.Modify', 'core.button.CreateOtherBill'
    ],
    init: function() {
        var me = this;
        var grid = Ext.getCmp('grid');
        me.FormUtil = Ext.create('erp.util.FormUtil');
        me.GridUtil = Ext.create('erp.util.GridUtil');
        me.BaseUtil = Ext.create('erp.util.BaseUtil');
        this.control({
        	'erpModifyCommonButton':{
        		afterrender:function(btn){
    				var status = Ext.getCmp("pi_statuscode");
    				var pi_status = Ext.getCmp("pi_invostatuscode");
    				if((status && status.value == 'POSTED' )||(pi_status && pi_status.value!='ENTERING')){
    					btn.show();
    				}
    			}
        	},
            'erpCreateBillButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pi_statuscode'),
                        url = "";;
                    if (status && status.value != 'POSTED') {
                        btn.hide();
                    }
                    if (caller == 'ProdInOut!Sale' || caller == 'ProdInOut!SaleReturn') {
                        me.BaseUtil.getSetting('sys', 'autoCreateArBill', function(bool) {
                            if (!bool) {
                                btn.hide();
                            }
                        });
                    }
                    if (caller == 'ProdInOut!PurcCheckin' || caller == 'ProdInOut!PurcCheckout' || caller == 'ProdInOut!OutesideCheckReturn' || caller == 'ProdInOut!OutsideCheckIn' || caller == 'ProdInOut!GoodsOut' || caller == 'ProdInOut!GoodsIn') {
                        me.BaseUtil.getSetting('sys', 'autoCreateApBill', function(bool) {
                            if (!bool) {
                                btn.hide();
                            }
                        });
                    }
                },
                click: function(btn) {
                    Ext.Ajax.request({
                        url: basePath + 'scm/reserve/createBill.action',
                        params: {
                            caller: caller,
                            id: Ext.getCmp('pi_id').value
                        },
                        method: 'post',
                        callback: function(options, success, response) {
                            var res = new Ext.decode(response.responseText);
                            if (res) {
                                if (res.exceptionInfo) {
                                    showMessage('提示', res.exceptionInfo);
                                } else {
                                    if (res.log == null || res.log == '')
                                        showMessage('提示', '产生形式发票成功');
                                }
                            } else {
                                showMessage('提示', '产生形式发票失败');
                            }

                        }
                    });
                }
            },
            'erpCreateOtherBillButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pi_statuscode'), chargeamount = Ext.getCmp('pi_chargeamount'),
                        url = "";
                    if (status && status.value != 'POSTED') {
                        btn.hide();
                    }
                    if (chargeamount && Ext.isEmpty(chargeamount.value) || chargeamount.value == '0') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    Ext.Ajax.request({
                        url: basePath + 'scm/reserve/createOtherBill.action',
                        params: {
                            caller: caller,
                            id: Ext.getCmp('pi_id').value
                        },
                        method: 'post',
                        callback: function(options, success, response) {
                            var res = new Ext.decode(response.responseText);
                            if (res) {
                                if (res.exceptionInfo) {
                                    showMessage('提示', res.exceptionInfo);
                                } else {
                                    if (res.log == null || res.log == '')
                                        showMessage('提示', '产生其它应收单成功');
                                }
                            } else {
                                showMessage('提示', '产生其它应收单失败');
                            }

                        }
                    });
                }
            },
            'erpCatchBatchButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pi_statuscode');
                    if (status && status.value == 'POSTED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    Ext.Ajax.request({
                        url: basePath + 'scm/reserve/catchBatch.action',
                        params: {
                            caller: caller,
                            id: Ext.getCmp('pi_id').value
                        },
                        method: 'post',
                        callback: function(options, success, response) {
                            var res = new Ext.decode(response.responseText);
                            if (res) {
                                if (res.exceptionInfo) {
                                    showMessage('提示', res.exceptionInfo);
                                } else {
                                    if (res.log == null || res.log == '')
                                        showMessage('提示', '抓取成功');
                                    var grid=Ext.getCmp('grid');
                                    grid.getData(gridCondition);
                                }
                            } else {
                                showMessage('提示', '抓取失败');
                            }

                        }
                    });
                }
            },
            'erpGridPanel2': {
                afterrender: function(grid) {
                    grid.plugins[0].on('beforeedit', function(args) {
                        if (args.field == "pd_inqty") {
                            return me.isAllowUpdateQty(args.record);
                        }
                        if (caller == 'ProdInOut!SaleAppropriationOut' || 'ProdInOut!OtherOut' == caller) {
                            if (args.field == "pd_ordercode") {
                                return me.isAllowSale(args.record);
                            }
                            if (args.field == "pd_plancode") {
                                return me.isAllowForeCast(args.record);
                            }
                        }
                    });
                    if (caller == 'ProdInOut!SaleReturn') {
                        if (Ext.getCmp('pi_sourcecode') && Ext.getCmp('pi_sourcecode').value != "") {
                            grid.readOnly = true;
                        }
                    }
                },
                itemclick: function(selModel, record) {
                    var bool = me.hasSource(selModel.ownerCt);
                    if (caller == 'ProdInOut!OtherIn' || caller == 'ProdInOut!OtherPurcIn') {
                        if (record.data.pd_id > 0) {
                            var btn = selModel.ownerCt.down('#erpEditSubpackageButton');
                            if (btn && !btn.hidden)
                                btn.setDisabled(false);
                        }
                    }
                    if (caller == 'ProdInOut!Sale' || caller == 'ProdInOut!SaleReturn') {
                        if (record.data.pd_id > 0) {
                            var btn = Ext.getCmp('UpdateProdInoutrateButton');
                            if (btn && !btn.hidden)
                                btn && btn.setDisabled(false);
                        }
                    }
                    if (caller == 'ProdInOut!PurcCheckout') {
                        if (record.data.pd_id > 0 && Ext.getCmp('pi_statuscode').value != 'POSTED' && Ext.isEmpty(record.data.pd_ordercode)) {
                            var btn = selModel.ownerCt.down('#erpGetPurcPrice');
                            if (btn && !btn.hidden)
                                btn.setDisabled(false);
                        }
                    }
                    if (record.data.pd_id != 0 && record.data.pd_id != null && record.data.pd_id != '') {
                        var btn = Ext.getCmp('updatebgxh');
                        btn && btn.setDisabled(false);
                        btn = Ext.getCmp('erpSaveOrdercodeButton');
                        btn && btn.setDisabled(false);
                        btn = Ext.getCmp('barcodebtn');
                        btn && btn.setDisabled(false);
                        btn = Ext.getCmp('erpUpdateDetailWHCode');
                        btn && btn.setDisabled(false);
                        btn = Ext.getCmp('erpUpdateBatchCode');
                        btn && btn.setDisabled(false);
						btn = Ext.getCmp('splitdetail');
						btn && btn.setDisabled(false);
						btn = Ext.getCmp('catchBatchByOrder');
						btn && btn.setDisabled(false);
                        if (Ext.getCmp('pi_statuscode').value != 'POSTED' && (!Ext.isEmpty(record.data.pd_pocode) || !Ext.isEmpty(record.data.pd_ordercode))) {
                            btn = Ext.getCmp('catchBatchByOrder');
                            btn && btn.setDisabled(false);
                        }
                    }
                    if (!bool)
                        this.GridUtil.onGridItemClick(selModel, record);
                }
            },
            'erpDeleteDetailButton': {
                afterrender: function(btn){
                	if(caller == 'ProdInOut!PurcCheckin'){
                		btn.ownerCt.add({
                            xtype: 'erpBarcodeButton'
                        });
                	}
                	if(caller == 'ProdInOut!Sale'){
                		btn.ownerCt.add({
    						text: $I18N.common.button['UpdateProdInoutrateButton'],
    						id: 'UpdateProdInoutrateButton',
    						cls: 'x-btn-gray',
    						disabled: true
    					});
                		btn.ownerCt.add({
                            xtype: 'erpCatchBatchByOrderButton'
                        });
                		btn.ownerCt.add({
    						text: $I18N.common.button['erpUpdateDetailWHCode'],
    						id: 'erpUpdateDetailWHCode',
    						cls: 'x-btn-gray',
    						disabled: true
    					});
                	}
                }
            },
            /**
    		 * 明细分拆
    		 */
    		'#splitdetail': { 
    			afterrender: function(btn) {
                     var status = Ext.getCmp('pi_invostatuscode'), poststatus = Ext.getCmp('pi_statuscode');
                     if (status && status.value == 'AUDITED') {
                         btn.hide();
                     }
                     if (poststatus && poststatus.value == 'POSTED') {
                         btn.hide();
                     }
                },
    			click: function(btn) {
    				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
    			    me.SplitDetail(record);
    			}
    		},
            '#UpdateProdInoutrateButton': {
                click: function(btn) {
                    var record = btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();
                    me.updatepdscaleremark(record);
                }
            },
            /**
             * 更改报关型号
             */
            '#updatebgxh': {
                click: function(btn) {
                    var record = btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();
                    me.Updatebgxh(record);
                }
            },
           /* 'erpDeleteDetailButton': {
                afterrender: function(btn) {
                    btn.ownerCt.add({
                        xtype: 'erpBarcodeButton'
                    });
                }
            },*/
            'erpUpdateRemarkButton': {
                beforerender: function(btn) {
                    var status = Ext.getCmp('pi_invostatuscode'),
                        poststatus = Ext.getCmp('pi_statuscode');
                    if (caller == 'ProdInOut!OtherIn') {
                        if (status && status.value == 'COMMITED') {
                            Ext.getCmp('pi_remark').setReadOnly(false);
                        } else {
                            btn.hide();
                        }
                    }
                    if (caller == 'ProdInOut!PurcCheckin') {
                        if (poststatus && poststatus.value == 'POSTED') {
                            Ext.getCmp('pi_remark').setReadOnly(false);
                        } else {
                            btn.hide();
                        }
                    }
                    if (caller == 'ProdInOut!PurcCheckout') {
                        if (status && status.value == 'AUDITED') {
                            Ext.getCmp('pi_remark').setReadOnly(false);
                        } else {
                            btn.hide();
                        }
                    }
                    if (caller == 'ProdInOut!OutesideCheckReturn') {
                        if (status && status.value == 'AUDITED') {
                            Ext.getCmp('pi_remark').setReadOnly(false);
                        } else {
                            btn.hide();
                        }
                    }
                },
                click: function(btn) {
                    var remark = Ext.getCmp('pi_remark').value;
                    var id = Ext.getCmp('pi_id').value;
                    Ext.Ajax.request({
                        url: basePath + 'scm/sale/updateProdInOutOtherInRemark.action',
                        params: {
                            id: id,
                            remark: remark
                        },
                        method: 'post',
                        callback: function(options, success, response) {
                            var res = new Ext.decode(response.responseText);
                            if (res.exceptionInfo) {
                                showError(rs.exceptionInfo);
                            } else {
                                showMessage('提示', '更新备注成功!');
                                window.location.reload();
                            }
                        }
                    });
                }
            },
            'erpBarcodeButton': {
                click: function(btn) {
                    var pdid = btn.ownerCt.ownerCt.ownerCt.items.items[1].selModel.selected.items[0].data["pd_id"];
                    var id = Ext.getCmp("pi_id").value;
                    var formCondition1 = "pd_idIS" + pdid + " and pi_idIS'" + id + "'";
                    var gridCondition1 = "bi_pdidIS" + pdid + " and bi_piidIS'" + id + "'";
                    var linkCaller = '';
                    //获取出入库单DS_INOROUT字段判读是出库单还是入库单
                    Ext.Ajax.request({
                        url: basePath + '/pm/bom/getDescription.action',
                        params: {
                            tablename: "documentsetup",
                            field: 'ds_inorout',
                            condition: "ds_name='" + Ext.getCmp("pi_class").value + "'",
                            caller: caller
                        },
                        method: 'post',
                        callback: function(options, success, response) {
                            var res = new Ext.decode(response.responseText);
                            if (res.exceptionInfo) {
                                showError(rs.exceptionInfo);
                            } else {
                                var inOrOut = res.description;
                                if (inOrOut == "IN" || inOrOut == "-OUT") {
                                    linkCaller = "ProdInOut!BarcodeIn";
                                } else if (inOrOut == "-IN" || inOrOut == "OUT") {
                                    linkCaller = "ProdInOut!BarcodeOut";
                                }
                                var win = new Ext.window.Window({
                                    id: 'win',
                                    height: '100%',
                                    width: '95%',
                                    maximizable: true,
                                    buttonAlign: 'center',
                                    layout: 'anchor',
                                    items: [{
                                        tag: 'iframe',
                                        frame: true,
                                        anchor: '100% 100%',
                                        layout: 'fit',
                                        html: '<iframe id="iframe_' + linkCaller + '" src="' + basePath + 'jsps/scm/reserve/setBarcode.jsp?_noc=1&whoami=' + linkCaller + '&formCondition=' + formCondition1 + '&gridCondition=' + gridCondition1 + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
                                    }],
                                    listeners: {
                                        'beforeclose': function(view, opt) {
                                            var postStatus = Ext.getCmp('pi_statuscode');
                                            if (postStatus && postStatus.value != 'POSTED') {
                                                var grid = Ext.getCmp("grid");
                                                grid.getData(gridCondition);
                                            }
                                        }
                                    }
                                });
                                win.show();
                            }
                        }
                    });
                },
                afterrender: function(btn) {
                    btn.setDisabled(true);
                }
            },
            '#erpEditSubpackageButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pi_invostatuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    var record = btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();
                    me.EditSubpackage(record);
                }
            },
            'erpTurnProdinoutReturnButton': {
                click: function(btn) {
                    var id = Ext.getCmp('pi_id').value;
                    Ext.Ajax.request({
                        url: basePath + 'scm/sale/turnTurnProdinoutReturn.action',
                        params: {
                        	//caller:caller,
                            id: id
                        },
                        method: 'post',
                        callback: function(options, success, response) {
                            var res = new Ext.decode(response.responseText);
                            if (res.exceptionInfo) {
                                showError(rs.exceptionInfo);
                            } else {
                                if (res.log)
                                    showMessage('提示', res.log);
                            }
                        }
                    });
                }
            },
            'erpSplitButton': {
                afterrender: function(btn) {
                    var poststatus = Ext.getCmp('pi_statuscode');
                    if (poststatus && poststatus.value == 'POSTED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    warnMsg("确定拆分单据?", function(btn) {
                        if (btn == 'yes') {
                            var id = Ext.getCmp('pi_id').value;
                            var piclass = Ext.getCmp('pi_class').value;
                            Ext.Ajax.request({
                                url: basePath + 'scm/reserve/split.action',
                                params: {
                                    id: id,
                                    caller: caller,
                                    cls: piclass
                                },
                                method: 'post',
                                callback: function(options, success, response) {
                                    var res = new Ext.decode(response.responseText);
                                    if (res.exceptionInfo) {
                                        showError(res.exceptionInfo);
                                    } else {
                                        if (res.log)
                                            showMessage('提示', res.log);
                                    }
                                }
                            });
                        }
                    });
                }
            },
            'erpGridWinButton': {
                afterrender: function(btn) {
                    var id = Ext.getCmp('pi_id').value,
                        piclass = Ext.getCmp('pi_class').value,
                        cal;
                    if (piclass == '采购验收单') {
                        cal = 'ProdChargeDetail!CGYS';
                    } else if (piclass == '采购验退单') {
                        cal = 'ProdChargeDetail!CGYT';
                    } else if (piclass == '出货单') {
                        cal = 'ProdChargeDetail!XSCH';
                    } else if (piclass == '销售退货单') {
                        cal = 'ProdChargeDetail!XSTH';
                    }
                    btn.setConfig({
                        text: '费用明细',
                        caller: cal,
                        condition: 'pd_piid=' + id,
                        paramConfig: {
                            pd_piid: id,
                            getUrl: 'scm/reserve/getProdCharge.action?piclass=' + piclass + '&piid=' + id
                        }
                    });
                },
                beforesave: function(btn) {
                    var f = btn.ownerCt.ownerCt,
                        p = f.down('field[name=pi_statuscode]');
                    if (p && 'POSTED' == p.getValue()) {
                        Ext.Msg.alert("提示", "该单据已经过账,不能修改费用明细！");
                        return false;
                    }
                    return true;
                }
            },
            'field[name=pi_testcolumn1]': {
                beforerender: function(field) {
                    field.labelWidth = 800;
                }
            },
            'erpSaveButton': {
                click: function(btn) {
                    var form = me.getForm(btn);
                    if (Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '') {
                        me.BaseUtil.getRandomNumber(caller, 2, form.codeField); //自动添加编号
                    }
                    me.save(btn);
                }
            },
            'erpPrintA4Button': {
                click: function(btn) {
                    var reportName = '';
                    var kind = Ext.getCmp('pi_class').value;
                    if (kind == '拨出单') {
                        reportName = "piolist_bc4"
                    } else if (kind == '出货单') {
                        reportName = "sendlist"
                    } else if (kind == '生产退料单') {
                        reportName = "PIOLISTM_BackA4"
                    } else if (kind == '生产补料单') {
                        reportName = "PIOLIST_blA4"
                    } else if (kind == '生产领料单') {
                        reportName = "PIOLISTMA4"
                    } else if (kind == '其它出库单') {
                        reportName = "piolist_outA4"
                    } else if (kind == '其它入库单') {
                        reportName = "piolist_inA4"
                    } else if (kind == '报废单') {
                        reportName = "piolist_bfA4"
                    }
                    var condition = '{prodinout.pi_id}=' + Ext.getCmp('pi_id').value + '';
                    var id = Ext.getCmp('pi_id').value;
                    me.FormUtil.onwindowsPrint(id, reportName, condition);
                }
            },
            'erpUpdatePdpriceButton': {
                click: function(btn) {
                    var p = Ext.getCmp('pi_statuscode');
                    if (p && 'POSTED' == p.getValue()) {
                        Ext.Msg.alert("提示", "该单据已经过账,不能修改费用明细！");
                        return;
                    }
                    var id = Ext.getCmp('pi_id').value;
                    Ext.Ajax.request({
                        url: basePath + 'scm/reserve/updatepdPrice.action',
                        params: {
                            id: id
                        },
                        method: 'post',
                        callback: function(options, success, response) {
                            var res = new Ext.decode(response.responseText);
                            window.location.reload();
                            if (!res.bool) {
                                btn.hide();
                            }
                        }
                    });
                }
            },
            'erpDeleteButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp(me.getForm(btn).statuscodeField),
                        poststatus = Ext.getCmp('pi_statuscode');
                    if (status && status.value == 'DELETED') {
                        btn.hide();
                    }
                    if (poststatus && poststatus.value == 'POSTED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onDelete({
                        pu_id: Number(Ext.getCmp('pi_id').value)
                    });
                }
            },
            'erpResetbatchButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pi_statuscode');
                    if (status && status.value != 'UNPOST') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    Ext.Ajax.request({
                        url: basePath + 'scm/reserve/resetBatchcode.action',
                        params: {
                            caller: caller,
                            id: Ext.getCmp('pi_id').value
                        },
                        method: 'post',
                        callback: function(options, success, response) {
                            var res = new Ext.decode(response.responseText);
                            if (res.exceptionInfo) {
                                showError(res.exceptionInfo);
                                return;
                            } else if (res.success) {
                                showMessage("提示", "批号重置成功！");
                                window.location.reload();
                            }
                        }
                    });
                }

            },
            'erpUpdateButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp(me.getForm(btn).statuscodeField);
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                    status = Ext.getCmp('pi_statuscode');
                    if (status && 'POSTED' == status.value) {
                        btn.hide();
                    }
                },
                click: {
                	lock: 2000,
                	fn: function(btn) {
	                    var grid = Ext.getCmp('grid'),
	                        items = grid.store.data.items,
	                        c = Ext.getCmp('pi_inoutno').value;
	                    var piclass = Ext.getCmp('pi_class').value,
	                        date = Ext.getCmp('pi_date').value;
	                    var bool = true;
	                    if (caller == 'ProdInOut!PurcCheckout') {
	                        Ext.each(items, function(item) {
	                            if (!Ext.isEmpty(item.data['pd_ordercode'])) {
	                                if (item.data['pd_outqty'] > item.data['pd_acceptqty']) {
	                                    bool = false;
	                                    showError('明细表第' + item.data['pd_pdno'] + '数量大于采购单的验收数量');
	                                    return false;
	                                }
	                            }
	                        });
	                    }
	                    if (caller == 'ProdInOut!OtherPurcIn') {
	                        Ext.each(items, function(item) {
	                            if (!Ext.isEmpty(item.data['pd_ordercode'])) {
	                                if (Ext.Date.format(item.data['pd_vendorreplydate'], 'Ymd') < Ext.Date.format(new Date(), 'Ymd')) {
	                                    bool = false;
	                                    showError('明细表第' + item.data['pd_pdno'] + '还料日期小于系统当前日期');
	                                    return false;
	                                }
	                            }
	                        });
	                    }
	                    Ext.Array.each(items, function(item) {
	                        if (!Ext.isEmpty(item.data['pd_prodcode'])) {
	                            /*item.set('pd_inoutno', c);
	                            item.set('pd_piclass', piclass);*/
	                        	item.data['pd_inoutno']=c;
	                        	item.data['pd_piclass']=piclass;
	                        }
	                        if (caller == 'ProdInOut!OtherIn' || caller == 'ProdInOut!OtherPurcIn') {
	                            if (item.data['pd_unitpackage'] == null || item.data['pd_unitpackage'] == '' ||
	                                item.data['pd_unitpackage'] == '0' || item.data['pd_unitpackage'] == 0) {
	                                /*item.set('pd_unitpackage', item.data['pd_inqty']);*/
	                            	item.data['pd_unitpackage']=item.data['pd_inqty'];
	                            }
	                        }
	                    });
	                    if (caller != 'ProdInOut!AppropriationOut' && caller != 'ProdInOut!AppropriationIn') {
	                        var whcode = Ext.getCmp('pi_whcode'),
	                            whname = Ext.getCmp('pi_whname');
	                        if (whcode && whname) {
	                            Ext.Array.each(items, function(item) {
	                                if (!Ext.isEmpty(item.data['pd_prodcode'])) {
	                                    if (Ext.isEmpty(item.data['pd_whcode'])) {
	                                        /*item.set('pd_whcode', whcode.value);
	                                        item.set('pd_whname', whname.value);*/
	                                    	item.data['pd_whcode']=whcode.value;
	                                    	item.data['pd_whname']=whname.value;
	                                    }
	                                }
	                            });
	                        }
	                    }
	                    if (caller == 'ProdInOut!SaleAppropriationOut' || 'ProdInOut!OtherOut' == caller) {
	                        Ext.Array.each(items, function(item) {
	                            if (!Ext.isEmpty(item.data['pd_plancode'])) {
	                                if (item.data['pd_outqty'] > item.data['pd_sfdqty']) {
	                                    bool = false;
	                                    showError('明细表第' + item.data['pd_pdno'] + '数量大于销售预测单数量');
	                                    return false;
	                                }
	                            }
	                            if (!Ext.isEmpty(item.data['pd_ordercode'])) {
	                                if (item.data['pd_outqty'] > item.data['sd_qty']) {
	                                    bool = false;
	                                    showError('明细表第' + item.data['pd_pdno'] + '数量大于销售单数量');
	                                    return false;
	                                }
	                            }
	                        });
	                    }
	                    if (caller == 'ProdInOut!SaleAppropriationOut' || 'ProdInOut!AppropriationOut' == caller) {
	                        var pi_purpose = Ext.getCmp('pi_purpose'),
	                            pi_purposename = Ext.getCmp('pi_purposename');
	                        var pi_whcode = Ext.getCmp('pi_whcode'),
	                            pi_whname = Ext.getCmp('pi_whname');
	                        if (pi_whcode && pi_whname) {
	                            Ext.Array.each(items, function(item) {
	                                if (!Ext.isEmpty(item.data['pd_prodcode'])) {
	                                    if (Ext.isEmpty(item.data['pd_whcode'])) {
	                                        /*item.set('pd_whcode', pi_whcode.value);
	                                        item.set('pd_whname', pi_whname.value);*/
	                                    	item.data['pd_whcode']=pi_whcode.value;
	                                    	item.data['pd_whname']=pi_whname.value;
	                                    }
	                                }
	                            });
	                        }
	                        if (pi_purpose && pi_purposename) {
	                            Ext.Array.each(items, function(item) {
	                                if (!Ext.isEmpty(item.data['pd_prodcode'])) {
	                                    if (Ext.isEmpty(item.data['pd_inwhcode'])) {
	                                        /*item.set('pd_inwhcode', pi_purpose.value);
	                                        item.set('pd_inwhname', pi_purposename.value);*/
	                                    	item.data['pd_inwhcode']=pi_purpose.value;
	                                    	item.data['pd_inwhname']=pi_purposename.value;
	                                    }
	                                }
	                            });
	                        }
	                    }
	                    // 拨入单、拨出单，默认第一行的仓库
	                    me.setDetailWarehouse(grid);
	                    if (caller == 'ProdInOut!Make!Return' || caller == 'ProdInOut!OutsideReturn' || caller == 'ProdInOut!Make!Useless') {
	                        var firstItem = grid.store.getAt(0);
	                        if (firstItem) {
	                            var desc = firstItem.get('pd_description'),
	                                dc = firstItem.get('pd_departmentcode'),
	                                dn = firstItem.get('pd_departmentname'),
	                                whcode = firstItem.get('pd_whcode'),
	                                whname = firstItem.get('pd_whname'),
	                                pd_textbox = firstItem.get('pd_textbox');
	                            Ext.Array.each(items, function(item) {
	                                if (!Ext.isEmpty(item.data['pd_prodcode'])) {
	                                    if (Ext.isEmpty(item.data['pd_description'])) {
	                                        /*item.set('pd_description', desc);*/
	                                    	item.data['pd_description']=desc;
	                                    }
	                                    if (Ext.isEmpty(item.data['pd_departmentcode'])) {
	                                        /*item.set('pd_departmentcode', dc);
	                                        item.set('pd_departmentname', dn);*/
	                                    	item.data['pd_departmentcode']=dc;
	                                    	item.data['pd_departmentname']=dn;
	                                    }
	                                    if (Ext.isEmpty(item.data['pd_whcode'])) {
	                                        /*item.set('pd_whcode', whcode);
	                                        item.set('pd_whname', whname);*/
	                                    	item.data['pd_whcode']=whcode;
	                                    	item.data['pd_whname']=whname;
	                                    }
	                                    if (caller == 'ProdInOut!Make!Useless') {
	                                        if (Ext.isEmpty(item.data['pd_textbox'])) {
	                                            /*item.set('pd_textbox', pd_textbox);*/
	                                        	item.data['pd_textbox']=pd_textbox;
	                                        }
	                                    }
	                                }
	                            });
	                        }
	                    }
	                    if (bool) {
	                        me.FormUtil.onUpdate(me);
	                    }
                	}
                }
            },
            'erpAddButton': {
                click: function() {
                    me.FormUtil.onAdd('add' + caller, '新增出入库单', "jsps/b2c/purchase/b2cProdInOut.jsp?whoami=" + caller);
                }
            },
            'erpCloseButton': {
                click: function(btn) {
                    me.FormUtil.beforeClose(me);
                }
            },
            'field[name=pi_currency]': {
                beforetrigger: function(field) {
                    var t = field.up('form').down('field[name=pi_date]'),
                        value = t.getValue();
                    if (value) {
                        field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
                    }
                }
            },
            'erpSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp(me.getForm(btn).statuscodeField),
                        poststatus = Ext.getCmp('pi_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                    if (poststatus && poststatus.value == 'POSTED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    var grid = Ext.getCmp('grid');
                    var items = grid.store.data.items;
                    var bool = true;
                    var date = Ext.getCmp('pi_date').value;
                    if (caller == 'ProdInOut!OtherIn' || caller == 'ProdInOut!OtherOut') {
                        var type = Ext.getCmp('pi_type').value;
                        if (type == null || type == '') {
                            showError("主表类型字段未填写,不能提交!");
                            return;
                        }
                    }
                    if (caller == 'ProdInOut!PurcCheckout') {
                        Ext.each(items, function(item) {
                            if (!Ext.isEmpty(item.data['pd_ordercode'])) {
                                if (item.data['pd_outqty'] > item.data['pd_acceptqty']) {
                                    bool = false;
                                    showError('明细表第' + item.data['pd_pdno'] + '数量大于采购单的验收数量');
                                    return;
                                }
                            }
                        });
                    }
                    if (caller == 'ProdInOut!OtherPurcIn') {
                        Ext.each(items, function(item) {
                            if (!Ext.isEmpty(item.data['pd_ordercode'])) {
                                if (Ext.Date.format(item.data['pd_vendorreplydate'], 'Ymd') < Ext.Date.format(new Date(), 'Ymd')) {
                                    bool = false;
                                    showError('明细表第' + item.data['pd_pdno'] + '还料日期小于系统当前日期');
                                    return;
                                }
                            }
                        });
                    }
                    /*Ext.each(items, function(item){
    					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
    						if(item.data['pd_outqty'] == null || item.data['pd_outqty'] == ''){
	    						bool = false;
	    						showError("明细第" + item.data['pd_pdno'] + "行出库数量未填写，不能提交");return;
    						}
    					}
    				});*/
                    if (caller == 'ProdInOut!SaleAppropriationOut' || 'ProdInOut!OtherOut' == caller) {
                        Ext.Array.each(items, function(item) {
                            if (!Ext.isEmpty(item.data['pd_plancode'])) {
                                if (item.data['pd_outqty'] > item.data['pd_sfdqty']) {
                                    bool = false;
                                    showError('明细表第' + item.data['pd_pdno'] + '数量大于销售预测单数量');
                                    return;
                                }
                            }
                            if (!Ext.isEmpty(item.data['pd_ordercode'])) {
                                if (item.data['pd_outqty'] > item.data['sd_qty']) {
                                    bool = false;
                                    showError('明细表第' + item.data['pd_pdno'] + '数量大于销售单数量');
                                    return;
                                }
                            }
                        });
                    }
                    if (caller == 'ProdInOut!AppropriationOut') {
                        Ext.each(items, function(item) {
                            if (item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != '') {
                                if (Ext.getCmp('pd_outqty') && item.data['pd_outqty'] == null || item.data['pd_outqty'] == '') {
                                    bool = false;
                                    showError("明细第" + item.data['pd_pdno'] + "行出库数量未填写，不能提交");
                                    return;
                                }
                            }
                        });
                    }
                    if (bool) {
                        me.FormUtil.onSubmit(Ext.getCmp('pi_id').value);
                    }
                }
            },
            'erpResSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp(me.getForm(btn).statuscodeField),
                        poststatus = Ext.getCmp('pi_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                    if (poststatus && poststatus.value == 'POSTED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onResSubmit(Ext.getCmp('pi_id').value);
                }
            },
            'erpAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp(me.getForm(btn).statuscodeField),
                        poststatus = Ext.getCmp('pi_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                    if (poststatus && poststatus.value == 'POSTED') {
                        btn.hide();
                    }
                },
                click: {
                	lock: 2000,
                	fn: function(btn) {
                        var grid = Ext.getCmp('grid');
                        var items = grid.store.data.items;
                        var bool = true;
                        if (caller == 'ProdInOut!OtherPurcIn') {
                            Ext.each(items, function(item) {
                                if (!Ext.isEmpty(item.data['pd_ordercode'])) {
                                    if (Ext.Date.format(item.data['pd_vendorreplydate'], 'Ymd') < Ext.Date.format(new Date(), 'Ymd')) {
                                        bool = false;
                                        showError('明细表第' + item.data['pd_pdno'] + '还料日期小于系统当前日期');
                                        return;
                                    }
                                }
                            });
                        }
                        if (caller == 'ProdInOut!SaleAppropriationOut' || 'ProdInOut!OtherOut' == caller) {
                            Ext.Array.each(items, function(item) {
                                if (!Ext.isEmpty(item.data['pd_plancode'])) {
                                    if (item.data['pd_outqty'] > item.data['pd_sfdqty']) {
                                        bool = false;
                                        showError('明细表第' + item.data['pd_pdno'] + '数量大于销售预测单数量');
                                        return;
                                    }
                                }
                                if (!Ext.isEmpty(item.data['pd_ordercode'])) {
                                    if (item.data['pd_outqty'] > item.data['sd_qty']) {
                                        bool = false;
                                        showError('明细表第' + item.data['pd_pdno'] + '数量大于销售单数量');
                                        return;
                                    }
                                }
                            });
                        }
                        if (bool)
                            me.FormUtil.onAudit(Ext.getCmp('pi_id').value);
                    }
                }
            },
            'erpResAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp(me.getForm(btn).statuscodeField),
                        postStatus = Ext.getCmp('pi_statuscode');
                    if ((status && status.value != 'AUDITED') || (postStatus && postStatus.value == 'POSTED')) {
                        btn.hide();
                    }
                },
                click: {
                	lock: 2000,
                	fn: function(btn) {
                        me.FormUtil.onResAudit(Ext.getCmp('pi_id').value);
                    }
                }
            },
            'erpPrintButton': {
                click: function(btn) {
                    me.onPrint();
                }
            },
            'erpPrintotherinButton': { // 返修机入仓单
                click: function(btn) {
                    var reportName = "piolist_otherin";
                    var condition = '{prodinout.pi_id}=' + Ext.getCmp('pi_id').value + '';
                    var id = Ext.getCmp('pi_id').value;
                    me.FormUtil.onwindowsPrint(id, reportName, condition);
                }
            },
            'erpPrintotheroutButton': { // 返修机出仓单
                click: function(btn) {
                    var reportName = "piolist_otherout";
                    var condition = '{prodinout.pi_id}=' + Ext.getCmp('pi_id').value + '';
                    var id = Ext.getCmp('pi_id').value;
                    me.FormUtil.onwindowsPrint(id, reportName, condition);
                }
            },
            'erpPrintwithPriceButton': { // 无价打印==erpPrintButton
                click: function(btn) {
                    var reportName = "sendlist_yessale_yesprice";
                    var condition = '{prodinout.pi_id}=' + Ext.getCmp('pi_id').value + '';
                    var id = Ext.getCmp('pi_id').value;
                    me.FormUtil.onwindowsPrint(id, reportName, condition);
                }
            },
            'erpPrintBKTButton': { // 打印补客退==erpPrintButton
                click: function(btn) {
                    var reportName = "sendlist_bh";
                    var condition = '{prodinout.pi_id}=' + Ext.getCmp('pi_id').value + '';
                    var id = Ext.getCmp('pi_id').value;
                    me.FormUtil.onwindowsPrint(id, reportName, condition);
                }
            },
            'erpPrintPriceButton': { // (有价)打印,和erpPrintwithPriceButton一样，某些客户需要默认为有效
                click: function(btn) {
                    var reportName = "sendlist_yessale_yesprice";
                    var condition = '{prodinout.pi_id}=' + Ext.getCmp('pi_id').value + '';
                    var id = Ext.getCmp('pi_id').value;
                    me.FormUtil.onwindowsPrint(id, reportName, condition);
                }
            },
            'erpPrintnosaleButton': { // 无PO打印
                click: function(btn) {
                    var reportName = "sendlist_nosale";
                    var condition = '{prodinout.pi_id}=' + Ext.getCmp('pi_id').value + '';
                    var id = Ext.getCmp('pi_id').value;
                    me.FormUtil.onwindowsPrint(id, reportName, condition);
                }
            },
            'erpPrintBZTButton': {
                click: function(btn) {
                    var reportName = "sale_packing";
                    var condition = '{prodinout.pi_id}=' + Ext.getCmp('pi_id').value + '';
                    me.FormUtil.onwindowsPrint(Ext.getCmp('pi_id').value, reportName, condition);
                }
            },
            'erpPrintInvoiceButton': {
                click: function(btn) {
                    var reportName = "sale_invoice";
                    var condition = '{prodinout.pi_id}=' + Ext.getCmp('pi_id').value + '';
                    var id = Ext.getCmp('pi_id').value;
                    me.FormUtil.onwindowsPrint(id, reportName, condition);
                }
            },
            'erpPrintNoCustomerButton': { // 无客户打印
                click: function(btn) {
                    var reportName = "sendlist_yessale_nocustomer";
                    var condition = '{prodinout.pi_id}=' + Ext.getCmp('pi_id').value + '';
                    var id = Ext.getCmp('pi_id').value;
                    me.FormUtil.onwindowsPrint(id, reportName, condition);
                }
            },
            'erpPrintNoPriceButton': { // 无价打印==erpPrintButton
                click: function(btn) {
                    me.onPrint();
                }
            },
            'erpPostButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pi_statuscode');
                    if (status && status.value != 'UNPOST') {
                        btn.hide();
                    }
                },
                click: {
                	lock: 2000,
                	fn: function(btn) {
	                    var grid = Ext.getCmp('grid'),
	                        items = grid.store.data.items,
	                        bool = true;
	                    var date = Ext.getCmp('pi_date').value;
	                    if (caller == 'ProdInOut!OtherIn' || caller == 'ProdInOut!OtherOut') {
	                        var type = Ext.getCmp('pi_type').value;
	                        if (type == null || type == '') {
	                            showError("主表类型字段未填写,不能提交!");
	                            return;
	                        }
	                    }
	                    if (caller == 'ProdInOut!PurcCheckout') {
	                        Ext.each(items, function(item) {
	                            if (!Ext.isEmpty(item.data['pd_ordercode'])) {
	                                if (item.data['pd_outqty'] > item.data['pd_acceptqty']) {
	                                    bool = false;
	                                    showError('明细表第' + item.data['pd_pdno'] + '数量大于采购单的验收数量');
	                                    return;
	                                }
	                            }
	                        });
	                    }
	                    if (caller == 'ProdInOut!OtherPurcIn') {
	                        Ext.each(items, function(item) {
	                            if (!Ext.isEmpty(item.data['pd_ordercode'])) {
	                                if (Ext.Date.format(item.data['pd_vendorreplydate'], 'Ymd') < Ext.Date.format(new Date(), 'Ymd')) {
	                                    bool = false;
	                                    showError('明细表第' + item.data['pd_pdno'] + '还料日期小于系统当前日期');
	                                    return;
	                                }
	                            }
	                        });
	                    }
	                    /*Ext.each(items, function(item){
	    					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
	    						if(item.data['pd_outqty'] == null || item.data['pd_outqty'] == ''){
		    						bool = false;
		    						showError("明细第" + item.data['pd_pdno'] + "行出库数量未填写，不能提交");return;
	    						}
	    					}
	    				});*/
	                    if (caller == 'ProdInOut!AppropriationOut') {
	                        Ext.each(items, function(item) {
	                            if (item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != '') {
	                                if (Ext.getCmp('pd_outqty') && item.data['pd_outqty'] == null || item.data['pd_outqty'] == '') {
	                                    bool = false;
	                                    showError("明细第" + item.data['pd_pdno'] + "行出库数量未填写，不能提交");
	                                    return;
	                                }
	                            }
	                        });
	                    }
	                    if (caller == 'ProdInOut!SaleAppropriationOut' || 'ProdInOut!OtherOut' == caller) {
	                        Ext.Array.each(items, function(item) {
	                            if (!Ext.isEmpty(item.data['pd_plancode'])) {
	                                if (item.data['pd_outqty'] > item.data['pd_sfdqty']) {
	                                    bool = false;
	                                    showError('明细表第' + item.data['pd_pdno'] + '数量大于销售预测单数量');
	                                    return;
	                                }
	                            }
	                            if (!Ext.isEmpty(item.data['pd_ordercode'])) {
	                                if (item.data['pd_outqty'] > item.data['sd_qty']) {
	                                    bool = false;
	                                    showError('明细表第' + item.data['pd_pdno'] + '数量大于销售单数量');
	                                    return;
	                                }
	                            }
	                        });
	                    }
	                    if (bool) {
	                        me.FormUtil.onPost(Ext.getCmp('pi_id').value);
	                    }
	                }
                }
            },
            'erptecaiButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pi_statuscode');
                    if (caller != 'ProdInOut!DefectIn' || status.value != 'POSTED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    var pi_id = Ext.getCmp('pi_id').value;
                    me.batchdeal('ProdIN!ToProdOtherOut!Deal', ' pd_piid=' + Ext.getCmp('pi_id').value + ' and nvl(pd_yqty,0) < nvl(pd_inqty,0) + nvl(pd_outqty,0)', 'scm/reserve/erptecai.action');
                    //之前是判断单据状态 现在取消  需要根据明细数量来确定是否可以特采
                    /*Ext.Ajax.request({
    					url: basePath + 'scm/reserve/erptecai.action',
    					params: {
    						caller: caller,
    						id: pi_id
    					},
    					callback: function(opt, s, r) {
    						var rs = Ext.decode(r.responseText);
    						if(rs.exceptionInfo) {
    							showError(rs.exceptionInfo);
    						} else {
    							if(rs.log)
    								showMessage('提示', rs.log);
    						}
    					}
    				});*/

                }
            },
            'erpResPostButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pi_statuscode');
                    if (status && status.value != 'POSTED') {
                        btn.hide();
                    }
                },
                click: {
                	lock: 2000,
                	fn: function(btn) {
                        me.FormUtil.onResPost(Ext.getCmp('pi_id').value);
                    }
                }
            },
            'erpTurnProdinoutIn': {
                afterrender: function(btn) {
                    var status = Ext.getCmp(me.getForm(btn).statuscodeField);
                    if (caller == 'ProdInOut!OtherOut' && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    var id = Ext.getCmp('pi_id').value;
                    Ext.Ajax.request({
                        url: basePath + 'scm/reserve/turnProdinoutIn.action',
                        params: {
                            id: id
                        },
                        callback: function(opt, s, r) {
                            var rs = Ext.decode(r.responseText);
                            if (rs.exceptionInfo) {
                                showError(rs.exceptionInfo);
                            } else {
                                if (rs.log)
                                    showMessage('提示', rs.log);
                            }
                        }
                    });
                }
            },
            'erpSetMMQTYButton': {
                click: function(btn) {
                    warnMsg("确认本次发料数为当前维护的实际可发数?", function(btn) {
                        if (btn == 'yes') {
                            me.FormUtil.setLoading(true); //loading...
                            Ext.Ajax.request({
                                url: basePath + 'scm/reserve/SetMMQTY.action',
                                params: {
                                    id: Ext.getCmp('pi_id').value,
                                    caller: caller
                                },
                                method: 'post',
                                callback: function(opt, s, r) {
                                    me.FormUtil.setLoading(false);
                                    var rs = Ext.decode(r.responseText);
                                    if (rs.exceptionInfo) {
                                        showError(rs.exceptionInfo);
                                    } else {
                                        if (rs.log)
                                            showMessage('提示', rs.log);
                                    }
                                    window.location.reload();
                                }
                            });
                        }
                    });
                }
            },
            'dbfindtrigger[name=pd_ordercode]': {
                focus: function(t) {
                    t.setHideTrigger(false);
                    t.setReadOnly(false);
                    if (Ext.getCmp('pi_cardcode')) {
                        var code = Ext.getCmp('pi_cardcode').value;
                        if (code != null && code != '') {
                            var obj = me.getCodeCondition();
                            if (obj && obj.field) {
                                t.dbBaseCondition = obj.field + "='" + code + "'";
                            }
                        }
                    }
                    if (caller == 'ProdInOut!OutReturn') { //借货归还单
                        var code = Ext.getCmp('pi_cardcode').value;
                        if (code != null && code != '') {
                            if (t.dbBaseCondition == null || t.dbBaseCondition == '') {
                                t.dbBaseCondition = "pi_cardcode='" + code + "'";
                            } else {
                                t.dbBaseCondition = t.dbBaseCondition + " and pi_cardcode='" + code + "'";
                            }
                        }
                    }
                },
                aftertrigger: function(t) {
                    if (Ext.getCmp('pi_cardcode')) {
                        var obj = me.getCodeCondition();
                        if (obj && obj.fields) {
                            me.FormUtil.getFieldsValue(obj.tablename, obj.fields, obj.myfield + "='" + t.value + "'", obj.tFields);
                        }
                    }
                }
            },
            'dbfindtrigger[name=pd_orderdetno]': {
                focus: function(t) {
                    t.setHideTrigger(false);
                    t.setReadOnly(false); //用disable()可以，但enable()无效
                    var record = Ext.getCmp('grid').selModel.lastSelected;
                    var code = record.data['pd_ordercode'];
                    if (code == null || code == '') {
                        showError("请先选择关联单号!");
                        t.setHideTrigger(true);
                        t.setReadOnly(true);
                    } else {
                        var field = me.getBaseCondition();
                        if (field) {
                            t.dbBaseCondition = field + "='" + code + "'";
                        }
                    }
                }
            },
            'multidbfindtrigger[name=pd_orderdetno]': {
                focus: function(t) {
                    t.setHideTrigger(false);
                    t.setReadOnly(false); //用disable()可以，但enable()无效
                    var record = Ext.getCmp('grid').selModel.lastSelected;
                    var code = record.data['pd_ordercode'];
                    if (code == null || code == '') {
                        showError("请先选择关联单号!");
                        t.setHideTrigger(true);
                        t.setReadOnly(true);
                    } else {
                        var field = me.getBaseCondition();
                        if (field) {
                            t.dbBaseCondition = field + "='" + code + "'";
                        }
                    }
                }
            },
            'dbfindtrigger[name=pd_batchcode1]': {
                afterrender: function(t) {
                    var record = Ext.getCmp('grid').selModel.lastSelected;
                    var pr = record.data['pd_prodcode'],
                        wh = record.data['pd_whcode'];
                    if (pr == null || pr == '') {
                        showError("请先选择料号!");
                        return;
                    }
                    if (wh == null || wh == '') {
                        if (Ext.getCmp('pi_whcode')) {
                            wh = Ext.getCmp('pi_whcode').value;
                            if (wh == null || wh == '') {
                                showError("请先选择仓库!");
                                return;
                            }
                        }
                    }
                    t.dbBaseCondition = "ba_whcode='" + wh + "' AND ba_prodcode='" + pr + "'";
                }
            },
            'dbfindtrigger[name=pd_batchcode]': {
                focus: function(t) {
                    t.setHideTrigger(false);
                    t.setReadOnly(false); //用disable()可以，但enable()无效
                    var record = Ext.getCmp('grid').selModel.lastSelected;
                    var pr = record.data['pd_prodcode'];
                    if (pr == null || pr == '') {
                        showError("请先选择料号!");
                        t.setHideTrigger(true);
                        t.setReadOnly(true);
                    } else {
                        var code = record.data['pd_whcode'];
                        if (code == null || code == '') {
                            if (Ext.getCmp('pi_whcode')) {
                                code = Ext.getCmp('pi_whcode').value;
                                if (code == null || code == '') {
                                    showError("请先选择仓库!");
                                    t.setHideTrigger(true);
                                    t.setReadOnly(true);
                                } else {
                                    t.dbBaseCondition = "ba_whcode='" + code + "' AND ba_prodcode='" + pr + "'";
                                }
                            } else {
                                t.dbBaseCondition = "ba_prodcode='" + pr + "'";
                            }
                        } else {
                            t.dbBaseCondition = "ba_whcode='" + code + "' AND ba_prodcode='" + pr + "'";
                        }
                    }
                }
            },
            'multidbfindtrigger[name=pd_batchcode]': {
                focus: function(t) {
                    t.setHideTrigger(false);
                    t.setReadOnly(false); //用disable()可以，但enable()无效
                    var record = Ext.getCmp('grid').selModel.lastSelected;
                    var pr = record.data['pd_prodcode'];
                    if (pr == null || pr == '') {
                        showError("请先选择料号!");
                        t.setHideTrigger(true);
                        t.setReadOnly(true);
                    } else {
                        var code = record.data['pd_whcode'];
                        if (code == null || code == '') {
                            if (Ext.getCmp('pi_whcode')) {
                                code = Ext.getCmp('pi_whcode').value;
                                if (code == null || code == '') {
                                    showError("请先选择仓库!");
                                    t.setHideTrigger(true);
                                    t.setReadOnly(true);
                                } else {
                                    t.dbBaseCondition = "ba_whcode='" + code + "' AND ba_prodcode='" + pr + "'";
                                }
                            } else {
                                t.dbBaseCondition = "ba_prodcode='" + pr + "'";
                            }
                        } else {
                            t.dbBaseCondition = "ba_whcode='" + code + "' AND ba_prodcode='" + pr + "'";
                        }
                    }
                }
            },
            /*'field[name=pi_whcode]': {
    			aftertrigger: function(f){
    				if(f.value != null && f.value != ''){
    					var grid = Ext.getCmp('grid');
    					var whname = Ext.getCmp('pi_whname');
    				    Ext.Array.each(grid.store.data.items, function(item){
    				    	if(item.data['pd_whcode'] == null || item.data['pd_whcode'] == ''){
    				    		item.set('pd_whcode', f.value);
    				    		item.set('pd_whname', whname.value);
    				    	}
    					});
    				}
    			}
    		},
    		'field[name=pi_purpose]': {
    			aftertrigger: function(f){
    				if(f.value != null && f.value != ''){
    					var grid = Ext.getCmp('grid');
    					var posename = Ext.getCmp('pi_purposename');
    				    Ext.Array.each(grid.store.data.items, function(item){
    				    	if(item.data['pd_inwhcode'] == null || item.data['pd_inwhcode'] == ''){
    				    		item.set('pd_inwhcode', f.value);
    				    		if(posename){
    				    			item.set('pd_inwhname', posename.value);	
    				    		}

    				    	}
    					});
    				}
    			}
    		},*/
            'dbfindtrigger[name=pi_paymentcode]': {
                afterrender: function(trigger) {
                    if (trigger.fieldConfig == 'PT') {
                        trigger.dbKey = 'pi_cardcode';
                        if (caller == 'ProdInOut!Sale' || caller == 'ProdInOut!SaleReturn' || caller == 'ProdInOut!SaleBorrow') {
                            trigger.mappingKey = 'cu_code';
                            trigger.dbMessage = '请先选客户编号！';
                        }
                    }
                }
            },
            'dbfindtrigger[name=pi_address]': {
                afterrender: function(trigger) {
                    if (trigger.fieldConfig == 'PT') {
                        trigger.dbKey = 'pi_cardcode';
                        if (caller == 'ProdInOut!Sale' || caller == 'ProdInOut!SaleReturn' || caller == 'ProdInOut!SaleBorrow') {
                            trigger.mappingKey = 'cu_code';
                            trigger.dbMessage = '请先选客户编号！';
                        }
                    }
                }
            },
            'dbfindtrigger[name=pi_custcode2]': {
                afterrender: function(trigger) {
                    trigger.dbKey = 'pi_cardcode';
                    trigger.mappingKey = 'cu_code';
                    trigger.dbMessage = '请先选客户编号！';
                }
            },
            'dbfindtrigger[name=pi_invoiceremark]': {
                afterrender: function(trigger) {
                    trigger.dbKey = 'pi_cardcode';
                    trigger.mappingKey = 'cu_code';
                    trigger.dbMessage = '请先选客户编号！';
                }
            },
            'dbfindtrigger[name=pi_packingremark]': {
                afterrender: function(trigger) {
                    trigger.dbKey = 'pi_cardcode';
                    trigger.mappingKey = 'cu_code';
                    trigger.dbMessage = '请先选客户编号！';
                }
            },
            /*	'dbfindtrigger[name=pi_receivecode]': {
    			afterrender:function(trigger){
    				if (caller == 'ProdInOut!Sale') {
    					trigger.dbKey='pi_cardcode';
    	    			trigger.mappingKey='cu_code';
    	    			trigger.dbMessage='请先选客户编号！';
    				}
    			}
    		},*/ //万利达收货客户可以随意改成其他客户
            'erpGetPriceButton': {
                click: function() {

                }
            },
            //转其它出库单
            'erpTurnOtherOutButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp("pi_statuscode");
                    if (status && status.value != 'POSTED') {
                        btn.hide();
                    }
                },
                click: function(m) {
                    me.batchdeal('ProdIN!ToProdOtherOut!Deal', ' pd_piid=' + Ext.getCmp('pi_id').value + ' and nvl(pd_yqty,0) < nvl(pd_inqty,0) + nvl(pd_outqty,0)', 'scm/reserve/turnDefectOut.action?type=ProdInOut!OtherOut');
                }
            },
            '#erpUpdateDetailWHCode': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pi_invostatuscode'),
                        poststatus = Ext.getCmp('pi_statuscode');
                    if (status && status.value == 'ENTERING') {
                        btn.hide();
                    }
                    if (poststatus && poststatus.value == 'POSTED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    var record = btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();
                    this.UpdateDetailWHCode(record);
                }
            },
            //转出货单
            'OutButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp("pi_statuscode");
                    if (status && status.value != 'POSTED') {
                        btn.hide();
                    }
                },
                click: function(m) {           	
                    me.batchdeal('ProdIN!ToProdOut!Deal', ' pd_piid=' + Ext.getCmp('pi_id').value + ' and nvl(pd_yqty,0) < nvl(pd_inqty,0) + nvl(pd_outqty,0)', 'scm/reserve/turnDefectOut.action?type=ProdInOut!Sale');
                }
            },
            //转换货出库单
            'erpTurnExOutButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp("pi_statuscode");
                    if (status && status.value != 'POSTED') {
                        btn.hide();
                    }
                },
                click: function(m) {                
                    me.batchdeal('ProdInOut!ToExchangeOut!Deal', ' pd_piid=' + Ext.getCmp('pi_id').value + ' and nvl(pd_yqty,0) < nvl(pd_inqty,0) + nvl(pd_outqty,0)', 'scm/reserve/turnDefectOut.action?type=ProdInOut!ExchangeOut');
                }
            },
            //转借货归还单
            'erpTurnOutReturnButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp("pi_statuscode");
                    if (status && status.value != 'POSTED') {
                        btn.hide();
                    }
                },
                click: function(m) {
                    me.batchdeal('ProdIN!ToProdOutReturn!Deal', ' pd_piid=' + Ext.getCmp('pi_id').value + ' and nvl(pd_yqty,0) < nvl(pd_inqty,0) + nvl(pd_outqty,0)', 'scm/reserve/turnDefectIn.action?type=ProdInOut!OutReturn');
                }
            },
            //转用品退仓单
            'erpTurnGoodsOutButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp("pi_statuscode");
                    if (status && status.value != 'POSTED') {
                        btn.hide();
                    }
                },
                click: function(m) {
                    me.batchdeal('ProdIN!ToProdOutGoodsOut!Deal', ' pd_piid=' + Ext.getCmp('pi_id').value + ' and nvl(pd_yqty,0) < nvl(pd_inqty,0) + nvl(pd_outqty,0)', 'scm/reserve/turnDefectIn.action?type=ProdInOut!GoodsShutout');
                }
            },
            'erpTurnMrbButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp("pi_statuscode");
                    if (status && status.value != 'POSTED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.batchdeal('ProdIO!ToMRB!Deal', 'pd_piid=' + Ext.getCmp('pi_id').value + ' AND nvl(pd_yqty,0)<nvl(pd_inqty,0) + nvl(pd_outqty,0)', 'scm/reserve/turnMRB.action');
                }
            },
            'erpSubpackageButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pi_invostatuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    warnMsg("确定分装?", function(btn) {
                        if (btn == 'yes') {
                            me.FormUtil.setLoading(true); //loading...
                            Ext.Ajax.request({
                                url: basePath + 'scm/reserve/Subpackage.action',
                                params: {
                                    id: Ext.getCmp('pi_id').value
                                },
                                method: 'post',
                                callback: function(opt, s, r) {
                                    me.FormUtil.setLoading(false);
                                    var rs = Ext.decode(r.responseText);
                                    if (rs.exceptionInfo) {
                                        showError(rs.exceptionInfo);
                                    } else {
                                        if (rs.log)
                                            showMessage('提示', rs.log);
                                    }
                                }
                            });
                        }
                    });
                }
            },
            'erpClearSubpackageButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pi_invostatuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    warnMsg("确定清除分装?", function(btn) {
                        if (btn == 'yes') {
                            me.FormUtil.setLoading(true); //loading...
                            Ext.Ajax.request({
                                url: basePath + 'scm/reserve/ClearSubpackage.action',
                                params: {
                                    id: Ext.getCmp('pi_id').value
                                },
                                method: 'post',
                                callback: function(opt, s, r) {
                                    me.FormUtil.setLoading(false);
                                    var rs = Ext.decode(r.responseText);
                                    if (rs.exceptionInfo) {
                                        showError(rs.exceptionInfo);
                                    } else {
                                        if (rs.log)
                                            showMessage('提示', rs.log);
                                    }
                                }
                            });
                        }
                    });
                }
            },
            'erpPrintBarButton': {
                click: function(btn) {
                    var reportName = "bar_53";
                    var condition = '{ProdIODetailBar.pdb_inoutno}=' + "'" + Ext.getCmp('pi_inoutno').value + "'";
                    var id = Ext.getCmp('pi_id').value;
                    me.FormUtil.onwindowsPrint(id, reportName, condition);
                }
            },
            'erpUpdateInfoButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pi_invostatuscode'),
                        poststatus = Ext.getCmp('pi_statuscode');
                    if (status && status.value == 'ENTERING') {
                        btn.hide();
                    }
                    if (poststatus && poststatus.value == 'POSTED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    var me = this,
                        win = Ext.getCmp('borrowCargoType-win');
                    if (!win) {
                        var type = Ext.getCmp('pi_outtype'),
                            remark = Ext.getCmp('pi_remark'),
                            val1 = type ? type.value : '',
                            val2 = remark ? remark.value : '';
                        win = Ext.create('Ext.Window', {
                            id: 'borrowCargoType-win',
                            title: '更新借货出货单 ' + Ext.getCmp('pi_inoutno').value + ' 的借货类型',
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
                                    fieldLabel: '借货类型',
                                    name: 'pi_outtype',
                                    allowBlank: false,
                                    value: val1
                                }, {
                                    margin: '3 0 0 0',
                                    xtype: 'textfield',
                                    name: 'pi_remark',
                                    fieldLabel: '备注',
                                    value: val2
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
                                            a = form.down('dbfindtrigger[name=pi_outtype]'),
                                            b = form.down('textfield[name=pi_remark]');
                                        if (form.getForm().isDirty()) {
                                            me.updateInfo(Ext.getCmp('pi_id').value, a.value, b.value);
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
                    }
                    win.show();
                }
            },
            'erpZxbzsBarcodeButton': { //按最小包装数生成条码
                afterrender: function(btn) {
                    var poststatus = Ext.getCmp('pi_statuscode');
                    if (poststatus && poststatus.value == 'POSTED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    warnMsg("确定生成?", function(btn) {
                        if (btn == 'yes') {
                            me.FormUtil.setLoading(true); //loading...
                            Ext.Ajax.request({
                                url: basePath + 'scm/reserve/GenerateBarcodeByZxbzs.action',
                                params: {
                                    pi_id: Ext.getCmp('pi_id').value,
                                    pi_class: Ext.getCmp("pi_class").value,
                                    caller: caller
                                },
                                method: 'post',
                                callback: function(opt, s, r) {
                                    me.FormUtil.setLoading(false);
                                    var rs = Ext.decode(r.responseText);
                                    if (rs.exceptionInfo) {
                                        showError(rs.exceptionInfo);
                                    } else {
                                        Ext.MessageBox.alert('系统提示', '按最小包装数批量生成条码成功!');
                                    }
                                }
                            });
                        }
                    });
                }
            },
            'erpPrintAllButton': {
                afterrender: function(btn) {
                    btn.setText('打印全部条码');
                },
                click: function(btn) {
                    var lps_barcaller = 'ProdIO!BarPrintAll';
                    var win = new Ext.window.Window({
                        id: 'win',
                        maximizable: true,
                        buttonAlign: 'center',
                        layout: 'anchor',
                        title: '打印模板选择',
                        modal: true,
                        items: [{
                            tag: 'iframe',
                            frame: true,
                            anchor: '100% 100%',
                            layout: 'fit',
                            html: '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/scm/reserve/selPrintTemplate.jsp?whoami=' + lps_barcaller + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
                        }]
                    });
                    win.show();
                }
            },
            'erpCatchBatchByOrderButton': {
                click: function(btn) {
                    var grid = btn.ownerCt.ownerCt;
                    var record = grid.getSelectionModel().getLastSelected();
                    var id = Ext.getCmp("pi_id").value;
                    //选择明细行按订单抓取批号
                    Ext.Ajax.request({
                        url: basePath + '/scm/reserve/catchBatchByOrder.action',
                        params: {
                            pd_id: record.data.pd_id,
                            pd_piid: record.data.pd_piid,
                            caller: caller
                        },
                        method: 'post',
                        callback: function(options, success, response) {
                            var res = new Ext.decode(response.responseText);
                            if (res.exceptionInfo) {
                                showError(res.exceptionInfo);
                            } else if (res.success) {
                                showMessage('提示', '按订单号抓取批次成功!', 1000);
                                grid.getData("pd_piid=" + id);
                            }
                        }
                    });
                },
                afterrender: function(btn) {
                    btn.setDisabled(true);
                }
            },
            'erpCatchBatchByClientButton': {
                afterrender: function(btn) {
                    var poststatus = Ext.getCmp('pi_statuscode');
                    if (poststatus && poststatus.value == 'POSTED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                	var grid = Ext.getCmp('grid');
                    Ext.Ajax.request({
                        url: basePath + 'scm/reserve/catchBatchByClient.action',
                        params: {
                        	type : 'ByClient',
                            pi_id: Ext.getCmp('pi_id').value,
                            caller: caller
                        },
                        method: 'post',
                        callback: function(options, success, response) {
                            var res = new Ext.decode(response.responseText);
                            if (res) {
                                if (res.exceptionInfo) {
                                    showMessage('提示', res.exceptionInfo);
                                } else {
                                    if (res.log == null || res.log == ''){
                                    	grid.getData(gridCondition);
                                    	showMessage('提示', '按委托方抓取成功');
                                    }
                                }
                            } else {
                                showMessage('提示', '按委托方抓取失败');
                            }

                        }
                    });
                }
            },
            'erpCatchBatchByCustButton': {
                afterrender: function(btn) {
                    var poststatus = Ext.getCmp('pi_statuscode');
                    if (poststatus && poststatus.value == 'POSTED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                	var grid = Ext.getCmp('grid');
                    Ext.Ajax.request({
                        url: basePath + 'scm/reserve/catchBatchByClient.action',
                        params: {
                        	type : 'ByCust',
                            pi_id: Ext.getCmp('pi_id').value,
                            caller: caller
                        },
                        method: 'post',
                        callback: function(options, success, response) {
                            var res = new Ext.decode(response.responseText);
                            if (res) {
                                if (res.exceptionInfo) {
                                    showMessage('提示', res.exceptionInfo);
                                } else {
                                    if (res.log == null || res.log == ''){
                                    	grid.getData(gridCondition);
                                    	showMessage('提示', '按客户抓取成功');
                                    }
                                }
                            } else {
                                showMessage('提示', '按客户抓取失败');
                            }

                        }
                    });
                }
            },
            'field[name=pi_packingcode]':{
  			   afterrender:function(f){
  				   f.setFieldStyle({
  					   'color': 'blue'
  				   });
  				   f.focusCls = 'mail-attach';
  				   var c = Ext.Function.bind(me.openPacking, me);
  				   Ext.EventManager.on(f.inputEl, {
  					   mousedown : c,
  					   scope: f,
  					   buffer : 100
  				   });
  			   }
 			},
 			'field[name=pi_invoicecode]':{
   			   afterrender:function(f){
   				   f.setFieldStyle({
   					   'color': 'blue'
   				   });
   				   f.focusCls = 'mail-attach';
   				   var c = Ext.Function.bind(me.openInvoice, me);
   				   Ext.EventManager.on(f.inputEl, {
   					   mousedown : c,
   					   scope: f,
   					   buffer : 100
   				   });
   			   }
  			},
            'erpTurnPaInButton': {
            	afterrender: function(btn) {
                    var pi_invoicecode = Ext.getCmp('pi_invoicecode'), pi_packingcode = Ext.getCmp('pi_packingcode'),
                    	pi_invostatuscode = Ext.getCmp('pi_invoicecode'), pi_class = Ext.getCmp('pi_class').value;
                    if(pi_class !='销售退货单' && pi_class !='出货单'){
                    	 btn.hide();
                    }
                    if (pi_invoicecode && !Ext.isEmpty(pi_invoicecode.value)) {
                        btn.hide();
                    }
                    if (pi_packingcode && !Ext.isEmpty(pi_packingcode.value)) {
                        btn.hide();
                    }
                    if (pi_class =='销售退货单' && pi_invostatuscode.value !='AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                	var grid = Ext.getCmp('grid');
                	warnMsg("确定要生成发票箱单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/reserve/turnPackInvo.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('pi_id').value,
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
    	    		    				showMessage("localJson", localJson.log);
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
                }
            }
        });
    },
    getForm: function(btn) {
        return btn.ownerCt.ownerCt;
    },
    save: function(btn) {
        var me = this;
        var bool = true;
        var form = me.getForm(btn);
        if (Ext.getCmp('Fin_Code')) {
            Ext.getCmp('Fin_Code').setValue(Ext.getCmp(form.codeField).value); //流水号
        }
        var grid = Ext.getCmp('grid'),
            items = grid.store.data.items,
            c = Ext.getCmp('pi_inoutno').value;
        var piclass = Ext.getCmp('pi_class').value,
            date = Ext.getCmp('pi_date').value;
        Ext.Array.each(items, function(item) {
            if (!Ext.isEmpty(item.data['pd_prodcode'])) {
               /* item.set('pd_inoutno', c);
                item.set('pd_piclass', piclass);*/
            	item.data['pd_inoutno']=c;
            	item.data['pd_piclass']=piclass;
            }
        });
        if (caller == 'ProdInOut!OtherPurcIn') {
            Ext.each(items, function(item) {
                if (!Ext.isEmpty(item.data['pd_ordercode'])) {
                    if (Ext.Date.format(item.data['pd_vendorreplydate'], 'Ymd') < Ext.Date.format(new Date(), 'Ymd')) {
                        bool = false;
                        showError('明细表第' + item.data['pd_pdno'] + '还料日期小于系统当前日期');
                        return false;
                    }
                }
            });
        }
        if (caller == 'ProdInOut!PurcCheckout') {
            Ext.each(items, function(item) {
                if (!Ext.isEmpty(item.data['pd_ordercode'])) {
                    if (item.data['pd_outqty'] > item.data['pd_acceptqty']) {
                        bool = false;
                        showError('明细表第' + item.data['pd_pdno'] + '数量大于采购单的验收数量');
                        return false;
                    }
                }
            });
        }
        if (caller == 'ProdInOut!AppropriationOut') {
            var recorder = Ext.getCmp('pi_recordman');
            if (recorder) {
                Ext.Array.each(items, function(item) {
                    if (!Ext.isEmpty(item.data['pd_prodcode'])) {
                        if (Ext.isEmpty(item.data['pd_seller'])) {
                            /*item.set('pd_seller', recorder.value);*/
                        	item.data['pd_seller']=recorder.value;
                        }
                    }
                });
            }
        }
        if (caller != 'ProdInOut!AppropriationOut' && caller != 'ProdInOut!AppropriationIn') {
            var whcode = Ext.getCmp('pi_whcode'),
                whname = Ext.getCmp('pi_whname');
            if (whcode && whname) {
                Ext.Array.each(items, function(item) {
                    if (!Ext.isEmpty(item.data['pd_prodcode'])) {
                        if (Ext.isEmpty(item.data['pd_whcode'])) {
                           /* item.set('pd_whcode', whcode.value);
                            item.set('pd_whname', whname.value);*/
                        	item.data['pd_whcode']=whcode.value;
                        	item.data['pd_whname']=whname.value;
                        }
                        if (caller == 'ProdInOut!OtherIn' || caller == 'ProdInOut!OtherPurcIn') {
                            if (item.data['pd_unitpackage'] == null || item.data['pd_unitpackage'] == '' ||
                                item.data['pd_unitpackage'] == '0' || item.data['pd_unitpackage'] == 0) {
                                /*item.set('pd_unitpackage', item.data['pd_inqty']);*/
                            	item.data['pd_unitpackage']=item.data['pd_inqty'];
                            }
                        }
                    }
                });
            }
        }
        if (caller == 'ProdInOut!SaleAppropriationOut' || 'ProdInOut!OtherOut' == caller) {
            Ext.Array.each(items, function(item) {
                if (!Ext.isEmpty(item.data['pd_plancode'])) {
                    if (item.data['pd_outqty'] > item.data['pd_sfdqty']) {
                        bool = false;
                        showError('明细表第' + item.data['pd_pdno'] + '数量大于销售预测单数量');
                        return false;
                    }
                }
                if (!Ext.isEmpty(item.data['pd_ordercode'])) {
                    if (item.data['pd_outqty'] > item.data['sd_qty']) {
                        bool = false;
                        showError('明细表第' + item.data['pd_pdno'] + '数量大于销售单数量');
                        return false;
                    }
                }
            });
        }
        if (caller == 'ProdInOut!SaleAppropriationOut' || 'ProdInOut!AppropriationOut' == caller) {
            var pi_purpose = Ext.getCmp('pi_purpose'),
                pi_purposename = Ext.getCmp('pi_purposename');
            var pi_whcode = Ext.getCmp('pi_whcode'),
                pi_whname = Ext.getCmp('pi_whname');
            if (pi_whcode && pi_whname) {
                Ext.Array.each(items, function(item) {
                    if (!Ext.isEmpty(item.data['pd_prodcode'])) {
                        if (Ext.isEmpty(item.data['pd_whcode'])) {
                            /*item.set('pd_whcode', pi_whcode.value);
                            item.set('pd_whname', pi_whname.value);*/
                        	item.data['pd_whcode']=pi_whcode.value;
                        	item.data['pd_whname']=pi_whname.value;
                        }
                    }
                });
            }
            if (pi_purpose && pi_purposename) {
                Ext.Array.each(items, function(item) {
                    if (!Ext.isEmpty(item.data['pd_prodcode'])) {
                        if (Ext.isEmpty(item.data['pd_inwhcode'])) {
                            /*item.set('pd_inwhcode', pi_purpose.value);
                            item.set('pd_inwhname', pi_purposename.value);*/
                        	item.data['pd_inwhcode']=pi_purpose.value;
                        	item.data['pd_inwhname']=pi_purposename.value;
                        }
                    }
                });
            }
        }
        // 拨入单、拨出单，默认第一行的仓库
        me.setDetailWarehouse(grid);
        if (caller == 'ProdInOut!Make!Return' || caller == 'ProdInOut!OutsideReturn' || caller == 'ProdInOut!Make!Useless') {
            var firstItem = grid.store.getAt(0);
            if (firstItem) {
                var desc = firstItem.get('pd_description'),
                    dc = firstItem.get('pd_departmentcode'),
                    dn = firstItem.get('pd_departmentname'),
                    whcode = firstItem.get('pd_whcode'),
                    whname = firstItem.get('pd_whname'),
                    pd_textbox = firstItem.get('pd_textbox');
                Ext.Array.each(items, function(item) {
                    if (!Ext.isEmpty(item.data['pd_prodcode'])) {
                        if (Ext.isEmpty(item.data['pd_description'])) {
                            /*item.set('pd_description', desc);*/
                        	item.data['pd_description']=desc;
                            
                        }
                        if (Ext.isEmpty(item.data['pd_departmentcode'])) {
                            /*item.set('pd_departmentcode', dc);
                            item.set('pd_departmentname', dn);*/
                        	item.data['pd_departmentcode']=dc;
                        	item.data['pd_departmentname']=dn;
                        }
                        if (Ext.isEmpty(item.data['pd_whcode'])) {
                          /* item.set('pd_whcode', whcode);
                            item.set('pd_whname', whname);*/
                            item.data['pd_whcode']=whcode;
                            item.data['pd_whname']=whname
                        }
                        if (caller == 'ProdInOut!Make!Useless') {
                            if (Ext.isEmpty(item.data['pd_textbox'])) {
                                /*item.set('pd_textbox', pd_textbox);*/
                            	item.data['pd_textbox']=pd_textbox;
                            }
                        }
                    }
                });
            }
        }
        if (bool)
            me.FormUtil.beforeSave(me);
    },
    setDetailWarehouse: function(grid) {
        var me = this;
        if ('ProdInOut!AppropriationIn' == caller || 'ProdInOut!AppropriationOut' == caller || 'ProdInOut!SaleAppropriationOut' == caller) {
            var firstItem = grid.store.getAt(0);
            if (firstItem) {
                var whcode = firstItem.get('pd_whcode'),
                    whname = firstItem.get('pd_whname'),
                    inwhcode = firstItem.get('pd_inwhcode'),
                    inwhname = firstItem.get('pd_inwhname');
                grid.store.each(function() {
                    if (!me.GridUtil.isBlank(grid, this.data)) {
                        if (!Ext.isEmpty(this.get('pd_prodcode'))) {
                            if (Ext.isEmpty(this.get('pd_whcode'))) {
                                this.set('pd_whcode', whcode);
                                this.set('pd_whname', whname);
                            }
                            if (Ext.isEmpty(this.get('pd_inwhcode'))) {
                                this.set('pd_inwhcode', inwhcode);
                                this.set('pd_inwhname', inwhname);
                            }
                        }
                    }
                });
            }
        }
    },
    /**
     *修改明细仓库
     * */
    UpdateDetailWHCode: function(record) {
        var me = this;
        var win = Ext.create('Ext.window.Window', {
            width: 430,
            height: 250,
            closeAction: 'destroy',
            title: '<h1>修改仓库信息</h1>',
            layout: {
                type: 'vbox'
            },
            items: [{
                margin: '5 0 0 5',
                xtype: 'dbfindtrigger',
                fieldLabel: '仓库编号',
                name: 'whcode',
                value: record.data.pd_whcode,
                id: 'whcode'
            }, {
                margin: '5 0 0 5',
                xtype: 'textfield',
                fieldLabel: '仓库名称',
                name: 'whname',
                value: record.data.pd_whname,
                id: 'whname'
            }, {
                margin: '5 0 0 5',
                xtype: 'checkbox',
                fieldLabel: '是否修改所有明细',
                labelWidth: 120,
                name: 'isalldetail',
                id: 'isalldetail'
            }],
            buttonAlign: 'center',
            buttons: [{
                xtype: 'button',
                text: '保存',
                width: 60,
                iconCls: 'x-button-icon-save',
                handler: function(btn) {
                    var w = btn.up('window');
                    me.saveWhInfo(w);
                    win.close();
                    win.destroy();
                }
            }, {
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
    saveWhInfo: function(w) {
        var whcode = w.down('field[name=whcode]').getValue();
        var isalldetail = w.down('field[name=isalldetail]').getValue();
        grid = Ext.getCmp('grid'),
            record = grid.getSelectionModel().getLastSelected();
        if (!whcode) {
            showError('请先设置仓库信息!');
            return;
        } else {
            var dd = {
                whcode: whcode,
                whname: w.down('field[name=whname]').getValue(),
                pd_id: record.data.pd_id,
                isalldetail: isalldetail
            };
            Ext.Ajax.request({
                url: basePath + 'scm/reserve/updateWhCodeInfo.action',
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
    /**
     * pd_orderdetno的限制条件
     */
    getBaseCondition: function() {
        var field = null;
        switch (caller) {
            case 'ProdInOut!PurcCheckin': //采购验收单
                field = "pd_code";
                break;
            case 'ProdInOut!PurcCheckout': //采购验退单
                field = "pd_code";
                break;
            case 'ProdInOut!Sale': //出货单
                field = "sd_code";
                break;
            case 'ProdInOut!Make!Return': //生产退料单
                field = "mm_code";
                break;
            case 'ProdInOut!Make!Give': //生产补料单
                field = "mm_code";
                break;
            case 'ProdInOut!Picking': //生产领料单
                field = "mm_code";
                break;
            case 'ProdInOut!Make!Consume': //生产耗料单
                field = "mm_code";
                break;
            case 'ProdInOut!Make!Useless': //生产报废单
                field = "mm_code";
                break;
            case 'ProdInOut!SaleAppropriationOut': //销售拨出单
                field = "sd_code";
                break;
            case 'ProdInOut!SaleReturn': //销售退货单
                field = "sd_code";
                break;
            case 'ProdInOut!OtherOut': //其它出库单
                field = "sd_code";
                break;
            case 'ProdInOut!OutsidePicking': //委外领料单
                field = "ma_code";
                break;
            case 'ProdInOut!OutsideReturn': //委外退料单
                field = "mm_code";
                break;
            case 'ProdInOut!DefectIn': //不良品入库单
                field = "pd_code";
                break;
            case 'ProdInOut!DefectOut': //不良品入库单
                field = "pd_code";
                break;
            case 'ProdInOut!OutsideCheckIn': //委外验收单
                field = "mm_code";
                break;
            case 'ProdInOut!OSMake!Give': //委外补料单
                field = "mm_code";
                break;
            case 'ProdInOut!SampleSale': //样品出货单
                field = "sd_code";
                break;
            case 'ProdInOut!SampleMake!Give': //工程补料单
                field = "mm_code";
                break;
            case 'ProdInOut!SamplePicking': //工程领料单
                field = "mm_code";
                break;
            case 'ProdInOut!PurcCheckin!PLM': //研发采购验收单
                field = "pd_code";
                break;
            case 'ProdInOut!PurcCheckout!PLM': //研发采购验退单
                field = "pd_code";
                break;
            case 'ProdInOut!PartitionStockIn': //拆件入库单
                field = "mm_code";
                break;
        }
        return field;
    },
    /**
     * pd_ordercode的限制条件
     */
    updatepdscaleremark: function(record) {
        var win = Ext.create('Ext.Window', {
            id: 'win',
            title: '发货比例信息维护',
            height: 300,
            width: 400,
            items: [{
                margin: '3 0 0 0',
                id: 'pd_scaleremark',
                xtype: 'textfield',
                fieldLabel: '比例备注',
                name: 'pd_scaleremark',
                value: record.data.pd_scaleremark
            }],
            closeAction: 'destory',
            buttonAlign: 'center',
            layout: {
                type: 'vbox',
                align: 'center'
            },
            buttons: [{
                text: '保存信息',
                cls: 'x-btn-blue',
                handler: function(btn) {
                    var id = record.data.pd_id;
                    Ext.Ajax.request({
                        url: basePath + 'scm/sale/updatepdscaleremark.action',
                        params: {
                            id: id,
                            field: 'pd_id',
                            data: Ext.getCmp('pd_scaleremark').value
                        },
                        callback: function(opt, s, r) {
                            var rs = Ext.decode(r.responseText);
                            if (rs.exceptionInfo) {
                                showMessage('提示', rs.exceptionInfo.replace('AFTERSUCCESS', ''));
                            } else {
                                saveSuccess(function() {
                                    window.location.reload();
                                });
                            }
                        }
                    });
                }
            }, {
                text: '保存全部',
                cls: 'x-btn-blue',
                handler: function(btn) {
                    var id = record.data.pd_piid;
                    Ext.Ajax.request({
                        url: basePath + 'scm/sale/updatepdscaleremark.action',
                        params: {
                            id: id,
                            field: 'pd_piid',
                            data: Ext.getCmp('pd_scaleremark').value
                        },
                        callback: function(opt, s, r) {
                            var rs = Ext.decode(r.responseText);
                            if (rs.exceptionInfo) {
                                showMessage('提示', rs.exceptionInfo.replace('AFTERSUCCESS', ''));
                            } else {
                                saveSuccess(function() {
                                    window.location.reload();
                                });
                            }
                        }
                    });
                }
            }, {
                text: $I18N.common.button.erpCloseButton,
                cls: 'x-btn-blue',
                handler: function(btn) {
                	var window = btn.ownerCt.ownerCt;
                	window.close();
                	window.destroy();
                }
            }]
        });
        win.show();
    },
    getCodeCondition: function() {
        var field = null;
        var fields = '';
        var tablename = '';
        var myfield = '';
        var tFields = '';
        switch (caller) {
            case 'ProdInOut!PurcCheckin': //采购验收单
                field = "pu_vendcode";
                tFields = 'pi_cardid,pi_cardcode,pi_title,pi_currency,pi_rate,pi_payment,pi_paymentcode,pi_transport,pi_paydate,pi_receivecode,pi_receivename';
                fields = 'pu_vendid,pu_vendcode,pu_vendname,pu_currency,pu_rate,pu_payments,pu_paymentscode,pu_transport,pu_suredate,pu_receivecode,pu_receivename';
                tablename = 'Purchase';
                myfield = 'pu_code';
                break;
            case 'ProdInOut!PurcCheckout': //采购验退单
                field = "pu_vendcode";
                tFields = 'pi_cardid,pi_cardcode,pi_title,pi_currency,pi_rate,pi_payment,pi_paymentcode,pi_transport,pi_paydate,pi_receivecode,pi_receivename';
                fields = 'pu_vendid,pu_vendcode,pu_vendname,pu_currency,pu_rate,pu_payments,pu_paymentscode,pu_transport,pu_suredate,pu_receivecode,pu_receivename';
                tablename = 'Purchase';
                myfield = 'pu_code';
                break;
            case 'ProdInOut!Sale': //出货单
                tFields = 'pi_cardid,pi_cardcode,pi_title,pi_currency,pi_rate,pi_payment,pi_paymentcode,pi_transport,pi_sellercode,pi_belongs,pi_receivecode,pi_receivename,pi_transport';
                fields = 'sa_custid,sa_custcode,sa_custname,sa_currency,sa_rate,sa_payments,sa_paymentscode,sa_transport,sa_sellercode,sa_seller,sa_shcustcode,sa_shcustname,sa_transport';
                tablename = 'Sale';
                myfield = 'sa_code';
                field = "sa_custcode";
                break;
            case 'ProdInOut!AppropriationIn': //拨入单
                tFields = 'pi_cardcode,pi_title';
                fields = 'ma_custcode,ma_custname';
                tablename = 'Make';
                myfield = 'ma_code';
                field = "ma_custcode";
                break;
            case 'ProdInOut!SaleAppropriationOut': //销售拨出单
                tFields = 'pi_cardid,pi_cardcode,pi_title';
                fields = 'sa_custid,sa_custcode,sa_custname';
                tablename = 'Sale';
                myfield = 'sa_code';
                field = "sa_custcode";
                break;
            case 'ProdInOut!SaleReturn': //销售退货单
                tFields = 'pi_cardid,pi_cardcode,pi_title,pi_currency,pi_rate,pi_paymentcode,pi_payment,pi_transport,pi_sellercode,pi_belongs,pi_receivecode,pi_receivename,pi_transport';
                fields = 'sa_custid,sa_custcode,sa_custname,sa_currency,sa_rate,sa_paymentscode,sa_payments,sa_transport,sa_sellercode,sa_seller,sa_shcustcode,sa_shcustname,sa_transport';
                tablename = 'Sale';
                myfield = 'sa_code';
                field = "sa_custcode";
                break;
            case 'ProdInOut!AppropriationOut': //拨出单
                tFields = 'pi_cardcode,pi_title';
                fields = 'ma_custcode,ma_custname';
                tablename = 'Make';
                myfield = 'ma_code';
                field = "ma_custcode";
                break;
            case 'ProdInOut!DefectIn': //不良品入库单
                field = "pu_vendcode";
                tFields = 'pi_cardid,pi_cardcode,pi_title,pi_currency,pi_rate,pi_payment,pi_transport,pi_paydate';
                fields = 'pu_vendid,pu_vendcode,pu_vendname,pu_currency,pu_rate,pu_payments,pu_transport,pu_suredate';
                tablename = 'Purchase';
                myfield = 'pu_code';
                break;
            case 'ProdInOut!DefectOut': //不良品出库单
                field = "pu_vendcode";
                tFields = 'pi_cardid,pi_cardcode,pi_title,pi_currency,pi_rate,pi_payment,pi_transport,pi_paydate';
                fields = 'pu_vendid,pu_vendcode,pu_vendname,pu_currency,pu_rate,pu_payments,pu_transport,pu_suredate';
                tablename = 'Purchase';
                myfield = 'pu_code';
                break;
            case 'ProdInOut!OutsidePicking': //委外领料单
                field = "ma_vendcode";
                tFields = 'pi_cardcode,pi_title,pi_departmentcode,pi_departmentname';
                fields = 'ma_vendcode,ma_vendname,ma_departmentcode,ma_departmentname';
                tablename = 'Make';
                myfield = 'ma_code';
                break;
            case 'ProdInOut!OutsideReturn': //委外退料单
                field = "ma_vendcode";
                tFields = 'pi_cardcode,pi_title,pi_departmentcode,pi_departmentname';
                fields = 'ma_vendcode,ma_vendname,ma_departmentcode,ma_departmentname';
                tablename = 'Make';
                myfield = 'ma_code';
                break;
            case 'ProdInOut!OutsideCheckIn': //委外验收单
                field = "ma_vendcode";
                tFields = 'pi_cardcode,pi_title,pi_departmentcode,pi_departmentname';
                fields = 'ma_vendcode,ma_vendname,ma_departmentcode,ma_departmentname';
                tablename = 'Make';
                myfield = 'ma_code';
                break;
            case 'ProdInOut!OutesideCheckReturn': //委外验退单
                field = "ma_vendcode";
                tFields = 'pi_cardcode,pi_title,pi_departmentcode,pi_departmentname,pi_currency,pi_rate';
                fields = 'ma_vendcode,ma_vendname,ma_departmentcode,ma_departmentname,ma_currency,ma_rate';
                tablename = 'Make';
                myfield = 'ma_code';
                break;
            case 'ProdInOut!OutReturn': //借货归还单
                tFields = 'pi_sellercode,pi_belongs,pi_cop,pi_shr,pi_expresscode,pi_emcode,pi_emname,pi_address';
                fields = 'pi_emcode,pi_emname,pi_cop,pi_shr,pi_expresscode,pi_emcode,pi_emname,pi_address';
                tablename = 'ProdInOut';
                myfield = 'pi_inoutno';
                break;
            case 'ProdInOut!PurcCheckin!PLM': //研发采购验收单
                field = "pu_vendcode";
                tFields = 'pi_cardid,pi_cardcode,pi_title,pi_currency,pi_rate,pi_payment,pi_transport,pi_paydate,pi_receivecode,pi_receivename';
                fields = 'pu_vendid,pu_vendcode,pu_vendname,pu_currency,pu_rate,pu_payments,pu_transport,pu_suredate,pu_receivecode,pu_receivename';
                tablename = 'Purchase';
                myfield = 'pu_code';
                break;
            case 'ProdInOut!PurcCheckout!PLM': //研发采购验退单
                field = "pu_vendcode";
                tFields = 'pi_cardid,pi_cardcode,pi_title,pi_currency,pi_rate,pi_payment,pi_transport,pi_paydate,pi_receivecode,pi_receivename';
                fields = 'pu_vendid,pu_vendcode,pu_vendname,pu_currency,pu_rate,pu_payments,pu_transport,pu_suredate,pu_receivecode,pu_receivename';
                tablename = 'Purchase';
                myfield = 'pu_code';
                break;
            case 'ProdInOut!ExchangeOut': //换货出库单
                tFields = 'pi_cardid,pi_cardcode,pi_title,pi_currency,pi_rate,pi_payment,pi_transport,pi_sellercode,pi_belongs,pi_receivecode,pi_receivename,pi_transport';
                fields = 'sa_custid,sa_custcode,sa_custname,sa_currency,sa_rate,sa_payments,sa_transport,sa_sellercode,sa_seller,sa_shcustcode,sa_shcustname,sa_transport';
                tablename = 'Sale';
                myfield = 'sa_code';
                field = "sa_custcode";
                break;
            case 'ProdInOut!GoodsOut': //用品验退单
                tFields = 'pi_cardid,pi_cardcode,pi_title,pi_payment,pi_receivecode,pi_receivename,pi_paymentcode';
                fields = 've_id,ve_code,ve_name,ve_payment,ve_apvendcode,ve_apvendname,ve_paymentcode';
                tablename = 'Vendor left join Oapurchase on op_vecode=ve_code';
                myfield = 'op_code';
                field = "op_vecode";
                break;
        }
        var obj = new Object();
        obj.field = field;
        obj.fields = fields;
        obj.tFields = tFields;
        obj.tablename = tablename;
        obj.myfield = myfield;
        return obj;
    },
    /**
     * 有来源不能新增明细
     */
    hasSource: function(grid) {
        var bool = false,
            field = null;
        switch (caller) {
            //	    	   case 'ProdInOut!Sale'://出货单
            //	    	   field = 'pd_snid';
            //	    	   break;
            case 'ProdInOut!PurcCheckin': //采购验收单
                field = 'pd_qcid';
                break;
            case 'ProdInOut!OutsideCheckIn': //委外验收单
                field = 'pd_qcid';
                break;
            case 'ProdInOut!DefectIn': //不良品入库单
                field = 'pd_qcid';
                break;
        }
        if (field != null) {
            var s = null;
            grid.store.each(function(item) {
                s = item.get(field);
                if (s != null && s != '' && s > 0) {
                    bool = true;
                    return;
                }
            });
        }
        return bool;
    },
    isAllowUpdateQty: function(record) {
        var bool = true;
        switch (caller) {
            case 'ProdInOut!PurcCheckin': //采购验收单
                if (record.get('pd_qcid') != null && record.get('pd_qcid') > 0)
                    bool = false;
                break;
            case 'ProdInOut!OutsideCheckIn': //委外验收单
                if (record.get('pd_qcid') != null && record.get('pd_qcid') > 0)
                    bool = false;
                break;
            case 'ProdInOut!DefectIn': //不良品入库单
                if (record.get('pd_qcid') != null && record.get('pd_qcid') > 0)
                    bool = false;
                break;
            case 'ProdInOut!Make!In': //完工入库单
                if (record.get('pd_qcid') != null && record.get('pd_qcid') > 0)
                    bool = false;
                break;
        }
        return bool;
    },
    onPrint: function() {
        var me = this,
            whichKind = Ext.getCmp('pi_class').value;
        var reportName = '';
        if (whichKind == "出货单") {
            reportName = "sendlist_yessale";
        } else if (whichKind == "无订单出货单") {
            reportName = "sendlist_nosale";
        } else if (whichKind == "不良品入库单") {
            reportName = "pio_notokin";
        } else if (whichKind == "不良品出库单") {
            reportName = "pio_notokout";
        } else if (whichKind == "其它采购入库单") {
            reportName = "piolist_opin";
        } else if (whichKind == "其它采购出库单") {
            reportName = "piolist_opout";
        } else if (whichKind == "拨入单") {
            reportName = "piolist_br";
        } else if (whichKind == "拨出单") {
            reportName = "piolist_bc";
        } else if (whichKind == "其它入库单") {
            reportName = "piolist_in";
        } else if (whichKind == "其它出库单") {
            reportName = "piolist_out";
        } else if (whichKind == "报废单") {
            reportName = "piolist_bf";
        } else if (whichKind == "换货入库单") {
            reportName = "pio_changein";
        } else if (whichKind == "换货出库单") {
            reportName = "pio_changeout";
        } else if (whichKind == "销售退货单") {
            reportName = "retulist";
        } else if (whichKind == "采购验收单") {
            reportName = "acclist";
        } else if (whichKind == "采购验退单") {
            reportName = "piolist_yt";
        } else if (whichKind == "销售拨入单") {
            reportName = "piolist";
        } else if (whichKind == "销售拨出单") {
            reportName = "piolist_salebc";
        } else if (whichKind == "生产领料单") {
            reportName = "PIOLISTM";
        } else if (whichKind == "生产退料单") {
            reportName = "PIOLISTM_Back";
        } else if (whichKind == "完工入库单") {
            reportName = "finish";
        } else if (whichKind == "结余退料单") {
            reportName = "PIOLISTM_JY";
        } else if (whichKind == "拆件入库单") {
            reportName = "chaijian";
        } else if (whichKind == "生产补料单") {
            reportName = "PIOLIST_bl";
        } else if (whichKind == "生产耗料单") {
            reportName = "PIOLISTM_HL";
        } else if (whichKind == "委外领料单") {
            reportName = "Expiolist";
        } else if (whichKind == "委外退料单") {
            reportName = "PIOLIST_wwtl";
        } else if (whichKind == "委外验收单") {
            reportName = "EXPLIST_ys";
        } else if (whichKind == "委外验退单") {
            reportName = "EXPLIST_yt";
        } else if (whichKind == "生产报废单") {
            reportName = "MakeScrap";
        } else if (whichKind == "无订单退货单") {
            reportName = "retulist_nosale";
        } else if (whichKind == "委外补料单") {
            reportName = "Expiolist";
        } else if (whichKind == "盘盈调整单") {
            reportName = "piolist_tz";
        } else if (whichKind == "盘亏调整单") {
            reportName = "piolist_tz";
        } else if (whichKind == "辅料入库单") {
            reportName = "piolist_flr";
        } else if (whichKind == "辅料出库单") {
            reportName = "piolist_flc";
        } else if (whichKind == "研发领料单") {
            reportName = "piolist_yfll";
        } else if (whichKind == "研发退料单") {
            reportName = "piolist_yftl";
        } else if (whichKind == "借货出货单") {
            reportName = "sendlist_jh";
        } else if (whichKind == "借货归还单") {
            reportName = "sendlist_jhgh";
        } else if (whichKind == "用品验收单") {
            reportName = "piolist_ypys";
        } else if (whichKind == "用品验退单") {
            reportName = "piolist_ypyt";
        } else if (whichKind == "用品领用单") {
            reportName = "piolist_yply";
        } else if (whichKind == "用品借用单") {
            reportName = "piolist_ypjy";
        } else if (whichKind == "用品归还单") {
            reportName = "piolist_ypgh";
        } else if (whichKind == "用品退仓单") {
            reportName = "piolist_ypgh";
        }
        var condition = '{prodinout.pi_id}=' + Ext.getCmp('pi_id').value + '';
        var id = Ext.getCmp('pi_id').value;
        me.FormUtil.onwindowsPrint2(id, reportName, condition, function() {
            if (whichKind == '生产退料单' || whichKind == '拆件入库单' || whichKind == '拆件完工入库单')
                window.location.reload();
        });
    },
    /**
     *编辑分装明细
     **/
    EditSubpackage: function(record) {
        var width = Ext.isIE ? screen.width * 0.7 * 0.9 : '80%',
            height = Ext.isIE ? screen.height * 0.75 : '100%';
        var pd_id = record.get('pd_id');
        Ext.create('Ext.Window', {
            width: width,
            height: height,
            autoShow: true,
            layout: 'anchor',
            items: [{
                tag: 'iframe',
                frame: true,
                anchor: '100% 100%',
                layout: 'fit',
                html: '<iframe id="iframe_dbfind" src="' + basePath + 'jsps/scm/reserve/prodIODetail.jsp?formCondition=pd_id=' +
                    pd_id + '&gridCondition=pdb_pdid=' + pd_id + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
            }]
        });
    },
    loadOnHandQty: function(grid, id) {
        Ext.Ajax.request({
            url: basePath + 'scm/reserve/loadOnHandQty.action',
            params: {
                caller: caller,
                id: id
            },
            callback: function(opt, s, r) {
                var rs = Ext.decode(r.responseText);
                if (rs.exceptionInfo) {
                    showMessage('提示', rs.exceptionInfo.replace('AFTERSUCCESS', ''));
                } else {
                    var data = [];
                    if (!rs.data || rs.data.length == 2) {
                        grid.GridUtil.add10EmptyData(grid.detno, data);
                        grid.GridUtil.add10EmptyData(grid.detno, data);
                    } else {
                        data = Ext.decode(rs.data.replace(/,}/g, '}').replace(/,]/g, ']'));
                    }
                    grid.store.loadData(data);
                }
            }
        });
    },
    Updatebgxh: function(record) {
        var win = this.bgxhwindow;
        if (!win) {
            win = this.getBgxhWindow();
        }
        win.show();
    },
    getBgxhWindow: function() {
        var me = this;
        return Ext.create('Ext.window.Window', {
            width: 330,
            height: 180,
            closeAction: 'hide',
            cls: 'custom-blue',
            title: '<h1>更改报关型号</h1>',
            layout: {
                type: 'vbox'
            },
            items: [{
                margin: '5 0 0 5',
                xtype: 'textfield',
                fieldLabel: '报关型号',
                name: 'pd_bgxh',
                id: 'pd_bgxh'
            }, {
                margin: '5 0 0 5',
                xtype: 'fieldcontainer',
                fieldLabel: '全部更新',
                combineErrors: false,
                defaults: {
                    hideLabel: true
                },
                layout: {
                    type: 'column',
                    defaultMargins: {
                        top: 0,
                        right: 5,
                        bottom: 0,
                        left: 0
                    }
                },
                items: [{
                    xtype: 'checkbox',
                    columnidth: 0.4,
                    fieldLabel: '全部更新',
                    name: 'allupdate',
                    id: 'allupdate'
                }, {
                    xtype: 'displayfield',
                    fieldStyle: 'color:red',
                    columnidth: 0.6,
                    value: '  *更改当前所有明细'
                }]
            }],
            buttonAlign: 'center',
            buttons: [{
                xtype: 'button',
                text: '保存',
                width: 60,
                iconCls: 'x-button-icon-save',
                handler: function(btn) {
                    var w = btn.up('window');
                    me.saveBgxh(w);
                    w.hide();
                }
            }, {
                xtype: 'button',
                columnWidth: 0.1,
                text: '关闭',
                width: 60,
                iconCls: 'x-button-icon-close',
                margin: '0 0 0 10',
                handler: function(btn) {
                    btn.up('window').hide();
                }
            }]
        });
    },
    saveBgxh: function(w) {
        var pd_bgxh = w.down('field[name=pd_bgxh]').getValue(),
            grid = Ext.getCmp('grid'),
            record = grid.getSelectionModel().getLastSelected();
        if (!pd_bgxh) {
            showError('请先设置报关型号.');
            return;
        } else {
            var allupdate = w.down('field[name=allupdate]').getValue();
            var dd = {
                pd_id: record.data.pd_id,
                pd_piid: record.data.pd_piid,
                pd_bgxh: pd_bgxh ? pd_bgxh : null,
                allupdate: allupdate ? 1 : 0,
                caller: caller
            };
            Ext.Ajax.request({
                url: basePath + 'scm/reserve/updatebgxh.action',
                params: {
                    _noc: 1,
                    data: unescape(Ext.JSON.encode(dd))
                },
                method: 'post',
                callback: function(opt, s, res) {
                    var r = new Ext.decode(res.responseText);
                    if (r.success) {
                        grid.getData('pd_piid=' + record.data.pd_piid);
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
    batchdeal: function(nCaller, condition, url) {
        var win = new Ext.window.Window({
            id: 'win',
            height: "100%",
            width: "80%",
            maximizable: true,
            buttonAlign: 'center',
            layout: 'anchor',
            items: [{
                tag: 'iframe',
                frame: true,
                anchor: '100% 100%',
                layout: 'fit',
                html: '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/editorColumn.jsp?caller=' + nCaller +
                    "&condition=" + condition + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
            }],
            buttons: [{
                name: 'confirm',
                text: $I18N.common.button.erpConfirmButton,
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
                text: $I18N.common.button.erpCloseButton,
                iconCls: 'x-button-icon-close',
                cls: 'x-btn-gray',
                handler: function() {
                    Ext.getCmp('win').close();
                }
            }]
        });
        win.show();
    },
    updateInfo: function(id, val1, val2) {
        Ext.Ajax.request({
            url: basePath + 'scm/reserve/updateBorrowCargoType.action',
            params: {
                id: id,
                type: val1,
                remark: val2
            },
            callback: function(opt, s, r) {
                var rs = Ext.decode(r.responseText);
                if (rs.exceptionInfo) {
                    showError(rs.exceptionInfo);
                } else {
                    Ext.Msg.alert("提示", "更新成功！");
                    window.location.reload();
                }
            }
        });
    },
    isAllowSale: function(record) {
        var bool = true;
        if (!Ext.isEmpty(record.get('pd_plancode'))) bool = false;
        return bool;
    },
    isAllowForeCast: function(record) {
        var bool = true;
        if (!Ext.isEmpty(record.get('pd_ordercode'))) bool = false;
        return bool;
    },
    /**
	 *明细拆分
	 * */
	SplitDetail:function(record){
		var width = Ext.isIE ? screen.width * 0.7 * 0.9 : '80%',
	            height = Ext.isIE ? screen.height * 0.75 : '100%';
	    var pd_id = record.get('pd_id');
	    Ext.create('Ext.Window', {
	    	width: width,
	        height: height,
	        autoShow: true,
	        layout: 'anchor',
	        items: [{
	        	tag: 'iframe',
	            frame: true,
	            anchor: '100% 100%',
	            layout: 'fit',
	            html: '<iframe id="iframe_dbfind" src="' + basePath + 'jsps/scm/reserve/splitProdIODetail.jsp?formCondition=pd_id=' +
	            	pd_id + '&gridCondition=pd_id=' + pd_id + '&whoami=' +caller+ '-Detail&_noc=1" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
	            }]
	    });
	},
	openInvoice: function(e, el, obj) {
		var f = obj.scope, form = f.ownerCt,
			i = form.down('#pi_invoicecode');
		if(i && i.value) {
			url = 'jsps/scm/reserve/invoice.jsp?formCondition=in_codeIS' + i.value + '&gridCondition=id_codeIS' + i.value;
			openUrl(url);
		}
	},
	openPacking : function(e, el, obj) {
		var f = obj.scope, form = f.ownerCt,
			i = form.down('#pi_packingcode');
		if(i && i.value) {
			url = 'jsps/scm/reserve/packing.jsp?formCondition=pi_codeIS' + i.value + '&gridCondition=pd_codeIS' + i.value;
			openUrl(url);
		}
	}
});