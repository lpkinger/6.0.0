Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.PackageTransfer', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: [
        'pm.mes.PackageTransfer', 'core.trigger.DbfindTrigger',
        'core.form.YnField', 'core.grid.YnColumn', 'core.grid.TfColumn',
        'core.button.Query', 'core.button.Close', 'core.button.Print',
        'core.trigger.TextAreaTrigger','core.trigger.BoxCodeTrigger2'
    ],
    init: function() {
        var me = this;
        this.control({
        	'#pa_outboxcode':{//需要转移的号带出数量
        		specialkey: function(f, e) { //按ENTER执行确认
                    if (e.getKey() == e.ENTER) {
                        if (f.value != null && f.value != '') {
                            me.getOriginalQty(f.value);
                        }
                    }
                }
        	},
        	'#pa_outboxnew':{
        		afterrender:function(){
        			Ext.create('Ext.tip.ToolTip', {
					     target: 'pa_outboxnew-triggerWrap',
					     html: '生成箱号'
					 });
        		},
        		specialkey: function(f, e) { //按ENTER执行确认
                    if (e.getKey() == e.ENTER) {
                        if (f.value != null && f.value != '') {
                        	var old = Ext.getCmp('pa_outboxcode').value;
                        	if(Ext.isEmpty(old)){
                        		showError('需要转移的包装箱号不允许为空');
                        		return;
                        	}
                            me.getFormStore( Ext.getCmp("form").getForm().getValues());
                        }
                    }
                }
        	},
            '#entercode': {
                specialkey: function(f, e) { //按ENTER执行确认
                    if (e.getKey() == e.ENTER) {
                        if (f.value != null && f.value != '') {
                            me.onConfirm();
                        }
                    }
                }
            },

            'button[id=generatePackageBtn]': { //生成包装箱号
                click: function(btn) {
                    var pa_totalqtynew = Ext.getCmp("pa_totalqtynew").value,
                        pa_outboxcode = Ext.getCmp("pa_outbox").value;
                    var result = Ext.getCmp('t_result');
                    if (Ext.isEmpty(pa_outboxcode)) {
                        showError('请先指定箱号!');
                        return;
                    } else if (Ext.isEmpty(pa_totalqtynew) || pa_totalqtynew == 0 || pa_totalqtynew == '0') {
                        showError("箱内数量不允许为空或者零!");
                        return;
                    }
                    Ext.Ajax.request({ //拿到grid的columns
                        url: basePath + "pm/mes/generateNewPackage.action",
                        params: {
                            pa_totalqtynew: pa_totalqtynew, //目标箱号箱内容量
                            pa_oldcode: pa_outboxcode // 原箱号 
                        },
                        method: 'post',
                        callback: function(options, success, response) {
                            var res = new Ext.decode(response.responseText);
                            if (res.exceptionInfo) {
                                result.append(res.exceptionInfo, 'error');
                                showError(res.exceptionInfo);
                                return;
                            }
                            var data = res.data;                                                    
                            if (data ) { //设置包装箱号
                            	result.append('生成箱号：' + data['pa_code'] + '成功！');
                                Ext.getCmp("pa_outboxnew").setValue(data['pa_code']);
                                Ext.getCmp("pa_totalqtynew").setValue(data['pa_totalqtynew']);
                            }
                        }
                    });
                }
            }
        });
    },
    getOriginalQty:function(data){
    	Ext.Ajax.request({//拿到grid的columns
    		url : basePath + "pm/bom/getDescription.action",
    		params: {
    			tablename: 'package',
    			field: 'pa_totalqty',
    			condition: "pa_status=0 and pa_outboxcode='"+data+"'"
    		},
    		method : 'post',
    		callback : function(options,success,response){
    		var res = new Ext.decode(response.responseText);
    		if(res.exceptionInfo){
    			showError(res.exceptionInfo);
    			Ext.getCmp('pa_outboxcode').setValue('');
    			Ext.getCmp('pa_totalqty').setValue('');
    			return;
    		}
    		if(res.description == null){
    			showError('箱号:'+data+'错误，不存在或者状态无效!');
    			Ext.getCmp('pa_outboxcode').setValue('');
    			Ext.getCmp('pa_totalqty').setValue('');
    			return;
    		}else if(res.description == '0' || res.description == 0){
    			showError('箱号:'+data+'错误，库存数量为0!');
    			Ext.getCmp('pa_outboxcode').setValue('');
    			Ext.getCmp('pa_totalqty').setValue('');
    			return;
    		}else{//包装箱号正确设置编号数量
    			Ext.getCmp('pa_totalqty').setValue(res.description);
    			Ext.getCmp('pa_outboxnew').focus(true);
    		}
    	 }
	  });
    },
    onConfirm: function() { //确认采集序列号	或者子箱号	
        var me = this;
        var serial = Ext.getCmp('serial').value,
            pa_outboxcode = Ext.getCmp('pa_outboxcode').value,
            pa_outboxnew = Ext.getCmp('pa_outboxnew').value,
            entercode = Ext.getCmp('entercode').value,
            result = Ext.getCmp('t_result');
        //判断剩余装箱数量
        if (Ext.isEmpty(pa_outboxcode)) {
            showError('请先指定箱号!');
            return;
        } else if (Ext.isEmpty(pa_outboxnew)) {
            showError('请先指定目标箱号!');
            return;
        }
        if (serial) { //输入序列号
            if (Ext.isEmpty(entercode)) {
                result.append('请输入序列号!');
                return;
            }
            var condition = {
                pa_oldcode: pa_outboxcode,
                pa_newcode: pa_outboxnew,
                serialcode: entercode
            };
            Ext.Ajax.request({ //采集序列号
                url: basePath + "pm/mes/getPackageDetailSerial.action",
                params: {
                    condition: unescape(escape(Ext.JSON.encode(condition)))
                },
                method: 'post',
                callback: function(options, success, response) {
                    var res = new Ext.decode(response.responseText);
                    if (res.exceptionInfo) {
                        result.append(res.exceptionInfo, 'error');
                        Ext.getCmp("entercode").setValue('');
                        showError(res.exceptionInfo);
                        return;
                    } else {
                        result.append('采集序列号：' + entercode + '成功！');
                        //数据添加到grid中
                        me.loadNewStore({
                            pd_outboxcode: pa_outboxnew,
                            pd_barcode: entercode,
                            pd_innerqty: '1'
                        });
                        Ext.getCmp("entercode").setValue('');
                    }
                }
            });
        } else { //输入子箱号
            if (Ext.isEmpty(entercode)) {
                result.append('请输入子箱号!');
                return;
            }
        }
    },
    getFormStore: function(data) {
        if (data['pa_outboxcode'] == data['pa_outboxnew']) {
            showError('目标箱号不允许和原箱号相同！');
            Ext.getCmp('pa_outboxnew').setValue('');
            Ext.getCmp('pa_totalqtynew').setValue('');
            return;
        }
        Ext.Ajax.request({
            url: basePath + 'pm/mes/getFormTStore.action',
            params: {
                condition: unescape(escape(Ext.JSON.encode(data)))
            },
            method: 'post',
            callback: function(options, success, response) {
                var r = new Ext.decode(response.responseText);
                if (r.exceptionInfo) {
                    showError(r.exceptionInfo);  return;                   
                } else if (r.data) {
                    Ext.getCmp("form").getForm().setValues(r.data);
                }
            }
        });
    },
    loadNewStore: function(data) {
        var me = this;
        var grid = Ext.getCmp("querygrid");
        var datas = [];
        var items = grid.store.data.items;
        Ext.each(items, function(item, index) {
            var o = new Object();
            Ext.each(grid.columns, function(c) {
                if (!Ext.isEmpty(item.data[c.dataIndex])) {
                    o[c.dataIndex] = item.data[c.dataIndex];
                }
            });
            if (!o) {
                datas.push(o);
            }
        });
        datas.push(data);
        grid.store.loadData(datas);
    }

});