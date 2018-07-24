Ext.define('erp.view.hr.wage.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.erpEmpWageStandard',
    items: [{
             xtype:'dbfindtrigger',
             hideTrigger:false,
             labelAlign:'right',
             fieldLabel:'薪资标准ID',
             id: 'em_wsid',
             name: 'em_wsid'
    },{
             xtype: 'textfield',
             fieldLabel:'薪资标准名称',
             labelAlign:'right',
             id: 'ws_name',
             name: 'ws_name',
             readOnly: true
    }, {
            xtype:'button',
            text : '保存设置',
            width: 80,
            style: {
                width: 80
            },
            id : 'submit',
            iconCls : 'x-button-icon-submit',
            handler : function(){
                var wsid = Ext.getCmp("em_wsid").rawValue;
                var records = Ext.getCmp('tree-panel').getChecked();
                if (wsid == null || wsid == "") {
                    Ext.MessageBox.alert("提示", "请选择薪资标准！");
                    return;
                }

                if (records.length == 0) {
                    Ext.MessageBox.alert("提示", "请选择需要设置薪资标准的员工！");
                    return;
                }

                var ids = "";
                Ext.each(records, function(item, index) {
                    ids = ids + item.get("id");
                    if (index < records.length - 1) {
                        ids = ids + ",";
                    }
                });

                Ext.Ajax.request({
                    url : basePath + 'hr/wage/WageStandardSet.action',
                    params : {
                        wsid: wsid,
                        condition: "em_id in (" + ids + ")"
                    },
                    method : 'post',
                    callback : function(options,success,response){
                        var ret = new Ext.decode(response.responseText);
                         if (ret.success) {
                            Ext.Msg.alert("提示", "设置成功!");
                         } else {
                            Ext.Msg.alert('提示','操作失败!');
                         }
                    }
                });
            }
    }]
});