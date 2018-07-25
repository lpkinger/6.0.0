Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.BatchDeal', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil', 'erp.util.EventSource'],
    views:[
     		'fa.ars.ProdToARBill.Viewport','fa.ars.ProdToARBill.Form','fa.ars.ProdToARBill.GridPanel','core.form.MultiField',
     			'core.form.SeparNumber','core.button.VastDeal','core.button.VastPrint','core.button.VastAnalyse','core.button.GetVendor',
	     		'core.button.VastTurnPurc','core.button.DealMake','core.button.MakeOccur','core.button.SaleOccur',
	     		'core.button.AllThrow','core.button.SelectThrow','core.button.VastTurnARAPCheck',
	     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField',
	     		'core.form.MonthDateField','core.button.ByAmount','core.trigger.TextAreaTrigger','core.form.YnField',
	     		'core.trigger.AddDbfindTrigger','core.grid.YnColumn','core.form.SeparNumber'
     	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpProdToARBillFormPanel': {
    			alladded: function(form){
    				var grid = Ext.getCmp('batchDealGridPanel');
    				if(grid)
    					me.resize(form, grid);
    			}
    		},
    		'erpProdToARBillGridPanel': {
    			afterrender: function(grid){
    				grid.plugins[0].on('afteredit',function(e){
    					grid.selModel.countAmount();
    				});
    				var form = Ext.getCmp('dealform');
        			if(form)
        				me.resize(form, grid);
    			}
    		},
    		'erpVastDealButton': {
    			click: {
    				fn:function(btn){
    					me.beforeVastDeal(btn);
    				},
        			lock:2000
    			}
    		},
    		'erpVastAnalyseButton': {
    			click: {
    				fn:function(btn){
    					console.log(111);
    					me.vastDeal(btn.ownerCt.ownerCt.dealUrl, btn);
    				},
        			lock:2000
    			}
    		},
    		'erpVastPrintButton': {
    			click: {
    				fn:function(btn){
    					me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    				},
        			lock:2000
    			}
    		},
    		'erpByAmountButton':{
    			click: {
    				fn:function(btn){
	    				//提取grid中的数据   查看数据中的客户编号是否与FORM中的一致   一致则可执行  不一直需要提示
	    				var grid =Ext.getCmp('batchDealGridPanel');
	    				var items = grid.store.data.items;
	    				var custcode = '', field = 'pi_arcode';
	    				if(caller =='ProdInOut!ToARBill!Deal!ars'){
	    					custcode = Ext.getCmp('pi_arcode').getValue();
	    				} else if(caller =='ProdInOut!ToAPBill!Deal!ars'){
	    					custcode = Ext.getCmp('pi_receivecode').getValue();
	    					field = 'pi_receivecode';
	    				}
	    				var bool = true;
	    				if(custcode==''||custcode == null){
	    					bool = false;
	    				}else{
	    					Ext.each(items,function(item,index){
	        					if(item.data[field]!=custcode){
	        						bool = false;
	        					}
	        				});
	    				}
	    				
	    				if(bool){
	    					me.amountButton();
	    				}else{
	    					if(caller =='ProdInOut!ToARBill!Deal!ars'){
	    						Ext.Msg.alert("提示","请按客户筛选结果!");
	    					}else if(caller =='ProdInOut!ToAPBill!Deal!ars'){
	    						Ext.Msg.alert("提示","请按供应商筛选结果");
	    					}
	    				}
    				},
        			lock:2000
    			}
    		},
    		//应付的
    		'field[name=differ]': {
				change: function(field){
					var d = field.value || 0;
					me.countAmount(d);
				}
    		},
    		//应收的
    		'field[name=ab_differ]': {
				change: function(field){
					var d = field.value || 0;
					me.countAmount(d);
				}
    		}
    	});
    },
    countAmount: function(differ){
    	var me = this;
    	var grid = Ext.getCmp('batchDealGridPanel');
    	var items = grid.selModel.selected.items;
    	var taxsum = 0;
    	var	priceFormat = grid.down('gridcolumn[dataIndex=pd_thisvoprice]').format,
    		fsize = (priceFormat && priceFormat.indexOf('.') > -1) ? 
    				priceFormat.substr(priceFormat.indexOf('.') + 1).length : 6;
    	if(caller =='ProdInOut!ToARBill!Deal!ars' || caller =='ARBill!ToARCheck!Deal' || caller=='ProdInOut!ToARCheck!Deal' ){
        	Ext.each(items,function(item,index){
        		var a = grid.BaseUtil.numberFormat(Number(item.data['pd_thisvoprice']),fsize);
        		var b = grid.BaseUtil.numberFormat(Number(item.data['pd_thisvoqty']),2);
        		var rate = grid.BaseUtil.numberFormat(Number(item.data['pi_rate']),4);
        		var taxrate = grid.BaseUtil.numberFormat(Number(item.data['pd_taxrate']),4);
        		taxsum = taxsum + (grid.BaseUtil.numberFormat((a*(b*100)/100)*taxrate/(100+taxrate)*rate,4));
        	});
        	Ext.getCmp('ab_taxamount')._val = taxsum;
           	Ext.getCmp('ab_taxamount').setValue(Ext.util.Format.number(taxsum  + Number(differ), "0.00"));
    	}else if(caller=='ProdInOut!ToAPBill!Deal!ars' || caller=='ProdInOut!ToAPCheck!Deal' || caller=='APBill!ToAPCheck!Deal'){
        	Ext.each(items,function(item,index){
        		var a = grid.BaseUtil.numberFormat(Number(item.data['pd_thisvoprice']),fsize);
        		var b = grid.BaseUtil.numberFormat(Number(item.data['pd_thisvoqty']),2);
        		var taxrate = grid.BaseUtil.numberFormat(Number(item.data['pd_taxrate']),4);
        		taxsum = taxsum + grid.BaseUtil.numberFormat((a*(b*100)/100)*taxrate/(100+taxrate),2);
        	});
           	Ext.getCmp('taxsum')._val = taxsum;
           	Ext.getCmp('taxsum').setValue(Ext.util.Format.number(taxsum  + Number(differ), "0.00"));
    	}else{
        	Ext.each(items,function(item,index){
        		var taxrate = grid.BaseUtil.numberFormat(Number(item.data['pd_taxrate']),4);
        		var a = Number(item.data['pd_thisvoprice']);
        		var b = Number(item.data['pd_thisvoqty']);
        		taxsum = taxsum +  Number(grid.BaseUtil.numberFormat((a*b*taxrate/100)/(1+taxrate/100),2));
        	});
           	Ext.getCmp('taxsum')._val = taxsum;
           	Ext.getCmp('taxsum').setValue(Ext.util.Format.number(taxsum  + Number(differ), "0.00"));
    	}

    },
    resize: function(form, grid){
    	if(!this.resized && form && grid && form.items.items.length > 0){
    		var height = window.innerHeight, 
				fh = form.getEl().down('.x-panel-body>.x-column-inner').getHeight();
			form.setHeight(35 + fh);
			grid.setHeight(height - fh - 35);
			this.resized = true;
		}
    },
    beforeVastDeal:function(btn){
    	//panduan 币别是否一致
    	//判断客户是否一致
    	var me = this;
    	me.vastDeal(btn.ownerCt.ownerCt.dealUrl, btn);
    },
    formOnQuery:function(){
    	var me = this;
		var grid = Ext.getCmp('batchDealGridPanel');
		var form = Ext.getCmp('dealform');
		var condition = grid.defaultCondition || '';
		Ext.each(form.items.items, function(f){
			if(f.logic != null && f.logic != ''){
				if(f.xtype == 'checkbox' && f.value == true){
					if(condition == ''){
						condition += f.logic;
					} else {
						condition += ' AND ' + f.logic;
					}
				} else if(f.xtype == 'datefield' && f.value != null&&!contains(f.logic, 'to:', true)){
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d');
					if(condition == ''){
						condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
					} else {
						condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
					}
				} else if(f.xtype == 'datetimefield' && f.value != null){
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d H:i:s');
					if(condition == ''){
						condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					} else {
						condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					}
				} else if(f.xtype == 'numberfield' && f.value != null && f.value != ''){
					if(condition == ''){
						condition += f.logic + '=' + f.value;
					} else {
						condition += ' AND ' + f.logic + '=' + f.value;
					}
				} else {
					//一般情况下，在执行批量处理时,是不需要把form的数据传回去,
					//但某些情况下，需要将form的某些字段的值也传回去
					//例如 请购批量转采购，如果指定了采购单号，就要把采购单号传回去
					if(contains(f.logic, 'to:', true)){
						if(!grid.toField){
							grid.toField = new Array();
						}
						grid.toField.push(f.logic.split(':')[1]);
					} else {
						if(f.value != null && f.value != ''){
							if(contains(f.value, 'BETWEEN', true) && contains(f.value, 'AND', true)){
								if(condition == ''){
									condition += f.logic + " " + f.value;
								} else {
									condition += ' AND (' + f.logic + " " + f.value + ")";
								}
							} else if(contains(f.value, '||', true)){
								var str = '';
								Ext.each(f.value.split('||'), function(v){
									if(v != null && v != ''){
										if(str == ''){
											str += f.logic + "='" + v + "'";
										} else {
											str += ' OR ' + f.logic + "='" + v + "'";
										}
									}
								});
								if(condition == ''){
									condition += str;
								} else {
									condition += ' AND (' + str + ")";
								}
							} else {
								if(condition == ''){
									condition += f.logic + "='" + f.value + "'";
								} else {
									condition += ' AND (' + f.logic + "='" + f.value + "')";
								}
							}
						}
					}
				}
			}
		});
		var gridParam = {caller: caller, condition: condition};
		grid.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
		Ext.getCmp('pi_counttotal').setValue('0');
		Ext.getCmp('pi_amounttotal').setValue('0');
//		me.amountButton();
    },
    
    //点击按金额开票按钮后的功能
    amountButton:function(){
    	var totalStr = Ext.getCmp("pi_total").getValue();
		var grid = Ext.getCmp('batchDealGridPanel');
		var selectArray = new Array();			//前面需要打勾的数据 
		var total = Ext.Number.from(totalStr,0);	//开票金额field 中的数据
//		pd_thisvoqty   pd_thisvoprice
		var total2 =0;
		var ar = 0;
		
		if(total>0){
			Ext.each(grid.store.data.items,function(item,index){
				//此方法是所选的金额总和刚好大于输入的金额
				if(item.data['pd_thisvoqty']>0){
					var oneitemtotal =Ext.Number.from(item.data['pd_thisvoprice'],0);
					var itemtotal = 0;
					if(caller =='ProdInOut!ToAPBill!Deal!ars'){
						itemtotal = (item.data['pd_inqty']-item.data['pd_invoqty'])*item.data['pd_thisvoprice'];
					}else if(caller =='ProdInOut!ToARBill!Deal!ars'){
						itemtotal = (item.data['pd_outqty']-item.data['pd_invoqty'])*item.data['pd_thisvoprice'];
					}
					if(total2<total){
						selectArray.push(item);
						if(total2+itemtotal>total){
							if(oneitemtotal!=0){
								ar = (total-total2)/oneitemtotal;
								if(ar>parseInt(ar)){
									ar = parseInt(ar)+1;
								}else{
									ar = parseInt(ar);
								}
							}
						}
					}
//					if(ar !=0){
//						return false;
//					}else{
						total2 = total2+itemtotal;
//					}
				}
				//下面的计算方式是所选的金额总和小于输入的金额
				
				/*
				//发货数量大于0  代表可以开票
				if(item.data['pd_thisvoqty']>0){
					
					var oneitemtotal =Ext.Number.from(item.data['pd_thisvoprice'],0);
					//本次开票数量 = 发货数量-已转发票数
					//当初始化的时候  本次开票数 为最大可开票数量
					var itemtotal = (item.data['pd_outqty']-item.data['pd_invoqty'])*item.data['pd_thisvoprice'];
					if(total2+oneitemtotal<=total){
						selectArray.push(item);
						if(total2+itemtotal>total){
							if(oneitemtotal!=0){
								ar = (total-total2)/oneitemtotal;
								//取最后一条的数量   取整数   比如：如果开票金额里填写5000   则ar个*单价 加前面所勾选的合计<=5000   (ar+1)*单价  加前面所勾选的合计>5000
								ar = parseInt(ar);
							}
						}
					}
					if(ar !=0){
						return false;
					}else{
						total2 = total2+itemtotal;
					}
				}
			*/});
			
		}
		grid.selModel.select(selectArray);						//在符合条件的数据前面打勾
		if(grid.selModel.lastSelected){
			grid.selModel.lastSelected.set('pd_thisvoqty',ar);		//为最后一条数据中的数量赋值
		}
		
		//自动计算发票书的金额总和
		//相应的方法在此BatchDeal中GRID对应的view 中
		grid.selModel.countAmount();
		
    },
    countGrid: function(){
    	//重新计算合计栏值
    	var grid = Ext.getCmp('batchDealGridPanel');
    	Ext.each(grid.columns, function(column){
			if(column.summary){
				var sum = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						sum += Number(item.value);
					}
				});
				Ext.getCmp(column.dataIndex + '_sum').setText(column.text + '(sum):' + sum);
			} else if(column.average) {
				var average = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						average += Number(item.value);
					}
				});
				average = average/grid.store.data.items.length;
				Ext.getCmp(column.dataIndex + '_average').setText(column.text + '(average):' + average);
			} else if(column.count) {
				var count = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						count++;
					}
				});
				Ext.getCmp(column.dataIndex + '_count').setText(column.text + '(count):' + count);
			}
		});
    },
    
    vastDeal: function(url,btn){
    	var grid = Ext.getCmp('batchDealGridPanel');
    	var form = Ext.getCmp('dealform');
    	var records = grid.selModel.getSelection();
		if(records.length > 0){
			var params = new Object();
			params.caller = caller;
			var data = new Array();
			var bool = false;
			Ext.each(records, function(record, index){
				var f = form.fo_detailMainKeyField;
				if((grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0) 
	        		||(f && this.data[f] != null && this.data[f] != ''
		        		&& this.data[f] != '0' && this.data[f] != 0)){
					bool = true;
					var o = new Object();
					if(grid.keyField){
						o[grid.keyField] = record.data[grid.keyField];
					} else {
						params.id[index] = record.data[form.fo_detailMainKeyField];
					}
					if(grid.toField){
						Ext.each(grid.toField, function(f, index){
							var v = Ext.getCmp(f).value;
							if(v != null && v.toString().trim() != '' && v.toString().trim() != 'null'){
								if(!Ext.isDate(v)){
									v = String(v);
								}
								o[f] = v;
							}
						});
					}
					if(grid.necessaryFields){
						Ext.each(grid.necessaryFields, function(f, index){
							var v = record.data[f];
							if(Ext.isDate(v)){
								v = Ext.Date.toString(v);
							} else {
								v = String(v);
							}
							o[f] = v;
						});
					}
					data.push(o);
				}
			});
			if(bool){
				if(btn){
		    		btn.setDisabled(true);
		    	}
				params.data = Ext.encode(data);
				new erp.util.EventSource({
					url : basePath + url,
					data: params,
					showProgress: data.length > 30,
					onComplete: function(result) {
						if(typeof (f = Ext.getCmp('differ')) !== 'undefined'){
	    					f.setValue('0');
	    				}
	    				if(typeof (f = Ext.getCmp('ab_differ')) !== 'undefined'){
	    					f.setValue('0');
	    				}
	    				window.setTimeout(function(){//解决明细太多提示框卡住问题
	    					if(result.log){
	    						showMessage("提示", result.log);
		    				}else{
		    					showMessage("提示", "操作成功");
		    				}
	    					if(btn){
	    			    		btn.setDisabled(false);
	    			    	}
	    				}, 1000);
	    				Ext.getCmp('dealform').onQuery();
					},
					onError: function(error) {
						if(error){
							var errorJson = null;
							try {
					                errorJson = JSON.parse(error);
					            } catch (e) {
					            }
				   			if(errorJson&&errorJson.exceptionInfo){
				   				var str = errorJson.exceptionInfo;			   				
				   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){			   					
				   					str = str.replace('AFTERSUCCESS', '');	
				   					grid.multiselected = new Array();
				   					Ext.getCmp('dealform').onQuery();
				   				}
				   				showError(str);
				   				if(btn){
		    			    		btn.setDisabled(false);
		    			    	}
				   				return;
				   			}else{
				   				showMessage("提示", error);
				   				if(btn){
		    			    		btn.setDisabled(false);
		    			    	}
				   			}
						}
					}
				});
			} else {
				showError("没有需要处理的数据!");
			}
		}else{
			showError("没有需要处理的数据!");
		}
    }
});