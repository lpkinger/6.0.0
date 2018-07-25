Ext.define('erp.view.hr.attendance.Toolbar1', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.erpEmpWorkDateModel',
    items: [{
             xtype:'textfield',
             hideTrigger:false,
             labelAlign:'right',
             fieldLabel:'排班模板id',
             id: 'em_wdid',
             name: 'em_wdid',
             hidden:true
    },{
             xtype: 'dbfindtrigger',
             fieldLabel:'排班模板编码',
             labelAlign:'right',
             id: 'em_code',
             name: 'em_code',
    },{
	        xtype: 'textfield',
	        fieldLabel:'排班模板名称',
	        labelAlign:'right',
	        id: 'em_name',
	        name: 'em_name',
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
                var wdid = Ext.getCmp("em_wdid").rawValue;
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
                    url : basePath + 'hr/attendance/setEmpWorkDateModel.action',
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
    },'-',{
        xtype:'button',
        text : '取消设置',
        width: 80,
        style: {
            width: 80
        },
        id : 'cancel',
        iconCls : 'x-button-icon-close',
        handler : function(){
            var wdid = Ext.getCmp("em_wdid").rawValue;
            var records = Ext.getCmp('tree-panel').getChecked();

            if (records.length == 0) {
                Ext.MessageBox.alert("提示", "请选择需要取消班次的员工！");
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
                url : basePath + 'hr/attendance/cancelEmpWorkDateModel.action',
                params : {
                    wdid: wdid,
                    condition: "em_id in (" + ids + ")"
                },
                method : 'post',
                callback : function(options,success,response){
                    var ret = new Ext.decode(response.responseText);
                     if (ret.success) {
                        Ext.Msg.alert("提示", "取消成功!");
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
    					
                     } else {
                        Ext.Msg.alert('提示','取消失败!');
                     }
                }
            });
        }
    },'-' ,{
        xtype: 'datefield',
        fieldLabel:'开始日期',
        labelAlign:'right',
        id: 'eml_startdate',
        name: 'eml_startdate',
    } ,{
       xtype: 'datefield',
       fieldLabel:'结束日期',
       labelAlign:'right',
       id: 'eml_enddate',
       name: 'eml_enddate'
    }, {
       xtype:'button',
       text : '更新员工班次',
       width: 80,
       style: {
           width: 80
       },
       id : 'update',
       iconCls : 'x-button-icon-save',
       handler : function(){
           var records = Ext.getCmp('tree-panel').getChecked();
    	   var startdate = Ext.getCmp('eml_startdate').value;
    	   var enddate = Ext.getCmp('eml_enddate').value;
    	   
           if (startdate == null || startdate == "") {
               Ext.MessageBox.alert("提示", "请选择开始时间！");
               return;
           }
           if (enddate == null || enddate == "") {
               Ext.MessageBox.alert("提示", "请选择结束时间！");
               return;
           }

           if (records.length == 0) {
               Ext.MessageBox.alert("提示", "请选择需要设置班次的员工！");
               return;
           }
           var ids = new Array();
           Ext.each(records, function(item, index) {
        	   ids.push(item.get("id"));

           });
           console.log(ids);
           Ext.Ajax.request({
               url : basePath + 'hr/attendance/updateEmpWorkDateList.action',
               params : {
                   startdate:startdate,
                   enddate:enddate,
                   ids:ids
               },
               method : 'post',
               callback : function(options,success,response){
                   var ret = new Ext.decode(response.responseText);
                    if (ret.success) {
                       Ext.Msg.alert("提示", "更新成功!");

                    } else {
                       Ext.Msg.alert('提示',ret.error);
                    }
               }
           });
       }
}]
});