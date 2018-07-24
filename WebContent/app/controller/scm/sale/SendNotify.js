Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.SendNotify', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['core.button.PrintByCondition','core.form.Panel', 'scm.sale.Quotation', 'core.grid.Panel2', 'core.toolbar.Toolbar', 'core.form.MultiField', 
            'core.button.Save', 'core.button.Add', 'core.button.ResAudit', 'core.button.Audit', 
            	'core.button.ResSubmit', 'core.button.Close', 'core.button.Delete', 'core.button.Update', 
            	'core.button.DeleteDetail', 'core.button.Consign', 'core.button.Submit', 
            	'core.button.TurnProdIO', 'core.button.Flow', 'core.button.Print','core.button.PrintHK','core.button.PrintInvoice', 'core.button.PrintBZT', 
            	'core.button.TurnOtherOut','core.button.SaveShip','core.button.TurnProdAppropriationOut', 'core.button.TurnExOut',
            'core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger', 'core.form.YnField', 'core.grid.Panel5', 'core.form.FileField','core.grid.YnColumn','core.button.Split'],
    init: function() {
        var me = this;
        this.control({
            'erpGridPanel2': {
                afterrender: function(grid) {
                    var status = Ext.getCmp('sn_statuscode');
                    if (status && status.value != 'ENTERING' && status.value != 'COMMITED') {
                        Ext.each(grid.columns,
                        function(c) {
                            c.setEditor(null);
                        });
                    }
                },
                reconfigure: function(grid) {
                    var f = Ext.getCmp('sn_id');
                    if (f && !Ext.isEmpty(f.value)) {
                        me.loadOnHandQty(grid, f.value);
                    }
                },
                itemclick: this.onGridItemClick
            },
            '#splitButton': {
   			   click: function(btn){
   				   var record = btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();
   				   if(record.modified.snd_outqty&&record.modified.snd_outqty!=record.data.snd_outqty){
   				       showError("明细行数量已经被修改，请先更新后再拆分");
   				       return;
   				   }
   				   me.SaleSplit(record);
   			   }
   		    },
            'field[name=sn_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=sn_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
            'erpSaveButton': {
                click: function(btn) {
                    var form = me.getForm(btn);
                    if (Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '') {
                        me.BaseUtil.getRandomNumber(); //自动添加编号
                    }
                    this.beforeSaveSendNotify();
                }
            },
            'erpDeleteButton': {
                click: function(btn) {
                    me.FormUtil.onDelete({
                        id: Number(Ext.getCmp('sn_id').value)
                    });
                }
            },
            'erpUpdateButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('sn_statuscode');
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
                    me.FormUtil.onAdd('addSendNotify', '新增出货通知单', 'jsps/scm/sale/sendNotify.jsp');
                }
            },
            'erpCloseButton': {
                click: function(btn) {
                    me.FormUtil.beforeClose(me);
                }
            },
            'erpPrintButton': {
                click: function(btn) {
                    var reportName = "SendNotify";
                    var condition = '{SendNotify.sn_id}=' + Ext.getCmp('sn_id').value + '';
                    me.FormUtil.onwindowsPrint2(Ext.getCmp('sn_id').value, reportName, condition);
                }
            },
            'erpPrintBZTButton': {
                click: function(btn) {
                    var reportName = "SendNotify_packing";
                    var condition = '{SendNotify.sn_id}=' + Ext.getCmp('sn_id').value + '';
                    me.FormUtil.onwindowsPrint(Ext.getCmp('sn_id').value, reportName, condition);
                }
            },
            'erpPrintInvoiceButton': {
                click: function(btn) {
                    var reportName = "SendNotify_invoice";
                    var condition = '{SendNotify.sn_id}=' + Ext.getCmp('sn_id').value + '';
                    me.FormUtil.onwindowsPrint(Ext.getCmp('sn_id').value, reportName, condition);
                }
            },
            'erpPrintHKButton': {
                click: function(btn) {
                    var reportName = "SendNotify_HK";
                    var condition = '{SendNotify.sn_id}=' + Ext.getCmp('sn_id').value + '';
                    me.FormUtil.onwindowsPrint(Ext.getCmp('sn_id').value, reportName, condition);
                }
            },
            'erpSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('sn_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click:  {
                	fn:function(btn){
                		var grid = Ext.getCmp('grid'),
                        items = grid.store.data.items;
                        var rateMsg = '';
                        // 税率为0提示
                        Ext.each(items,
                        function(item) {
                            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
                                if (item.data['snd_taxrate'] == 0) {
                                    rateMsg += item.data['snd_pdno'] + ' ';
                                }
                            }
                        });
                        if (rateMsg != '') {
                            warnMsg('明细行:' + rateMsg + '的税率为0，是否继续?',
                            function(btn) {
                                if (btn == 'ok' || btn == 'yes') {
                                    me.FormUtil.onSubmit(Ext.getCmp('sn_id').value);
                                }
                            });
                        } else {
                            me.FormUtil.onSubmit(Ext.getCmp('sn_id').value, false, me.beforeUpdate, me);
                        }
                	},
                	lock:3000
                }
            },
            'erpResSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('sn_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onResSubmit(Ext.getCmp('sn_id').value);
                }
            },
            'erpAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('sn_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onAudit(Ext.getCmp('sn_id').value);
                }
            },
            'erpResAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('sn_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onResAudit(Ext.getCmp('sn_id').value);
                }
            },
            'erpTurnOtherOutButton':{//转其它出库
            	afterrender: function(btn) {
                    var status = Ext.getCmp('sn_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
            	click:function(){
            		me.turn('SendNotify!ToProdIN!Deal', 'snd_snid=' + Ext.getCmp('sn_id').value +' and nvl(snd_yqty,0) < nvl(snd_outqty,0) and snd_statuscode=\'AUDITED\'', 'scm/sale/turnProdOut.action?type=ProdInOut!OtherOut');
            	}
            },
            'erpTurnExOutButton':{//转换货出库单
            	afterrender: function(btn) {
                    var status = Ext.getCmp('sn_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
            	click:function(){
            		me.turn('SendNotify!ToProdIN!Deal', 'snd_snid=' + Ext.getCmp('sn_id').value +' and nvl(snd_yqty,0) < nvl(snd_outqty,0) and snd_statuscode=\'AUDITED\'', 'scm/sale/turnProdOut.action?type=ProdInOut!ExchangeOut');
            	}
            },
            'erpTurnProdAppropriationOutButton':{//转拨出单
            	afterrender: function(btn) {
                    var status = Ext.getCmp('sn_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
            	click:function(){
            		me.turn('SendNotify!ToAppropriationOut!Deal', 'snd_snid=' + Ext.getCmp('sn_id').value + ' and snd_statuscode=\'AUDITED\'', 'scm/sale/turnProdOut.action?type=ProdInOut!AppropriationOut');
            	}
            },
            'erpTurnProdIOButton':{//转出货单
            	afterrender: function(btn) {
                    var status = Ext.getCmp('sn_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                }
            },
            'erpSaveShipButton':{
            	afterrender:function(btn){
            		var status = Ext.getCmp('sn_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }else{
                    	Ext.each(Ext.getCmp('form').items.items,function(item){
                    		if(item.groupName=='船务信息'){
                    			item.setReadOnly(false);
                    		}
                    	});
                    }
            	},
            	click:function(){
            		var values=new Object();
            		Ext.each(Ext.getCmp('form').items.items,function(item){
                		if(item.groupName=='船务信息'){
                			values[item.name]=item.value;
                		}
                	});
            		values['sn_id']=Ext.getCmp('sn_id').value;
            		var params=new Object();
            		params.formStore = unescape(escape(Ext.JSON.encode(values)));
            		var main = parent.Ext.getCmp("content-panel");
            		main.getActiveTab().setLoading(true);//loading...
            		Ext.Ajax.request({
            	   		url : basePath + 'scm/sale/saveShip.action',//scm/sale/turnProdOut.action?type=ProdInOut!OtherOut
            	   		params: params,
            	   		method : 'post',
            	   		timeout: 6000000,
            	   		callback : function(options,success,response){
            	   			main.getActiveTab().setLoading(false);
            	   			var localJson = new Ext.decode(response.responseText);
            	   			if(localJson.exceptionInfo){
            	   				var str = localJson.exceptionInfo;
            	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
            	   					str = str.replace('AFTERSUCCESS', '');
            	   				}
            	   				showError(str);return;
            	   			}
                			if(localJson.success){
                				showMessage("提示","保存船务信息成功");
            	   				window.location.reload();
            	   			}
            	   		}
            		});
            	}
            },
            'dbfindtrigger[name=sn_toplace]': {
                afterrender: function(trigger) {
                    trigger.dbKey = 'sn_custcode';
                    trigger.mappingKey = 'cu_code';
                    trigger.dbMessage = '请先选客户编号！';
                }
            },
            'dbfindtrigger[name=sn_custcode2]': {
                afterrender: function(trigger) {
                    trigger.dbKey = 'sn_custid';
                    trigger.mappingKey = 'cu_id';
                    trigger.dbMessage = '请先选客户编号！';
                }
            },
            'dbfindtrigger[name=sn_invoiceremark]': {
                afterrender: function(trigger) {
                    trigger.dbKey = 'sn_custid';
                    trigger.mappingKey = 'cu_id';
                    trigger.dbMessage = '请先选客户编号！';
                }
            },
            'dbfindtrigger[name=sn_packingremark]': {
                afterrender: function(trigger) {
                    trigger.dbKey = 'sn_custid';
                    trigger.mappingKey = 'cu_id';
                    trigger.dbMessage = '请先选客户编号！';
                }
            },
            'dbfindtrigger[name=sn_receivecode]': {
                afterrender: function(trigger) {
                    trigger.dbKey = 'sn_custid';
                    trigger.mappingKey = 'cu_id';
                    trigger.dbMessage = '请先选客户编号！';
                }
            },
            'dbfindtrigger[name=sn_paymentscode]': {
                afterrender: function(trigger) {
                    if (trigger.fieldConfig == 'PT') {
                        trigger.dbKey = 'sn_custcode';
                        trigger.mappingKey = 'cu_code';
                        trigger.dbMessage = '请先选客户编号！';
                    }
                }
            },
            'field[name=snd_batchcode]': {
                beforetrigger: function(t) {
                    var record = t.record,
                    grid = t.owner;
                    if (record && grid) {
                        var form = grid.ownerCt.down('form'),
                        p = grid.xtype == 'erpGridPanel2' ? record.get('snd_prodcode') : form.down('field[name=snd_prodcode]').value;
                        var w = record.get('snd_warehousecode');
                        t.dbBaseCondition = '';
                        if (!Ext.isEmpty(w)) {
                            t.dbBaseCondition = ' ba_whcode=\'' + w + '\'';
                        }
                        if (!Ext.isEmpty(p)) {
                            if (t.dbBaseCondition.length > 0) {
                                t.dbBaseCondition += ' and ';
                            }
                            t.dbBaseCondition += ' ba_prodcode=\'' + p + '\'';
                        }
                    }
                }
            },
            'dbfindtrigger[name=snd_ordercode]': {
                focus: function(t) {
                    t.setHideTrigger(false);
                    t.setReadOnly(false);
                    if (Ext.getCmp('sn_custcode')) {
                        var code = Ext.getCmp('sn_custcode').value;
                        if (code != null && code != '') {
                            var obj = me.getCodeCondition();
                            if (obj && obj.field) {
                                t.dbBaseCondition = obj.field + "='" + code + "'";
                            }
                            if (!Ext.getCmp('sn_custcode').readOnly) {
                                Ext.getCmp('sn_custcode').setReadOnly(true);
                                Ext.getCmp('sn_custcode').setFieldStyle(Ext.getCmp('sn_custcode').fieldStyle + ';background:#f1f1f1;');
                            }
                        }
                    }
                },
                aftertrigger: function(t) {
                    var code = Ext.getCmp('sn_custcode').value;
                    if (code == null || code.toString().trim() == '') {
                        var obj = me.getCodeCondition();
                        me.FormUtil.getFieldsValue(obj.tablename, obj.fields, obj.myfield + "='" + t.value + "'", obj.tFields);
                    }
                }
            },
            'dbfindtrigger[name=snd_orderdetno]': {
                focus: function(t) {
                    t.setHideTrigger(false);
                    t.setReadOnly(false);
                    var record = Ext.getCmp('grid').selModel.getLastSelected();
                    var code = record.data['snd_ordercode'];
                    if (code == null || code == '') {
                        showError("请先选择关联订单号!");
                        t.setHideTrigger(true);
                        t.setReadOnly(true);
                    } else {
                        t.dbBaseCondition = "sa_code='" + code + "'";
                    }
                }
            },
            'field[name=sn_statuscode]': {
                change: function(f) {
                    var grid = Ext.getCmp('grid');
                    if (grid && f.value != 'ENTERING' && f.value != 'COMMITED') {
                        grid.setReadOnly(true); //只有未审核的订单，grid才能编辑
                    }
                }
            },
            'field[name=sn_outcredit]': {
    			afterrender: function(f){
    				if(f.value != null && f.value != '' && f.value != 0){
    					f.inputEl.setStyle({color: 'OrangeRed'});
    				}
    			}
            },
            'field[name=sn_outamount]': {
    			afterrender: function(f){
    				if(f.value != null && f.value != '' && f.value != 0){
    					f.inputEl.setStyle({color: 'OrangeRed'});
    				}
    			}
            },
            'dbfindtrigger[name=snd_batchcode]': {
                focus: function(t) {
                    t.setHideTrigger(false);
                    t.setReadOnly(false); //用disable()可以，但enable()无效
                    var record = Ext.getCmp('grid').selModel.getLastSelected();
                    var pr = record.data['snd_prodcode'];
                    if (pr == null || pr == '') {
                        showError("请先选择料号!");
                        t.setHideTrigger(true);
                        t.setReadOnly(true);
                    }
                    if (pr != null && pr != '') {
                        var obj = me.getCodeCondition();
                        if (obj && obj.field) {
                            t.dbBaseCondition = "ba_prodcode='" + pr + "'";
                        }
                    }
                    if (record.data['snd_warehousecode'] != null) {
                        t.dbBaseCondition = t.dbBaseCondition + " and ba_whcode='" + record.data['snd_warehousecode'] + "'";
                    }
                }
            }
        });
    },
    onGridItemClick: function(selModel, record) { //grid行选择
    	var btn = Ext.getCmp('splitButton');
    	if(!Ext.isEmpty(record.data.snd_id) && (record.data.snd_yqty == 0 || record.data.snd_yqty == null)){
    		btn && btn.setDisabled(false);
    	} else {
    		btn && btn.setDisabled(true);
    	}
        this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn) {
        return btn.ownerCt.ownerCt;
    },
    createSplitForm: function(record) {
        var me = this;
        return Ext.create('Ext.form.Panel', {
            xtype: 'form',
            layout: 'column',
            region: 'north',
            frame: true,
            defaults: {
                xtype: 'textfield',
                columnWidth: 0.5,
                readOnly: true,
                fieldStyle: 'background:#f0f0f0;border: 0px solid #8B8970;color:blue;'
            },
            items: [{
                fieldLabel: '订单编号',
                value: record.data.snd_ordercode
            },
            {
                fieldLabel: '序号',
                value: record.data.snd_pdno
            },
            {
                fieldLabel: '产品编号',
                name: 'snd_prodcode',
                value: record.data.snd_prodcode
            },
            {
                fieldLabel: '产品名称',
                value: record.data.pr_detail
            },
            {
                fieldLabel: '数量',
                value: record.data.snd_outqty
            }],
            buttonAlign: 'center',
            buttons: [{
                xtype: 'button',
                columnWidth: 0.12,
                text: '保存',
                width: 60,
                iconCls: 'x-button-icon-save',
                handler: function(btn) {
                    me.saveSplit(btn.ownerCt.ownerCt.ownerCt.down('grid'), record);
                    window.location.reload();
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
                    btn.ownerCt.ownerCt.ownerCt.close();
                }
            }]
        });
    },
    saveSplit: function(grid, record) {
        var store = grid.getStore(),  form = Ext.getCmp('form');
        var count = 0;
        var jsonData = new Array();
        var dd;
        Ext.Array.each(store.data.items, function(item) {
            if (item.data.snd_outqty != 0 && item.data.snd_warehousecode != null && item.data.snd_outqty > 0) {
                dd = new Object();
                //说明是新增批次
                dd['snd_outqty'] = item.data.snd_outqty;
                dd['snd_id'] = item.data.snd_id;
                dd['snd_warehousecode'] = item.data.snd_warehousecode;
                dd['snd_warehouse'] = item.data.snd_warehouse;
                dd['snd_pdno'] = item.data.snd_pdno;
                dd['snd_batchid'] = item.data.snd_batchid;
                dd['snd_batchcode'] = item.data.snd_batchcode;
                jsonData.push(Ext.JSON.encode(dd));
                count += Number(item.data.snd_outqty);
            }
        });
        var assqty = Number(record.data.snd_outqty);
        if(Math.abs(form.BaseUtil.numberFormat(count,4)-form.BaseUtil.numberFormat(assqty, 4)) > 0.001){
            showError('分拆数量必须等于原数量!');
            return;
        } else {
            var r = new Object();
            r['snd_id'] = record.data.snd_id;
            r['snd_snid'] = record.data.snd_snid;
            r['snd_pdno'] = record.data.snd_pdno;
            var params = new Object();
            params.formdata = unescape(Ext.JSON.encode(r).replace(/\\/g, "%"));
            params.data = unescape(jsonData.toString().replace(/\\/g, "%"));
            Ext.Ajax.request({
                url: basePath + 'scm/sale/SendNotifyBatch.action',
                params: params,
                waitMsg: '拆分中...',
                method: 'post',
                callback: function(options, success, response) {
                    var localJson = new Ext.decode(response.responseText);
                    if (localJson.success) {
                        saveSuccess(function() {
                            //add成功后刷新页面进入可编辑的页面 
                            this.loadSplitData(originaldetno, said, record);
                        });
                    } else if (localJson.exceptionInfo) {
                        var str = localJson.exceptionInfo;
                        if (str.trim().substr(0, 12) == 'AFTERSUCCESS') { //特殊情况:操作成功，但是出现警告,允许刷新页面
                            str = str.replace('AFTERSUCCESS', '');
                            saveSuccess(function() {
                                //add成功后刷新页面进入可编辑的页面 
                                this.loadSplitData(originaldetno, said, record);
                            });
                            showError(str);
                        } else {
                            showError(str);
                            return;
                        }
                    } else {
                        saveFailure();
                    }
                }
            });
        }
    },
    createSplitGrid: function(record) {
        return Ext.create('Ext.grid.Panel', {
            region: 'south',
            layout: 'fit',
            id: 'smallgrid',
            height: '80%',
            features: [{
                ftype: 'summary'
            }],
            dbfinds: [{
                field: 'snd_batchcode',
                dbGridField: 'ba_code'
            },
            {
                field: 'snd_batchid',
                dbGridField: 'ba_id'
            },
            {
                field: 'snd_warehousecode',
                dbGridField: 'wh_code;ba_whcode'
            },
            {
                field: 'snd_warehouse',
                dbGridField: 'wh_description'
            },
            {
                field: 'ba_qty',
                dbGridField: 'ba_remain'
            }],
            columnLines: true,
            store: Ext.create('Ext.data.Store', {
                fields: [{
                    name: 'snd_pdno',
                    type: 'int'
                },
                {
                    name: 'snd_outqty',
                    type: 'number'
                },
                {
                    name: 'snd_warehousecode',
                    type: 'string'
                },
                {
                    name: 'snd_warehouse',
                    type: 'string'
                },
                {
                    name: 'snd_batchcode',
                    type: 'string'
                },
                {
                    name: 'snd_batchid',
                    type: 'int'
                },
                {
                    name: 'snd_id',
                    type: 'int'
                },
                {
                    name: 'ba_qty',
                    type: 'number'
                }],
                data: []
            }),
            plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
                clicksToEdit: 1
            })],
            tbar: [{
                tooltip: '添加批次',
                iconCls: 'x-button-icon-add',
                width: 25,
                handler: function() {
                    var store = Ext.getCmp('smallgrid').getStore();
                    var r = new Object();
                    r.snd_outqty = 0;
                    r.snd_id = 0;
                    r.snd_pdno = store.getCount() + Number(record.data.snd_pdno);
                    store.insert(store.getCount(), r);
                }
            },
            {
                tooltip: '删除批次',
                width: 25,
                itemId: 'delete',
                iconCls: 'x-button-icon-delete',
                handler: function(btn) {
                    var sm = Ext.getCmp('smallgrid').getSelectionModel();
                    var record = sm.getSelection();
                    var snd_id = record[0].data.snd_id;
                    if (snd_id && snd_id != 0) {
                        Ext.Msg.alert('提示', '不能删除已拆批次或原始行号!');
                        return;
                    }
                    var store = Ext.getCmp('smallgrid').getStore();
                    store.remove(record);
                    if (store.getCount() > 0) {
                        sm.select(0);
                    }
                },
                disabled: true
            }],
            listeners: {
                itemmousedown: function(selmodel, record) {
                    selmodel.ownerCt.down('#delete').setDisabled(false);
                }
            },
            columns: [{
                dataIndex: 'snd_pdno',
                header: '序号',
                width: 60,
                format: '0',
                xtype: 'numbercolumn'
            },
            {
                dataIndex: 'snd_outqty',
                header: '数量',
                align: 'right',
                width: 120,
                summaryType: 'sum',
                xtype: 'numbercolumn',
                summaryRenderer: function(value, summaryData, dataIndex) {
                	var store = this.view.store, v = store.sum(dataIndex);
                	if(v != value) {
                		store.fireEvent('datachanged', store);
                	}
                	return Ext.util.Format.number(value, '0,000.00'); 
                },
                editable: true,
                format: '0,000.00',
                editor: {
                    xtype: 'numberfield',
                    format: '0.00',
                    hideTrigger: true
                }
            },
            {
                dataIndex: 'snd_warehousecode',
                header: '仓库编号',
                dbfind: 'WareHouse|wh_code',
                width: 100,
                editable: true,
                editor: {
                    xtype: 'dbfindtrigger'
                }
            },
            {
                dataIndex: 'snd_warehouse',
                header: '仓库名称',
                width: 100,
                editable: false
            },
            {
                dataIndex: 'snd_batchcode',
                header: '批号',
                width: 100,
                dbfind: 'Batch|ba_code',
                editable: true,
                editor: {
                    xtype: 'dbfindtrigger'
                }
            },
            {
                dataIndex: 'snd_batchid',
                header: '批号ID',
                xtype: 'numbercolumn',
                width: 100,
                editable: false
            },
            {
                dataIndex: 'snd_id',
                header: 'sndid',
                xtype: 'numbercolumn',
                width: 0
            },
            {
                dataIndex: 'ba_qty',
                header: '库存数',
                summaryType: 'sum',
                xtype: 'numbercolumn',
                summaryRenderer: function(value, summaryData, dataIndex) {
                	return Ext.util.Format.number(value, '0,000.00');
                },
                width: 100
            }]
        });
    },
    SaleSplit: function(record) {
        var me = this,
        originaldetno = Number(record.data.snd_pdno);
        var said = Number(record.data.snd_snid);
        var win = Ext.create('Ext.window.Window', {
            width: 850,
            height: '80%',
            iconCls: 'x-grid-icon-partition',
            title: '<h1>出货通知单拆分</h1>',
            items: [me.createSplitForm(record), me.createSplitGrid(record)]
        });
        win.show();
        this.loadSplitData(originaldetno, said, record, win.down('grid'));
    },
    loadSplitData: function(detno, said, record, grid) {
        grid.setLoading(true); //loading...
        Ext.Ajax.request({ //拿到grid的columns
            url: basePath + "common/loadNewGridStore.action",
            params: {
                caller: 'SendnotifySplit',
                condition: 'snd_pdno=' + detno + " AND snd_snid=" + said + " order by snd_id desc"
            },
            method: 'post',
            callback: function(options, success, response) {
                grid.setLoading(false);
                var res = new Ext.decode(response.responseText);
                if (res.exceptionInfo) {
                    showError(res.exceptionInfo);
                    return;
                }
                var data = res.data;
                if (!data || data.length == 0) {
                    grid.store.removeAll();
                    var o = new Object();
                    o.snd_pdno = detno;
                    o.snd_outqty = record.data.snd_outqty;
                    o.snd_id = record.data.snd_id;
                    o.ba_qty = record.get('pw_onhand');
                    data.push(o);
                }
                grid.store.loadData(data);
                //自定义event
                grid.addEvents({
                    storeloaded: true
                });
                grid.fireEvent('storeloaded', grid, data);
            }
        });
    },
    beforeSaveSendNotify: function() {
        var grid = Ext.getCmp('grid'),
        items = grid.store.data.items;
        var bool = true;
        var cust = Ext.getCmp('sn_custid').value,
        sncode = Ext.getCmp('sn_code').value;
        if (cust == null || cust == '' || cust == '0' || cust == 0) {
            showError('未选择客户，或客户编号无效!');
            return;
        }
       Ext.Array.each(items,
        function(item) { 
    	   item.data['snd_code']=sncode;
        });
        var rateMsg = '';
        //数量不能为空或0
        Ext.each(items,
        function(item) {
            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
                if (item.data['snd_outqty'] == null || item.data['snd_outqty'] == '' || item.data['snd_outqty'] == '0' || item.data['snd_outqty'] == 0) {
                    bool = false;
                    showError('明细表第' + item.data['snd_pdno'] + '行的数量为空');
                    return;
                }
                if (item.data['snd_taxrate'] == 0) {
                    rateMsg += item.data['snd_pdno'] + ' ';
                }
            }
        });
        if (rateMsg != '') {
            warnMsg('明细行:' + rateMsg + '的税率为0，是否继续?',
            function(btn) {
                if (btn != 'ok' && btn != 'yes') {
                    bool = false;
                }
            });
        }
        //保存sale
        if (bool) this.FormUtil.beforeSave(this);
    },
    beforeUpdate: function() {
        var grid = Ext.getCmp('grid'),
        items = grid.store.data.items;
        var bool = true;
        var cust = Ext.getCmp('sn_custid').value,
        sncode = Ext.getCmp('sn_code').value;
        if (cust == null || cust == '' || cust == '0' || cust == 0) {
            showError('未选择客户，或客户编号无效!');
            return;
        }
        Ext.Array.each(items,function(item) { 
                  item.data['snd_code']=sncode;
        });
        var rateMsg = '';
        //数量不能为空或0
        Ext.each(items,
        function(item) {
            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
                if (item.data['snd_outqty'] == null || item.data['snd_outqty'] == '' || item.data['snd_outqty'] == '0' || item.data['snd_outqty'] == 0) {
                    bool = false;
                    showError('明细表第' + item.data['snd_pdno'] + '行的数量为空');
                    return;
                }
                if (item.data['snd_taxrate'] == 0) {
                    rateMsg += item.data['snd_pdno'] + ' ';
                }
            }
        });
        if (rateMsg != '') {
            warnMsg('明细行:' + rateMsg + '的税率为0，是否继续?',
            function(btn) {
                if (btn != 'ok' && btn != 'yes') {
                    bool = false;
                }
            });
        }
        //保存
        if (bool) this.FormUtil.onUpdate(this);
    },
    /**
	 * snd_ordercode的限制条件
	 */
    getCodeCondition: function() {
        var field = null;
        var fields = '';
        var tablename = '';
        var myfield = '';
        var tFields = 'sn_custid,sn_custcode,sn_custname,sn_currency,sn_rate,sn_payments,sn_payment,sn_toplace,sn_sellerid,sn_sellername,sn_arcustcode,sn_arcustname';
        switch (caller) {
        case 'SendNotify':
            //出货通知单
            field = "sa_custcode";
            fields = 'sa_custid,sa_custcode,sa_custname,sa_currency,sa_rate,sa_paymentsid,sa_payments,sa_toplace,sa_sellerid,sa_seller,sa_apcustcode,sa_apcustname';
            tablename = 'Sale';
            myfield = 'sa_code';
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
    loadOnHandQty: function(grid, id) {
        Ext.Ajax.request({
            url: basePath + 'scm/sale/loadOnHandQty.action',
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
    turn: function(nCaller, condition, url){
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
   	   				    	window.location.reload();
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