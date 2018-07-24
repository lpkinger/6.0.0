Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.BOMFiCost', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.bom.BOMFiCost','core.form.Panel', 'core.trigger.DbfindTrigger',
    		'core.button.Print','core.button.Close','core.button.BOMCost','core.button.Gather'
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
    					pr_code = Ext.getCmp('bo_mothercode').value;
    				if(bo_id== "" || bo_id == null){
    				   showError("请先选择需要计算成本的BOM");
    				}
    				form.setLoading(true);
        			Ext.Ajax.request({
        				url: basePath + 'pm/bom/costFi.action',
        				params: {
							caller:caller,
        					bo_id: bo_id,
        					pr_code: pr_code
        				},
        				timeout: 60000,
        				callback: function(opt, s, r) {
        					form.setLoading(false);
        					var rs = Ext.decode(r.responseText);
        					Ext.getCmp('bo_id').setValue(rs.data.boid);
        					if(rs.success) {
        						alert('计算完成!');
        					}
        				}
        			});
    			}
    		},
    		'erpPrintButton': {
    			click:function(btn){
    				var reportName = "BOMCostView",
						id = Ext.getCmp('bo_id').value,
						prodcode=Ext.getCmp('bo_mothercode').value;
    				if(id == "" || id == null){
    				   showError("请先选择需要打印成本的BOM");
    				   return;
    				}
					var condition = '{BOM.bo_id}=' + id+' and {BomStruct.bs_topmothercode}='+"'"+Ext.getCmp('bo_mothercode').value+"'";
					var thisreport="";
					Ext.Ajax.request({
				   		url : basePath + 'common/enterprise/getprinturl.action?caller=' + caller,
				   		callback: function(opt, s, r) {
				   			var re = Ext.decode(r.responseText);
				   			var thisreport=re.reportname;				   			
				   			//===========================================
							var rpname = re.reportName;						
							if(thisreport==""||thisreport==null||thisreport=='null'){
								thisreport=reportName;
							}
							console.log("reportname="+thisreport);
				   		}
			   		});
					
					this.FormUtil.onwindowsPrintBom(id, thisreport, condition,prodcode);
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