Ext.define('erp.view.common.print.Form', {
	extend : 'Ext.form.Panel',
	alias : 'widget.erpPrintFormPanel',
	id : 'printform',
	autoScroll : true,
	frame : true,
	padding:0,
	style : 'border-width: 1px;',
	defaultType : 'textfield',
	layout : 'column',
	labelSeparator : ':',
	fieldDefaults : {
		fieldStyle : "background:#fff;color:#515151;",
		labelAlign : "right",
		blankText : $I18N.common.form.blankText
	},
	FormUtil : Ext.create('erp.util.FormUtil'),
	tbar : {id:'print_tbar',padding:'5px',items:[{
				name : 'print',
				text : $I18N.common.button.erpPrintButton,
				iconCls : 'x-button-icon-print',
				cls : 'x-btn-gray',
				height : 26,
				handler : function(btn) {
					var form = Ext.getCmp('printform');
					if(printType=='jasper'){
						form.jasperReportPrint();
					}else{
					var reportName = '';
					var condition = '';
					var cop = form.title;
					var code = '';
					var custcode = '';
					var prodcode = '';
					var fromdate = '';
					var enddate = '';
					var id = "1";
					var idS = '';
					var vendorcode = '';
					var purchaseman = '';
					var status = '';
					var whcode = '';
					var salecode = '';
					var todate = '';
					var dateFW = '';
					var assifall = '';
					var tablename = form.tablename;

					var defaultCondition = "";
					var thisreport = "";
					if (cop == "销售额同比增长图(按业务员)" || cop == "销售额同比增长图(按业务员)") {
						fromdate = Ext.Date.format(Ext.getCmp('pi_date').firstVal, 'Y-m-d');
						todate = Ext.Date.format(Ext.getCmp('pi_date').secondVal, 'Y-m-d');
					}
					if (cop == "出入库明细表(过账日期)") {
						fromdate = Ext.Date.format(Ext.getCmp('pi_date1').firstVal, 'Y-m-d');
						todate = Ext.Date.format(Ext.getCmp('pi_date1').secondVal, 'Y-m-d');
					}
					if (cop == "明细分类账报表") {
						fromdate = Ext.Date.format(Ext.getCmp('sl_date').firstVal, 'Y-m-d');
						todate = Ext.Date.format(Ext.getCmp('sl_date').secondVal, 'Y-m-d');
					}
					if (cop == "期间库存表") {
						fromdate = Ext.Date.format(Ext.getCmp('pw_date').firstVal, 'Y-m-d');
						todate = Ext.Date.format(Ext.getCmp('pw_date').secondVal, 'Y-m-d');
					}
					if (cop == "库存周转率分析表" || cop == "原材料周转天数表") {
						fromdate = Ext.Date.format(Ext.getCmp('pi_date').firstVal, 'Y-m-d');
						todate = Ext.Date.format(Ext.getCmp('pi_date').secondVal, 'Y-m-d');
					}
					if (cop == "产品分析表") {
						fromdate = Ext.Date.format(Ext.getCmp('vr_recorddate').firstVal, 'Y-m-d');
						todate = Ext.Date.format(Ext.getCmp('vr_recorddate').secondVal, 'Y-m-d');
					}
					if (cop == "待处理统计‎(按检验人-日期‎)") {
						fromdate = Ext.Date.format(Ext.getCmp('ve_date').firstVal, 'Y-m-d');
						todate = Ext.Date.format(Ext.getCmp('ve_date').secondVal, 'Y-m-d');
					}
					if (cop == "供应商交货达成率报表") {
						fromdate = Ext.Date.format(Ext.getCmp('pd_delivery').firstVal, 'Y-m-d');
						todate = Ext.Date.format(Ext.getCmp('pd_delivery').secondVal, 'Y-m-d');
					}
					if (cop == "采购成本降价表(按最新入库)") {
						fromdate = Ext.Date.format(Ext.getCmp('pi_date').firstVal, 'Y-m-d');
						todate = Ext.Date.format(Ext.getCmp('pi_date').secondVal, 'Y-m-d');
						enddate = Ext.Date.format(Ext.getCmp('pi_date1').value, 'Y-m-d');
					}
					if (cop == "月COSTDOWN统计表") {
						fromdate = Ext.Date.format(Ext.getCmp('pu_date').firstVal, 'Y-m-d');
						todate = Ext.Date.format(Ext.getCmp('pu_date').secondVal, 'Y-m-d');
						enddate = Ext.Date.format(Ext.getCmp('PU_UPDATEDATE').value, 'Y-m-d');
					}
					if (cop == "在制仓库库存表(汇总)") {
						var myDate = new Date();
						var y = myDate.getFullYear();
						var m = myDate.getMonth() + 1;
						var d = myDate.getDate();
						m = m < 10 ? "0" + m : m;
						d = d < 10 ? "0" + d : d;
						todate = y + "-" + m + "-" + d;
					}
					if (cop == "制造单备料单(A4)") {
						idS = Ext.getCmp('ma_code').value;
					}
					if(cop=="客户信用执行报表"){
		        		todate=Ext.getCmp('ccr_yearmonth').value;
		        	}
					if (cop == "派车费用统计") {
						var dateRange = Ext.getCmp('tt_date');
						dateFW = dateRange.value;
						var custcode = Ext.getCmp('tt_carcode').value;
						todate = custcode;
						fromdate = Ext.getCmp('tt_date').firstVal;
						enddate = Ext.getCmp('tt_date').secondVal;
					}
					if (cop == "销售订单达成率表") {
						todate = Ext.Date.format(Ext.getCmp('sa_date').firstVal, 'Y-m-d');
						;

					}
					if (cop == "在制仓库库存表(制单)!") {
						var myDate = new Date();
						var y = myDate.getFullYear();
						var m = myDate.getMonth() + 1;
						var d = myDate.getDate();
						m = m < 10 ? "0" + m : m;
						d = d < 10 ? "0" + d : d;
						todate = y + "-" + m + "-" + d;
					}
					if (cop == "未发货客户订单明细表") {
						var dateRange = Ext.getCmp('sa_date');
						var dateValuePrint = dateRange.valuePrint;
						dateFW = dateRange.value;
					}
					if (cop == "集团应收周报表") {
						fromdate = Ext.Date.format(Ext.getCmp('ab_date').firstVal, 'Y-m-d');
						todate = Ext.Date.format(Ext.getCmp('ab_date').secondVal, 'Y-m-d');
					}
					if (cop == "集团业务员考核"|| cop == "集团课长考核") {
						fromdate = Ext.Date.format(Ext.getCmp('pi_date').firstVal, 'Y-m-d');
						todate = Ext.Date.format(Ext.getCmp('pi_date').secondVal, 'Y-m-d');
					}
					if (cop == "超期欠款表") {
						todate =Ext.getCmp('ab_yearmonth').value;
					}
					if (cop == "产值日报表") {
						todate =Ext.getCmp('pi_month').value;
					}
					if (cop == "产值月报表") {
						fromdate = Ext.getCmp('pi_month_n').firstVal;
						todate =  Ext.getCmp('pi_month_n').secondVal;
					}
					if(cop=='部门费用报表'){
						var flag=true;
						var yearmonth=Ext.getCmp('am_yearmonth').value;
						var catecode=Ext.getCmp('vd_catecode').value;
						if(yearmonth==null||yearmonth==''||catecode==null||catecode==''){
							showError('期间及科目编号不能为空');
							return;
						}else{
							  Ext.Ajax.request({
		    		 			  url : basePath +'common/getFieldsDatas.action',
		    		 			  params: {
		    					  caller: 'category,catemonth',
		    					  fields: 'distinct ca_code,ca_name',
		    			   		  condition: "ca_code=cm_catecode and cm_yearmonth='"+yearmonth+"' " +
		    			   		  		"and (cm_nowdebit<>0 or cm_nowcredit<>0)  and CA_PCODE='"+catecode+"' order by ca_code"
		    		  			  },
		    		  			  method : 'post',
		    		  			  async:false,
		    		  			  callback : function(opt, s, res){
			    					  var r = new Ext.decode(res.responseText);
			    					  if(r.exceptionInfo){
			    						   showError(r.exceptionInfo);return;
			    					   } else if(r.success && r.data){
				    					  	var data = Ext.decode(r.data), s = [];
				    					  	if(r.data.length<3){
				    					  		flag=false;
				    					  	}
		    								Ext.each(data, function(d){
							    				s.push(d.CA_CODE+'|'+ d.CA_NAME);
		    								});
		    								if(s.length<19){
												for(var i=s.length;i<20;i++){
													s.push('|');
			    								}
											}else{
												s=Ext.Array.slice(s,0,19);
											}   
											 todate=s.join('@');
				    					 }		    					 
			    					}
							  });
						}		
						if(!flag){
							showError('当前期间科目下无数据');
							return;
						}
					}
					if(cop == 'BOM配套表'){   
						var flag = true,error;
					  	Ext.Ajax.request({
						   		url : basePath + 'pm/bom/BOMStructPrintAll.action',
						   		async:false,
						   		callback: function(opt, s, r) {
						   			var r = Ext.decode(r.responseText); 
						   			if(r && r.exceptionInfo){
						   				flag = false;
						   				error = r.exceptionInfo;
					 			    }					 			    
						   		}
					   	});
		    			if(!flag){
							showError(error || 'BOM多级展开失败!');
							return;
						}
					}
					if(cop == '物料收发明细表'){
						fromdate = Ext.getCmp('pp_yearmonth').firstVal;
						todate   = Ext.getCmp('pp_yearmonth').secondVal;
					}
					var me = this;
					Ext.Ajax.request({
						url : basePath + 'common/enterprise/getprinturl.action?caller=' + caller,
						callback : function(opt, s, r) {
							var re = Ext.decode(r.responseText);
							if(re.printtype=="jasper"){
								form.jasperReportPrint();
							}else{
								defaultCondition = re.condition;
								thisreport = re.reportname;
								if (defaultCondition != null) {
									if (commonContition != "") {
										commonContition = defaultCondition + ' and ' + commonContition;
									} else {
										commonContition = defaultCondition;
									}
								}
								// ===========================================
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
								form.FormUtil.batchPrint(idS, thisreport, commonContition, cop, todate, dateFW, fromdate,
										enddate, urladdress, whichsystem);
								// 在这里传条件和报表名字
								
							}
							
						}
					});

					var length = form.items.length;
					var i = 0;
					var tablename = form.tablename;
					var reportName = "";
					var thisvalue = "";
					var field = "";
					var logicField = "";
					// 设置一个默认条件
					var commonContition = defaultCondition;
					while (i < length) {
						// 判断逻辑类型是否为空，不为空条件需替代为逻辑字段,不为空需要判断逻辑类型的字段类型，日期型需要格式化
						logicField = form.items.items[i].logic;
						if(logicField!=null&&logicField.indexOf('@')!=-1){
							logicField=logicField.split('@')[0];
						}
						if(logicField!=null&&logicField.indexOf('$')!=-1){
							logicField=logicField.split('$')[0];
						}
						field = form.items.keys[i];
						thisvalue = Ext.getCmp(form.items.keys[i]).value;
						if (thisvalue !=null && thisvalue != "" && logicField != null) {
							// 判断逻辑字段的类型，日期需要格式化
							if (form.items.items[i].xtype == "condatefield") {
								if(fromdate==''){
									fromdate = Ext.Date.format(Ext.getCmp(field).firstVal, 'Y-m-d');
								}
								if(todate==''){
									todate = Ext.Date.format(Ext.getCmp(field).secondVal, 'Y-m-d');
								}
								var dateRange = Ext.getCmp(logicField);
								var dateValuePrint = dateRange.valuePrint;
								if (commonContition != null && commonContition != '') {
									commonContition += ' and ' + dateValuePrint;
								} else {
									commonContition = dateValuePrint;
								}

							} else if (form.items.items[i].xtype == "ftfindfield") {
								var codeRange = Ext.getCmp(logicField);
								var codeValuePrint = codeRange.valuePrint;
								//===特殊处理,查询范围字段为明细行
								if(logicField=='vd_catecode'){
									codeValuePrint=codeValuePrint.replace(/voucher/g,'VoucherDetail'); 
								}
								if (commonContition != null && commonContition != '') {
									commonContition += ' and ' + codeValuePrint;
								} else {
									commonContition = codeValuePrint;
								}
							}else if(form.items.items[i].xtype == "textareafield"){
								if(cop=="工单备料批量打印"){
								var strs= new Array(); 
								var newstrs='';
								strs=Ext.getCmp('ma_code').value.split("\n");						
								for (i=0;i<strs.length ;i++ ) 
								{ 
									newstrs+="'"+strs[i]+"'"+","; 
								} 
		                        var textvalue=newstrs.substring(0, newstrs.length-1);
		                         commonContition +=' and ' +'{' + logicField + '}in' + "[" + textvalue + "]";
								}
							}
							else if (form.items.items[i].xtype == "datefield") {
								thisvalue = Ext.Date.format(thisvalue, 'Y-m-d');
								todate = thisvalue;
								// 倒算库存金额带日期,倒算未开票
								if (commonContition != null && commonContition != '') {
									commonContition += ' and ' + '{' + logicField + '}<=' + "date(" + "'" + thisvalue
											+ "'" + ")";
								} else {
									commonContition = '{' + logicField + '}<=' + "date(" + "'" + thisvalue + "'" + ")";
								}
							}else if (form.items.items[i].xtype == "erpYnField") {
								if (commonContition != null && commonContition != '') {
									commonContition += ' and ' + '{' + logicField + '}=' + thisvalue;
								} else {
									commonContition = '{' + logicField + '}=' + thisvalue;
								}
							} else if (form.items.items[i].xtype == "adddbfindtrigger" || form.items.items[i].xtype == "multidbfindtrigger") {
								var arr=thisvalue.split('#');
									var l=arr.length;
				    				var con='';
				    				for(var a=0;a<l;a++){
				    					if(a==l-1){
				    						con+=arr[a];
				    					}else{
				    						con+=arr[a]+"','";
				    					}
				    				}
								if (commonContition != null && commonContition != '') {
									commonContition += " and " + "{" + logicField + "} in ['" + con+"']";
								} else {
									commonContition = "{" + logicField + "} in ['" + con+"']";
								}
							} else if (form.items.items[i].xtype == "conmonthdatefield"){
								thisvalue=thisvalue.replace(/BETWEEN/g,'');
								thisvalue=thisvalue.replace(/AND/g,',');
								var a=thisvalue.substring(0,7);
								var b=thisvalue.substring(10,thisvalue.length);
								commonContition +='{' + logicField + '}>='+a+ ' and '+ '{' + logicField + '}<='+b ;
							}else if(form.items.items[i].xtype == "mastertrigger"){
								var arrcode = '';
								strs = thisvalue.split(",");
								if (strs.length > 1) {
									for (i = 0; i < strs.length; i++) {
										arrcode += "'" + strs[i] + "'" + ",";
									}
									commonContition += ' and ' + '{' + logicField + '}in ['
											+ arrcode.substring(0, arrcode.length - 1) + ']';
								} else {
									commonContition += ' and ' + '{' + logicField + '}=' + "'" + thisvalue+ "'";
								}
							}else {
								if (commonContition != null && commonContition != '') {
									// 判断是否是采购员ID，因为采购员ID设置成DBFIND，则判断不出是数值型还是字符型，故需要特殊处理一下
									if (logicField.indexOf('buyerid') > 0 || logicField.indexOf('sellerid') > 0
											|| logicField.indexOf('yearmonth') > 0||logicField.indexOf('id') > 0||logicField.indexOf('bo_id') > 0) {
										commonContition += ' and ' + '{' + logicField + '}=' + thisvalue;
									} else if (cop == "集团毛利润汇总表" || cop == "集团毛利润分析表-按客户" || cop == "集团毛利润分析表-按品牌"
											|| cop == "集团毛利润分析表-按业务员" || cop == "集团应收汇总表" || cop == "集团应付汇总表"
											|| cop == "集团呆库存汇总表-按业务员" || cop == "集团死库存汇总表-按业务员"
											|| cop == "集团呆库存汇总表-按业务员" || cop == "集团毛利润明细分析表") {
										var arrcode = '';
										strs = thisvalue.split(",");
										if (strs.length > 1) {
											for (i = 0; i < strs.length; i++) {
												arrcode += "'" + strs[i] + "'" + ",";
											}
											commonContition += ' and ' + '{' + logicField + '}in ['
													+ arrcode.substring(0, arrcode.length - 1) + ']';
										} else {
											commonContition += ' and ' + '{' + logicField + '}=' + "'" + thisvalue
													+ "'";
										}
									} else {
										commonContition += ' and ' + '{' + logicField + '}=' + "'" + thisvalue + "'";
									}

								} else {
									// 判断是否是采购员ID，因为采购员ID设置成DBFIND，则判断不出是数值型还是字符型，故需要特殊处理一下
									if (logicField.indexOf('buyerid') > 0 || logicField.indexOf('sellerid') > 0
											|| logicField.indexOf('yearmonth') > 0 || logicField.indexOf('gmpd_month') > 0) {
										if (cop == "成本汇总表") {
											commonContition = '{' + logicField + '}=' + "'" + thisvalue + "'";
										} else {
											commonContition = '{' + logicField + '}=' + thisvalue;
										}
									} else {
										if (cop == "分仓库库存报表" || cop == "仓库盘点表(按品牌规格)" || cop == "集团库存数量金额表"
												|| cop == "集团库存汇总表" || cop == "集团品牌库存汇总表" || cop == "集团银行余额汇总表"
												|| cop == "集团业务员应付汇总表") {
											var arrcode = '';
											var strs = new Array();
											if (cop == "分仓库库存报表" || cop == "仓库盘点表(按品牌规格)") {
												strs = thisvalue.split("#");
											} else {
												strs = thisvalue.split(",");
											}

											if (strs.length > 1) {
												for (i = 0; i < strs.length; i++) {
													arrcode += "'" + strs[i] + "'" + ",";
												}
												commonContition = '{' + logicField + '}in ['
														+ arrcode.substring(0, arrcode.length - 1) + ']';
											} else {
												commonContition = '{' + logicField + '}=' + "'" + thisvalue + "'";
											}

										} else {
											commonContition = '{' + logicField + '}=' + "'" + thisvalue + "'";
										}
									}

								}

							}

						}
						i = i + 1;
						// }
					}
					}
				}
			},'->', {
				text : $I18N.common.button.erpCloseButton,
				iconCls : 'x-button-icon-close',
				cls : 'x-btn-gray',
				height : 26,
				handler : function() {
					var win = parent.Ext.getCmp('queryWin')
					if(win){
						win.close();
					}else{
						var main = parent.Ext.getCmp("content-panel");
						main.getActiveTab().close();
					}		
				}
			} ]},
	initComponent : function() {
		var param = {
			caller : caller,
			condition : ''
		};
		this.FormUtil.getItemsAndButtons(this, 'common/singleFormItems.action', param);
		this.callParent(arguments);
		this.addKeyBoardEvents();
		// this.initFields(this);
	},
	/**
	 * 监听一些事件
	 * <br>
	 * Ctrl+Alt+S	单据配置维护
	 * Ctrl+Alt+P	参数、逻辑配置维护
	 */
	addKeyBoardEvents: function(){
		var me = this;
		Ext.EventManager.addListener(document.body, 'keydown', function(e){
			if(e.altKey && e.ctrlKey) {
				if(e.keyCode == Ext.EventObject.S) {
					var url = "jsps/ma/form.jsp?formCondition=fo_idIS" + me.fo_id + "&gridCondition=fd_foidIS" + me.fo_id, 
						forms = Ext.ComponentQuery.query('form'), 
						grids = Ext.ComponentQuery.query('gridpanel'),
						formSet = [], gridSet = [];
					if(forms.length > 0) {
						Ext.Array.each(forms, function(f){
							f.fo_id && (formSet.push(f.fo_id));
						});
					}
					if(grids.length > 0) {
						Ext.Array.each(grids, function(g){
							if(g.xtype.indexOf('erpGridPanel') > -1)
								gridSet.push(window.caller);
							else if(g.caller)
								gridSet.push(g.caller);
						});
					}
					if(formSet.length > 0 || gridSet.length > 0) {
						url = "jsps/ma/multiform.jsp?formParam=" + formSet.join(',') + '&gridParam=' + gridSet.join(',');
					}
					me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', url);
				} else if(e.keyCode == Ext.EventObject.P) {
					me.FormUtil.onAdd('configs-' + caller, '逻辑配置维护(' + caller + ')', "jsps/ma/logic/config.jsp?whoami=" + caller);
				}
			}
		});
	},
	jasperReportPrint:function(){
		var form=this;
		var conditionArr= new Array();
		var otherParameters=new Object();
		var params=new Object();
		Ext.each(form.items.items,function(item){
			var logicField = item.logic,field = item.name,thisvalue = Ext.getCmp(field).value,
			paramsName=new Array(),otherParamName=new Array();
			if(thisvalue !=null && thisvalue != "" && logicField != null){
				if(logicField.indexOf('$')!=-1){
					otherParamName=logicField.split('$');
					logicField=otherParamName[0];
				}
				if(logicField.indexOf('@')!=-1){
					paramsName=logicField.split('@');
					logicField=paramsName[0];
				}
				if (item.xtype == "condatefield") {
					var firstValue=Ext.Date.format(Ext.getCmp(field).firstVal, 'Y-m-d');
					var secondValue=Ext.Date.format(Ext.getCmp(field).secondVal, 'Y-m-d');
					if(logicField){
						conditionArr.push("trunc("+logicField+") between to_date('"+firstValue+"','yyyy-mm-dd') and " +
								"to_date('"+secondValue+"','yyyy-mm-dd')");
					}						
					if(paramsName.length>1){
						params[paramsName[1]]=firstValue;
						params[paramsName[2]]=secondValue;
					}
					if(otherParamName.length>1){
						otherParameters[otherParamName[1]]=firstValue;
						otherParameters[otherParamName[2]]=secondValue;
					}
				}else if(item.xtype == "datefield"){
					thisvalue = Ext.Date.format(thisvalue, 'Y-m-d');
					if(logicField){
						conditionArr.push("trunc("+logicField+")<=to_date('"+thisvalue+"','yyyy-mm-dd')");
					}
					if(paramsName.length>1)
						params[paramsName[1]]=thisvalue;
					if(otherParamName.length>1){
						otherParameters[otherParamName[1]]=thisvalue;
					}
				}else if(item.xtype == "adddbfindtrigger" || item.xtype == "multidbfindtrigger"){
						console.log('测试安全');
						var arr=thisvalue.split('#');
						var l=arr.length;
						if(l>0 && logicField){
							conditionArr.push(logicField+" in ('"+arr.join("','")+"')");
						}
				}else if(item.xtype == "textareafield"){
					if(logicField){
						conditionArr.push(logicField+" in ('"+thisvalue.split("\n").join("','")+"')");
					}
				}else if (item.xtype == "conmonthdatefield"){
					thisvalue=thisvalue.replace(/BETWEEN/g,'');
					thisvalue=thisvalue.replace(/AND/g,',');
					var a=thisvalue.substring(0,7);
					var b=thisvalue.substring(10,thisvalue.length);
					if(logicField){
						conditionArr.push(logicField+" >="+a+ " and "+ logicField+ "<="+b);
					}
					if(paramsName.length>1){
						params[paramsName[1]]=a;
						params[paramsName[2]]=b;
					}
					if(otherParamName.length>1){
						otherParameters[otherParamName[1]]=a;
						otherParameters[otherParamName[2]]=b;
					}
				}else if(item.xtype == "mastertrigger"){
					var arr=thisvalue.split(',');
					var l=arr.length;
					if(l>0 && logicField){
						conditionArr.push(logicField+" in ('"+arr.join("','")+"')");
					}
				} else{
					if(logicField){
						conditionArr.push(logicField+"='"+thisvalue+"'");
					}
					if(paramsName.length>1)
						params[paramsName[1]]=thisvalue;
					if(otherParamName.length>1){
						otherParameters[otherParamName[1]]=thisvalue;
					}
				}
			}
			
		});
		wherecondition= conditionArr.join(' and '); 
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
					condition=wherecondition.length>0?condition+' and '+wherecondition:condition;
					other=Ext.encode(otherParameters);
					condition=encodeURIComponent(condition);
					var url = res.info.printurl + '?userName='+res.info.userName+'&reportName='+res.info.reportName+'&whereCondition='
					+condition+'&otherParameters='+other+'&printType='+res.info.printtype+'&title='+res.info.title;
					window.open(url,'_blank');
				}else if(res.exceptionInfo){
					var str = res.exceptionInfo;
					showError(str);return;
				}
			}
	    });
	}
});