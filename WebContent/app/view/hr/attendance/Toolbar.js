Ext.define('erp.view.hr.attendance.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.erpAttendApplyRange',
    items: [{
             xtype:'dbfindtrigger',
             hideTrigger:false,
             labelAlign:'right',
             fieldLabel:'项目ID',
             id: 'em_aiid',
             name: 'em_aiid'
    },{
             xtype: 'textfield',
             fieldLabel:'项目名称',
             labelAlign:'right',
             id: 'ai_name',
             name: 'ai_name',
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
                var aiid = Ext.getCmp("em_aiid").rawValue;
                var records = Ext.getCmp('tree-panel').getChecked();
                if (aiid == null || aiid == "") {
                    Ext.MessageBox.alert("提示", "请选择考勤项目！");
                    return;
                }

                if (records.length == 0) {
                    Ext.MessageBox.alert("提示", "请选择需要设置考勤项目的员工！");
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
                    url : basePath + 'hr/attendance/AttendRangeSet.action',
                    params : {
                        aiid: aiid,
                        ids: ids
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