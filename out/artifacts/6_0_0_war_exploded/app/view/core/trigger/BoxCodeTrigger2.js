/**
 * 自动获取包装箱号的trigger
 */
Ext.define('erp.view.core.trigger.BoxCodeTrigger2', {
    extend: 'Ext.form.field.Trigger',
    alias: 'widget.boxcodetrigger2',
    triggerCls: 'x-form-autocode-trigger',
    afterrender: function() {
        this.addEvent({
            'beforetrigger': true
        });
    },
    onTriggerClick: function() {  
    	warnMsg('确定生成包装箱号?', function(btn){
			if(btn == 'yes'){
                    var pa_totalqtynew = Ext.getCmp("pa_totalqtynew").value,
                        pa_outboxcode = Ext.getCmp("pa_outboxcode").value;
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
                } else {
					return;
				}
			}); 			
    }
});