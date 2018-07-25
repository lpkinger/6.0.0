/**
 * 批量对账按钮
 */
Ext.define('erp.view.core.button.VastTurnARAPCheck', {
    extend: 'Ext.Button',
    alias: 'widget.erpVastTurnARAPCheckButton',
    iconCls: 'x-button-icon-check',
    cls: 'x-btn-gray',
    tooltip: '批量对账',
    id: 'erpVastTurnARAPCheckButton',
    text: $I18N.common.button.erpVastTurnARAPCheckButton,
    initComponent: function() {
        this.callParent(arguments);
    },
    width: 110,
    handler: function(btn) {
        var datef = Ext.getCmp('pi_date'), 
        	from = datef ? (datef.firstVal ? Ext.Date.toString(datef.firstVal) : null ): null, 
        	to = datef ? (datef.secondVal ? Ext.Date.toString(datef.secondVal) : null ): null;
        this.save(btn.ownerCt.ownerCt.dealUrl, from, to);
    },
    save: function(url, from, to) {
        var grid = Ext.getCmp('batchDealGridPanel');
        var form = Ext.getCmp('dealform');
        var checkdata=[];
    	Ext.each(grid.tempStore,function(d){
    		var keys=Ext.Object.getKeys(d);
			Ext.each(keys, function(k){
				checkdata.push(d[k]);
			});
    	});
    	var items = grid.selModel.getSelection();
        if(checkdata.length>0 && items.length>0){
        	showError('暂存区已经有数据，当前筛选界面勾选的数据无效，请取消勾选或添加到暂存区');
        	return;
        }else if(items.length>0){
        	Ext.each(items, function(item, index){
	        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
	        		item.index = this.data[grid.keyField];
        			grid.multiselected.push(item);    
	        	}
	        });
        } else if(checkdata.length>0){
        	grid.multiselected = checkdata;
        }
        var records = Ext.Array.unique(grid.multiselected);
        if (records.length > 0) {
            var params = new Object();
            params.caller = caller;
            var data = new Array();
            var bool = false;
            Ext.each(records, function(record, index) {
                var f = form.fo_detailMainKeyField;
                if ((grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != '' && this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0) || (f && this.data[f] != null && this.data[f] != '' && this.data[f] != '0' && this.data[f] != 0)) {
                    bool = true;
                    var o = new Object();
                    if (grid.keyField) {
                        o[grid.keyField] = record.data[grid.keyField];
                    } else {
                        params.id[index] = record.data[form.fo_detailMainKeyField];
                    }
                    if (grid.toField) {
                        Ext.each(grid.toField, function(f, index) {
                            var v = Ext.getCmp(f).value;
                            if (v != null && v.toString().trim() != '' && v.toString().trim() != 'null') {
                                o[f] = v;
                            }
                        });
                    }
                    if (grid.necessaryFields) {
                        Ext.each(grid.necessaryFields, function(f, index) {
                            o[f] = record.data[f];
                        });
                    }
                    data.push(o);
                }
            });
            if (bool) {
                params.data = Ext.encode(data);
                params.fromDate = from;
                params.toDate = to;
                var main = parent.Ext.getCmp("content-panel");
                main.getActiveTab().setLoading(true); //loading...
                Ext.Ajax.request({
                    url: basePath + url,
                    params: params,
                    method: 'post',
                    callback: function(options, success, response) {
                        main.getActiveTab().setLoading(false);
                        var localJson = new Ext.decode(response.responseText);
                        if (localJson.exceptionInfo) {
                            showError(localJson.exceptionInfo);
                            return;
                        }
                        if (localJson.success) {
                        	grid.tempStore={};//操作成功后清空暂存区数据
                            if (localJson.log) {
                                showMessage("提示", localJson.log, 15000);
                            }
                            Ext.Msg.alert("提示", "处理成功!", function() {
                                Ext.getCmp('dealform').onQuery();
                            });
                        }
                    }
                });
            } else {
                showError("没有需要处理的数据!");
            }
        } else {
            showError("请勾选需要的明细!");
        }
    }
});