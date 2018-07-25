Ext.define('erp.view.hr.attendance.Toolbar3', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.erpEmpToolbar3',
    items: [{
             xtype:'textfield',
             hideTrigger:false,
             labelAlign:'right',
             fieldLabel:'id',
             id: 'em_wddefaultid',
             name: 'em_wddefaultid',
             hidden:true
    },{
             xtype: 'dbfindtrigger',
             fieldLabel:'班次编号',
             labelAlign:'right',
             id: 'wd_code',
             name: 'wd_code',
    },{
	        xtype: 'textfield',
	        fieldLabel:'班次名称',
	        labelAlign:'right',
	        id: 'wd_name',
	        name: 'wd_name',
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
                var wdid = Ext.getCmp("em_wddefaultid").rawValue;
                var records = Ext.getCmp('tree-panel').getChecked();
                if (wdid == null || wdid == "") {
                    Ext.MessageBox.alert("提示", "请选择班次！");
                    return;
                }

                if (records.length == 0) {
                    Ext.MessageBox.alert("提示", "请选择需要设置班次的员工！");
                    return;
                }

                var ids = "";
                var selectIds = new Array();
                Ext.each(records, function(item, index) {
                	selectIds.push(item.getPath('id'));
                    ids = ids + item.get("id");
                    if (index < records.length - 1) {
                        ids = ids + ",";
                    }
                });
                Ext.Ajax.request({
                    url : basePath + 'hr/attendance/setEmpWorkDate.action',
                    params : {
                        wdid: wdid,
                        condition: "em_id in (" + ids + ")"
                    },
                    method : 'post',
                    callback : function(options,success,response){
                        var ret = new Ext.decode(response.responseText);
                         if (ret.success) {
                            Ext.Msg.alert("提示", "设置成功!");
                            var store = Ext.getCmp('tree-panel').getExpandItem();
                            var ids = new Array();
                            Ext.each(store,function(item,index){
                            	ids.push(item.getPath('id'));
                            });
                            Ext.getCmp('tree-panel').getTreeRootNode(0);
        					setTimeout(function(){
        						Ext.each(ids,function(id,index){
        							 Ext.getCmp('tree-panel').expandPath(id,'id');
        						});

        					},300);
        					
//    						setTimeout(function(){
//        						Ext.each(records,function(item,index){
////        							Ext.getCmp('tree-panel').selectPath(id,'id');
//        							Ext.getCmp('tree-panel').getSelectionModel().select(item);
//        						});
//    						},500);
        					
                         } else {
                            Ext.Msg.alert('提示','操作失败!');
                         }
                    }
                });
            }
    }]
});