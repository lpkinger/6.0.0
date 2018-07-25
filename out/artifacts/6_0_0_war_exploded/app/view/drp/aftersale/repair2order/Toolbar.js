Ext.define('erp.view.drp.aftersale.repair2order.Toolbar', {
    extend: 'Ext.toolbar.Paging',
    alias: 'widget.erpRepair2OrderToolbar',
    doRefresh:function(){
        this.moveFirst();
    },
    items: [{
        xtype: 'combo',
        id:'rotype',
        editable:false,
        fieldLabel: '派工单类型',
        name: 'rotype',
        displayField: 'dlc_display',
        valueField: 'dlc_value',
        labelAlign:'right',
        store: Ext.create('Ext.data.Store', {
            fields: ['dlc_display','dlc_value'],
            remoteSort:true,
            simpleSortMode: true,
            proxy: {
                type: 'ajax',
                url : basePath + 'drp/aftersale/CustomerRepair2OrderType.action?caller=turnRepairOrder',
                reader: {
                    type: 'json',
                    root: 'rotype'
                }
            },
            autoLoad:true
        }),
        listeners:{
             "change":function(){
                 if(Ext.getCmp('rotype').getValue()=='退换货'){
                     Ext.getCmp('ro_repairemname').setReadOnly(true);
                     Ext.getCmp('ro_repairemname').setValue('');
                     Ext.getCmp('ro_repairemid').setValue('');
                 }else{
                     Ext.getCmp('ro_repairemname').setReadOnly(false);
                     Ext.getCmp('ro_repairemname').setValue('');
                     Ext.getCmp('ro_repairemid').setValue('');
                 }
             }
        }
    }, {
             xtype:'dbfindtrigger',
             hideTrigger:false,
             labelAlign:'right',
             fieldLabel:'维修人',
             id:'ro_repairemname',
             name:'ro_repairemname'
    },{
             xtype:'hidden',
             id:'ro_repairemid',
             name:'ro_repairemid'
    }, {
            xtype:'button',
            text : '转派工单',
            id : 'submit',
            iconCls : 'x-button-icon-submit',
            handler : function(){
                var repairemid = Ext.getCmp("ro_repairemid").getValue();
                var repairemname = Ext.getCmp("ro_repairemname").getValue();
                var rotype = Ext.getCmp("rotype").getValue();
                var records = Ext.getCmp("grid").getSelectionModel().getSelection();
                var crid = "";
                var ids = "";
                if (rotype == null || rotype == "" || rotype == undefined) {
                    Ext.MessageBox.alert("提示","请选择派工单类型！");
                    return ;
                }

                if (records.length == 0) {
                    Ext.MessageBox.alert("提示","请选择您要操作的行！");
                    return ;
                } else {
                    for (var i = 0; i < records.length; i++) {
                        ids += records[i].get("crd_id");
                        if (i < records.length - 1) {
                            ids = ids + ",";
                        }
                    }
                    crid = records[0].get("crd_crid");
                }

                //alert(repairemid + " " + repairemname + " " + rotype + " " + ids);
                Ext.Ajax.request({
                    url : basePath + 'drp/aftersale/turnRepairOrder.action?caller=turnRepairOrder',
                    params : {
                        crid: crid,
                        em_uu: repairemid,
                        em_name: repairemname,
                        rotype: rotype,
                        crdids: ids
                    },
                    method : 'post',
                    callback : function(options,success,response){
                        var ret = new Ext.decode(response.responseText);
                         if (ret.success) {
                            Ext.Msg.alert("提示", "转派工单成功!");
                         } else {
                            Ext.Msg.alert('提示','操作失败');
                         }
                    }
                });
            }
    },{
            text : ' ',
            id : 'space4',
            minWidth : 5,
            disabled : true
    }],
    updateInfo : function(){
         var page=this.child('#inputItem').getValue();
            var me = this,
            displayItem = me.child('#displayItem'),
            //store = me.store,//update by yingp
            pageData = me.getPageData();
            pageData.fromRecord=(page-1)*pageSize+1;
            pageData.toRecord=page*pageSize > dataCount ? dataCount : page*pageSize;//
            pageData.total=dataCount;
/*	    			me.store.totalCount = dataCount;
            me.store.pageSize = pageSize;
            pageData.pageCount = Math.ceil(dataCount / pageSize);*/
            dataCount, msg;
            if (displayItem) {
                if (dataCount === 0) {
                    msg = me.emptyMsg;
                } else {
                    msg = Ext.String.format(
                        me.displayMsg,
                        pageData.fromRecord,
                        pageData.toRecord,
                        pageData.total
                    );
                }
                displayItem.setText(msg);
                me.doComponentLayout();
            }
        },
        getPageData : function(){
            var store = this.store,
               totalCount = store.getTotalCount();
               totalCount=dataCount;
            return {
                total : totalCount,
                currentPage : store.currentPage,
                pageCount: Math.ceil(dataCount / pageSize),
                fromRecord: ((store.currentPage - 1) * store.pageSize) + 1,
                toRecord: Math.min(store.currentPage * store.pageSize, totalCount)
            };
        },
        onPagingKeyDown : function(field, e){
            var me = this,
                k = e.getKey(),
                pageData = me.getPageData(),
                increment = e.shiftKey ? 10 : 1,
                pageNum = 0;

            if (k == e.RETURN) {
                e.stopEvent();
                pageNum = me.readPageFromInput(pageData);
                if (pageNum !== false) {
                    pageNum = Math.min(Math.max(1, pageNum), pageData.pageCount);
                    me.child('#inputItem').setValue(pageNum);
                    if(me.fireEvent('beforechange', me, pageNum) !== false){
                        page = pageNum;
                        Ext.getCmp("grid").getColumnsAndStore(caller, condition, page, pageSize);
                    }

                }
            } else if (k == e.HOME || k == e.END) {
                e.stopEvent();
                pageNum = k == e.HOME ? 1 : pageData.pageCount;
                field.setValue(pageNum);
            } else if (k == e.UP || k == e.PAGEUP || k == e.DOWN || k == e.PAGEDOWN) {
                e.stopEvent();
                pageNum = me.readPageFromInput(pageData);
                if (pageNum) {
                    if (k == e.DOWN || k == e.PAGEDOWN) {
                        increment *= -1;
                    }
                    pageNum += increment;
                    if (pageNum >= 1 && pageNum <= pageData.pages) {
                        field.setValue(pageNum);
                    }
                }
            }
            me.updateInfo();
            fn(me,pageNum);
        },
        moveFirst : function(){
            var me = this;
            me.child('#inputItem').setValue(1);
            value=1;
            page = value;
            Ext.getCmp("grid").getColumnsAndStore(caller, condition, page, pageSize);
            me.updateInfo();
            fn(me,value);
        },
        movePrevious : function(){
            var me = this;
            me.child('#inputItem').setValue(me.child('#inputItem').getValue()-1);
            value=me.child('#inputItem').getValue();
            page = value;
            Ext.getCmp("grid").getColumnsAndStore(caller, condition, page, pageSize);
            me.updateInfo();
            fn(me,value);

        },
        moveNext : function(){
            var me = this,
            last = me.getPageData().pageCount;
            total=last;
            me.child('#inputItem').setValue(me.child('#inputItem').getValue()+1);
            value=me.child('#inputItem').getValue();
            page = value;
            Ext.getCmp("grid").getColumnsAndStore(caller, condition, page, pageSize);
            me.updateInfo();
            fn(me,value);
        },
        moveLast : function(){
            var me = this,
            last = me.getPageData().pageCount;
            total=last;
            me.child('#inputItem').setValue(last);
            value=me.child('#inputItem').getValue();
            page = value;
            Ext.getCmp("grid").getColumnsAndStore(caller, condition, page, pageSize);
            me.updateInfo();
            fn(me,value);
        },
        onLoad : function() {
            var e = this, d, b, c, a;
            if (!e.rendered) {
                return
            }
            d = e.getPageData();
            b = d.currentPage;
            c = Math.ceil(dataCount / pageSize);
            a = Ext.String.format(e.afterPageText, isNaN(c) ? 1 : c);
            e.child("#afterTextItem").setText(a);
            e.child("#inputItem").setValue(b);
            e.child("#first").setDisabled(b === 1);
            e.child("#prev").setDisabled(b === 1);
            e.child("#next").setDisabled(b === c || c===1);//
            e.child("#last").setDisabled(b === c || c===1);
            e.child("#refresh").enable();
            e.updateInfo();
            e.fireEvent("change", e, d);
        },
        afterOnLoad : function() {
            var e = this, d, c, a;
            if (!e.rendered) {
                return
            }
            d = e.getPageData();
            b = d.currentPage;
            c = Math.ceil(dataCount / pageSize);
            a = Ext.String.format(e.afterPageText, isNaN(c) ? 1 : c);
            e.child("#afterTextItem").setText(a);
            e.updateInfo();
            e.fireEvent("change", e, d);
            e.child('#last').setDisabled(c <= 1 || page == c);
            e.child('#next').setDisabled(c <= 1 || page == c);
        }
});
function fn(me,value){
    me.child('#last').setDisabled(value==total);
    me.child('#next').setDisabled(value==total);
    me.child('#first').setDisabled(value<=1);
    me.child('#prev').setDisabled(value<=1);
}