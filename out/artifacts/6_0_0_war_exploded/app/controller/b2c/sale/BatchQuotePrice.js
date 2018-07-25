Ext.QuickTips.init();
Ext.define('erp.controller.b2c.sale.BatchQuotePrice', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'b2c.common.Viewport','b2c.common.b2cBatchDealForm','b2c.common.b2cBatchDealGrid','b2c.sale.b2cPanel',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField',
     		'core.form.ConMonthDateField','core.form.YnField','core.form.MonthDateField','core.button.BatchQuotePrice',
     		'core.form.FtDateField','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn','core.button.Close'
     	],
    GridUtil:Ext.create('erp.util.GridUtil'), 	
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    init:function(){
    	var me = this;   
    	this.FormUtil = Ext.create('erp.util.FormUtil');
    	this.control({ 
    		'#setrange':{
    			click:function(btn){
    				var prof = Ext.getCmp('profits');
    				var profValue = prof.value;
    				if(prof!=null){
    					prof.setValue('');
    				}
    				
					var grid = Ext.getCmp('batchDealGridPanel');
					var count = grid.getStore().getCount();
    				if(count>0){
    					var rangeStr = Ext.getCmp('range').value;
    					var range;
    					if(rangeStr!=null){
    						var pattern = /^(-?)(\d+)(.?\d*)$/;
    						var result = rangeStr.match(pattern);
    						if(result!=null){
    							range = result[2];
    							if(result[3]!=null){
    								range += result[3];
    							}
    							var profit = Ext.getCmp('profits').value;
    							for(var i=0;i<grid.getStore().getCount();i++){
    								var record = grid.getStore().getAt(i);
    								var saleprice = record.get("go_saleprice");
    								if(saleprice!=null&&saleprice>0){
    									if(result[1]==''){
    										var newPrice = saleprice*(1+range*0.01);
    										record.set("newprice",newPrice);
    									}else if(result[1]=="-"){
    										if(range<=100){
        										var newPrice = saleprice*(1-range*0.01);
        										record.set("newprice",newPrice);	
    										}else{
    											showError("调整幅度不能小于-100");
    											return;
    										}
    									}
    								}		
    							}
    							grid.getView().refresh();
    							showMessage("提示", "设定成功",1000);
    						}
    					}
        			}
    				}
    		},
    		'#setprofit':{
    			click:function(btn){
    				var range = Ext.getCmp('range');
    				var rangeValue = range.value;
    				if(rangeValue!=null){
    					range.setValue('');
    				}
    				
					var grid = Ext.getCmp('batchDealGridPanel');
					var count = grid.getStore().getCount();
    				if(grid.getStore().getCount()>0){
    					var profitStr = Ext.getCmp('profits').value;
    					var profit;
    					if(profitStr!=null){
    						var pattern = /^(\d+)(.?\d*)$/;
    						var result = profitStr.match(pattern);
    						if(result!=null){
    							profit = result[1];
    							if(result[2]!=null){
    								profit += result[2];
    							}
    
    							for(var i=0;i<grid.getStore().getCount();i++){
    								var record = grid.getStore().getAt(i);
    								var avpurcprice = record.get("pr_avpurcprice");
    								if(avpurcprice!=null&&avpurcprice>0){
    									var newPrice = avpurcprice*(1+profit*0.01);
    									record.set("newprice",newPrice);
    								}		
    							}
    							grid.getView().refresh();
    							showMessage("提示", "设定成功",1000);
    						}
    					}	
        			}
    				}
    		},
    		'erpBatchPriceButton': {//确定报价
    			click: function(btn){
					var me = this;  				
					var currency = Ext.getCmp('currency').value;
					var taxrate = Ext.getCmp('taxrate').value;
					var gridStore = me.NeedSelectThrow();
					var flag = typeof gridStore === 'undefined';
					if(!flag){
						var jsondata = unescape(Ext.JSON.encode(gridStore).replace(/\\/g,"%"));
						var params = {};
						params.currency = currency;
						params.taxrate = taxrate;
						params.gridStore = jsondata;
						var transdata = JSON.stringify(params);
						me.FormUtil.getActiveTab().setLoading(true);
						Ext.Ajax.request({
						url : basePath + "b2c/batchquoteprice.action",
						method : 'post',
						params:{
							caller:'QuotePrice',
							parameters:transdata
						},
						callback : function(options,success,response){
							me.FormUtil.getActiveTab().setLoading(false);
							var localJson = new Ext.decode(response.responseText);
//							if(localJson.exceptionInfo){
//								showError(localJson.exceptionInfo);
//							}
//							if(localJson.success){
							Ext.Msg.alert('提示','报价成功',function(btn){
	   							Ext.getCmp('dealform').onQuery();
		   					});
//							}
						}
						});	
					}
    			}
    		},
    		'erpBatchDealGridPanel': {
    			afterrender: function(grid){
    				var form = Ext.getCmp('dealform');
        			if(form && form.items.items.length > 0){
        				//根据form字段的多少来调节form所占高度
        				var height = window.innerHeight;
            			var cw = 0;
            			Ext.each(form.items.items, function(){
            				cw += this.columnWidth;
            			});
            			cw = Math.ceil(cw);
            			if(cw == 0){
            				cw = 5;
            			} else if(cw > 2 && cw <= 5){
            				cw -= 1;
            			} else if(cw > 5 && cw < 8){
            				cw = 4;
            			}
            			cw = Math.min(cw, 5);
            			form.setHeight(height*cw/10 + 10);
            			grid.setHeight(height*(10 - cw)/10 - 10);            			
        			};
        			grid.store.on('datachanged',function(store){     
        			    grid.selModel.select(grid.multiselected);
        			});
    			},
    			storeloaded:function(grid){
    				/*grid.down('#storeCount').update({
        				count: grid.store.getCount()
        			});*/
    				grid.multiselected=[];
    			},
    			itemclick: function(view,record){
    				me.itemclick(view,record,me);
    			}
    		}, 
    		'erpBatchDealFormPanel': {
    			alladded: function(form){
    				var grid = Ext.getCmp('batchDealGridPanel');
    				//me.resize(form, grid);
    				var items = form.items.items, autoQuery = false;
					Ext.each(items, function() {
						var val = getUrlParam(this.name);
						if(!Ext.isEmpty(val)) {
							this.setValue(val);
							autoQuery = true;
							if(this.xtype == 'dbfindtrigger') {
								this.autoDbfind('form', caller, this.name, this.name + " like '%" + val + "%'");
							}
						}
					});
					if(!form.tempStore && grid){
						grid.columns[1].hide();
					}
					if(autoQuery) {
						setTimeout(function(){
							form.onQuery();
						}, 1000);
					}
					if(form.source=='allnavigation'){
        				Ext.each(form.dockedItems.items[0].items.items,function(btn){
        					btn.setDisabled(true);
        				});
        			}
    			//},
    			//afterrender:function(form){
					var item = [{
							xtype:'form',
							id:'dealFormTest',
							tempStore:false,
							detailkeyfield : '',
							frame : true,
							header : false,// 不显示title
							layout : 'column',
							padding : '0 0 0 0',
							autoScroll : true,
							defaultType : 'textfield',
							labelSeparator : ':',
							buttonAlign : 'center',
							columnWidth:1,
							items : [ {
								xtype : 'textfield',
								name : 'currency',
								id : 'currency',
								fieldLabel : '销售币别',
								readOnly:true,
								labelAlign: 'left',
								columnWidth : .25,
								cls: "form-field-allowBlank"
							},  {
								xtype : 'textfield',
								name : 'monthrate',
								id : 'monthrate',
								fieldLabel : '月度汇率',
								readOnly:true,
								hidden:true,
								labelAlign: 'left',
								columnWidth : .25,
								cls: "form-field-allowBlank"
							},{
								xtype : 'textfield',
								name : 'taxrate',
								id : 'taxrate',
								minValue : 0,
								fieldLabel : '税率(%)',
								labelAlign: 'left',
								columnWidth : .25,
								cls: "form-field-allowBlank"
							}, {
								xtype : 'textfield',
								name : 'range',
								id : 'range',
								minValue : 0,
								fieldLabel : '调整幅度(%)',
								labelAlign: 'left',
								emptyText:'如:2或-2',
								regex:/^-?\d+.?\d*$/,
								regexText:'请输入正确的格式，如2或-2',
								columnWidth : .2,
								cls: "form-field-allowBlank"
							},
						    {
								xtype: 'button',
						    	name:'setrange',
						    	id:'setrange',
						    	text:'设定',
						    	iconCls:'x-button-icon-submit',
						    	cls:'x-btn-gray',
						    	columnWidth : .05
						    },
							{
								xtype : 'textfield',
								name : 'profits',
								id : 'profits',
								minValue : 0,
								fieldLabel : '固定利润(%)',
								labelAlign: 'left',
								columnWidth : .2,
								emptyText:'请输入大于0的数',
								regex:/^\d+.?\d*$/,
								regexText:'请输入大于0的数',
								cls: "form-field-allowBlank"
							},
						    {
								xtype: 'button',
						    	name:'setprofit',
						    	id:'setprofit',
						    	text:'设定',
						    	iconCls:'x-button-icon-submit',
						    	cls:'x-btn-gray',
						    	columnWidth : .05
						    },
							{
								xtype : 'textfield',
								name : 'selfcurrency',
								id : 'selfcurrency',
								fieldLabel : '本位币别',
								readOnly:true,
								labelAlign: 'left',
								columnWidth : .25,
								cls: "form-field-allowBlank"
							}]	,
							listeners: {
						        afterrender: function(f){
									Ext.Ajax.request({
								   		url : basePath + "b2c/getCurrencyAndTaxrate.action?caller=sys&code=defaultCurrency",
								   		method : 'get',
								   		callback : function(options,success,response){
								   			var res = Ext.decode(response.responseText);	   		
											var taxrate = Ext.getCmp("taxrate");
											var selfcurrency = Ext.getCmp("selfcurrency");
											var monthrate = Ext.getCmp("monthrate");
											selfcurrency.setValue(res["currency"]);
											taxrate.setValue(res["taxrate"]);
											monthrate.setValue(res["monthrate"]);
								   		}
									});
						        	me.BaseUtil.getSetting('B2CSetting','B2CDefaultCurrency',function(val){
						        		var currency = Ext.getCmp('currency');
						        		currency.setValue(val);
						        	});
						        }
						    }
					}];
					form.add(item);
    			}
    		}

    	});
    },
	 itemclick:function(view,record,me){
//	    	var show=0;
//	    	me.onGridItemClick(view,record); 
//	    	var fieldValue=record.data["mdd_prodcode"];
//	    	var btn = Ext.getCmp('getb2cproductkind');
//	        if(fieldValue==undefined||fieldValue==""||fieldValue==null){
//		        if(btn && !btn.disabled){
//					 btn.setDisabled(true);
//				}
//	        }else{
//	        	if(btn && btn.disabled){
//				     btn.setDisabled(false);
//				}
//	        }
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
    NeedSelectThrow: function(){  //获取选取的需要操作的明细
    	var grid = Ext.getCmp('batchDealGridPanel');
    	var items = grid.getMultiSelected();
    	var me = this;
        Ext.each(items, function(item, index){
        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        		grid.multiselected.push(item);
        	}
        });
		var records = Ext.Array.unique(grid.multiselected);
		var data = new Array();
		var form = Ext.getCmp("dealform");
		if(records.length>0){
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
								if(Ext.isDate(v)){
									v = Ext.Date.toString(v);
								}
								o[f] = v;
							} else {
								o[f] = '';
							}
						});
					}
					if(grid.necessaryFields){
						Ext.each(grid.necessaryFields, function(f, index){
							var v = record.data[f];
							if(Ext.isDate(v)){
								v = Ext.Date.toString(v);
							}
							if(Ext.isNumber(v)){
								v = (v).toString();
							}
							o[f] = v;
						});
					}
					data.push(o);
				}
			});
			if(bool && !me.dealing){
				return data;
			}else {
				showError("没有需要处理的数据!");
				return;
			}
		}else {
			showError("请勾选需要的明细!");
			return;
		}		
	 },
		onGridItemClick: function(selModel, record, id){
			var me = this.GridUtil || this;
			var grid = selModel.ownerCt;
			if(grid && !grid.readOnly && !grid.NoAdd){
				var index = grid.store.indexOf(record);
				if(index == grid.store.indexOf(grid.store.last())){
					//me.add10EmptyItems(grid);//就再加10行
		    	}
				var btn = grid.down('erpDeleteDetailButton');
				if(btn)
					btn.setDisabled(false);
				btn = grid.down('erpAddDetailButton');
				if(btn)
					btn.setDisabled(false);
				btn = grid.down('copydetail');
				if(btn)
					btn.setDisabled(false);
				btn = grid.down('pastedetail');
				if(btn)
					btn.setDisabled(false);
				btn = grid.down('updetail');
				if(btn)
					btn.setDisabled(false);
				btn = grid.down('downdetail');
				if(btn)
					btn.setDisabled(false);
				if(grid.down('tbtext[name=row]')){
					grid.down('tbtext[name=row]').setText(index+1);
				}
			}
		},
		resize: function(form, grid){/*
	    	if(!this.resized && form && grid && form.items.items.length > 0){
	    		var height = window.innerHeight, 
					fh = form.getEl().down('.x-panel-body>.x-column-inner').getHeight();
				form.setHeight(35 + fh);
				grid.setHeight(height - fh - 35);
				this.resized = true;
			}
	    */}
});


		 