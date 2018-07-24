Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.CreditChange', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'scm.sale.CreditChange', 'core.form.Panel',
			'core.form.MultiField', 'core.form.FileField', 'core.button.Add',
			'core.button.Save', 'core.button.Close', 'core.button.Upload',
			'core.button.Update', 'core.button.Delete', 'core.button.Sync',
			'core.button.Submit', 'core.button.ResSubmit', 'core.button.Print',
			'core.button.ResAudit', 'core.button.Audit','core.form.SeparNumber',
			'core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger',
			'core.form.YnField', 'core.button.CustAnticipate','core.button.FormsDoc'],
	init : function() {
		var me = this;
		this.control({
			'erpFormPanel': {
    			afterload: function(form){
    				var main = getUrlParam('main');
					formCondition = getUrlParam('formCondition');
					if(main&&!formCondition){
						me.FormUtil.autoDbfind(caller, 'cc_custcode', main);
					}
    			}
    		},
			'erpSaveButton' : {
				click : function(btn) {
					var form = me.getForm(btn);
					if (Ext.getCmp(form.codeField).value == null
							|| Ext.getCmp(form.codeField).value == '') {
						me.BaseUtil.getRandomNumber();// 自动添加编号
					}
					var newtempdate = new Date(Ext.util.Format.date(Ext
							.getCmp('cc_newtempcreditdate').value, 'Y-m-d'));
					var now = new Date(Ext.util.Format
							.date(new Date(), 'Y-m-d'));
					if(Ext.getCmp('cc_newtempcreditdate').value==null && Ext.getCmp('cc_newtempcredit').value!=null && Ext.getCmp('cc_newtempcredit').value >0){
						showError('请先填写临时额度有效期');
						return;
					}
					if (now > newtempdate) {
						showError('新临时额度有效期小于当前日期，请更改!');
						return;
					}
					this.FormUtil.beforeSave(this);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					var newtempdate = new Date(Ext.util.Format.date(Ext
							.getCmp('cc_newtempcreditdate').value, 'Y-m-d'));
					var now = new Date(Ext.util.Format
							.date(new Date(), 'Y-m-d'));
					if(Ext.getCmp('cc_newtempcreditdate').value==null && Ext.getCmp('cc_newtempcredit').value!=null && Ext.getCmp('cc_newtempcredit').value >0){
						showError('请先填写临时额度有效期');
						return;
					}
					if (now > newtempdate) {
						showError('新临时额度有效期小于当前日期，请更改!');
						return;
					}
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton' : {
				click : function() {
					me.FormUtil.onAdd('addCreditChange', '新增客户信用变更','jsps/scm/sale/creditChange.jsp');
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('cc_id').value);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cc_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('cc_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cc_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('cc_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cc_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('cc_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cc_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('cc_id').value);
				}
			},
			'erpPrintButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cc_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					var reportName = '';
					reportName = "CreditChange";
					var condition = '{CreditChange.cc_id}='
							+ Ext.getCmp('cc_id').value + '';
					var id = Ext.getCmp('cc_id').value;
					me.FormUtil.onwindowsPrint(id, reportName, condition);
				}
			},
			'textfield[name=cc_custcode]' : {
				aftertrigger : function(field) {
					if (field != null && field != '') {
						Ext.getCmp('cc_newcredit').setValue(
								Ext.getCmp('cc_credit').value);
					}
				}
			},
			'erpCustAnticipateButton' : {
				click : function(btn) {
					//直接打开集团逾期应收汇总表（按客户），传界面客户编号+系统当前日期条件
					var form = me.getForm(btn);
					var custcode=Ext.getCmp('cc_custcode').value;
					var now = new Date();
					var date_=now.getFullYear()+"-"+((now.getMonth()+1)<10?"0":"")+(now.getMonth()+1)+"-"+(now.getDate()<10?"0":"")+now.getDate();
					Ext.Ajax.request({
						url : basePath + 'common/enterprise/getprinturl.action?caller=FIN!ARBILL_VIEW_Custom!group',
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
											condition=res.info.whereCondition==''?'where 1=1':'where '+res.info.whereCondition;
											condition = condition + "and ab_date <= to_date('"+date_+"','yyyy-mm-dd') and ab_custcode='"+custcode+"'";
											other=Ext.encode(otherParameters);
											var url = res.info.printurl + '?userName='+res.info.userName+'&reportName='+res.info.reportName+'&whereCondition='+condition+'&otherParameters= '+other+' &printType='+res.info.printtype;
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
								var commonContition="{ARBILL_VIEW.AB_DATE}<=date('"+date_+"') and {ARBILL_VIEW.AB_CUSTCODE}='"+custcode+"'";
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
		});
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	}
});