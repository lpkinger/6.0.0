Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.BOMCostCustom', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.bom.BOMCostCustom','core.form.Panel', 'core.trigger.DbfindTrigger','core.button.Close','core.button.BOMCost',
    		'core.form.ConMonthDateField','core.form.ConDateField','core.button.PrintDetail'
    	],
    init:function(){
    	this.control({ 
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpBOMCostButton': {
    			click: function(btn) {
    				var form = btn.ownerCt.ownerCt,
    					bo_id = Ext.getCmp('bo_id').value,
    					bv_bomversionid = Ext.getCmp('bv_bomversionid').value;
    				var data = JSON.stringify(form.getValues());
    				var bo_principle1_user = Ext.getCmp('bo_principle1_user').value;
    				var bo_rate1_user = Ext.getCmp('bo_rate1_user').value;
    				var bo_currency1_user = Ext.getCmp('bo_currency1_user').value;
    				var bo_ifusecurrency_user = Ext.getCmp('bo_ifusecurrency_user').value;
    				if(bo_id== "" || bo_id == null){
    				   showError("请先选择需要计算成本的BOM");
    				   return ;
    				}
    				if(bo_principle1_user == "" || bo_principle1_user == null){
    					showError("请先设置取价原则");
    					return ;
    				}
    				if((bo_rate1_user =="" || bo_rate1_user == null) && !bo_ifusecurrency_user){
    					showError("请先设置币别、汇率");
    					return ;
    				}
    				if(Ext.getCmp('bo_date').value==null){
    					showError("请先选择日期期间")
      				   return;
 	   				}
 	   				fromdate = Ext.Date.format(Ext.getCmp('bo_date').firstVal, 'Y-m-d');
 					todate = Ext.Date.format(Ext.getCmp('bo_date').secondVal, 'Y-m-d');
    				form.setLoading(true);
        			Ext.Ajax.request({
        				url: basePath + 'pm/bom/bomCostCustom.action',
        				params: {
        					bo_id: bo_id,
        					bv_bomversionid: bv_bomversionid,
        					fromdate: fromdate,
        					todate: todate,
        					data: data
        				},
        				timeout: 60000,
        				callback: function(opt, s, r) {
        					form.setLoading(false);
        					var rs = Ext.decode(r.responseText);
        					if(rs.exceptionInfo){
    							var str = rs.exceptionInfo;
    							showError(str);return;
        					}
        					Ext.getCmp('bv_bomversionid').setValue(rs.data.bv_bomversionid);
        					if(rs.success) {
        						alert('计算完成!');
        					}
        				}
        			});
    			}
    		},
    		'erpPrintDetailButton': {
    			beforerender:function(bt){
    				bt.text= $I18N.common.button.erpPrintDetailButton;
    			},
    			click:function(btn){
    				var form = btn.ownerCt.ownerCt,
    				    params=new Object();
    				var bomversionid=Ext.getCmp('bv_bomversionid').value;
    				var bo_id = Ext.getCmp('bo_id').value;
    				if(bomversionid== "" || bomversionid == null){
    					showError("版本号为空不能打印");
    					return;
    				}
    				if(bo_id== "" || bo_id == null){
     				   showError("BOMID为空不能打印");
     				  return;
     				}
    				params['bo_id']=bo_id;
    				params['bv_bomversionid']=bomversionid;
    				form.setLoading(true);
    			    Ext.Ajax.request({
    			    	url : basePath +'common/JasperReportPrint/print.action',
    					params: {
    						params: unescape(escape(Ext.JSON.encode(params))),
    						caller:caller,
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
    							condition= condition +" and bs_topbomid='"+bo_id+"' and bs_bomversionid='"+bomversionid+"'" ;
    							condition=encodeURIComponent(condition);
    							var url = res.info.printurl + '?userName='+res.info.userName+'&reportName='+res.info.reportName+'&whereCondition='
    							+condition+'&otherParameters=&printType='+res.info.printtype+'&title='+res.info.title;
    							window.open(url,'_blank');
    						}else if(res.exceptionInfo){
    							var str = res.exceptionInfo;
    							showError(str);return;
    						}
    					}
    			    });
    			}
    		},
    		'erpGatherButton': {
    			click:function(btn){
    				var	id = Ext.getCmp('bo_id').value,
							prodcode=Ext.getCmp('bo_mothercode').value,
							form=btn.ownerCt.ownerCt;
    				if(id == "" || id == null){
    				   showError("请先选择需要汇总成本的BOM");
    				   return;
    				}
    				var params = new Object();
	    			params['bo_id'] = id;
    				if(printType=='jasper'){
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
									var printcondition = '(BOM.bo_id='+id+' and BOMSTRUCT.bs_sonbomid>0) ';
									printcondition = res.info.whereCondition=='' ? 'where '+printcondition :'where '+res.info.whereCondition+' and '+printcondition;
									var url = res.info.printurl + '?userName='+res.info.userName+'&reportName='+res.info.reportName+'&whereCondition='+printcondition+'&otherParameters=&printType='+res.info.printtype;
									window.open(url,'_blank');
								}else if(res.exceptionInfo){
									var str = res.exceptionInfo;
									showError(str);return;
								}
							}
					    });
    				}else{
	    				var reportName = "BOMCostView";
	    				var condition = '{BOM.bo_id}=' + id+' and '+'{BOMSTRUCT.bs_sonbomid}>'+"0";
						this.FormUtil.onwindowsPrintBom(id, reportName, condition,prodcode);
    				}
    			}
    		}
    	});
    }
});