Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.customzl', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'scm.product.customzl','core.form.Panel',
      		'core.button.Audit','core.button.Save','core.button.Close','core.button.Add','core.button.Delete',
      		'core.button.Upload','core.button.Update','core.button.Delete','core.button.Print','core.button.DateCalculate',
      		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField',
      		'core.form.FileField','core.form.MonthDateField'
      	],
      	init:function(){
      		var me = this;
      		this.control({ 
      			'erpSaveButton': {
      				click: function(btn){
      					this.FormUtil.beforeSave(this);
      				}
      			},
      			'erpAddButton': {
    				click: function(){
    					me.FormUtil.onAdd('addcustomzl'+new Date().getTime(), '新增自定义区间', 'jsps/scm/product/customzl.jsp');
    				}
    			},
	   	   		'erpUpdateButton': {
	   	   			click: function(btn){
	   	   				this.FormUtil.onUpdate(this);
	   	   			}
	   	   		},
	   	   	    'erpDeleteButton' : {
					click: function(btn){
						me.FormUtil.onDelete(Ext.getCmp('cz_id').value);
					}
				},
				'erpDateCalculateButton':{
      				click:function(btn){
      					me.calculate(btn);
      				}
      			},
	   	   	    'erpPrintButton': {
	   	   			click: function(btn){
			    	 	var me = this;
	   	   				var form=btn.ownerCt.ownerCt;
	   	   		        var kind=Ext.getCmp('cz_type').value;
	   	   		        var todate=Ext.Date.format(Ext.getCmp('cz_todate').value,'Y-m-d');
	   	   		        var reportName="";
	   	   		        var condition="";
	   	   		        var otherParameters=new Object();
	   	   		        if(kind=="自定义库存帐龄表"){
	   	   		        	reportName="PwAgeAll_custom";
	   	   		            condition="{batch_view.ba_date}<="+"date('"+todate+"')"+" and "+"{BATCH_VIEW.THISREMAIN}<>0";
	   	   		        }else if(kind=="应收帐龄表"){
	   	   		            reportName="ARAgeAll_custom";
	   	   		        }else if(kind=="应付帐龄表"){
	   	   		            reportName="APAgeAll_custom";
	   	   		        }else if(kind=="预收帐龄表"){
	   	   		            reportName="PRERECAgeAll_custom";
	   	   		        }else if(kind=="预付帐龄表"){
	   	   		            reportName="PREPAYAgeAll_custom";
	   	   		        }else if(kind=="发出商品账龄"){
	   	   		            reportName="GOODSSEND_custom";
	   	   		        }else if(kind=="应付暂估账龄"){
	   	   		            reportName="ESTIMATE_custom";
	   	   		        }else if (kind=="到期应付帐龄表"){
	   	   		        	 reportName="APAgeAll_Due_custom";
	   	   		        }else if (kind=="到期应收帐龄表"){
	   	   		        	 reportName="ARAgeAll_Due_custom";
	   	   		        }else if (kind=="其他应收账龄表"){
	   	   		        	 reportName="ARBill_OTRS_custom";
	   	   		        }else if (kind=="其他应付账龄表"){
	   	   		        	 reportName="APBill_OTRS_custom";
	   	   		        }else if (kind=="应收账龄表（总账）"){
	   	   		        	 reportName="ARAgeAll_ledger_custom";
	   	   		        }else if (kind=="应付账龄表（总账）"){
	   	   		        	 reportName="APAgeAll_ledger_custom";
	   	   		        }
				        var fromdate1='';var fromdate2='';var fromdate3='';var fromdate4='';var fromdate5='';var fromdate6='';var fromdate7='';var fromdate8='';var fromdate9='';var fromdate10='';var fromdate11='';var fromdate12='';var fromdate13='';var fromdate14='';var fromdate15='';
				        var todate1='';var todate2='';var todate3='';var todate4='';var todate5='';var todate6='';var todate7='';var todate8='';var todate9='';var todate10='';var todate11='';var todate12='';var todate13='';var todate14='';var todate15='';
						var from='',to='';
						for(var i=1;i<16;i++){
							var fromdate='',todatet='';
							var fd=Ext.getCmp('cz_fromdate'+i).value;
							var td=Ext.getCmp('cz_todate'+i).value;
							var fdl='';
							if(i>1){
								fdl=Ext.getCmp('cz_fromdate'+(i-1)).value;
							}
							if(fd!=null&&fd!=''&&td!=null&&fd!=''&&td-fd>=0){
								fromdate=Ext.Date.format(fd,'Y-m-d');
								todatet=Ext.Date.format(td,'Y-m-d');
								if(i>1){
									if(fdl==null||fdl==''){
										showError('区间设置有误:区间应连续设置，请设置区间'+(i-1));
										return;
									}else if(fdl-td!=86400000){
										showError('区间设置有误:区间应连续设置，区间'+i+'的截止日期应该与区间'+(i-1)+'的开始日期，天数差为1');
										return;
									}
								}
							}else if((fd==null||fd=='')&&(td==null||td=='')){
							}else{
								showError('区间'+i+"设置有误");
								return;
							}
							if(i==1){
								from+=fromdate,to+=todatet;
							}else{
								from+='@'+fromdate,to+='@'+todatet;
							}
							otherParameters['fromdate'+i]=fromdate;
							otherParameters['todate'+i]=todatet;
							
						}						
						Ext.Ajax.request({
							url : basePath + 'common/enterprise/getprinturl.action',
							params:{
								caller:caller,
								reportName:reportName
							},
							callback : function(opt, s, r) {
								var re = Ext.decode(r.responseText);
								console.log(re.printtype);								
	   	   				if(printType=='jasper'||re.printtype=='jasper'){
	   	   					var params=new Object();
	    					params['todate']=Ext.Date.format(Ext.getCmp('cz_todate').value,'Y-m-d');
	    					otherParameters['todate']=Ext.Date.format(Ext.getCmp('cz_todate').value,'Y-m-d');
	    					form.setLoading(true);
						    Ext.Ajax.request({
						    	url : basePath +'common/JasperReportPrint/print.action',
								params: {
									params: unescape(escape(Ext.JSON.encode(params))),
									caller:caller,
									reportname:reportName
								},
								method : 'post',
								timeout: 360000,
								callback : function(options,success,response){
									form.setLoading(false);
									var res = new Ext.decode(response.responseText);
									if(res.success){
										var condition='';
										condition=res.info.whereCondition==''?'where 1=1':'where '+res.info.whereCondition;
										other=Ext.encode(otherParameters);
										var url = res.info.printurl + '?userName='+res.info.userName+'&reportName='+res.info.reportName+'&whereCondition='+condition+'&otherParameters='+other+'&printType='+res.info.printtype;
										window.open(url,'_blank');
									}else if(res.exceptionInfo){
										var str = res.exceptionInfo;
										showError(str);return;
									}
								}
						    });
		   	   			}else{
		   	   				var c=from+'@'+to;
				    	 	console.log(me);
				    	 	console.log(me.ownerCt);
					   		Ext.Ajax.request({
						   		url : basePath + 'common/enterprise/getprinturl.action?caller=' + caller,
						   		params: {
									reportName: reportName
								},
						   		callback: function(opt, s, r) {
						   			var re = Ext.decode(r.responseText);
						   			thisreport=re.reportname;
						   			if(thisreport==null||thisreport==''){
							   			thisreport=reportName;
						   			}
						   			var whichsystem = re.whichsystem;
									var urladdress = "";
									var rpname = re.reportName;
									if(Ext.isEmpty(rpname) || rpname == "null"){
										urladdress = re.printurl;
									} else if(rpname.indexOf(thisreport) > 0){
										urladdress = re.ErpPrintLargeData;
									} else{
										urladdress = re.printurl;
									}
									me.FormUtil.batchPrint('',thisreport,condition,kind,todate,'',c,'',urladdress,whichsystem);
							   	}
					   		});
		   	   			}
							}
						});	
	   	   			}
	   	   		},	
	   	   		'erpCloseButton': {
	   	   			click: function(btn){
	   	   				me.FormUtil.beforeClose(me);
	   	   			}
	   	   		}
	   	   	});
	    },
	    getForm: function(btn){
	    	return btn.ownerCt.ownerCt;
	   	},
	   	calculate:function(btn){
	   		var me = this;
	   		var form=btn.ownerCt,id=Ext.getCmp('cz_id').value;
	   		form.setLoading(true);
			Ext.Ajax.request({
				url : basePath + 'scm/product/calculateDate.action?caller=' + caller,
				params:{id:id},
				callback: function(opt, s, r) {
			   		form.setLoading(false);
					var re = Ext.decode(r.responseText);
					if(re.success){
						 showMessage('提示', '区间日期计算成功!');
	    				 window.location.reload();
					}else{
						var str = re.exceptionInfo;
						showError(str);
						return;
					}
				}
			});
	   	}
   });