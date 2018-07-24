Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.CustomerRelive', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.sale.CustomerRelive','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn','core.form.FileField',
      		'core.button.CopyAll','core.button.CustAnticipate'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				me.onGridItemClick(selModel, record);
    			},
    			reconfigure: function(grid, store, columns){
					var detail = getUrlParam('detail');
					gridCondition = getUrlParam('gridCondition');
					if(detail&&!gridCondition){
						me.GridUtil.autoDbfind(grid, 'crd_custcode', detail);
					}
				}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				me.FormUtil.beforeSave(me);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete({id: Number(Ext.getCmp('cr_id').value)});
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.beforeUpdate();
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addCustomerRelive', '客户解挂申请', 'jsps/scm/sale/customerrelive.jsp' );
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('cr_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('cr_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('cr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('cr_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('cr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('cr_id').value);
    			}
    		},

    		'erpPrintButton':{
    			click:function(btn){
    				
    			}
    		},
    		'erpCustAnticipateButton' : {
				click : function(btn) {
					//直接打开逾期应收账款汇总（按客户），传界面客户编号+系统当前日期条件
					var grid = btn.grid ||btn.ownerCt.ownerCt;
					var records = grid.selModel.getSelection();
					if(records.length > 0){
						var custcode=records[0].data.crd_custcode;
						var now = new Date();
						var date_=now.getFullYear()+"-"+((now.getMonth()+1)<10?"0":"")+(now.getMonth()+1)+"-"+(now.getDate()<10?"0":"")+now.getDate();
						Ext.Ajax.request({
							url : basePath + 'common/enterprise/getprinturl.action?caller=ARBill!Cust!Print',
							callback : function(opt, s, r) {
								var re = Ext.decode(r.responseText);
								if(printType=='jasper'||re.printtype=='jasper'){
									var params=new Object(),otherParameters=new Object();
			    					params['todate']=date_;
			    					otherParameters['todate']=date_;
									Ext.Ajax.request({
								    	url : basePath +'common/JasperReportPrint/print.action',
										params: {
											params: unescape(escape(Ext.JSON.encode(params))),
											caller:'ARBill!CSAA!Print',
											reportname:''
										},
										method : 'post',
										timeout: 360000,
										callback : function(options,success,response){
											form.setLoading(false);
											var res = new Ext.decode(response.responseText);
											if(res.success){
												var condition='';
												condition = res.info.whereCondition==''?'where 1=1':'where '+res.info.whereCondition;
												condition = condition + "and ab_date <= to_date('"+date_+"','yyyy-mm-dd') and ab_custcode='"+custcode+"'";
												other=Ext.encode(otherParameters);
												var url = res.info.printurl + '?userName='+res.info.userName+'&reportName='+res.info.reportName+'&whereCondition='+condition+'&otherParameters='+other+' &printType='+res.info.printtype;
												window.open(url,'_blank');
											}else if(res.exceptionInfo){
												var str = res.exceptionInfo;
												showError(str);return;
											}
										}
								    });
								}else{
									var defaultCondition = re.condition;
									var thisreport = re.reportname;
									var commonContition="{ARBill.AB_DATE}<=date('"+date_+"') and {ARBill.ab_custcode}='"+custcode+"'";
									if (defaultCondition != null) {
										commonContition = defaultCondition + ' and ' + commonContition;
									}
									var whichsystem = re.whichsystem;
									var urladdress = "";
									var rpname = re.reportName;
									if (Ext.isEmpty(rpname) || rpname == "null") {
										urladdress = re.printurl;
									} else if (rpname.indexOf(thisreport) > 0) {
										urladdress = re.ErpPrintLargeData;
									} else {									
										urladdress = re.printurl;
									}
									me.FormUtil.batchPrint('', thisreport, commonContition, '应收账龄分析表', date_, '','',
												'', urladdress, whichsystem);								
									}
								}								
						});
					}
				}
			}
    	});
    }, 
	beforeUpdate: function(){
		var bool = true;
		if (bool) {
			this.FormUtil.onUpdate(this);
		}
	},
    onGridItemClick: function(selModel, record){//grid行选择
    	if ( record.data.crd_custcode != null&& record.data.crd_custcode != '') {
            var btn = Ext.getCmp('erpCustAnticipateButton');
            btn && btn.setDisabled(false);
        }else{
        	var btn = Ext.getCmp('erpCustAnticipateButton');
            btn && btn.setDisabled(true);
        }
    	this.GridUtil.onGridItemClick(selModel, record);    	
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});