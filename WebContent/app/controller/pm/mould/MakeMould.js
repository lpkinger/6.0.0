Ext.QuickTips.init();
Ext.define('erp.controller.pm.mould.MakeMould', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
            'core.button.PrintByCondition','core.form.Panel','pm.mould.MakeMould','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.form.YnField',
            'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.PrintA4','core.button.Upload','core.button.ResAudit',
            'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
            'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.grid.YnColumn','core.button.Flow','core.button.Get',
            'core.button.GetMaterial','core.button.DeleteMaterial','core.button.ChangeMaterial','core.button.GetCraft','core.button.HistoryProdIO',
            'core.button.RefreshQty','core.button.MakeFlow','core.trigger.MultiDbfindTrigger','core.button.EnforceEnd',
            'core.button.CalMake','core.button.Check','core.button.ResCheck', 'core.button.End', 'core.button.ResEnd','core.button.ModifyMaterial',
            'core.button.SubRelation','core.button.TurnOSMake','core.button.GetOSVendor','core.button.UpdateRemark','core.button.Commonquery',
            'core.button.UpdateTeamcode','core.button.GetPrice','core.button.OSInfoUpdate','core.button.UpdateMaterialWH',
            'core.button.TurnOSToMake','core.button.BomUseMatch','erp.view.core.grid.HeaderFilter','core.button.UpdateMaStyle',
            'core.button.ShiPAddressUpdate','core.button.MrpOpen','core.button.MrpClose','core.button.DisableBomPast','core.button.CopyByConfigs','core.button.Modify'
            ],
    init:function(){
        var me = this;
        me.FormUtil = Ext.create('erp.util.FormUtil');
        me.GridUtil = Ext.create('erp.util.GridUtil');
        me.BaseUtil = Ext.create('erp.util.BaseUtil');
        this.control({
            '#ma_remark':{
                beforerender: function(field){
                    field.readOnly=false;
                }
            },
            '#ma_teamcode':{
                beforerender: function(field){
                    field.readOnly=false;
                }
            },
            '#ma_style':{
                beforerender: function(field){
                    field.readOnly=false;
                }
            },
            'button[id=Voucher]':{
            	afterrender:function(btn){
            		var btn = Ext.getCmp('Voucher');
            		btn.hide();
            	}
            },
            'erpGridPanel2': { 
                afterrender: function(grid){
                    var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value != 'ENTERING' ){
                        grid.setReadOnly(true);
                    }
                },
                reconfigure:function(grid){
                    var items = grid.store.data.items;
                    var totaluseqty = grid.store.getSum(items, 'mm_totaluseqty');
                    var havegetqty = grid.store.getSum(items, 'mm_havegetqty');
                    if( totaluseqty!=0 || havegetqty!=0){
                        /*Ext.getCmp('resAudit').hide();
                        Ext.getCmp('resCheck').hide();*/
                    }
                },
                itemclick: function(view,record){
                    me.itemclick(view,record,me);
                }
            },
            'field[name=ma_currency]': {
                beforetrigger: function(field) {
                    var value = Ext.getCmp('ma_date').value;
                    if(value) {
                        field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
                    }
                }
            },
            'field[name=ma_tasktype]': {
                afterrender:function(field){
                    if (field.value=='OS' && caller=='Make!Base'){
                        window.location.href=window.location.href.replace('whoami=Make!Base','whoami=Make');
                    }
                }
            },
            'erpBomUseMatchButton':{
                afterrender:function(btn){
                    btn.setDisabled(true);
                }
            },
            'erpMakeFlowButton':{
                click: function(btn){
                        var id=Ext.getCmp('ma_id').value;
                        var formCondition="ma_idIS"+id;
                        var gridCondition = "mf_maid="+Ext.getCmp('ma_id').value;
                        var linkCaller='MakeFlow';
                        var win = new Ext.window.Window(
                                    {  
                                        id : 'win',
                                        height : '90%',
                                        width : '95%',
                                        maximizable : true,
                                        buttonAlign : 'center',
                                        layout : 'anchor',
                                        items : [ {
                                            tag : 'iframe',
                                            frame : true,
                                            anchor : '100% 100%',
                                            layout : 'fit',
                                             html : '<iframe id="iframe_'+linkCaller+'" src="'+basePath+'jsps/pm/make/makeflow.jsp?_noc=1&whoami='+linkCaller+'&gridCondition='+gridCondition+'&formCondition='+formCondition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
                                        } ]

                        });
                        win.show(); 
                }
            },
            'erpSaveButton': {
                click: function(btn){
                    var form = me.getForm(btn), codeField = Ext.getCmp(form.codeField);
                    if(codeField.value == null || codeField.value == ''){
                        me.BaseUtil.getRandomNumber(caller);//自动添加编号
                        var res = me.getLeadCode(Ext.getCmp('ma_kind').value);
                        if(res != null && res != ''){
                            codeField.setValue(res + codeField.getValue());
                        }
                    }
                    this.FormUtil.beforeSave(this);
                }
            },
            'erpCloseButton': {
                click: function(btn){
                    this.FormUtil.beforeClose(this);
                }
            }, 
            'erpDeleteDetailButton': {
                afterrender: function(btn){
                    btn.ownerCt.add({
                        xtype: 'erpSubRelationButton'
                    });
                    btn.ownerCt.add({
                        xtype: 'erpModifyMaterialButton'
                    });
                    btn.ownerCt.add({
                        xtype: 'erpMrpOpenButton'
                    });
                    btn.ownerCt.add({
                        xtype: 'erpMrpCloseButton'
                    });
                    
                }
                
            },
            'erpUpdateButton': {
                afterrender: function(btn){
                    var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value != 'ENTERING' ){
                        btn.hide();
                    }
                },
                click: function(btn){
                    var oldmaqty=0;
                    if (Ext.getCmp('ma_id').value>0){
                        oldmaqty=me.getFdValue('make','ma_qty','ma_id='+Ext.getCmp('ma_id').value); 
                    } 
                    this.FormUtil.onUpdate(this);
                    if (oldmaqty-Ext.getCmp('ma_qty').value!=0){
                        alert('工单需求数量已经被修改，请重新计算用料！');
                    }
                }
            },
            'erpDeleteButton': {
                click: function(btn){
                    me.FormUtil.onDelete(Ext.getCmp('ma_id').value);
                }
            },
            'erpAddButton': {
                click: function(){
                    me.FormUtil.onAdd('addMakeBase', '新增单据', 'jsps/pm/make/makePlant.jsp?whoami=' + caller);
                }
            },
            'erpSubmitButton': {
                afterrender: function(btn){
                    var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value != 'AUDITED'){
                        btn.hide();
                    }
                    status = Ext.getCmp('ma_checkstatuscode');
                    if(status && status.value != 'UNAPPROVED'){
                        btn.hide();
                    }
                },
                click: {	
                    lock: 2000,
                	fn: function(btn) {
	                    /*var type = Ext.getCmp('ma_tasktype').getValue();
	                    if(type != 'MAKE'){// 委外工单 考虑加工单价
	                        var p = Ext.getCmp('ma_price');
	                        if(p && (Ext.isEmpty(p.value) || p.value == 0)){
	                            Ext.Msg.alert("提示","请先填写加工单价后再提交！");
	                            return;
	                        }
	                    }*/
	                    me.FormUtil.onSubmit(Ext.getCmp('ma_id').value);
                   }
                }
            },
            'erpResSubmitButton': {
                afterrender: function(btn){
                    var status = Ext.getCmp('ma_checkstatuscode');
                    if(status && status.value != 'COMMITED'){
                        btn.hide();
                    }
                },
                click: {	
                    lock: 2000,
                	fn: function(btn) {
                   		 me.FormUtil.onResSubmit(Ext.getCmp('ma_id').value);
                	}
                }
            },
            'erpAuditButton': {
                afterrender: function(btn){
                    var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value != 'ENTERING'){
                        btn.hide();
                    }
                },
                click:{	
                    lock: 2000,
                	fn: function(btn){
                    	me.FormUtil.onAudit(Ext.getCmp('ma_id').value);
                	}
                }
            },
            'erpResAuditButton': {
                afterrender: function(btn){
                    var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value != 'AUDITED'){
                        btn.hide();
                    }
                    var checkStatus = Ext.getCmp('ma_checkstatuscode');
                    if(checkStatus && checkStatus.value == 'COMMITED'){
                        btn.hide();
                    }
                },
                click:{	
                    lock: 2000,
                	fn: function(btn){
	                    if (!confirm('确定要反审核单据?')){
	                        return;
	                    }
	                    me.FormUtil.onResAudit(Ext.getCmp('ma_id').value);
                	}
                }
            },
             'erpPrintButton':{
                click:function(btn){
                    var maclass=Ext.getCmp('ma_tasktype').value;
                    var reportName="";
                    var condition="";
                    if(maclass == "MAKE"){
                        reportName="MAKE";
                        condition='{Make.ma_id}='+Ext.getCmp('ma_id').value;
                        //+' and (' + 'isnull({MAKEMATERIAL.MM_MATERIALSTATUS}) or {MAKEMATERIAL.MM_MATERIALSTATUS}=\' \')';
                    } else {
                        reportName="MAKEWW";                
                        condition='{Make.ma_id}='+Ext.getCmp('ma_id').value;
                        //+' and (' + 'isnull({MAKEMATERIAL.MM_MATERIALSTATUS}) or {MAKEMATERIAL.MM_MATERIALSTATUS}=\' \')';                    
                    }
                    
                    var id=Ext.getCmp('ma_id').value;
                    me.FormUtil.onwindowsPrint2(id,reportName,condition);
                }
            },
             'erpPrintA4Button':{
                click:function(btn){
                    var maclass=Ext.getCmp('ma_tasktype').value;
                    var reportName="";
                    var condition="";
                    if(maclass == "MAKE"){
                        reportName="MAKE";
                        condition='{MA_MAKEUSEPRE.ma_id}='+Ext.getCmp('ma_id').value;
                    } else {
                        reportName="MAKEWWA4";
                        condition='{Make.ma_id}='+Ext.getCmp('ma_id').value;
                    }
                    //var condition='{MA_MAKEUSEPRE.ma_id}='+Ext.getCmp('ma_id').value;
                    var id=Ext.getCmp('ma_id').value;
                    me.FormUtil.onwindowsPrint(id,reportName,condition);
                }
            },
            'erpGetMaterialButton':{
                afterrender: function(btn){
                    btn.hide();//暂时不启用
                    var status = Ext.getCmp('ma_chechkstatuscode');
                    if(status && status.value != 'APPROVE'){
                        btn.hide();
                    }
                }  
            },
            'erpDeleteMaterialButton':{
                afterrender: function(btn){
                    btn.hide();//暂时不启用
                    var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value != 'ENTERING'){
                        btn.hide();
                    }
                }
            },
            'erpEnforceEndButton':{
                afterrender: function(btn){
                    var status = Ext.getCmp('ma_statuscode');
                    if(status && (status.value == 'FINISH' || status.value == 'FREEZE')){
                        btn.hide();
                    }
                },
                click: function(btn){
                	Ext.MessageBox.show({
						title:'强制结案',
						msg:'<font style="color:#F00">请输入结案备注:</font>',
						width:300,
						buttons:Ext.MessageBox.OKCANCEL,  
                		multiline:true,  
                		fn:function(btn,text){  
                    		if(btn == 'ok'||btn == 'yes'){
                    			if(text == '' || text == null){
                    				showError('结案备注必填!');
                    			}else{
	                                Ext.Ajax.request({
	                                    url : basePath + 'pm/make/enforceEndMake.action',
	                                    params: {
	                                        id: Ext.getCmp('ma_id').value,
	                                        remark:text
	                                    },
	                                    method : 'post',
	                                    callback : function(options,success,response){
	                                        var localJson = new Ext.decode(response.responseText);
	                                        if(localJson.success){
	                                            Ext.Msg.alert("提示","操作成功！");
	                                            window.location.reload();
	                                        } else {
	                                            if(localJson.exceptionInfo){
	                                                var str = localJson.exceptionInfo;
	                                                if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	                                                    str = str.replace('AFTERSUCCESS', '');
	                                                    showMessage('提示', str);
	                                                    window.location.reload();
	                                                } else if(str == 'OK'){
	                                                    Ext.Msg.alert("提示","强制结案成功！");
	                                                } else {
	                                                    showError(str);return;
	                                                }
	                                            }
	                                        }
	                                    }
	                                }); 
                    			}
                            }
                		}  
    				});
                }
            },
            'erpGetOSVendorButton': { 
                afterrender: function(btn){
                    var status = Ext.getCmp('ma_statuscode');
                    if(status && (status.value == 'FINISH')){
                        btn.hide();
                    }
                    var tasktype = Ext.getCmp('ma_tasktype');
                    if(tasktype && (tasktype.value != 'OS')){
                        btn.hide();
                    }
                },
                click: function(btn){ 
                    if (!confirm('确定要自动获取委外商和单价?')){
                        return;
                    }
                    Ext.Ajax.request({
                        url: basePath + '/scm/purchase/getMakeVendorPrice.action',
                        params: {
                            id: Ext.getCmp('ma_id').value
                        },
                        callback: function(opt, s, r) {
                            var rs = Ext.decode(r.responseText);
                            if(rs.exceptionInfo) {
                                showError(rs.exceptionInfo);
                            } else {
                                Ext.Msg.alert("提示","获取成功！");
                                window.location.reload();
                            }
                        }
                    });  
                }
            },
            'erpGetCraftButton':{
                afterrender: function(btn){
                    btn.hide();//暂时不启用
                    var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value != 'AUDITED'){
                        btn.hide();
                    }
                    if(status && status.value != 'APPROVE'){
                        btn.hide();
                    }
                },
                click: function(btn){
                    me.FormUtil.onResAudit(Ext.getCmp('ma_id').value);
                }
            },
            'erpChangeMaterialButton': {
                afterrender: function(btn){
                    btn.hide();//暂时不启用
                    var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value != 'AUDITED'){
                        btn.hide();
                    }
                }  
            },
            'erpGetPriceButton':{
                afterrender: function(btn){
                    var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value == 'FINISH'){
                        btn.hide();
                    }
                    status = Ext.getCmp('ma_checkstatuscode');
                    if (status && status.value == 'APPROVE'){
                        btn.hide();
                    }
                },
                click: function(btn){
                     var win = new Ext.window.Window({
                     width:300,
                     height:220,
                     id:'win',
                     title:'<h1>取价方式</h1>',
                     items:[{
                         xtype:'radiofield',
                         name:'getprice',
                         id:'radio1',
                         boxLabel:'按供应商'
                     },{
                         xtype:'radiofield',
                         name:'getprice',
                         id:'radio2',
                         boxLabel:'按最低价'
                     }],
                     buttonAlign:'center',
                     buttons:[{
                        xtype:'button',
                        columnWidth:0.12,
                        text:'确认',
                        width:60,
                        iconCls: 'x-button-icon-save',
                        handler:function(btn){
                               //如果选中的是radio1的话getprice为true,否则为false
                               var getprice=Ext.getCmp('radio1').value;
                               var url='';
                               if(getprice){
                                   if(Ext.getCmp('ma_vendcode').value==null||Ext.getCmp('ma_vendcode').value==''){
                                	   showError('供应商号不能为空');
                                	   return ;
                                   }
                                   url='/scm/purchase/getVendorPrice.action';
                               }
                               else{
                                   url='/scm/purchase/getMakeVendorPrice.action';
                               }
                               Ext.Ajax.request({
                                url: basePath + url,
                                params: {
                                	id: Ext.getCmp('ma_id').value,
									vendcode: Ext.getCmp('ma_vendcode').value,
									curr: Ext.getCmp('ma_currency').value
                                },
                                callback: function(opt, s, r) {
                                    var rs = Ext.decode(r.responseText);
                                    if(rs.exceptionInfo) {
                                        showError(rs.exceptionInfo);
                                    } else {
                                        Ext.Msg.alert("提示","获取成功！");
                                        window.location.reload();
                                    }
                                }
                            });
                        }
                    },{
                        xtype:'button',
                        columnWidth:0.1,
                        text:'关闭',
                        width:60,
                        iconCls: 'x-button-icon-close',
                        margin:'0 0 0 10',
                        handler:function(btn){
                            Ext.getCmp('win').close();
                        }
                    }]
                 });
                win.show(); 
                
                }
            },
            'erpCalMakeButton': {  
                afterrender: function(btn){ 
                    var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value != 'ENTERING' && status.value != 'AUDITED'){
                        btn.hide();
                    }
                }   ,
                click: function(btn){
                    if (!confirm('确定要计算用料?')){
                        return;
                    }
                     var code = Ext.getCmp('ma_code').getValue(); 
                     if (code) {
                             Ext.Ajax.request({
                                url : basePath + 'pm/make/setMakeMaterial.action',
                                params: {
                                    caller: caller,
                                    code: code
                                },
                                method : 'post',
                                callback : function(options,success,response){
                                    var res = new Ext.decode(response.responseText);
                                    if(res.exceptionInfo){
                                        showError(res.exceptionInfo);return;
                                    } else { 
                                        window.location.reload();
                                    };
                                }
                            });  
                     }
                }
            },
            'dbfindtrigger[name=ma_saledetno]': {
                afterrender: function(t){
                    t.dbKey = "ma_salecode";
                    t.mappingKey = "sd_code";
                    t.dbMessage = "请先选择订单编号!";
                    var source=Ext.getCmp('ma_source');
                    if(source && source.value=="订单下推"){
                      	t.setHideTrigger(true);
    					t.setReadOnly(true);
    					t.setEditable(false);
                    }
                },
                beforetrigger:function(){
                	var source=Ext.getCmp('ma_source');
                	if(source && source.value=="订单下推"){
                		return false;
                	}
                }
            },
            'dbfindtrigger[name=ma_salecode]': {
	             afterrender: function(t){
	                    var source=Ext.getCmp('ma_source');
	                    if(source && source.value=="订单下推"){
	                      	t.setHideTrigger(true);
	    					t.setReadOnly(true);
	    					t.setEditable(false);
	                    }
	                },
	                beforetrigger:function(){
	                	var source=Ext.getCmp('ma_source');
	                	if(source && source.value=="订单下推"){
	                		return false;
	                	}
	                }
            },
            'combo[name=ma_sourcekind]':{
                change:function(field,newValue){
                    var codefield=Ext.getCmp('ma_salecode');
                    var detnofield=Ext.getCmp('ma_saledetno');
                    if(newValue=='Sale'){
                        //销售预测
                        codefield.dbCaller='Make!Base';
                        detnofield.dbCaller='Make!Base';
                    }else if(newValue=='SaleForeCast'){
                        codefield.dbCaller='Make!Base!ForeCast';
                        detnofield.dbCaller='Make!Base!ForeCast';
                        
                    }
                }
            },
            'dbfindtrigger[name=mm_prodcode]': {
                /*focus: function(t){
                    var grid = Ext.getCmp('grid');
                    var c = null;
                    Ext.each(grid.store.data.items, function(item){
                        if(item.data['mm_prodcode'] != null && item.data['mm_prodcode'] != ''){
                            if(c == null){
                                c = "(pr_code<>'" + item.data['mm_prodcode'] + "'";
                            } else {
                                c += " and pr_code<>'" + item.data['mm_prodcode'] + "'";
                            }
                        }
                    });
                    if(c != null){
                        t.dbBaseCondition = c + ")";
                    }
                }*/
            },
            'textfield[name=ma_wccode]': {
                change: function(field){
                    if(field.value != null && field.value != ''){
                        var grid = Ext.getCmp('grid');
                        var d = field.value;
                        Ext.Array.each(grid.getStore().data.items,function(item){
                            if(item.data['mm_prodcode'] != null && item.data['mm_prodcode'] != '' ){
                                if(item.data['mm_wccode'] == null ||item.data['mm_wccode'] == '' ){
                                    item.set('mm_wccode',d);
                                } 
                            } 
                        });
                    }
                }
            },
            'datefield[name=ma_planenddate]':{
                change: function(f) {
                    var c = Ext.getCmp('ma_planbegindate').getValue();
                    var value = f.value;           
                    if( c != null && c != '' && value !=null && value != ''){
                      if(c > value){
                        Ext.getCmp('ma_planbegindate').setValue('');
                        showError('计划完工日期不能早于计划开工日期');
                      }
                    }
                }               
            },
            
            'datefield[name=ma_planbegindate]':{            
                change: function(f) {
                    var c = Ext.getCmp('ma_planenddate').getValue();
                    var value = f.value;           
                    if( value !=null && value != ''){                       
                       var formatV =  new Date(Ext.util.Format.date(value, 'Y-m-d'));
                       var now =  new Date(Ext.util.Format.date(new Date(),'Y-m-d'))
                        if(now > formatV){
                            Ext.getCmp('ma_planenddate').setValue('');
                            showError('计划开工日期不能早于今天的日期');
                        }
                        if(c != null && c != '' ){
                             if(value > c){
                                Ext.getCmp('ma_planenddate').setValue('');
                                showError('计划完工日期不能早于计划开工日期');
                             }
                        }
                    }
                }               
            },
            'erpCheckButton': {
                afterrender: function(btn){
                    var status = Ext.getCmp('ma_checkstatuscode');
                    if(status && status.value != 'COMMITED'){
                        btn.hide();
                    }
                },
                click:{ 
    				lock: 2000,
	                fn: function(btn){
                  		  me.FormUtil.onCheck(Ext.getCmp('ma_id').value);
	                }
                }
            },
            'erpResCheckButton': {
                afterrender: function(btn){
                    var status = Ext.getCmp('ma_checkstatuscode');
                    var status2 = Ext.getCmp('ma_statuscode');
                    if((status && status.value != 'APPROVE')||(status2 && status2.value == 'FINISH')){
                        btn.hide();
                    }
                    /*if(status && status.value != 'APPROVE' || Ext.getCmp('ma_statuscode').value != 'AUDITED'){
                        btn.hide();
                    }*/
                },
                click:{ 
    				lock: 2000,
	                fn: function(btn){
                   		 me.FormUtil.onResCheck(Ext.getCmp('ma_id').value);
	                }
                }
            },
            'erpEndButton': {
                afterrender: function(btn){
                    var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value != 'AUDITED'){
                        btn.hide();
                    }
                },
                click: function(btn){
                    me.FormUtil.onEnd(Ext.getCmp('ma_id').value);
                }
            },
            'erpResEndButton': {
                afterrender: function(btn){
                    var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value != 'FINISH'){
                        btn.hide();
                    }
                },
                click: function(btn){
                    me.FormUtil.onResEnd(Ext.getCmp('ma_id').value);
                }
            },
            'erpSubRelationButton':{
                click:function(btn){
                    var id=btn.ownerCt.ownerCt.ownerCt.items.items[0].selModel.selected.items[0].data["mm_id"];
                    var formCondition="mm_id IS"+id;
                    var gridCondition="mp_mmid IS"+id;
                    var linkCaller='MakeBase!Sub';
                    var win = new Ext.window.Window(
                                {  
                                    id : 'win',
                                    height : '90%',
                                    width : '95%',
                                    maximizable : true,
                                    buttonAlign : 'center',
                                    layout : 'anchor',
                                    items : [ {
                                        tag : 'iframe',
                                        frame : true,
                                        anchor : '100% 100%',
                                        layout : 'fit',
                                         html : '<iframe id="iframe_'+linkCaller+'" src="'+basePath+'jsps/pm/make/makeCommon.jsp?_noc=1&whoami='+linkCaller+'&gridCondition='+gridCondition+'&formCondition='+formCondition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
                                    } ]

                    });
                    win.show(); 
                },
                afterrender:function(btn){
                    btn.setDisabled(true);
                }
            },
            'erpModifyMaterialButton':{
                click:function(btn){
                    var id=btn.ownerCt.ownerCt.ownerCt.items.items[0].selModel.selected.items[0].data["mm_id"];
                    var formCondition="mm_id IS"+id;
                    var linkCaller='MakeMaterial!Modify';                   
                     var win = new Ext.window.Window(
                            {  
                                id : 'win',
                                height : '90%',
                                width : '95%',
                                maximizable : true,
                                buttonAlign : 'center',
                                layout : 'anchor',
                                items : [ {
                                    tag : 'iframe',
                                    frame : true,
                                    anchor : '100% 100%',
                                    layout : 'fit',
                                     html : '<iframe id="iframe_'+linkCaller+'" src="'+basePath+'jsps/pm/make/modifyForm.jsp?_noc=0&whoami='+linkCaller+'&formCondition='+formCondition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
                                } ],
                                listeners:{
                                  'beforeclose':function(view ,opt){
                                    var grid = Ext.getCmp('grid');
                                    var value = Ext.getCmp('ma_id').value;
                                    var gridCondition = grid.mainField + '=' + value,
                                    gridParam = {caller: caller, condition: gridCondition};
                                    me.GridUtil.loadNewStore(grid, gridParam);  
                                  } 
                                    
                                }
                            });
                    win.show(); 
                },
                afterrender:function(btn){
                    btn.setDisabled(true);
                    
                }
                
            },
            'erpBomUseMatchButton' :{
                click: function(btn){
                    
                    var ma_code = Ext.getCmp('ma_code').value;
                     Ext.Ajax.request({
                        url : basePath + 'pm/make/MakeMaterialCheck.action',
                        params: {
                            code:ma_code
                        },
                        async:false,
                        method : 'post',
                        callback : function(options,success,response){
                                var res = new Ext.decode(response.responseText);
                                if(res.exceptionInfo){
                                    showError(res.exceptionInfo);return;
                                } else {
//                                  var grid = Ext.getCmp('grid');
//                                  var value = Ext.getCmp('ma_id').value;
//                                  var gridCondition = grid.mainField + '=' + value,
//                                      gridParam = {caller: caller, condition: gridCondition};
//                                  me.GridUtil.loadNewStore(grid, gridParam);  
                                
                                     var win = new Ext.window.Window(
                                            {  
                                                id : 'winMatch',
                                                height : '90%',
                                                width : '95%',
                                                maximizable : true,
                                                buttonAlign : 'center',
                                                layout : 'anchor',
                                                items : [ {
                                                    tag : 'iframe',
                                                    frame : true,
                                                    anchor : '100% 100%',
                                                    layout : 'fit',
                                                    html : '<iframe id="iframe_BomUseMatchWin'+ma_code+'" src="'+basePath+'jsps/common/datalist.jsp?_noc=1&whoami=BomUseMatchWin&urlcondition=mc_makecode=\''+ma_code+'\'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
                                                } ]
                                            });
                                    win.show();
                                };
                            }
                    }); 
                },
                afterrender:function(btn){
                    btn.setDisabled(false);
                }
                
            },
            'erpHistoryProdIOButton':{
                click: function(){
                    var macode = Ext.getCmp('ma_code').value;
                    var record = Ext.getCmp('grid').selModel.lastSelected;
                    if(record){
                        var detno = record.data['mm_detno'],prod = record.data['mm_prodcode'];
                        var win = Ext.getCmp('history-win');
                        if(win == null){
                            win = Ext.create('Ext.window.Window', {
                                id: 'history-win',
                                width: '80%',
                                height: '100%',
                                maximizable : true,
                                layout: 'anchor',
                                closeAction: 'hide',
                                setMyTitle: function(code){//@param code 料号
                                    this.setTitle('物料编号:<font color=blue>' + code + '</font>&nbsp;工单号:<font color=blue>' + macode + '</font>&nbsp;的出入库明细&nbsp;&nbsp;');
                                },
                                reload: function(no, code){//@param code 料号
                                    var g = this.down('grid[id=history]');
                                    g.GridUtil.loadNewStore(g, {
                                        caller: g.caller,
                                        condition: "pd_ordercode ='" + macode + "' and pd_orderdetno="+ no + " order by pi_date desc"
                                    });
                                    g = this.down('grid[id=makescrap]');
                                    g.GridUtil.loadNewStore(g, {
                                        caller: g.caller,
                                        condition: "md_mmcode='" + macode + "' AND md_mmdetno=" + no
                                    });
                                    this.setMyTitle(code);
                                }
                            });
                            win.setMyTitle(prod);
                            win.show();
                            win.add(Ext.create('erp.view.core.grid.Panel2', {
                                id: 'history',
                                anchor: '100% 60%',
                                caller: 'ProdInOut!Make!History',
                                condition: "pd_ordercode ='" + macode + "' and pd_orderdetno='"+ detno + "' order by pi_date desc",
                                bbar: null,
                                listeners: {
                                    reconfigure: function(){
                                        win.add(Ext.create('erp.view.core.grid.Panel2', {
                                            id: 'makescrap',
                                            title: '报废单',
                                            anchor: '100% 40%',
                                            caller: 'MakeScrap!JM',
                                            condition: "md_mmcode='" + macode + "' AND md_mmdetno=" + detno,
                                            bbar: null
                                        }));
                                    }
                                }
                            }));
                        } else {
                            win.reload(detno, prod);
                            win.show();
                        }
                    } else {
                        Ext.Msg.alert("提示","请先选择明细!");
                    }
                }
            },
            'erpUpdateMaterialWHButton':{
            	afterrender: function(btn){
            		var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value == 'FINISH'){
                        btn.hide();
                    }
            	}
            },
            'erpUpdateTeamcodeButton':{
            	afterrender: function(btn){
            		var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value == 'FINISH'){
                        btn.hide();
                    }
            	}
            },
            'erpRefreshQtyButton':{
            	afterrender: function(btn){
            		var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value == 'FINISH'){
                        btn.hide();
                    }
            	},
                click: function(btn){
                    var maid=Ext.getCmp('ma_id').value;
                    Ext.Ajax.request({
                        url : basePath + "pm/make/refreshqty.action",
                        params:{
                            id: maid
                        },
                        method:'post',
                        callback:function(options,success,response){
                            var localJson = new Ext.decode(response.responseText);
                            if(localJson.success){
                                Ext.Msg.alert("提示","刷新成功！");
                                window.location.reload();
                            } else {
                                if(localJson.exceptionInfo){
                                    var str = localJson.exceptionInfo;
                                    if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
                                        str = str.replace('AFTERSUCCESS', '');
                                        showError(str);
                                    } else {
                                        showError(str);return;
                                    }
                                }
                            }
                        }
                    });
                }
            }, 
            'erpTurnOSMakeButton':{
            	afterrender: function(btn){
            		var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value == 'FINISH'){
                        btn.hide();
                    }
            	},
                click: function(btn){
                    if (!confirm('确定要转本单据为委外单?')){
                        return;
                    }   
                var kind = Ext.getCmp('ma_kind'),
                bg = 'background:#fffac0;color:#515151;';
                 Ext.create('Ext.window.Window',{
                     width:300,
                     height:120,
                     id:'win',
                     title:'<h1>请选择委外单工单类型</h1>',
                     items:[{
                         xtype:'dbfindtrigger',
                         fieldLabel:'工单类型',
                         name:'makind',
                         editable:false,
                         id:'makind',
                         fieldStyle: kind.allowBlank ? '' : bg
                     }],
                     buttonAlign:'center',
                     buttons:[{
                        xtype:'button',
                        columnWidth:0.12,
                        text:'确认',
                        width:60,
                        iconCls: 'x-button-icon-save',
                        handler:function(btn){
                            var kind=Ext.getCmp('makind').getValue();
                            var maid=Ext.getCmp('ma_id').value; 
                            if(kind == null ||kind == ''){
                                showError("请选择工单类型");
                                return ;
                            }
                            Ext.Ajax.request({
                                url : basePath + "pm/make/turnOSMake.action",
                                params:{
                                    id: maid,
                                    kind:kind,
                                    caller:caller
                                },
                                method:'post',
                                callback:function(options,success,response){
                                    var localJson = new Ext.decode(response.responseText);
                                    if(localJson.success){
                                        Ext.Msg.alert("提示","已成功转委外单！");
                                        var main = parent.Ext.getCmp("content-panel");
                                        main.getActiveTab().close(); 
                                    } else {
                                        if(localJson.exceptionInfo){
                                            var str = localJson.exceptionInfo;
                                            if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
                                                str = str.replace('AFTERSUCCESS', '');
                                                showError(str);
                                            } else {
                                                showError(str);return;
                                            }
                                        }
                                    }
                                }
                            });
                               
                           }
                    },{
                        xtype:'button',
                        columnWidth:0.1,
                        text:'关闭',
                        width:60,
                        iconCls: 'x-button-icon-close',
                        margin:'0 0 0 10',
                        handler:function(btn){
                            Ext.getCmp('win').close();
                        }
                    }]
                 }).show();    
                }
            },
            
            'erpTurnOSToMakeButton':{
            	afterrender: function(btn){
            		var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value == 'FINISH'){
                        btn.hide();
                    }
            	},
                click: function(btn){
                    if (!confirm('确定要转本单据为制造单?')){
                        return;
                    }
                 var kind = Ext.getCmp('ma_kind'),
                 bg = 'background:#fffac0;color:#515151;';
                 Ext.create('Ext.window.Window',{
                     width:300,
                     height:120,
                     id:'win',
                     title:'<h1>请选择制造工单类型</h1>',
                     items:[{
                         xtype:'dbfindtrigger',
                         fieldLabel:'工单类型',
                         name:'makind',
                         editable:false,
                         id:'makind',
                         fieldStyle: kind.allowBlank ? '' : bg
                     }],
                     buttonAlign:'center',
                     buttons:[{
                        xtype:'button',
                        columnWidth:0.12,
                        text:'确认',
                        width:60,
                        iconCls: 'x-button-icon-save',
                        handler:function(btn){
                            var kind=Ext.getCmp('makind').getValue();
                            var maid=Ext.getCmp('ma_id').value; 
                            if(kind == null ||kind == ''){
                                showError("请选择工单类型");
                                return ;
                            }
                            Ext.Ajax.request({
                                url : basePath + "pm/make/turnOSToMake.action",
                                params:{
                                    id: maid,
                                    kind:kind,
                                    caller:caller
                                },
                                method:'post',
                                callback:function(options,success,response){
                                var localJson = new Ext.decode(response.responseText);
                                if(localJson.success){
                                    Ext.Msg.alert("提示","已成功转制造单！");
                                    var main = parent.Ext.getCmp("content-panel");
                                    main.getActiveTab().close(); 
                                } else {
                                    if(localJson.exceptionInfo){
                                        var str = localJson.exceptionInfo;
                                        if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
                                            str = str.replace('AFTERSUCCESS', '');
                                            showError(str);
                                        } else {
                                            showError(str);return;
                                        }
                                    }
                                }
                            }
                            });                            
                           }
                    },{
                        xtype:'button',
                        columnWidth:0.1,
                        text:'关闭',
                        width:60,
                        iconCls: 'x-button-icon-close',
                        margin:'0 0 0 10',
                        handler:function(btn){
                            Ext.getCmp('win').close();
                        }
                    }]
                   }).show();   
             }
            },
            'erpMrpOpenButton':{
                  click: function(btn){
                       var grid = Ext.getCmp('grid');
                       var record = grid.selModel.lastSelected;
                       var id = record.data.mm_id;
                       Ext.Ajax.request({
                           url : basePath + "pm/make/openMrp.action",
                           params: {
                               id:id,
                               caller:caller
                           },
                           method : 'post',
                           async: false,
                           callback : function(options,success,response){
                               var res = new Ext.decode(response.responseText);
                               if(res.exceptionInfo){
                                   showError(res.exceptionInfo);
                                   return;
                               }
                               Ext.Msg.alert('提示','打开MRP成功!'); 
                               var condition='mm_maid='+Ext.getCmp('ma_id').value;
                               me.GridUtil.loadNewStore(grid,{caller:caller,condition:condition});
                           }
                       });
                   },
                   afterrender:function(btn){
                      btn.setDisabled(true);
                   }
              },
            'erpMrpCloseButton': {
                click: function(btn){
                   var grid = Ext.getCmp('grid');
                   var record = grid.selModel.lastSelected;
                   var id = record.data.mm_id;
                   Ext.Ajax.request({
                       url : basePath + "pm/make/CloseMrp.action",
                       params: {
                           id:id,
                           caller:caller
                       },
                       method : 'post',
                       async: false,
                       callback : function(options,success,response){
                           var res = new Ext.decode(response.responseText);
                           if(res.exceptionInfo){
                               showError(res.exceptionInfo);
                               return;
                           }
                           Ext.Msg.alert('提示','关闭MRP成功!'); 
                           var condition='mm_maid='+Ext.getCmp('ma_id').value;
                           me.GridUtil.loadNewStore(grid,{caller:caller,condition:condition});
                       }
                   });
               },
               afterrender:function(btn){
                 btn.setDisabled(true);
               }
            },
            'erpDisableBomPastButton': {
                afterrender:function(btn){
                    var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value == 'FINISH'){
                        btn.hide();
                    }
                }
            },
            'erpShiPAddressUpdateButton':{
            	afterrender:function(btn){
                    var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value == 'FINISH'){
                        btn.hide();
                    }
                }
            },
            'field[name=ma_prodcode]': {
                change: function(f){
                    if(f.value != null && f.value != ''){
                        me.FormUtil.getFieldValue('BOM', 'bo_id', "bo_mothercode='" + f.value + "'", 'ma_bomid');
                    }
                }
            },
            'field[name=ma_qty]': {
                /*
                change: function(f){
                    if(f.value == null || f.value == ''){
                        f.value = 0;
                    }
                    if(Ext.getCmp('ma_total')){
                       Ext.getCmp('ma_total').setValue(f.value*Ext.getCmp('ma_price').value);
                    }
                    var grid = Ext.getCmp('grid');
                    if(grid){
                        var items = grid.store.data.items;
                        Ext.each(items, function(item){//制单需求=制单套数*单位用量
                            if(item.data['mm_oneuseqty'] != null && item.data['mm_oneuseqty'] != 0){
                                item.set('mm_qty', item.data['mm_oneuseqty']*f.value);
                            }
                        });
                    }
                }
                */
            },
            'erpUpdateRemarkButton':{
                click:function(){
                    var remark=Ext.getCmp('ma_remark');
                        me.updateRemark(remark.value,Ext.getCmp('ma_id').value);
                    
                }
            },
            'erpUpdateTeamcodeButton':{
            	afterrender: function(btn){
            		var status = Ext.getCmp('ma_statuscode');
                    if(status && status.value == 'FINISH'){
                        btn.hide();
                    }
            	},
                click:function(){
                    var teamcode=Ext.getCmp('ma_teamcode');
                        me.updateTeamcode(teamcode.value,Ext.getCmp('ma_id').value);
                    
                }
            },
            'erpUpdateMaStyleButton':{//更新流程类型
                click:function(){
                    var mastyle=Ext.getCmp('ma_style');
                    me.updateMaStyle(mastyle.value,Ext.getCmp('ma_id').value);                  
                },
                afterrender:function(btn){
                    var checkstatus = Ext.getCmp("ma_checkstatuscode");
                    var status = Ext.getCmp("ma_statuscode");
                    if((checkstatus && (checkstatus.value == 'APPROVE'||checkstatus.value == 'COMMITED'))||(status && (status.value == 'ENTERING' || status.value == 'FINISH'))){
                        btn.hide();
                    }
                }
            },          
            'field[name=ma_price]': {
                change: function(f){
                    if(f.value == null || f.value == ''){
                        f.value = 0;
                    }
                    if(Ext.getCmp('ma_total')){//字段存在赋值，不存在不赋值
                       Ext.getCmp('ma_total').setValue(f.value*Ext.getCmp('ma_qty').value);
                    }
                }
            },
            'field[name=mm_oneuseqty]': {
                change: function(f){//制单需求=制单套数*单位用量
                    //制单需求=制单套数*单位用量
                    if(f.value != null && f.value > 0 && Ext.getCmp('ma_qty') && Ext.getCmp('ma_qty').value > 0){
                        var record = Ext.getCmp('grid').selModel.getLastSelected();
                        var precision = record.data['pr_precision'];
                        var qty = f.value*Ext.getCmp('ma_qty').value;
                        if(precision!=null && !isNaN(parseInt(precision)) && parseInt(precision)>=0){
                        	qty = Math.ceil(qty*Math.pow(10, precision))/Math.pow(10,precision); 
                        }
                        if(record.data['mm_qty'] != qty){
                            record.set('mm_qty', qty);
                        }
                    }
                }
            }
        });
    },
    getForm: function(btn){
        return btn.ownerCt.ownerCt;
    },
    itemclick:function(view,record,me){
        var show=0;
        me.GridUtil.onGridItemClick(view,record); 
        var fieldValue=record.data["mm_prodcode"];
        if(fieldValue==undefined||fieldValue==""||fieldValue==null){
           show=1;
           return; 
        }
        var status = Ext.getCmp('ma_statuscode').getValue(); 
        if(show==1 || status=='FINISH' ){
        Ext.getCmp('SubRelation').setDisabled(true);
        Ext.getCmp('ModifyMaterial').setDisabled(true); 
        Ext.getCmp('MrpClose').setDisabled(true);
        Ext.getCmp('MrpOpen').setDisabled(true);
        }else {
            Ext.getCmp('SubRelation').setDisabled(false);
            Ext.getCmp('ModifyMaterial').setDisabled(false);
            Ext.getCmp('MrpClose').setDisabled(false);
            Ext.getCmp('MrpOpen').setDisabled(false);
            }
    },
    updateRemark:function(remark,id){
        Ext.Ajax.request({
            url : basePath + 'pm/mould/updateRemark.action',
            params: {remark:remark,id:id},
            method : 'post',
            async:false,
            callback : function(options,success,response){
                var res = new Ext.decode(response.responseText);
                if(res.exceptionInfo != null){
                    showError(res.exceptionInfo);return;
                }
                showMessage("提示", '更新成功！');
                window.location.reload();
            }
        });
    },
    updateTeamcode:function(value,id){
        Ext.Ajax.request({
            url : basePath + 'pm/mould/updateTeamcode.action',
            params: {value:value,id:id},
            method : 'post',
            async:false,
            callback : function(options,success,response){
                var res = new Ext.decode(response.responseText);
                if(res.exceptionInfo != null){
                    showError(res.exceptionInfo);return;
                }
                showMessage("提示", '更新成功！');
                window.location.reload();
            }
        });
    },
     updateMaStyle:function(value,id){
        Ext.Ajax.request({
            url : basePath + 'pm/mould/updateMaStyle.action',
            params: {value:value,id:id},
            method : 'post',
            async:false,
            callback : function(options,success,response){
                var res = new Ext.decode(response.responseText);
                if(res.exceptionInfo != null){
                    showError(res.exceptionInfo);return;
                }
                showMessage("提示", '更新成功！');
                window.location.reload();
            }
        });
    },
    getFdValue: function(tablename,field, condition){//根据特征项code和特征值码获取特征值
        var result = '';
        Ext.Ajax.request({
            url : basePath + "/common/getFieldData.action",
            params: {
                caller: tablename,
                field: field,
                condition: condition
            },
            method : 'post',
            async: false,
            callback : function(options,success,response){
                var res = new Ext.decode(response.responseText);
                if(res.exceptionInfo){
                    showError(res.exceptionInfo);return;
                }
                if(res.success){
                    result = res.data;
                }
            }
        });
        return result;
    },
    getRandomNumber: function(table, type, codeField){
        if(Ext.getCmp('ma_kind')){
            var form = Ext.getCmp('form');
            if(form){
            table = table == null ? form.tablename : table;
            }
            type = type == null ? 2 : type;
            codeField = codeField == null ? form.codeField : codeField;
            Ext.Ajax.request({
                url : basePath + 'pm/mould/getCodeString.action',
                async: false,//同步ajax请求
                params: {
                    caller: caller,//如果table==null，则根据caller去form表取对应table
                    table: table,
                    type: type,
                    conKind:Ext.getCmp('ma_kind').getValue()
                },
                method : 'post',
                callback : function(options,success,response){
                    var localJson = new Ext.decode(response.responseText);
                    if(localJson.exceptionInfo){
                        showError(localJson.exceptionInfo);
                    }
                    if(localJson.success){
                        Ext.getCmp(codeField).setValue(localJson.code);
                    }
                }
            });
        } else {
            this.BaseUtil.getRandomNumber(caller);//自动添加编号
        }

    },
    getLeadCode: function(type) {
        var result = null;
        Ext.Ajax.request({
            url : basePath + 'common/getFieldData.action',
            async: false,
            params: {
                caller: 'MakeKind',
                field: 'mk_excode',
                condition: 'mk_name=\'' + type + '\''
            },
            method : 'post',
            callback : function(opt, s, res){
                var r = new Ext.decode(res.responseText);
                if(r.exceptionInfo){
                    showError(r.exceptionInfo);return;
                } else if(r.success){
                    result = r.data;
                }
            }
        });
        return result;
    }
});