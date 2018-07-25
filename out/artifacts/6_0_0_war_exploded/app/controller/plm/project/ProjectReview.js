Ext.QuickTips.init();
Ext.define('erp.controller.plm.project.ProjectReview', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['plm.project.ProjectReview', 'core.form.Panel', 'core.form.CheckBoxGroup', 'core.grid.Panel5', 'core.grid.Panel2', 'core.form.HrefField', 'core.grid.YnColumn', 'core.form.FileField', 
            'core.button.ProjectReview', 'core.button.Add', 'core.button.Submit', 'core.button.Audit', 'core.button.Save', 'core.button.Close', 
            'core.button.Print', 'core.button.Update', 'core.button.Delete', 'core.button.PlanMainTask', 'core.button.ResAudit', 
            'core.button.ResSubmit', 'core.button.Load', 
            'core.trigger.TextAreaTrigger', 'core.trigger.DbfindTrigger', 'plm.budget.ProjectChart', 
            'plm.project.ReviewForm', 'plm.project.ProjectCostBudgetGrid'],
    init: function() {
        var me = this;
        this.control({
            'projectcostbudget': {
                itemclick: this.onGridItemClick
            },
            'erpGridPanel2[id=projectKeyDeviceGrid]': {
                itemclick: this.onGridItemClick1
            },
            'erpCloseButton': {
            	   click: function(btn){
	    				   this.FormUtil.beforeClose(this);
	    			}
               /* afterrender: function(btn) {
                    var form = me.getForm(btn);
                    var readOnly = statuscode != 'ENTERING';
                    var items = me.getReviewItems(readOnly);
                    form.add(items);
                    form.add([{
                        xtype: 'fieldset',
                        title: '<h2>评审结果</h2>',
                        columnWidth: 1,
                        collapsible: true,
                        frame: false,
                        height: 100,
                        html: '<div style="background-color: #FFFAFA;color:red">' + Ext.getCmp('pr_systemresult').getValue() + '</div>'
                    }]);
                }*/
            },
            'htmleditor[name=pr_prjcode]': {
                afterrender: function(editor) {
                    editor.getToolbar().hide();
                    editor.readOnly = true;
                    editor.setValue('<a style="text-decoration:none;" href="javascript:parent.openFormUrl(' + editor.value + ',\'prj_code\',\'jsps/plm/project/project.jsp\',\'立项申请\'' + ');">' + editor.value + '</a>');

                }
            },
            'erpFormPanel': {
                beforerender: function(form) {

                }
            },
            'erpLoadButton': {
                click: function(btn) {
                    me.loadKeyDevice(Ext.getCmp('pr_producttype').getValue());
                },
                afterrender: function(btn) {
                    if (statuscode != 'ENTERING') {
                        btn.hide();
                    }
                }
            },
            'erpUpdateButton': {
                click: function(btn) {
                    me.update();
                },
                afterrender: function(btn) {
                    statuscode = Ext.getCmp('pr_statuscode').getValue();
                    if (statuscode != 'ENTERING') {
                        btn.hide();
                    }
                }

            },
            'erpSubmitButton': {
                click: function(btn) {
                    this.FormUtil.onSubmit(Ext.getCmp('pr_id').getValue());
                },
                afterrender: function(btn) {
                    if (statuscode != 'ENTERING') {
                        btn.hide();
                    }
                }
            },
            'erpResSubmitButton': {
                click: function(btn) {
                    this.FormUtil.onResSubmit(Ext.getCmp('pr_id').getValue());
                },
                afterrender: function(btn) {
                    if (statuscode != 'COMMITED') {
                        btn.hide();
                    }
                }
            },
            'erpAuditButton': {
                click: function(btn) {
                    this.FormUtil.onAudit(Ext.getCmp('pr_id').getValue());
                },
                afterrender: function(btn) {
                    if (statuscode != 'COMMITED') {
                        btn.hide();
                    }
                }
            },
            'erpResAuditButton': {
                click: function(btn) {
                    this.FormUtil.onResAudit(Ext.getCmp('pr_id').getValue());
                },
                afterrender: function(btn) {
                    if (statuscode != 'AUDITED') {
                        btn.hide();
                    }
                }
            },
            'erpPlanMainTaskButton': {
                click: function(btn) {
                    me.PlanMainTask(btn);
                },
                afterrender: function(btn) {
                    if (statuscode != 'AUDITED') {
                        btn.hide();
                    }
                }
            },
            'erpDeleteButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pr_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onDelete((Ext.getCmp('pr_id').value));
                }
            }
        });
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.gridLastSelected = record;
    	var grid = Ext.getCmp('projectCostBudgetGrid');
    	grid.lastSelectedRecord = record;
    	if(record.data[grid.necessaryField] == null || record.data[grid.necessaryField] == ''){
    		this.gridLastSelected.findable = true;//空数据可以在输入完code，并移开光标后，自动调出该条数据
    	} else {
    		this.gridLastSelected.findable = false;
    	}
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    onGridItemClick1: function(selModel, record){//grid行选择
    	this.gridLastSelected = record;
    	var grid = Ext.getCmp('projectKeyDeviceGrid');
    	if(record.data[grid.necessaryField] == null || record.data[grid.necessaryField] == ''){
    		this.gridLastSelected.findable = true;//空数据可以在输入完code，并移开光标后，自动调出该条数据
    	} else {
    		this.gridLastSelected.findable = false;
    	}
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getReviewItems: function(readOnly) {
        var me = this;
        var reviewitem = Ext.getCmp('pr_reviewitem').getValue().split("#");
        var reviewtitle = Ext.getCmp('pr_reviewtitle').getValue().split("#");
        var reviewresult = Ext.getCmp('pr_reviewresult').getValue().split("#");
        var items = new Array();
        var count = reviewitem.length % 2; //是否是2的倍
        for (var i = 0; i < reviewitem.length; i++) {
            if (count == 0 || i < reviewitem.length - 1) {
                items.push({
                    xtype: 'fieldset',
                    title: reviewtitle[i],
                    groupName: reviewtitle[i],
                    columnWidth: 0.5,
                    groupkind: 'review',
                    layout: 'column',
                    height: 130,
                    defaults: {
                        columnWidth: 1
                    },
                    collapsible: true,
                    readOnly: true,
                    setReadOnly: function(bool) {
                        var set = this,
                        inputEl = set.inputEl;
                        console.log(set);
                        if (inputEl) {
                            inputEl.dom.readOnly = readOnly;
                            inputEl.dom.setAttribute('aria-readonly', readOnly);
                        }
                        set[readOnly ? 'addCls': 'removeCls'](set.readOnlyCls);
                        set.readOnly = readOnly;
                        Ext.Array.each(set.items.items,
                        function(ite, index) {
                            if (ite.xtype == 'textarea') {
                                ite.setReadOnly(bool);
                                console.log(bool);
                                if (!bool) ite.setFieldStyle("background:#fffac0;color:#515151;");
                                else ite.setFieldStyle("background:#FFFAFA;color:#515151;");
                            } else {
                                Ext.Array.each(ite.items.items,
                                function(it) {
                                    it.setReadOnly(bool);
                                });
                            }
                        });
                    },
                    items: [{
                        xtype: 'textarea',
                        value: reviewitem[i],
                        readOnly: readOnly,
                        groupName: reviewtitle[i]
                    },
                    {
                        xtype: 'radiogroup',
                        fieldLabel: '评审结果',
                        radioValue: reviewresult[i],
                        id: 'rating_' + [i],
                        columns: 3,
                        groupName: reviewtitle[i],
                        readOnly: readOnly,
                        defaults: {
                            fieldCls: 'myradio',
                            listeners: {
                                /*change:function(radio){
	    								   radio.ownerCt.radioValue=radio.inputValue;
	    							   },*/
                                beforerender: function(radio) {
                                    if (radio.ownerCt.radioValue == radio.inputValue) {
                                        radio.checked = true;
                                    }
                                }
                            },
                            name: 'rating_' + [i]
                        },
                        items: me.getRadioItems(reviewresult[i], readOnly)
                    }]
                });
            } else {
                items.push({
                    xtype: 'fieldset',
                    title: reviewtitle[i],
                    groupName: reviewtitle[i],
                    columnWidth: 1,
                    groupkind: 'review',
                    layout: 'column',
                    collapsible: true,
                    defaults: {
                        columnWidth: 1
                    },
                    readOnly: true,
                    items: [{
                        xtype: 'textarea',
                        value: reviewitem[i],
                        readOnly: readOnly
                    },
                    {
                        xtype: 'radiogroup',
                        fieldLabel: '评审结果',
                        columnWidth: 0.5,
                        columns: 3,
                        radioValue: reviewresult[i],
                        id: 'rating_' + [i],
                        readOnly: readOnly,
                        defaults: {
                            fieldCls: 'myradio',
                            /*listeners:{
	    							   change:function(radio){
	    								   radio.ownerCt.radioValue=radio.inputValue;
	    							   } 
	    						   },*/
                            name: 'rating_' + [i]
                        },
                        items: me.getRadioItems(reviewresult[i], readOnly)
                    }]
                });
            }
        };
        return items;
    },
    getRadioItems: function(radiovalue, readOnly) {
        var arr = new Array();
        var object = null;
        for (var i = 3; i > 0; i--) {
            object = new Object();
            object.inputValue = i;
            object.readOnly = readOnly;
            if (i == 3) {
                object.boxLabel = 'A';
            } else if (i == 2) {
                object.boxLabel = 'B';
            } else {
                object.boxLabel = 'C';
            }
            if (radiovalue == i) {
                object.checked = true;
            }
            arr.push(object);
        }
        arr.push({
            boxLabel: 'O',
            hidden: true,
            checked: i == 0,
            inputValue: 0
        });
        return arr;
    },
    loadKeyDevice: function(producttype) {
        Ext.Ajax.request({
            url: basePath + 'plm/review/loadKeyDevice.action',
            method: 'post',
            params: {
                producttype: producttype,
                prid: Ext.getCmp('pr_id').getValue()
            },
            callback: function(options, success, response) {
                var rs = new Ext.decode(response.responseText);
                if (rs.exceptionInfo) {
                    showError(rs.exceptionInfo);
                    return;
                }
                if (rs.success) {
                    var grid = Ext.getCmp('projectKeyDeviceGrid');
                    grid.GridUtil.loadNewStore(grid, {
                        caller: "ProjectKeyDevice",
                        condition: 'pkd_prid=' + Ext.getCmp('pr_id').getValue()
                    });
                }
            }
        });
    },
    getForm: function(btn) {
        return btn.ownerCt.ownerCt;
    },
    save: function(btn) {
        var me = this;
        var form = me.getForm(btn);
        if (Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '') {
            me.BaseUtil.getRandomNumber(); //自动添加编号
        }
    },
    update: function(btn) {
        var mm = this;
		var form = Ext.getCmp('form');
		if(! mm.FormUtil.checkForm()){
			return;
		}
		var grids = Ext.ComponentQuery.query('gridpanel');
		if(grids.length > 0){
			var param1 = mm.GridUtil.getGridStore(grids[0]);
			var param2 = mm.GridUtil.getGridStore(grids[1]);
			mm.onUpdate(param1,param2);
		} else {
			mm.onUpdate([],[]);
		}
        /*  	   var form =Ext.getCmp('form');
	    	   var reviewresult="",reviewitem="",index=1,length=items.length,systemresult="",count=0,reviewlength=0,radioValue=0;	   
	    	   Ext.Array.each(form.items.items,function(item){
	    		   if(item.groupkind == 'review'){
	    		   var checked=item.items.items[1].getChecked()[0];
	    		   radioValue=checked.inputValue?checked.inputValue:0;
	    		   if(index<length){
	    			   reviewitem+=item.items.items[0].value+"#";
	    			   reviewresult+=radioValue+"#";
	    			   count+=Number(radioValue);
	    		   }else {
	    			   reviewitem+=item.items.items[0].value;
	    			   reviewresult+=radioValue;
	    			   count+=Number(radioValue);
	    		   }
	    		   if(radioValue!=0){
	    			   reviewlength++;
	    		   }
	    		   index++; 
	    		   }
	    	   });
//	    	   count=parseFloat(count+'.00' )/reviewlength;
//	    	   if(count==3){
//	    		   systemresult="<center><h5>评审等级: A</h5> 共"+length+"条评审项;评审"+reviewlength+"项;<br/>这么牛啊!果断开搞啊是不 !有奔头!</center>";
//	    	   }else if(count>2){
//	    		   systemresult="<center><h5>评审等级: B+</h5> 共"+length+"条评审项;评审"+reviewlength+"项;<br/>还可以风险很小!可以搞搞</center>";
//	    	   }else if(count==2){
//	    		   systemresult="<center><h5>评审等级: B</h5> 共"+length+"条评审项;评审"+reviewlength+"项;<br/>一般般!需要注意啊!</center>";
//	    	   }else if(count>1){
//	    		   systemresult="<center><h5>评审等级: B-</h5> 共"+length+"条评审项;评审"+reviewlength+"项;<br/>不行啊!真是不行!</center>";
//	    	   }else{
//	    		   systemresult="<center><h5>评审等级: C</h5> 共"+length+"条评审项;评审"+reviewlength+"项;<br/>果断不行，赚钱是好的，投资需谨慎啊!</center>";
//	    	   }
//	    	   Ext.getCmp('pr_systemresult').setValue(systemresult);
	    	   Ext.getCmp('pr_reviewitem').setValue(reviewitem);
	    	   Ext.getCmp('pr_reviewresult').setValue(reviewresult);*/
    },
    onUpdate:function(param1,param2){
		var me = this;
		var form = Ext.getCmp('form');
		param1 = param1 == null ? [] : "[" + param1.toString() + "]";
		param2 = param2 == null ? [] : "[" + param2.toString() + "]";
		if(form.getForm().isValid()){
			//form里面数据
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					//number类型赋默认值，不然sql无法执行
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
			});
			if(!me.FormUtil.contains(form.updateUrl, '?caller=', true)){
				form.updateUrl = form.updateUrl + "?caller=" + caller;
			}
			me.FormUtil.update(r, param1,param2);
		}else{
			me.FormUtil.checkForm();
		}
	},
    PlanMainTask: function(btn) {
        var form = btn.ownerCt.ownerCt;
        var id = Ext.getCmp('pr_id').getValue();
        Ext.Ajax.request({
            url: basePath + form.planTaskUrl,
            params: {
                id: id
            },
            method: 'post',
            callback: function(options, success, response) {
                var localJson = new Ext.decode(response.responseText);
                if (localJson.success) {
                    Ext.Msg.alert('提示', '下达研发任务书成功!', window.location.reload());
                } else {
                    if (localJson.exceptionInfo) {
                        var str = localJson.exceptionInfo;
                        if (str.trim().substr(0, 12) == 'AFTERSUCCESS') { //特殊情况:操作成功，但是出现警告,允许刷新页面
                            str = str.replace('AFTERSUCCESS', '');
                            submitSuccess(function() {
                                window.location.reload();
                            });
                        }
                        showMessage("提示", str);
                        return;
                    }
                }
            }
        });
    }
});